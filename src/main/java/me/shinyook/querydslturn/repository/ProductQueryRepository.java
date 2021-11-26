package me.shinyook.querydslturn.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.shinyook.querydslturn.domain.Product;
import me.shinyook.querydslturn.domain.QProduct;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static me.shinyook.querydslturn.domain.QProduct.product;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepository {
    private JPAQueryFactory queryFactory;

    public ProductQueryRepository(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    public List<Product> findByName(String name) {
        return queryFactory.selectFrom(product)
                .where(product.name.eq(name))
                .fetch();
    }

    public List<Product> findDynamicQuery(String name, Long price) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(name)) {
            builder.and(product.name.eq(name));
        }

        return queryFactory
                .selectFrom(product)
                .where(builder)
                .fetch();

    }
}
