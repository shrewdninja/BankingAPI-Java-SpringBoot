package com.example.banking.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Custom deserializer for BigDecimal to handle leading zeros
        SimpleModule module = new SimpleModule();
        module.addDeserializer(BigDecimal.class, new StdDeserializer<BigDecimal>(BigDecimal.class) {
            @Override
            public BigDecimal deserialize(com.fasterxml.jackson.core.JsonParser p, com.fasterxml.jackson.databind.DeserializationContext ctxt)
                    throws java.io.IOException {
                String value = p.getText().trim();
                try {
                    return new BigDecimal(value);
                } catch (NumberFormatException e) {
                    throw new JsonMappingException(p, "Invalid numeric format: " + value, e);
                }
            }
        });
        mapper.registerModule(module);

        converter.setObjectMapper(mapper);
        converters.add(converter);
    }
}