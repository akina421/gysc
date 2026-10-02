package com.hla.gysc.service.impl;

import com.hla.gysc.dao.LastIDMapper;
import com.hla.gysc.service.LastIDService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LastIDServiceImpl implements LastIDService {
    private final LastIDMapper lastIDMapper;

    @Override
    public int selectLastID() {
        return lastIDMapper.selectLastID();
    }
}
