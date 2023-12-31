package com.example.paymentservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OrderEventStreamListener implements StreamListener<String, MapRecord<String, String, String>> {
    private int paymentProcessId = 0;

    @Autowired
    private StringRedisTemplate redisTemplate;
    //
    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        Map<String, String> map = message.getValue();

        String userId = map.get("userId");
        String productId = map.get("productId");
        String price = map.get("price");

        String paymentIdStr = Integer.toString(paymentProcessId++);

        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("userId", userId);
        fieldMap.put("productId", productId);
        fieldMap.put("price", price);
        fieldMap.put("paymentProcessId", paymentIdStr);

        redisTemplate.opsForStream().add("payment-events", fieldMap);

        System.out.println("[Order consumed] Created payment: " + paymentIdStr);
    }
}
