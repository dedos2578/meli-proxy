package com.ml.meliproxy.service.context;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.ml.meliproxy.persistence.context.PersistenceContext;

@Configuration
@ComponentScan(basePackages = Constants.BASE_PACKAGE)
@Import(PersistenceContext.class)
public class AppContext  {
}
