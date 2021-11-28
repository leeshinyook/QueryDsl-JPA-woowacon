package me.shinyook.querydslturn.repository;

import me.shinyook.querydslturn.domain.Product;
import me.shinyook.querydslturn.domain.ProductCondition;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void init() {
        productQueryRepository = new ProductQueryRepository(entityManager);
        Product product = new Product("아이패드", ProductCondition.NEW, 600000L);
        entityManager.persist(product);
    }

    @Test
    void findByName() {
        List<Product> products = productQueryRepository.findByName("아이패드");
        assertThat(products.size()).isOne();
        List<String> productNames = products.stream().map(Product::getName).collect(Collectors.toList());
        for (String productName : productNames) {
            assertThat(productName).isEqualTo("아이패드");
        }
    }

    @Test
    void findProduct() {
        List<Product> products = productQueryRepository.findDynamicQueryAdvance("아이패드", 600000L);
        for (Product product : products) {
            assertThat(product.getName()).isEqualTo("아이패드");
        }
    }

}