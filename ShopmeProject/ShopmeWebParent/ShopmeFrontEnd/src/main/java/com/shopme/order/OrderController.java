package com.shopme.order;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.order.Order;
import com.shopme.common.exception.CustomerNotFoundException;
import com.shopme.customer.CustomerService;
import com.shopme.util.AuthenticationControllerHelperUtil;


@Controller
public class OrderController {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);
	
	private OrderService orderService;
	
	private AuthenticationControllerHelperUtil authenticationControllerHelperUtil;
	

	@Autowired
	public OrderController(OrderService orderService, 
			               CustomerService customerService,
			        
			               AuthenticationControllerHelperUtil authenticationControllerHelperUtil) {
		super();
		this.orderService = orderService;
		this.authenticationControllerHelperUtil = authenticationControllerHelperUtil;

	}
	
	
	@GetMapping("/orders")
	public String listFirstPage(Model model, HttpServletRequest request) throws CustomerNotFoundException {
		
		LOGGER.info("OrderController | listFirstPage is called");
		
		return listOrdersByPage(model, request, 1, "orderTime", "desc", null);
	}

	@GetMapping("/orders/page/{pageNum}")
	public String listOrdersByPage(Model model, HttpServletRequest request,
						@PathVariable(name = "pageNum") int pageNum,
						String sortField, String sortDir, String keyword
			) throws CustomerNotFoundException {

		
		Customer customer = authenticationControllerHelperUtil.getAuthenticatedCustomer(request);


		Page<Order> page = orderService.listForCustomerByPage(customer, pageNum, sortField, sortDir, keyword);
		
		
		List<Order> listOrders = page.getContent();

		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("listOrders", listOrders);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("moduleURL", "/orders");
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

		
		long startCount = (pageNum - 1) * OrderService.ORDERS_PER_PAGE + 1;
		
		model.addAttribute("startCount", startCount);
		

		long endCount = startCount + OrderService.ORDERS_PER_PAGE - 1;
		
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		

		model.addAttribute("endCount", endCount);

		return "orders/orders_customer";		
	}
	
	@GetMapping("/orders/detail/{id}")
	public String viewOrderDetails(Model model,
			@PathVariable(name = "id") Integer id, HttpServletRequest request) throws CustomerNotFoundException {

		
		Customer customer = authenticationControllerHelperUtil.getAuthenticatedCustomer(request);
		

		Order order = orderService.getOrder(id, customer);
		
		
		model.addAttribute("order", order);

		return "orders/order_details_modal";
	}
	
	
}
