package com.www.eleven.Time.Service;

import com.www.eleven.Time.Repo.TimeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimeSelectService {
    private final TimeRepo timeRepo;


}
