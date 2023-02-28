package com.www.eleven.Market.Repo;

import com.www.eleven.Market.Model.MarketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketRepo extends JpaRepository<MarketEntity,Long> {
}
