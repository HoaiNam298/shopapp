package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDetailDTO;
import com.project.shopapp.model.OrderDetail;

import java.util.List;

public interface OrderDetailService {

    OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO)  throws Exception;
    OrderDetail getOrderDetail(Long id) throws Exception;
    List<OrderDetail> findByOrderId(Long orderId);
    OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO)  throws Exception;
    void deleteById(Long id);
}
