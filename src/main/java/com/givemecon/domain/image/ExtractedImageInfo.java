package com.givemecon.domain.image;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ExtractedImageInfo {

    private String brandName;

    private LocalDate expDate;

    private String barcode;
}
