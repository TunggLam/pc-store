package com.example.pcstore.enums;

import com.example.pcstore.configurations.OTPTypeEnumDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = OTPTypeEnumDeserializer.class)
public enum OTPTypeEnum {

    REGISTER, FORGOT_PASSWORD

}
