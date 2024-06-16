package com.shopme.order;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.order.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {

	public Order findByIdAndCustomer(Integer id, Customer customer);
}
