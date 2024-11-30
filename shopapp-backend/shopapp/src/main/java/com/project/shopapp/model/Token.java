package com.project.shopapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "token", nullable = false, length = 255)
    private String token;

    @Column(name = "token_type", nullable = false, length = 50)
    private String tokenType;

    private LocalDateTime expirationDate;

    private Boolean revoked; //hủy chưa
    private Boolean expired; //hết hạn chưa

    @Column(name = "is_mobile")
    private Boolean isMobile; //Là mobile hay không

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
