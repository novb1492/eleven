package com.www.eleven.Product.Repo;

import com.www.eleven.Product.Dto.SelectDto;
import com.www.eleven.Product.Model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepo extends JpaRepository<ProductEntity,Long> {
    @Query("select new com.www.eleven.Product.Dto.SelectDto(p.name,p.id,p.imgUrl,p.commonColumn.state,p.price) " +
            "from  ProductEntity p " +
            "where p.marketEntity.id=:mid and p.kind=:kind and p.commonColumn.state<>:state")
    List<SelectDto> findByKindAndMid(@Param("kind") Integer kind, @Param("mid") long mid,@Param("state")int state);

    @Query("select p from ProductEntity p where p.id=:id and p.commonColumn.state=:st ")
    Optional<ProductEntity> findByIdAndState(@Param("id") long id, @Param("st") int state);
}
