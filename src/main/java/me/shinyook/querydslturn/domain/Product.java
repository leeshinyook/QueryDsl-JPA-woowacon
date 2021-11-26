package me.shinyook.querydslturn.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(value = EnumType.STRING)
    private ProductCondition productCondition;

    private Long price;

    public Product(String name, ProductCondition productCondition, Long price) {
        this.name = name;
        this.productCondition = productCondition;
        this.price = price;
    }

    protected Product() {

    }
}
