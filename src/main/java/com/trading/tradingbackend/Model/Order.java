package com.trading.tradingbackend.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trading.tradingbackend.Enums.ORDER_STATUS;
import com.trading.tradingbackend.Enums.ORDER_TYPE;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private User user;

    @Column(nullable = false)
    private ORDER_TYPE orderType;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private LocalDateTime timestamp=LocalDateTime.now();

    @Column(nullable = false)
    private ORDER_STATUS orderStatus;

    @OneToOne(mappedBy = "order",cascade = CascadeType.ALL)
    @ToString.Exclude // Exclude from toString to prevent StackOverflowError
    private OrderItem orderItem;


}
