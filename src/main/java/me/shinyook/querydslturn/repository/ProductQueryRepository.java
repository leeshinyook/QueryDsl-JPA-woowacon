package me.shinyook.querydslturn.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.shinyook.querydslturn.domain.Product;
import me.shinyook.querydslturn.domain.QProduct;
import me.shinyook.querydslturn.domain.QShop;
import me.shinyook.querydslturn.dto.ProductDto;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static me.shinyook.querydslturn.domain.QProduct.product;
import static me.shinyook.querydslturn.domain.QShop.shop;

@Repository
public class ProductQueryRepository {
    private final JPAQueryFactory queryFactory;

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
        if (price != null) {
            builder.and(product.price.eq(price));
        }

        return queryFactory
                .selectFrom(product)
                .where(builder)
                .fetch();
    }

    public List<Product> findDynamicQueryAdvance(String name, Long price) {
        return queryFactory
                .selectFrom(product)
                .where(eqName(name),
                        eqPrice(price))
                .fetch();
    }

    private BooleanExpression eqName(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        return product.name.eq(name);
    }

    private BooleanExpression eqPrice(Long price) {
        if (price == null) {
            return null;
        }
        return product.price.eq(price);
    }

    @Transactional(readOnly = true)
    public Boolean exist(Long productId) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(product)
                .where(product.productId.eq(productId))
                .fetchFirst();

        return fetchOne != null;
    }

    public List<Product> notCrossJoin() {
        return queryFactory
                .selectFrom(product)
                .innerJoin(product.shop, shop)
                .fetch();
    }

    public List<ProductDto> getProduct(String productName) {
        return queryFactory
                .select(Projections.fields(ProductDto.class,
                        product.productId,
                        Expressions.asString(productName).as("name"),
                        product.price
                ))
                .from(product)
                .where(product.name.eq(productName))
                .fetch();
    }
}
