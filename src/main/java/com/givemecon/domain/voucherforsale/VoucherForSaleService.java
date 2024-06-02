package com.givemecon.domain.voucherforsale;

import com.givemecon.domain.image.voucherforsale.VoucherForSaleImage;
import com.givemecon.domain.image.voucherforsale.VoucherForSaleImageRepository;
import com.givemecon.util.image_entity.ImageEntityUtils;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherRepository;
import com.givemecon.util.exception.concrete.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.givemecon.domain.voucherforsale.VoucherForSaleDto.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class VoucherForSaleService {

    private final MemberRepository memberRepository;

    private final VoucherRepository voucherRepository;

    private final VoucherForSaleRepository voucherForSaleRepository;

    private final VoucherForSaleImageRepository voucherForSaleImageRepository;

    private final ImageEntityUtils imageEntityUtils;

    public VoucherForSaleResponse save(String username, VoucherForSaleRequest requestDto) {
        Member seller = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        Voucher voucher = voucherRepository.findById(requestDto.getVoucherId())
                .orElseThrow(() -> new EntityNotFoundException(Voucher.class));

        VoucherForSaleImage voucherForSaleImage = voucherForSaleImageRepository.save(
                imageEntityUtils.createImageEntity(VoucherForSaleImage.class, requestDto.getImageFile()));

        VoucherForSale voucherForSale = voucherForSaleRepository.save(requestDto.toEntity());
        voucherForSale.updateSeller(seller);
        voucherForSale.updateVoucherForSaleImage(voucherForSaleImage);
        voucher.addVoucherForSale(voucherForSale);

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

    public VoucherForSaleResponse updateStatus(Long id, StatusCodeBody bodyDto) {
        VoucherForSale voucherForSale = voucherForSaleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(VoucherForSale.class));

        VoucherForSaleStatus newStatus = findStatus(bodyDto.getStatusCode());
        voucherForSale.updateStatus(newStatus);

        return new VoucherForSaleResponse(voucherForSale);
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

        voucherForSale.getVoucher().deleteVoucherForSale(voucherForSale);
        voucherForSaleRepository.delete(voucherForSale);
    }
}
