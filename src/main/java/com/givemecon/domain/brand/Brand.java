package com.givemecon.domain.brand;

import com.givemecon.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.*;

@Getter
@NoArgsConstructor
@Entity
public class Brand extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String icon;

    @Builder
    public Brand(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public void update(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }
}
