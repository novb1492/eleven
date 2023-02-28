package com.www.eleven.Product.Repo;

import com.www.eleven.Product.Dto.SelectDto;
import com.www.eleven.Product.Model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepo extends JpaRepository<ProductEntity,Long> {
    @Query("select new com.www.eleven.Product.Dto.SelectDto(p) " +
            "from  ProductEntity p " +
            "where p.marketEntity.id=:mid and p.kind=:kind")
    List<SelectDto> findByKindAndMid(@Param("kind") Integer kind, @Param("mid") long mid);
}
