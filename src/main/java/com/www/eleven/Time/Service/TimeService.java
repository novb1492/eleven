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
import com.www.eleven.Payment.Service.PaymentService;
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
    private final RedisTemplate<String, Object> redisTemplate;
    private final PaymentRepo paymentRepo;

    /**
     * 결제 요청 정보 및 예약정보 저장 함수
     * @param data
     * @return
     */
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
            ProductEntity p = productRepo.findByIdAndState(Long.parseLong(cp.get("id").toString()), Text.trueState).orElseThrow(() -> new IllegalArgumentException(Text.notFoundInDb+"&&&" + cp.get("name")));
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
        SeatEntity seatEntity = seatRepo.findByIdAndState(Text.trueState, seatId).orElseThrow(()->new IllegalArgumentException(Text.notFoundInDb));
        long marketId = seatEntity.getMarketEntity().getId();
        int cp = UtilService.getPriceToComma(priceRepo.findByMidAndHour(marketId, hourArr.length).get(0).getPrice());
        if(totalPrice<cp){
            throw new IllegalArgumentException("7");
        }
        totalPrice += UtilService.getPriceToComma(seatEntity.getPrice());
        /*
            결제 정보 및 예약정보 대기로 저장
         */
        PaymentEntity paymentEntity = savePayment(totalPrice);
        long pid = paymentEntity.getId();
        save(hourArr, marketId, seatId, pid, orderProductAndCountList);
        /*
            결제 요청후 검증 위해 redis 저장
         */
        String pk = Long.toString(pid);
        saveOrderInRedis(pid,seatId,marketId,totalPrice,hourArr,pk);
        /*
            결제창 생성위해 필요 값 전달
         */
        response.put("price",totalPrice);
        response.put("paymentid",pid);
        return response;
    }
    private void saveOrderInRedis(long pid,long seatId,long marketId,int totalPrice,Integer[] hourArr,String pk){
        LinkedHashMap<String, Object> payInfo = new LinkedHashMap<>();
        payInfo.put("pid", pid);
        payInfo.put("seatId", seatId);
        payInfo.put("marketId", marketId);
        payInfo.put("memberId", UtilService.getLoginInfo().getId());
        payInfo.put("created", LocalDateTime.now().toString());
        payInfo.put("totalPrice",totalPrice);
        payInfo.put("hourArr",hourArr);
        redisTemplate.opsForHash().put(pk, pk, payInfo);
    }
    /**
     * 예약 정보 저장함수
     * @param hourArr
     * @param marketId
     * @param seatId
     * @param pid
     * @param orderProductAndCountList
     * @return
     */
    public List<TimeEntity> save(Integer[]hourArr,long marketId,long seatId,long pid,List<Map<String,Object>> orderProductAndCountList){
        List<TimeEntity> timeEntities = new ArrayList<>();
        for(int hour:hourArr){
            checkAlready(seatId, hour);
            TimeEntity timeEntity = TimeInsertDto.dtoToEntity(hour, marketId, seatId, pid, orderProductAndCountList);
            timeRepo.save(timeEntity);
            timeEntities.add(timeEntity);
        }
        return timeEntities;
    }
    public void checkAlready(long seatId,int hour){
        TimeEntity already= timeRepo.findBySeatAndHourLock(seatId, UtilService.hourMakeToday(hour),Text.trueState).orElseGet(()-> null);
        if(already!=null){
            throw new IllegalArgumentException(Text.alreadyReservation);
        }
    }
    public void checkHour(int fh){
        int nh = LocalDateTime.now().getHour();
        if(fh<=nh){
            throw new IllegalArgumentException("4");
        }
    }
    public void okPayment( List<Integer>hourArr,long seatId,long pid){
        for(int hour:hourArr){
            checkAlready(seatId, hour);
            UtilService.checkUpdateDone(timeRepo.updateStateForReservation(Text.trueState, UtilService.hourMakeToday(hour), pid), 1);
        }
    }
    private PaymentEntity savePayment(int totalPrice){
        PaymentEntity paymentEntity = PaymentEntity.builder()
                .commonColumn(CommonColumn.builder().state(Text.waitState).build())
                .price(totalPrice)
                .buyer(MemberEntity.builder().id(UtilService.getLoginInfo().getId()).build()).build();
        paymentRepo.save(paymentEntity);
        return paymentEntity;
    }
}
