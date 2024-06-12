package com.shopme.customer;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.shopme.Ultility;
import com.shopme.common.entity.Customer;
import com.shopme.common.exception.CustomerNotFoundException;
import com.shopme.setting.EmailSettingBag;
import com.shopme.setting.SettingService;

@Controller
public class ForgotPasswordController {

	@Autowired
	private SettingService settingService;
	
	@Autowired
	private CustomerService customerService;
	
	@GetMapping("/forgot_password")
	public String showRequestForm() {
		
		return "customer/forgot_password_form";
	}
	
	@PostMapping("/forgot_password")
	public String processRequestForm(HttpServletRequest request, Model model) {
		String email = request.getParameter("email");
		try {
			String token = customerService.updateResetPasswordToken(email);
			String link = Ultility.getSiteURL(request) + "/reset_password?token=" + token;
			sendEmail(link, email);
			
			model.addAttribute("message", "We have sent a reset password to your email."
					+ "Please Check!");
		} catch (CustomerNotFoundException e) {
			model.addAttribute("error", e.getMessage());
		} catch (UnsupportedEncodingException | MessagingException e) {
			model.addAttribute("error", "Could not send email.");
		}
		
		
		return "customer/forgot_password_form";
	}
	
	private void sendEmail(String link, String email) throws UnsupportedEncodingException, MessagingException {
		EmailSettingBag emailSettings = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = Ultility.prepareMailSender(emailSettings);
		
		String toAddress = email;
		String subject = "Link to reset your password!";
		
		String content = "<p>Hello,</p>"
				+ "<p>You have requested to reset your password.</p>"
				+ "<p>Click the link below to change your password:</p>"
				+ "<p><a href=\"" + link + "\">Change my password</a></p>"
				+ "<br>"
				+ "<p>Ignore this email if you remember your password, or you have not made the request.</p>";
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);
		
		helper.setText(content, true);
		
		mailSender.send(message);
	}
	
	@GetMapping("/reset_password")
	public String showResetForm(@Param("token") String token, Model model) {
		Customer cutomer = customerService.getByResetPasswordToken(token);
		if(cutomer != null) {
			model.addAttribute("token", token);
		}else {
			model.addAttribute("pageTitle", "Invalid Token");
			model.addAttribute("message", "Invalid Token");
			return "message";
		}
		
		return "customer/reset_password_form";
	}
	@PostMapping("/reset_password")
	public String processResetForm(HttpServletRequest request, Model model) {
		String token = request.getParameter("token");
		String password = request.getParameter("password");
		
		try {
			customerService.updatePassword(token, password);
			model.addAttribute("message", "You have successfully changed your password.");
			model.addAttribute("pageTitle", "Reset Your Password");
			
			return "message";
		} catch (CustomerNotFoundException e) {
			model.addAttribute("pageTitle", "Invalid Token");
			model.addAttribute("message", e.getMessage());
			
			return "message";
		}
	}
}
