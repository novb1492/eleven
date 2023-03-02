package com.www.eleven.Time.Service;

import com.www.eleven.Common.CommonColumn;
import com.www.eleven.Common.Text;
import com.www.eleven.Common.UtilService;
import com.www.eleven.Market.Model.MarketEntity;
import com.www.eleven.Market.Seat.Model.SeatEntity;
import com.www.eleven.Market.Seat.Repo.SeatRepo;
import com.www.eleven.Member.Model.MemberEntity;
import com.www.eleven.Payment.Model.PaymentEntity;
import com.www.eleven.Payment.Repo.PaymentRepo;
import com.www.eleven.Price.Model.PriceEntity;
import com.www.eleven.Price.Repo.PriceRepo;
import com.www.eleven.Product.Model.ProductEntity;
import com.www.eleven.Product.Repo.ProductRepo;
import com.www.eleven.Time.Dto.TimeInsertDto;
import com.www.eleven.Time.Model.TimeEntity;
import com.www.eleven.Time.Repo.TimeRepo;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TimeService {
    private final TimeRepo timeRepo;
    private final SeatRepo seatRepo;
    private final ProductRepo productRepo;
    private final PriceRepo priceRepo;
    private final PaymentRepo paymentRepo;
    private final RedisTemplate<String, Object> redisTemplate;
    @Transactional(rollbackFor = Exception.class)
    public JSONObject save(TimeInsertDto data){
        Arrays.sort(data.getChoiceTimes());
        JSONObject response = new JSONObject();
        Integer[] hourArr = data.getChoiceTimes();
        int fh = hourArr[0];
        int lh = hourArr[hourArr.length - 1];
        checkHour(fh);
        List<Map<String, Object>> cps = data.getChoiceProducts();
        int totalPrice = 0;
        /*
            구매상품 총액계산,요청 제품 분류
        * */
        List<Map<String,Object>> orderProductAndCountList = new ArrayList<>();
        for(Map<String,Object>cp:cps){
            ProductEntity p = productRepo.findByIdAndState(Long.parseLong(cp.get("id").toString()), Text.trueState).orElseThrow(() -> new IllegalArgumentException("6&&&" + cp.get("name")));
            int count = Integer.parseInt(cp.get("count").toString());
            totalPrice += UtilService.getPriceToComma(p.getPrice())*count;
            LinkedHashMap<String, Object> stringObjectLinkedHashMap = new LinkedHashMap<>();
            stringObjectLinkedHashMap.put("id", p.getId());
            stringObjectLinkedHashMap.put("pri", p.getPrice());
            stringObjectLinkedHashMap.put("cou", count);
            orderProductAndCountList.add(stringObjectLinkedHashMap);
        }
        /*
            해당 매장 최소주문 금액 확인
         */
        long seatId = data.getSeatId();
        SeatEntity seatEntity = seatRepo.findByIdAndState(Text.trueState, seatId).orElseThrow(()->new IllegalArgumentException("5"));
        long marketId = seatEntity.getMarketEntity().getId();
        int cp = UtilService.getPriceToComma(priceRepo.findByMidAndHour(marketId, hourArr.length).get(0).getPrice());
        if(totalPrice<cp){
            throw new IllegalArgumentException("7");
        }
        totalPrice += UtilService.getPriceToComma(seatEntity.getPrice());
        /*
            결제 정보 및 예약정보 대기로 저장
         */
        PaymentEntity paymentEntity = PaymentEntity.builder()
                .commonColumn(CommonColumn.builder().state(Text.waitState).build())
                .price(totalPrice)
                .buyer(MemberEntity.builder().id(UtilService.getLoginInfo().getId()).build()).build();
        paymentRepo.save(paymentEntity);
        long pid = paymentEntity.getId();
        for(int hour:hourArr){
            timeRepo.save(TimeInsertDto.dtoToEntity(hour,marketId,seatId,pid,orderProductAndCountList));
        }
        /*
            결제 요청후 검증 위해 redis 저장
         */
        String pk = Long.toString(pid) +  Long.toString(seatId) +  Long.toString(marketId);
        LinkedHashMap<String, Object> payInfo = new LinkedHashMap<>();
        payInfo.put("pid", pid);
        payInfo.put("seatId", seatId);
        payInfo.put("marketId", marketId);
        payInfo.put("memberId", UtilService.getLoginInfo().getId());
        payInfo.put("created", LocalDateTime.now().toString());
        payInfo.put("totalPrice",totalPrice);
        redisTemplate.opsForHash().put(pk, pk, payInfo);
        response.put("price",totalPrice);
        response.put("paymentid",pid+"_time");
        return response;
    }
    public void checkHour(int fh){
        int nh = LocalDateTime.now().getHour();
        if(fh<=nh){
            throw new IllegalArgumentException("4");
        }
    }
}
