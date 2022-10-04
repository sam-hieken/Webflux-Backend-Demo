package com.lender.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/test")
public class TestController {
	@PostMapping("/event")
	public Mono<Void> eventOccurred(@RequestBody Map map) throws JsonProcessingException {
		System.out.println("Event occurred");
		System.out.println(new ObjectMapper().writeValueAsString(map));

		return Mono.empty();
	}
}
