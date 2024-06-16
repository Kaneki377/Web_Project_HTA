package com.shopme.admin.order;

import java.util.List;

import com.shopme.admin.error.OrderNotFoundException;
import com.shopme.admin.setting.SettingService;

@Controller
public class OrderController {
	private String defaultRedirectURL = "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";

	@Autowired
	private OrderService orderService;
	
	@Autowired
	private SettingService settingService;
	
	@GetMapping("/orders")
	public String listFirstPage() {
		
		
		return defaultRedirectURL;
	}

	@GetMapping("/orders/page/{pageNum}")
	public String listByPage(
			@PagingAndSortingParam(listName = "listOrders", moduleURL = "/orders") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser,
			HttpServletRequest request) {
		
		
		orderService.listByPage(pageNum, helper);
		loadCurrencySetting(request);
		
		return "orders/orders";
		}
	
	private void loadCurrencySetting(HttpServletRequest request) {
		List<Setting> currencySettings = settingService.getCurrencySettings();
		for(Setting setting : currencySettings) { 
			request.setAttribute(setting.getKey(), setting.getValue());
		}
	}
	
	
	@GetMapping("/orders/detail/{id}")
	public String viewOrderDetails(@PathVariable("id") Integer id, Model model, 
			RedirectAttributes ra, HttpServletRequest request,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser
			) {
		
		
		try {
			Order order = orderService.get(id);

			
			loadCurrencySetting(request);		

			model.addAttribute("order", order);

			return "orders/order_details_modal";
		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("messageError", ex.getMessage());			
			return defaultRedirectURL;
		}

	}
	
	@GetMapping("/orders/delete/{id}")
	public String deleteOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		
		try {
			orderService.delete(id);
			ra.addFlashAttribute("messageSuccess", "The order ID " + id + " has been deleted.");
			
			
		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("messageError", ex.getMessage());
			
		}

		return defaultRedirectURL;
	}
	
	@GetMapping("/orders/edit/{id}")
	public String editOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra,
			HttpServletRequest request) {
		
		LOGGER.info("OrderController | editOrder is called");
		
		try {
			Order order = orderService.get(id);;

			List<Country> listCountries = orderService.listAllCountries();
			
			LOGGER.info("OrderController | editOrder | order : " + order.toString());
			LOGGER.info("OrderController | editOrder | listCountries : " + listCountries);
			LOGGER.info("OrderController | editOrder | pageTitle : " + "Edit Order (ID: " + id + ")" );

			model.addAttribute("pageTitle", "Edit Order (ID: " + id + ")");
			model.addAttribute("order", order);
			model.addAttribute("listCountries", listCountries);

			return "orders/order_form";

		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			
			LOGGER.info("OrderController | editOrder | message : " + ex.getMessage());
			
			return defaultRedirectURL;
		}

	}
	
	@PostMapping("/order/save")
	public String saveOrder(Order order, HttpServletRequest request, RedirectAttributes ra) {
		
		LOGGER.info("OrderController | saveOrder is called");
		
		String countryName = request.getParameter("countryName");
		
		LOGGER.info("OrderController | saveOrder | countryName : " + countryName);
		
		order.setCountry(countryName);

		OrderUtil.updateProductDetails(order, request);
		OrderUtil.updateOrderTracks(order, request);

		orderService.save(order);		

		ra.addFlashAttribute("messageSuccess", "The order ID " + order.getId() + " has been updated successfully");
		
		LOGGER.info("OrderController | saveOrder | message : " + "The order ID " + order.getId() + " has been updated successfully");

		return defaultRedirectURL;
	}
	}



