package com.cienet.shipment.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfiguration {
    public static final String TimeFormatValue = "HH:mm:ss";
    @Value("${spring.jackson.date-format:yyyy-MM-dd}")
    private String dateFormatValue;
    @Value("${spring.jackson.joda-date-time-format:yyyy-MM-dd HH:mm:ss}")
    private String dateTimeFormatValue;

    @Bean(name = "dateformat")
    public DateTimeFormatter format() {
        return DateTimeFormatter.ofPattern(dateFormatValue);
    }

    @Bean(name = "datetimeformat")
    public DateTimeFormatter dateTimeFormat() {
        return DateTimeFormatter.ofPattern(dateTimeFormatValue);
    }

    @Bean
    public ObjectMapper objectMapper(JavaTimeModule javaTimeModule, @Qualifier("dateformat") DateTimeFormatter format, @Qualifier("datetimeformat") DateTimeFormatter dateTimeFormat) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormat));
        javaTimeModule.addSerializer(LocalTime.class,
            new LocalTimeSerializer(DateTimeFormatter.ofPattern(TimeFormatValue)));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormat));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(format));
        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }

    /**
     * Support for Java date and time API.
     *
     * @return the corresponding Jackson module.
     */
    @Bean
    public JavaTimeModule javaTimeModule() {
        return new JavaTimeModule();
    }

    @Bean
    public Jdk8Module jdk8TimeModule() {
        return new Jdk8Module();
    }

    /*
     * Support for Hibernate types in Jackson.
     */
    @Bean
    public Hibernate5Module hibernate5Module() {
        return new Hibernate5Module();
    }

    /*
     * Jackson Afterburner module to speed up serialization/deserialization.
     */
    @Bean
    public AfterburnerModule afterburnerModule() {
        return new AfterburnerModule();
    }
}
