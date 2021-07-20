package com.shopwise.admin.entity.controllers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object Design Pattern.
 * It is basically used to pass data with multiple attributes in one shot from client to server,
 * to avoid multiple calls to a remote server.
 */
@Getter
@Setter
@NoArgsConstructor
public class CategoryDTO {

    private Integer id;
    private String name;

    public CategoryDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
