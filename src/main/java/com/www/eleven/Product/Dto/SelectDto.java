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

    public SelectDto(String name, Long id, String img, Integer soldOut, String price) {
        this.name = name;
        this.id = id;
        this.img = img;
        this.soldOut = soldOut;
        this.price = price;
    }


}
