package com.project.shopapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "order_detail")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "price", nullable = false)
    private Float price;

    @Column(name = "number_of_products", nullable = false)
    private int numberOfProducts;

    @Column(name = "total_money", nullable = false)
    private Float totalMoney;

    @Column(name = "color", length = 20)
    private String color;

    @ManyToOne
    @JoinColumn(name = "order_id  ")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id  ")
    private Product product;
}
