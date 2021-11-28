package me.shinyook.querydslturn.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.shinyook.querydslturn.domain.Product;
import me.shinyook.querydslturn.domain.QProduct;
import me.shinyook.querydslturn.domain.QShop;
import me.shinyook.querydslturn.dto.ProductDto;
import me.shinyook.querydslturn.repository.common.OrderByNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
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
/*
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
*/
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

    public List<String> OptimizedGroupBy() {
        return queryFactory
                .select(product.name)
                .from(product)
                .groupBy(product.name)
                .orderBy(OrderByNull.DEFAULT)
                .fetch();
    }

    public List<ProductDto> getProducts(String productName, int pageNo, int pageSize) {
        List<Long> ids = queryFactory
                .select(product.productId)
                .from(product)
                .where(product.name.like(productName + "%"))
                .orderBy(product.productId.desc())
                .limit(pageSize)
                .offset(pageNo * pageSize)
                .fetch();
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return queryFactory
                .select(Projections.fields(ProductDto.class,
                        product.productId,
                        product.name,
                        product.price
                ))
                .from(product)
                .where(product.productId.in(ids))
                .orderBy(product.productId.desc())
                .fetch();
    }

    /**
     * DirtyChecking 을 사용하지 않음. Hibernate Session CacheEvict 필수.
     * @param productName
     * @param lowerProductId
     */
    public void update(String productName, Long lowerProductId) {
        queryFactory
                .update(product)
                .where(product.productId.loe(lowerProductId))
                .set(product.name, productName)
                .execute();
    }
}
