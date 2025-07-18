package com.pjserrano.orderprocessor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MyOrder implements Serializable {
    private int codeProductOrdered;
    private String name;
    private int quantity;
    private String address;
}