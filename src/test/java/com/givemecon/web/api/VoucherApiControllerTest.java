package com.givemecon.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.domain.brand.Brand;
import com.givemecon.domain.brand.BrandRepository;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherForSale;
import com.givemecon.domain.voucher.VoucherForSaleRepository;
import com.givemecon.domain.voucher.VoucherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

import static com.givemecon.web.dto.VoucherDto.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Transactional
@SpringBootTest
@WithMockUser(roles = "ADMIN")
class VoucherApiControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    VoucherForSaleRepository voucherSellingRepository;

    @Autowired
    BrandRepository brandRepository;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDoc) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .apply(documentationConfiguration(restDoc))
                .alwaysDo(print())
                .build();
    }

    @Test
    void save() throws Exception {
        // given
        Long price = 4_000L;
        String title = "Americano T";
        String image = "tall_americano.jpg";
        VoucherSaveRequest requestDto = VoucherSaveRequest.builder()
                .price(price)
                .title(title)
                .image(image)
                .build();

        // when
        ResultActions response = mockMvc.perform(post("/api/vouchers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(
                                modifyHeaders().remove("Host"),
                                prettyPrint()
                        ),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("저장할 기프티콘 타이틀"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("저장할 기프티콘 가격"),
                                fieldWithPath("image").type(JsonFieldType.STRING).description("저장할 기프티콘 이미지")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("저장된 기프티콘 id"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("저장된 기프티콘 타이틀"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("저장된 기프티콘 가격"),
                                fieldWithPath("image").type(JsonFieldType.STRING).description("저장된 기프티콘 이미지")
                        ))
                );

        // then
        List<Voucher> voucherList = voucherRepository.findAll();

        response
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(voucherList.get(0).getId()))
                .andExpect(jsonPath("price").value(voucherList.get(0).getPrice()))
                .andExpect(jsonPath("image").value(voucherList.get(0).getImage()));
    }

    @Test
    void findOne() throws Exception {
        // given
        Long price = 20_000L;
        String title = "Ice Cream Cake";
        String image = "ice_cream_cake.jpg";
        Voucher voucher = Voucher.builder()
                .price(price)
                .title(title)
                .image(image)
                .build();

        Long id = voucherRepository.save(voucher).getId();

        // when
        ResultActions response = mockMvc.perform(get("/api/vouchers/{id}", id))
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(modifyHeaders().remove("Host")),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("기프티콘 id"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("기프티콘 타이틀"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("기프티콘 가격"),
                                fieldWithPath("image").type(JsonFieldType.STRING).description("기프티콘 이미지")
                        ))
                );

        // then
        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("price").value(price))
                .andExpect(jsonPath("image").value(image));
    }

    @Test
    void findAll() throws Exception {
        // given
        for (int i = 1; i <= 5; i++) {
            Voucher voucher = Voucher.builder()
                    .price(10_000L)
                    .title("Voucher " + i)
                    .image("voucher_" + i + ".png")
                    .build();

            voucherRepository.save(voucher);
        }

        // when
        ResultActions response = mockMvc.perform(get("/api/vouchers"))
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(modifyHeaders().remove("Host")),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("기프티콘 id"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("기프티콘 타이틀"),
                                fieldWithPath("[].price").type(JsonFieldType.NUMBER).description("기프티콘 가격"),
                                fieldWithPath("[].image").type(JsonFieldType.STRING).description("기프티콘 이미지")
                        ))
                );

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void findAllByBrandName() throws Exception {
        // given
        Brand brand = Brand.builder()
                .name("Test Brand")
                .icon("test_brand.png")
                .build();

        Brand brandSaved = brandRepository.save(brand);

        for (int i = 1; i <= 5; i++) {
            Voucher voucher = Voucher.builder()
                    .price(10_000L)
                    .title("Voucher " + i)
                    .image("voucher_" + i + ".png")
                    .build();

            brandSaved.addVoucher(voucher);
            voucherRepository.save(voucher);
        }

        // when
        ResultActions response = mockMvc.perform(get("/api/vouchers?brandName={brandName}", brandSaved.getName()))
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(modifyHeaders().remove("Host")),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("기프티콘 id"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("기프티콘 타이틀"),
                                fieldWithPath("[].price").type(JsonFieldType.NUMBER).description("기프티콘 가격"),
                                fieldWithPath("[].image").type(JsonFieldType.STRING).description("기프티콘 이미지")
                        ))
                );

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void findSellingListByVoucherId() throws Exception {
        // given
        Long price = 4_000L;
        String title = "Americano T";
        String image = "americano.jpg";
        Voucher voucher = Voucher.builder()
                .price(price)
                .title(title)
                .image(image)
                .build();

        Voucher voucherSaved = voucherRepository.save(voucher);

        for (int i = 1; i <= 5; i++) {
            VoucherForSale voucherSelling = VoucherForSale.builder()
                    .title("Americano T")
                    .image("americano.jpg")
                    .price(4_000L)
                    .expDate(LocalDate.now())
                    .barcode("1111 1111 1111")
                    .build();

            voucherSellingRepository.save(voucherSelling);
            voucherSaved.addVoucherForSale(voucherSelling);
        }

        // when
        ResultActions response = mockMvc.perform(get("/api/vouchers/{id}/selling-list", voucherSaved.getId()))
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(modifyHeaders().remove("Host")),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("판매중인 기프티콘 id"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("판매중인 기프티콘 타이틀"),
                                fieldWithPath("[].price").type(JsonFieldType.NUMBER).description("판매중인 기프티콘 가격"),
                                fieldWithPath("[].expDate").type(JsonFieldType.STRING).description("판매중인 기프티콘 가격"),
                                fieldWithPath("[].barcode").type(JsonFieldType.STRING).description("판매중인 기프티콘 가격"),
                                fieldWithPath("[].image").type(JsonFieldType.STRING).description("판매중인 기프티콘 이미지")
                        ))
                );

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void update() throws Exception {
        // given
        Long price = 3_000L;
        String title = "Shine Musket Tang Hoo Ru";
        String image = "shine_musket_tang_hoo_ru.jpg";
        Voucher voucher = Voucher.builder()
                .price(price)
                .title(title)
                .image(image)
                .build();

        Long id = voucherRepository.save(voucher).getId();
        VoucherUpdateRequest requestDto = VoucherUpdateRequest.builder()
                .price(price)
                .image("new_shine_musket_hoo_ru.jpg")
                .build();

        // when
        ResultActions response = mockMvc.perform(put("/api/vouchers/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(
                                modifyHeaders().remove("Host"),
                                prettyPrint()
                        ),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("price").type(JsonFieldType.NUMBER).optional().description("수정할 기프티콘 가격"),
                                fieldWithPath("image").type(JsonFieldType.STRING).optional().description("수정할 기프티콘 이미지")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("수정된 기프티콘 id"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("수정된 기프티콘 타이틀"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("수정된 기프티콘 가격"),
                                fieldWithPath("image").type(JsonFieldType.STRING).description("수정된 기프티콘 이미지")
                        ))
                );

        // then
        List<Voucher> voucherList = voucherRepository.findAll();

        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(voucherList.get(0).getId()))
                .andExpect(jsonPath("price").value(voucherList.get(0).getPrice()))
                .andExpect(jsonPath("image").value(voucherList.get(0).getImage()));
    }

    @Test
    void deleteOne() throws Exception {
        // given
        Long price = 4_000L;
        String title = "Milk Tea L";
        String image = "large_milk_tea.jpg";
        Voucher voucher = Voucher.builder()
                .price(price)
                .title(title)
                .image(image)
                .build();

        Long id = voucherRepository.save(voucher).getId();

        // when
        ResultActions response = mockMvc.perform(delete("/api/vouchers/{id}", id))
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(modifyHeaders().remove("Host"))));

        // then
        response.andExpect(status().isNoContent());
        List<Voucher> voucherList = voucherRepository.findAll();
        assertThat(voucherList).isEmpty();
    }
}