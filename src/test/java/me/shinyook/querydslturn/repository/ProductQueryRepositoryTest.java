package me.shinyook.querydslturn.repository;

import me.shinyook.querydslturn.domain.Product;
import me.shinyook.querydslturn.domain.ProductCondition;
import me.shinyook.querydslturn.domain.Shop;
import me.shinyook.querydslturn.dto.ProductDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductQueryRepositoryTest {

    @Autowired
    EntityManager entityManager;

    ProductQueryRepository productQueryRepository;

    Product product;
    Shop shop;

    @BeforeEach
    void init() {
        productQueryRepository = new ProductQueryRepository(entityManager);
        shop = new Shop("애플");
        product = new Product("아이패드", ProductCondition.NEW, 600000L);
        product.setShop(shop);
        entityManager.persist(shop);
    }

    @Test
    @DisplayName("QueryDsl-JPA-Arg조회")
    void findByName() {
        List<Product> products = productQueryRepository.findByName("아이패드");
        assertThat(products.size()).isOne();
        List<String> productNames = products.stream().map(Product::getName).collect(Collectors.toList());
        for (String productName : productNames) {
            assertThat(productName).isEqualTo("아이패드");
        }
    }

    @Test
    @DisplayName("QueryDsl-JPA-동적쿼리조회")
    void findProduct() {
        List<Product> products = productQueryRepository.findDynamicQueryAdvance("아이패드", 600000L);
        for (Product product : products) {
            assertThat(product.getName()).isEqualTo("아이패드");
        }
    }

    @Test
    @DisplayName("QueryDsl-JPA-커스텀 Exist조회 (Limit1)")
    void existProduct() {
        Boolean exist = productQueryRepository.exist(product.getProductId());
        assertThat(exist).isTrue();
    }

    @Test
    @DisplayName("QueryDsl-JPA-명시적 조인")
    void notCrossJoin() {
        List<Product> products = productQueryRepository.notCrossJoin();
        for (Product product : products) {
            assertThat(product.getName()).isEqualTo("아이패드");
            assertThat(product.getShop().getName()).isEqualTo("애플");
        }
    }

    @Test
    @DisplayName("QueryDsl-JPA-Selection Projection 조회")
    void projectionSelect() {
        List<ProductDto> products = productQueryRepository.getProduct("아이패드");
        for (ProductDto product : products) {
            assertThat(product.getName()).isEqualTo("아이패드");
        }
    }

    @Test
    @DisplayName("QueryDsl-JPA-groupBy Optimizing 조회")
    void groupBy() {
        List<String> productNames = productQueryRepository.OptimizedGroupBy();
        for (String name : productNames) {
            assertThat(name).isEqualTo("아이패드");
        }
    }

    @Test
    @DisplayName("QueryDsl-JPA-groupBy CoveringIndex seperate 조회")
    void getProducts() {
        List<ProductDto> products = productQueryRepository.getProducts("아이", 0, 10);
        for (ProductDto productDto : products) {
            assertThat(productDto.getName()).contains("아이");
        }
        products = productQueryRepository.getProducts("이건없어", 0, 10);
        assertThat(products).isEmpty();

    }
}