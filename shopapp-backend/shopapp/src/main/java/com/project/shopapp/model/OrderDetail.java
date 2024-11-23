package com.project.shopapp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
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
//    @JsonProperty("number_of_products")
    private int numberOfProducts;

    @Column(name = "total_money", nullable = false)
//    @JsonProperty("total_money")
    private Float totalMoney;

    @Column(name = "color", length = 20)
    private String color;

    @ManyToOne
    @JoinColumn(name = "order_id  ")
    @JsonBackReference
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id  ")
    private Product product;
}
