package com.project.shoppingwithbookwormbot.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "other_services")
public class OtherService implements PricedItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "other_name")
    private String name;

    @Column(name = "other_price")
    private Integer price;
}
