package com.project.shopapp.services.Impl;

import com.project.shopapp.dtos.OrderDetailDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.model.Order;
import com.project.shopapp.model.OrderDetail;
import com.project.shopapp.model.Product;
import com.project.shopapp.repositories.OrderDetailRepository;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.services.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) throws Exception {
        Order order = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find order with order id: " + orderDetailDTO.getOrderId()));
        Product product = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find product with product id: " + orderDetailDTO.getProductId()));
        OrderDetail newOrderDetail = OrderDetail.builder()
                .order(order)
                .product(product)
                .price(orderDetailDTO.getPrice())
                .numberOfProducts(orderDetailDTO.getNumberOfProducts())
                .totalMoney(orderDetailDTO.getTotalMoney())
                .color(orderDetailDTO.getColor())
                .build();
        return orderDetailRepository.save(newOrderDetail);
    }

    @Override
    public OrderDetail getOrderDetail(Long id) throws Exception {
        return orderDetailRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find this order detail with id: " + id));
    }

    @Override
    public List<OrderDetail> findByOrderId(Long orderId){
        return orderDetailRepository.findByOrderId(orderId);
    }

    @Override
    @Transactional
    public OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO) throws Exception {
        OrderDetail orderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find order detail with product id: " + id));
        Order order = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find order with order id: " + orderDetailDTO.getOrderId()));
        Product product = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find product with product id: " + orderDetailDTO.getProductId()));

        orderDetail.setColor(orderDetailDTO.getColor());
        orderDetail.setPrice(orderDetailDTO.getPrice());
        orderDetail.setNumberOfProducts(orderDetailDTO.getNumberOfProducts());
        orderDetail.setTotalMoney(orderDetailDTO.getTotalMoney());
        orderDetail.setOrder(order);
        orderDetail.setProduct(product);
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        orderDetailRepository.deleteById(id);
    }
}
