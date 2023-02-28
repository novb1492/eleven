package com.www.eleven.Time.Service;

import com.www.eleven.Common.Text;
import com.www.eleven.Market.Model.MarketEntity;
import com.www.eleven.Market.Repo.MarketRepo;
import com.www.eleven.Price.Model.PriceEntity;
import com.www.eleven.Price.Repo.PriceRepo;
import com.www.eleven.Time.Model.TimeEntity;
import com.www.eleven.Time.Repo.TimeRepo;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SelectTimeService {
    private final TimeRepo timeRepo;
    private final MarketRepo marketRepo;
    private final PriceRepo priceRepo;
    public List<JSONObject> selectBySeatId(long seatId,long mid){
        MarketEntity marketEntity = marketRepo.findById(mid).orElseThrow(() -> new IllegalArgumentException("해당 매장을 찾을 수 없습니다"));
        List<TimeEntity> timeEntities=timeRepo.findBySeat(seatId, Timestamp.valueOf(LocalDate.now() + " " + marketEntity.getOt() + ":00"), Timestamp.valueOf(LocalDate.now() + " " + marketEntity.getCt() + ":00"));
        List<JSONObject> times = new ArrayList<>();
        int oh = Integer.parseInt(marketEntity.getOt().split(":")[0]);
        int ch = Integer.parseInt(marketEntity.getCt().split(":")[0]);
        for(int i=oh;i<= ch;i++){
            JSONObject time = new JSONObject();
            if(i<=LocalDateTime.now().getHour()){
                time.put("can",false);
                time.put("time",i);
                times.add(time);
                continue;

            }
            boolean flag = true;
            for(TimeEntity t:timeEntities){
                if(t.getCommonColumn().getState()==Text.trueState){
                    int tt = Integer.parseInt(t.getTime().toString().split(" ")[1].split(":")[0]);
                    if(tt==i){
                        flag = false;
                        break;
                    }
                }
            }
            time.put("can",flag);
            time.put("time",i);
            times.add(time);
        }
        return  times;
    }
    public int getPriceByHour(int hour,int mid){
        int totalPrice = 0;
        if(hour==0){
            return 0;
        }else if(hour<0) {
            throw new IllegalArgumentException("총 예약시간이 음수입니다");
        }
        List<PriceEntity> priceEntities = priceRepo.findByMidAndHour(mid, hour);
        int minus = 1000000;
        for(PriceEntity p:priceEntities){
            int m = hour - p.getHour();
            if(m<minus){
                totalPrice = Integer.parseInt(p.getPrice().replace(",", ""));
            }
        }
        return totalPrice;
    }
}
