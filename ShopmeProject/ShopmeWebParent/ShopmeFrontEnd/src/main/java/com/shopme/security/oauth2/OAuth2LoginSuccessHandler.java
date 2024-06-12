package com.shopme.security.oauth2;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerService;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired
	private CustomerService customerService;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		
		CustomerOAuth2User oAuth2User = (CustomerOAuth2User) authentication.getPrincipal();
		String clientName = oAuth2User.getClientName();
		String email = oAuth2User.getEmail();
		String name = oAuth2User.getName();
		String countryCode = request.getLocale().getCountry();
		
		AuthenticationType authenticationType = getAuthenticationType(clientName);
		
		Customer customer = customerService.getCustomerByEmail(email);
		if(customer == null) {
			customerService.addNewCustomerUponOAuthLogin(name, email, countryCode, authenticationType);
		}else {
			oAuth2User.setFullName(customer.getFullName());
			customerService.updateAuthentication(customer, authenticationType);
		}
		
		super.onAuthenticationSuccess(request, response, authentication);
	}
	
	private AuthenticationType getAuthenticationType(String clientName) {
		if(clientName.equals("Google")) {
			return AuthenticationType.GOOGLE;
		}else if(clientName.equals("Facebook")){
			return AuthenticationType.FACEBOOK;
		}else {
			return AuthenticationType.DATABASE;
		}
	}
	
	
	
}
