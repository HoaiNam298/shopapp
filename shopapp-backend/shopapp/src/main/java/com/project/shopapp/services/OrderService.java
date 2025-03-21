package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    Order createOrder(OrderDTO orderDTO)  throws Exception;
    Order getOrderById(Long id) throws Exception;
    List<Order> findByUserId(Long userId);
    Order updateOrder(Long orderId, OrderDTO orderDTO)  throws Exception;
    void deleteOrder(Long id);
    Page<Order> getOrdersByKeyword(String keyword, Pageable pageable);
}
