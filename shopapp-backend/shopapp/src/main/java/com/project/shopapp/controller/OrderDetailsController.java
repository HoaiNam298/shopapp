package com.project.shopapp.controller;

import com.project.shopapp.dtos.OrderDetailDTO;
import com.project.shopapp.model.OrderDetail;
import com.project.shopapp.response.OrderDetailResponse;
import com.project.shopapp.services.OrderDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/order_details")
@RequiredArgsConstructor
public class OrderDetailsController {

    private final OrderDetailService orderDetailService;

    @PostMapping("")
    public ResponseEntity<?> createOrderDetail(
            @Valid @RequestBody OrderDetailDTO orderDetailDTO,
            BindingResult result
    ) {
        try {
            if (result.hasErrors()){
                List<String> errorMessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessage);
            }
            OrderDetail newOrderDetail = orderDetailService.createOrderDetail(orderDetailDTO);
            return ResponseEntity.ok(OrderDetailResponse.fromeOrderDetail(newOrderDetail));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail(
            @Valid @PathVariable Long id
    ) {
        try {
            OrderDetail orderDetail = orderDetailService.getOrderDetail(id);
            return ResponseEntity.ok(OrderDetailResponse.fromeOrderDetail(orderDetail));
//            return orderDetail;
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Lấy danh sách order-detail của 1 order
    @GetMapping("/order/{order_id}")
    public ResponseEntity<?> getOrderDetails(
            @Valid @PathVariable("order_id") Long orderId
    ) {
        try {
            List<OrderDetail> orderDetails = orderDetailService.findByOrderId(orderId);
            List<OrderDetailResponse> orderDetailResponses = orderDetails
                    .stream()
                    .map(OrderDetailResponse::fromeOrderDetail)
                    .toList();
            return ResponseEntity.ok(orderDetailResponses);
//            return ResponseEntity.ok(orderDetails);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    // Admin
    public ResponseEntity<?> updateOrders(
            @Valid @PathVariable Long id,
            @Valid @RequestBody OrderDetailDTO orderDetailDTO,
            BindingResult result
    ){
        try {
            if (result.hasErrors()) {
                List<String> errorMessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessage);
            }
            OrderDetail orderDetail = orderDetailService.updateOrderDetail(id, orderDetailDTO);
            return ResponseEntity.ok(orderDetail);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    // Admin
    public ResponseEntity<?> deleteOrders(
            @Valid @PathVariable Long id
    ) {
        // Xóa mềm update active = false
        try {
            orderDetailService.deleteById(id);
            return ResponseEntity.ok("Delete order detail successfully with id " + id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
