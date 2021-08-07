package com.shopwise.common.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@NoArgsConstructor
@Table(name = "currencies")
@Getter
@Setter
public class Currency implements Serializable {
    @Serial
    private static final long serialVersionUID = 6107251674304555282L;

    public Currency(String name, String symbol, String code) {
        this.name = name;
        this.symbol = symbol;
        this.code = code;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(nullable = false, length = 6)
    private String symbol;

    @Column(nullable = false, length = 4)
    private String code;

    @Override
    public String toString() {
        return name + " - " +
                symbol + " - " +
                code;
    }
}
