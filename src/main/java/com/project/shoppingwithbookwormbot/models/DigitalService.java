package com.project.shoppingwithbookwormbot.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "digital_services")
public class DigitalService implements PricedItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "digital_name")
    private String name;

    @Column(name = "digital_price")
    private Integer price;
}
