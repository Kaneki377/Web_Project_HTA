package com.shopme.admin.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;




@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**" , "/css/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests()
		.antMatchers("/users/**", "/settings/**", "/countries/**", "/states/**").hasAuthority("Admin")
		.antMatchers("/categories/**", "/brands/**").hasAnyAuthority("Admin", "Editor")
		
		.antMatchers("/products/new", "/products/delete/**").hasAnyAuthority("Admin", "Editor")
		
		.antMatchers("/products/edit/**", "/products/save", "/products/check_unique")
			.hasAnyAuthority("Admin", "Editor", "Salesperson")
			
		.antMatchers("/products", "/products/", "/products/detail/**", "/products/page/**")
			.hasAnyAuthority("Admin", "Editor", "Salesperson", "Shipper")
			
		.antMatchers("/products/**").hasAnyAuthority("Admin", "Editor")
		
		.antMatchers("/orders", "/orders/", "/orders/page/**", "/orders/detail/**", "/orders/edit/**").hasAnyAuthority("Admin", "Salesperson", "Shipper")
		
		.antMatchers("/customers/**", "/orders/**", "/get_shipping_cost").hasAnyAuthority("Admin", "Salesperson")
		
		.anyRequest().authenticated()
		.and()
		.formLogin()
			.loginPage("/login")
			.usernameParameter("email")
			.permitAll()
		.and()
		.logout().permitAll()
		.and()
		.rememberMe()
			.key("AbcDefgKLDSLmvop_0123456789")
			.tokenValiditySeconds(3 * 24 * 60 * 60)  // 7 days 24 hours 60 minutes 60 seconds -> 7days
			;
		http.headers().frameOptions().sameOrigin();

	}

	@Override
	@Bean
	public UserDetailsService userDetailsService() {
		return new ShopmeUserDetailsService();
	}

    @Bean
    PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

    public DaoAuthenticationProvider authenticationProvider() {
    	DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    	authProvider.setUserDetailsService(userDetailsService());
    	authProvider.setPasswordEncoder(passwordEncoder());

    	return authProvider;
    }

}
