package com.www.eleven.Time.Dto;

import com.www.eleven.Common.CommonColumn;
import com.www.eleven.Common.Text;
import com.www.eleven.Common.UtilService;
import com.www.eleven.Market.Model.MarketEntity;
import com.www.eleven.Market.Seat.Model.SeatEntity;
import com.www.eleven.Member.Model.MemberEntity;
import com.www.eleven.Payment.Model.PaymentEntity;
import com.www.eleven.Time.Model.TimeEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.text.Utilities;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Data
public class TimeInsertDto {

    @Size(min = 1,message = "1")
    private Integer[] choiceTimes;
    @Min(value = 1,message = "2")
    private Integer seatId;
    @Size(min = 1,message = "3")
    private List<Map<String,Object>> choiceProducts;

    public static TimeEntity dtoToEntity(int hour,long marketId,long seatId,long paymentId, List<Map<String,Object>>proAndCounts){
        return TimeEntity.builder()
                .commonColumn(CommonColumn.set(Text.waitState))
                .time(Timestamp.valueOf(LocalDate.now()+" "+hour+":00:00"))
                .marketEntity(MarketEntity.builder().id(marketId).build())
                .insertUser(MemberEntity.builder().id(UtilService.getLoginInfo().getId()).build())
                .seatEntity(SeatEntity.builder().id(seatId).build())
                .products(proAndCounts.toString())
                .paymentEntity(PaymentEntity.builder().id(paymentId).build())
                .build();
    }
}
