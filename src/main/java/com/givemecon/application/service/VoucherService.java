package com.givemecon.application.service;

import com.givemecon.application.exception.InvalidRequestFieldException;
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
import com.givemecon.common.event.voucher.VoucherStatusUpdateEvent;
import com.givemecon.common.event.voucher.VoucherStatusUpdateEventPublisher;
import com.givemecon.infrastructure.s3.image_entity.ImageEntityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.givemecon.application.dto.VoucherDto.*;
import static com.givemecon.application.exception.errorcode.MemberErrorCode.*;
import static com.givemecon.application.exception.errorcode.VoucherErrorCode.*;
import static com.givemecon.application.exception.errorcode.VoucherKindErrorCode.*;
import static com.givemecon.domain.entity.voucher.VoucherStatus.FOR_SALE;
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

    private final VoucherStatusUpdateEventPublisher eventPublisher;

    public VoucherResponse save(String username, VoucherRequest requestDto) {
        Member seller = memberRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidRequestFieldException(INVALID_USERNAME));

        VoucherKind voucherKind = voucherKindRepository.findById(requestDto.getVoucherKindId())
                .orElseThrow(() -> new InvalidRequestFieldException(INVALID_VOUCHER_KIND_ID));

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
    public List<VoucherResponse> findAllByUsername(String username) {
        Member seller = memberRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidRequestFieldException(INVALID_USERNAME));

        return voucherRepository.findAllBySeller(seller).stream()
                .map(VoucherResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VoucherResponse> findAllByStatus(QueryParameter paramDto) {
        VoucherStatus status = findStatus(paramDto.getStatusCode());

        return voucherRepository.findAllByStatus(status).stream()
                .map(VoucherResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VoucherResponse> findAllByStatusAndUsername(QueryParameter paramDto, String username) {
        VoucherStatus status = findStatus(paramDto.getStatusCode());

        return voucherRepository.findAllByStatusAndUsername(status, username).stream()
                .map(VoucherResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VoucherResponse> findAllForSaleByVoucherKindId(Long voucherKindId, String username) {
        return voucherRepository.findAllExceptSellersByVoucherKindIdAndStatus(voucherKindId, FOR_SALE, username).stream()
                .map(VoucherResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public ImageResponse findImageUrl(Long voucherId) {
        Voucher voucher = voucherRepository.findOneWithImage(voucherId)
                .orElseThrow(() -> new InvalidRequestFieldException(INVALID_VOUCHER_ID));

        return new ImageResponse(voucher);
    }

    public VoucherResponse updateStatus(Long id, StatusUpdateRequest requestDto) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new InvalidRequestFieldException(INVALID_VOUCHER_ID));

        VoucherStatus newStatus = findStatus(requestDto.getStatusCode());
        voucher.updateStatus(newStatus);
        eventPublisher.publishEvent(new VoucherStatusUpdateEvent(voucher)); // 기프티콘 상태 변경 이벤트 발행

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
            throw new InvalidRequestFieldException(INVALID_STATUS_CODE);
        }

        return statuses[statusCode];
    }

    public void delete(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new InvalidRequestFieldException(INVALID_VOUCHER_ID));

        voucherRepository.delete(voucher);
    }
}
