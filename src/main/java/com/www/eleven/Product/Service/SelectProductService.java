package com.www.eleven.Product.Service;

import com.www.eleven.Product.Dto.SelectDto;
import com.www.eleven.Product.Repo.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SelectProductService {
    private final ProductRepo productRepo;

    public List<SelectDto>selectByKindAndMid(long mid ,int kind){
        return productRepo.findByKindAndMid(kind, mid);
    }
}
