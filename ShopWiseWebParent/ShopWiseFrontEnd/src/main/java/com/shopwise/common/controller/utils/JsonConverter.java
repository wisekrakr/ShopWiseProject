package com.shopwise.common.controller.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopwise.common.controller.ProductController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonConverter.class);

    public static String convert(Object value){
        ObjectMapper mapper = new ObjectMapper();

        String data = null;
        try {
            data = mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            LOGGER.info("JsonConverter: " + e.getMessage());
        }
        return data;
    }
}
