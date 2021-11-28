package me.shinyook.querydslturn.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(value = EnumType.STRING)
    private ProductCondition productCondition;

    private Long price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    public Product(String name, ProductCondition productCondition, Long price) {
        this.name = name;
        this.productCondition = productCondition;
        this.price = price;
    }

    public void setShop(Shop shop) {
        if (this.shop != null) {
            this.shop.getProducts().remove(this);
        }
        this.shop = shop;
        this.shop.getProducts().add(this);
    }
}
