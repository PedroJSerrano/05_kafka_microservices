package com.pjserrano.orders.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MyOrder {
    private int codeProductOrdered;
    private String name;
    private int quantity;
    private String address;
}
