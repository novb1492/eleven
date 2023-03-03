package com.www.eleven.Time.Repo;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TimeRepoImpl implements TimeRepoCustom {
    private final JPAQueryFactory jpaQueryFactory;


}
