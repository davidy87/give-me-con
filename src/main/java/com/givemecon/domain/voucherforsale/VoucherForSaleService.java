package com.givemecon.domain.voucherforsale;

import com.givemecon.domain.image.voucherforsale.VoucherForSaleImage;
import com.givemecon.domain.image.voucherforsale.VoucherForSaleImageRepository;
import com.givemecon.util.image_entity.ImageEntityUtils;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.voucherkind.VoucherKind;
import com.givemecon.domain.voucherkind.VoucherKindRepository;
import com.givemecon.util.exception.concrete.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.givemecon.domain.voucherforsale.VoucherForSaleDto.*;
import static com.givemecon.domain.voucherforsale.VoucherForSaleStatus.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class VoucherForSaleService {

    private final MemberRepository memberRepository;

    private final VoucherKindRepository voucherKindRepository;

    private final VoucherForSaleRepository voucherForSaleRepository;

    private final VoucherForSaleImageRepository voucherForSaleImageRepository;

    private final RejectedSaleRepository rejectedSaleRepository;

    private final ImageEntityUtils imageEntityUtils;

    public VoucherForSaleResponse save(String username, VoucherForSaleRequest requestDto) {
        Member seller = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        VoucherKind voucherKind = voucherKindRepository.findById(requestDto.getVoucherId())
                .orElseThrow(() -> new EntityNotFoundException(VoucherKind.class));

        VoucherForSaleImage voucherForSaleImage = voucherForSaleImageRepository.save(
                imageEntityUtils.createImageEntity(VoucherForSaleImage.class, requestDto.getImageFile()));

        VoucherForSale voucherForSale = voucherForSaleRepository.save(requestDto.toEntity());
        voucherForSale.updateSeller(seller);
        voucherForSale.updateVoucher(voucherKind);
        voucherForSale.updateVoucherForSaleImage(voucherForSaleImage);

        return new VoucherForSaleResponse(voucherForSale);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('USER')")
    public List<VoucherForSaleResponse> findAllByUsername(String username) {
        Member seller = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        return voucherForSaleRepository.findAllBySeller(seller).stream()
                .map(VoucherForSaleResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VoucherForSaleResponse> findAllByStatus(StatusCodeParameter paramDto) {
        VoucherForSaleStatus status = findStatus(paramDto.getStatusCode());

        return voucherForSaleRepository.findAllByStatus(status).stream()
                .map(VoucherForSaleResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public ImageResponse findImageUrl(Long voucherForSaleId) {
        VoucherForSale voucherForSale = voucherForSaleRepository.findOneWithImage(voucherForSaleId)
                .orElseThrow(() -> new EntityNotFoundException(VoucherForSale.class));

        return new ImageResponse(voucherForSale);
    }

    public VoucherForSaleResponse updateStatus(Long id, StatusUpdateRequest requestDto) {
        VoucherForSale voucherForSale = voucherForSaleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(VoucherForSale.class));

        VoucherForSaleStatus newStatus = findStatus(requestDto.getStatusCode());
        voucherForSale.updateStatus(newStatus);

        // 판매 요청 거절 시
        if (newStatus == REJECTED) {
            recordRejectedSale(voucherForSale.getId(), requestDto.getRejectedReason());
        }

        return new VoucherForSaleResponse(voucherForSale);
    }

    private void recordRejectedSale(Long voucherForSaleId, String rejectedReason) {
        RejectedSale rejectedSale = RejectedSale.builder()
                .voucherForSaleId(voucherForSaleId)
                .rejectedReason(rejectedReason)
                .build();

        rejectedSaleRepository.save(rejectedSale);
    }

    private VoucherForSaleStatus findStatus(Integer statusCode) {
        VoucherForSaleStatus[] statuses = VoucherForSaleStatus.values();

        if (statusCode < 0 || statusCode >= statuses.length) {
            throw new EntityNotFoundException(VoucherForSale.class);
        }

        return statuses[statusCode];
    }

    public void delete(Long id) {
        VoucherForSale voucherForSale = voucherForSaleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(VoucherForSale.class));

        voucherForSaleRepository.delete(voucherForSale);
    }
}
