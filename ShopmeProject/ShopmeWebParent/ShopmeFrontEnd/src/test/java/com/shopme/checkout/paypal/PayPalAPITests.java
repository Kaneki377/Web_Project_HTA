package com.shopme.checkout.paypal;


import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class PayPalAPITests {
	
	private static final String BASE_URL = "https://api.sandbox.paypal.com";
	private static final String GET_ORDER_API = "/v2/checkout/orders/";
	
	private static final String CLIENT_ID = "AcAQyDwop04uQTDH4cfPvzigCngC94KcrzwmS1S1xcI8xttQ8qtNy-wCoHfy08iTDxJpQJluXzq7x5oW";
	private static final String CLIENT_SECRET = "EMp826dBze5G6y3FypWw2bicOn66v7khXuJfYMiQ7Rtkfw0tNqWwAlidleXZZSvKHMa7niEsnu8IhNyb";
	
	@Test
	public void testGetOrderDetails() {
		String orderId = "1K546085L0032004G";
		String requestURL = BASE_URL + GET_ORDER_API + orderId;
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Accept-Language", "en_US");
		headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
		
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
		RestTemplate restTemplate = new RestTemplate();
		
		ResponseEntity<PayPalOrderResponse> response = restTemplate.exchange(
				requestURL, HttpMethod.GET, request, PayPalOrderResponse.class);
		PayPalOrderResponse orderResponse = response.getBody();
		
		System.out.println("Order ID " + orderResponse.getId() + " Status: " + orderResponse.getStatus());
		System.out.println("Validated: " + orderResponse.validate(orderId));
	}
}
