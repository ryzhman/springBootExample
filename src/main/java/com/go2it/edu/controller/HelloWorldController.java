package com.go2it.edu.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.openjson.JSONObject;

/**
 * @author Alex Ryzhkov
 */
@Controller
public class HelloWorldController {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getHelloWorldMessage() {
		//Use template instead of hard-coded HTML
		return "helloWorld";

//		return "<div style=\"text-align:center;\">" + "<h1>Hello world</h1>"
//				+ "<p> This is my first web-page </p>"
//				+ "<img src=https://cdn-images-1.medium.com/fit/t/1600/672/0*n-2bW82Z6m6U2bij.jpeg></img>"
//				+ "</div>";
	}

	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public String getHelloWorldMessageForUser(@RequestParam String name) {
		return "<div style=\"text-align:center;\">" + "<h1>Welcome, " + name + "</h1>"
				+ "<p> This is my first web-page </p>" + "</div>";
	}

	@RequestMapping(value = "/{resource}", method = RequestMethod.GET)
	public String getHelloWorldMessageFromResource(@PathVariable String resource) {
		return "<div style=\"text-align:center;\">" + "<h1>This request was done to the resource: " + resource
				+ "</h1>" + "</div>";
	}

	@RequestMapping(value = "/customizedWelcome", method = RequestMethod.POST)
	public String getHelloWorldWithFromJSON(@RequestBody String message) {
		JSONObject json = new JSONObject(message);
		String firstName = "Albert";
		String defaultPictureAddress = "https://cdn-images-1.medium.com/fit/t/1600/672/0*n-2bW82Z6m6U2bij.jpeg";

		String firstNameFromRequest = json.getString("firstName");
		if (StringUtils.isNotBlank(firstNameFromRequest)) {
			firstName = firstNameFromRequest;
		}
		String pictureURLFromRequest = json.getString("pictureURL");
		if (StringUtils.isNotBlank(pictureURLFromRequest)) {
			defaultPictureAddress = pictureURLFromRequest;

		}
		return "<div style=\"text-align:center;\">" + "<h1>Hello world from " + firstName + "</h1>"
				+ "<p> This is my first web-page </p>" + "<img src=" + defaultPictureAddress + "></img>"
				+ "</div>";

	}
}
