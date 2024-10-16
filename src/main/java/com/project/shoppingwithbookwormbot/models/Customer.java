package com.project.shoppingwithbookwormbot.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "username")
    private String username;

    //Переопределение метода toString().
    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", name='" + username + '\'' +
                '}';
    }
}
