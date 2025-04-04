package com.springstudy.backend.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {
    public ObjectMapper getObjectMapper() {return new ObjectMapper();}
}
