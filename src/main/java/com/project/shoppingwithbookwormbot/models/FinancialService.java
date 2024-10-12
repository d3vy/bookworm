package com.project.shoppingwithbookwormbot.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "financial_services")
public class FinancialService implements PricedItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "financial_name")
    private String name;

    @Column(name = "financial_price")
    private Integer price;
}
