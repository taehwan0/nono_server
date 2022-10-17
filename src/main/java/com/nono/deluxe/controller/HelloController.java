package com.nono.deluxe.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
public class HelloController {

	@GetMapping("/")
	public String helloPage() {
		return "hello";
	}
}
