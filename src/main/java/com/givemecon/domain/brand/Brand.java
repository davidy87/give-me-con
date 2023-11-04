package com.givemecon.domain.brand;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;

import static jakarta.persistence.GenerationType.*;

@Getter
@Entity
public class Brand {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String name;
}
