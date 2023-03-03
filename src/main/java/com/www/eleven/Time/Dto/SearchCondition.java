package com.www.eleven.Time.Dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SearchCondition {
    private String start;
    private String end;
    private int page;
    private int state;
}
