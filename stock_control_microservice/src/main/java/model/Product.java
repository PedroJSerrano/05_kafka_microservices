package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product implements Persistable<Integer>{
    @Id
    @Column("productCode")
    private int productCode;

    @Column("productName")
    private String productName;

    @Column("productCategory")
    private String productCategory;

    @Column("unitaryProductPrice")
    private double unitaryProductPrice;

    @Column("quantityInStock")
    private int quantityInStock;

    @Transient
    private boolean isNew;

    @Override
    public Integer getId() {
        return this.productCode;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }
}
