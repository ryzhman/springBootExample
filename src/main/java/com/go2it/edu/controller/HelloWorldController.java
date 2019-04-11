package com.go2it.edu.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Alex Ryzhkov
 */
@RestController
public class HelloWorldController {

	@RequestMapping("/")
	public String index() {
		return "<div style=\"text-align:center;\">"
				+ "<h1>Hello world</h1>" +
				"<p> This is my first web-page </p>" +
				"<img src=https://cdn-images-1.medium.com/fit/t/1600/672/0*n-2bW82Z6m6U2bij.jpeg></img>"
				+ "</div>";
	}

}
