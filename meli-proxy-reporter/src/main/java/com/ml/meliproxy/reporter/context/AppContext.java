package com.ml.meliproxy.reporter.context;

import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.ml.meliproxy.persistence.context.PersistenceContext;
import com.ml.meliproxy.reporter.context.serializers.DateJsonDeserializer;
import com.ml.meliproxy.reporter.context.serializers.DateJsonSerializer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = Constants.BASE_PACKAGE)
@Import(PersistenceContext.class)
public class AppContext implements WebMvcConfigurer {

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new StringHttpMessageConverter());
		converters.add(this.buildJackson2HttpMessageConverter());
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	protected MappingJackson2HttpMessageConverter buildJackson2HttpMessageConverter() {
		// Jackson Http Converter
		MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(this.getModule());
		objectMapper.registerModule(new AfterburnerModule());
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.setPropertyNamingStrategy(this.propertyNamingStrategy());
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

		jacksonConverter.setObjectMapper(objectMapper);
		return jacksonConverter;
	}

	protected PropertyNamingStrategy propertyNamingStrategy() {
		return PropertyNamingStrategy.SNAKE_CASE;
	}

	protected SimpleModule getModule() {
		// Register custom serializers
		SimpleModule module = new SimpleModule("DefaultModule", new Version(0, 0, 1, null, null, null));

		// Java Date
		module.addDeserializer(Date.class, new DateJsonDeserializer());
		module.addSerializer(Date.class, new DateJsonSerializer());
		return module;
	}
}
