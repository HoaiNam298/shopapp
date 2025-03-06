package com.project.shopapp.repositories;

import com.project.shopapp.model.Category;
import com.project.shopapp.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);
    Page<Product> findAll(Pageable pageable);

    @Query("Select o from Product o where " +
            "(:categoryId is null or :categoryId = 0 or o.category.id = :categoryId)" +
            "and (:keyword is null or :keyword = '' or o.name like %:keyword% or o.description like %:keyword%)")
    Page<Product> searchProducts (@Param("keyword") String keyword,
                                  @Param("categoryId") Long categoryId,
                                  Pageable pageable);

    @Query("SELECT o FROM Product o LEFT JOIN FETCH o.productImages WHERE o.id = :productId")
    Optional<Product> getDetailProduct(@Param("productId") Long productId);

    @Query("SELECT o FROM Product o WHERE o.id IN :productIds")
    List<Product> findProductsByIds(@Param("productIds") List<Long> productIds);

    List<Product> findByCategory(Category category);
}
