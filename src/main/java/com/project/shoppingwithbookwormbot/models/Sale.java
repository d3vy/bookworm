package com.project.shoppingwithbookwormbot.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "sales")
public class Sale implements PricedItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sale_name")
    private String name;

    @Column(name = "sale_price")
    private Integer price;
}
