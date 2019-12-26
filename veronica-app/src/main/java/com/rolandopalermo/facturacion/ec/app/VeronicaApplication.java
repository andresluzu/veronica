package com.rolandopalermo.facturacion.ec.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({"com.rolandopalermo.facturacion.ec"})
@EnableJpaRepositories(basePackages = {"com.rolandopalermo.facturacion.ec.persistence.repository"})
@EntityScan(basePackages = {"com.rolandopalermo.facturacion.ec.persistence.entity"})
@PropertySource("classpath:data.properties")
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class VeronicaApplication {

	public static void main(String[] args) {
		SpringApplication.run(VeronicaApplication.class, args);
	}

}