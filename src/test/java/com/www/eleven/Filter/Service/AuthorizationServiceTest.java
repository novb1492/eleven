package com.www.eleven.Filter.Service;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class AuthorizationServiceTest {

    @Test
    public void test(){
        Map<String, String> map = new HashMap<>();
        assertEquals(map.get("test"),null);
        assertEquals(map.isEmpty(),true);
    }
}