package com.lender.config;

import javax.persistence.Persistence;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MainConfig {	
	@Bean
	public Mutiny.SessionFactory sessionFactory() {
		return Persistence.createEntityManagerFactory("mr-persist")
				.unwrap(Mutiny.SessionFactory.class);
	}
}
