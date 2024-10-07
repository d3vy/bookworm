package com.project.shoppingwithbookwormbot.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "bank_services")
public class BankService implements PricedItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "bank_name")
    private String name;

    @Column(name = "bank_price")
    private Integer price;
}
