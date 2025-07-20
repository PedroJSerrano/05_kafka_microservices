package com.pjserrano.shipments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "shipments")
public class Shipments implements Persistable<Integer> {

    @Id
    @Column("shipId")
    private int shipId;

    @Column("productCode")
    private int productCode;

    @Column("orderDate")
    private LocalDateTime orderDate;

    @Column("address")
    private String address;

    @Column("status")
    private String status;

    @Transient
    private boolean isNew;

    @Override
    public Integer getId() { return this.shipId; }

    @Override
    public boolean isNew() { return this.isNew;}
}
