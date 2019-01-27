package com.anla.netty.xml;

import com.anla.netty.xml.pojo.Order;
import com.anla.netty.xml.pojo.OrderFactory;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @user anLA7856
 * @time 19-1-26 下午5:39
 * @description
 */
public class TestJsonOrder {
    public final static ObjectMapper mapper = new ObjectMapper();
    private final static String CHARSET_NAME = "UTF-8";
    static {
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    private String encode2Json(Order order) throws JsonProcessingException {
        String jsonString = mapper.writeValueAsString(order);
        System.out.println(jsonString);
        return jsonString;
    }

    private <T> T decode2Order(String jsonBody, Class<T> clazz) throws IOException {
        return mapper.readValue(jsonBody, clazz);
    }

    public static void main(String[] args) throws IOException {
        TestJsonOrder test = new TestJsonOrder();
        Order order = OrderFactory.create(123);
        String body = test.encode2Json(order);
        Order order1 = test.decode2Order(body, Order.class);
        System.out.println(order1);
    }
}
