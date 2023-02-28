package com.www.eleven.Product.Dto;

import com.www.eleven.Product.Model.ProductEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
public class SelectDto{

    String name;
    Long id;
    String img;
    Integer soldOut;
    String price;

    public SelectDto(ProductEntity productEntity) {
        this.name = productEntity.getName();
        this.id = productEntity.getId();
        this.img = productEntity.getImgUrl();
        this.soldOut = productEntity.getCommonColumn().getState();
        this.price = productEntity.getPrice();
    }
}
