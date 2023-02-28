package com.www.eleven.Common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;
import java.util.Map;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonColumn {

    @Column(name = "STATE", columnDefinition = "TINYINT")
    private Integer state;

    @Column(name = "CREATED")
    @CreatedDate
    private LocalDateTime created;

    public static CommonColumn set(int state){
        return CommonColumn.builder().state(state).build();
    }

    public static CommonColumn set(Map<String,Object> map){
        return CommonColumn.builder().state(Integer.parseInt(map.get("state").toString()))
                .created((LocalDateTime) map.get("created"))
                .build();
    }

    @Override
    public String toString() {
        return "CommonColumn{" +
                "state=" + state +
                ", created=" + created +
                '}';
    }
}
