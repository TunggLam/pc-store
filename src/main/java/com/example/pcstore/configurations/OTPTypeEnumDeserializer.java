package com.example.pcstore.configurations;

import com.example.pcstore.enums.OTPTypeEnum;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;


import java.io.IOException;

public class OTPTypeEnumDeserializer extends JsonDeserializer<OTPTypeEnum> {

    @Override
    public OTPTypeEnum deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String value = jsonParser.getText();
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return OTPTypeEnum.valueOf(value);
        } catch (Exception e) {
            return null;
        }
    }
}

