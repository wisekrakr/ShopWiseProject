package com.shopwise.common.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "product_details")
@NoArgsConstructor
@Getter
@Setter
public class ProductDetail implements Serializable{

    @Serial
    private static final long serialVersionUID = 4306493248136084267L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String value;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonBackReference
    private Product product;

    public ProductDetail(Integer id, String name, String value, Product product) {
        super();
        this.id = id;
        this.name = name;
        this.value = value;
        this.product = product;
    }

    public ProductDetail(String name, String value, Product product) {
        this.name = name;
        this.value = value;
        this.product = product;
    }

}
