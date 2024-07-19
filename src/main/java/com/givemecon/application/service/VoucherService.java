package com.givemecon.application.service;

import com.givemecon.common.exception.concrete.EntityNotFoundException;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.entity.voucher.RejectedSale;
import com.givemecon.domain.entity.voucher.Voucher;
import com.givemecon.domain.entity.voucher.VoucherImage;
import com.givemecon.domain.entity.voucher.VoucherStatus;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
import com.givemecon.domain.repository.MemberRepository;
import com.givemecon.domain.repository.voucher.RejectedSaleRepository;
import com.givemecon.domain.repository.voucher.VoucherImageRepository;
import com.givemecon.domain.repository.voucher.VoucherRepository;
import com.givemecon.domain.repository.voucherkind.VoucherKindRepository;
import com.givemecon.infrastructure.s3.image_entity.ImageEntityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.givemecon.application.dto.VoucherDto.*;
import static com.givemecon.domain.entity.voucher.VoucherStatus.SALE_REJECTED;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class VoucherService {

    private final MemberRepository memberRepository;

    private final VoucherKindRepository voucherKindRepository;

    private final VoucherRepository voucherRepository;

    private final VoucherImageRepository voucherImageRepository;

    private final RejectedSaleRepository rejectedSaleRepository;

    private final ImageEntityUtils imageEntityUtils;

    public VoucherResponse save(String username, VoucherRequest requestDto) {
        Member seller = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        VoucherKind voucherKind = voucherKindRepository.findById(requestDto.getVoucherKindId())
                .orElseThrow(() -> new EntityNotFoundException(VoucherKind.class));

        VoucherImage voucherImage = voucherImageRepository.save(
                imageEntityUtils.createImageEntity(VoucherImage.class, requestDto.getImageFile()));

        Voucher voucher = voucherRepository.save(Voucher.builder()
                .price(requestDto.getPrice())
                .expDate(requestDto.getExpDate())
                .barcode(requestDto.getBarcode())
                .voucherImage(voucherImage)
                .voucherKind(voucherKind)
                .seller(seller)
                .build());

        return new VoucherResponse(voucher);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('USER')")
    public List<VoucherResponse> findAllByUsername(String username) {
        Member seller = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        return voucherRepository.findAllBySeller(seller).stream()
                .map(VoucherResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VoucherResponse> findAllByStatus(StatusCodeParameter paramDto) {
        VoucherStatus status = findStatus(paramDto.getStatusCode());

        return voucherRepository.findAllByStatus(status).stream()
                .map(VoucherResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public ImageResponse findImageUrl(Long voucherId) {
        Voucher voucher = voucherRepository.findOneWithImage(voucherId)
                .orElseThrow(() -> new EntityNotFoundException(Voucher.class));

        return new ImageResponse(voucher);
    }

    public VoucherResponse updateStatus(Long id, StatusUpdateRequest requestDto) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Voucher.class));

        VoucherStatus newStatus = findStatus(requestDto.getStatusCode());
        voucher.updateStatus(newStatus);

        // 판매 요청 거절 시
        if (newStatus == SALE_REJECTED) {
            recordRejectedSale(voucher.getId(), requestDto.getRejectedReason());
        }

        return new VoucherResponse(voucher);
    }

    private void recordRejectedSale(Long voucherId, String rejectedReason) {
        RejectedSale rejectedSale = RejectedSale.builder()
                .voucherId(voucherId)
                .rejectedReason(rejectedReason)
                .build();

        rejectedSaleRepository.save(rejectedSale);
    }

    private VoucherStatus findStatus(Integer statusCode) {
        VoucherStatus[] statuses = VoucherStatus.values();

        if (statusCode < 0 || statusCode >= statuses.length) {
            throw new EntityNotFoundException(Voucher.class);
        }

        return statuses[statusCode];
    }

    public void delete(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Voucher.class));

        voucherRepository.delete(voucher);
    }
}
