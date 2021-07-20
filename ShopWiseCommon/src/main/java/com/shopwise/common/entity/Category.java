package com.shopwise.common.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@NoArgsConstructor
@Getter
@Setter
public class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 128, nullable = false, unique = true)
    private String name;

    @Column(name = "alias", length = 64, nullable = false, unique = true)
    private String alias;

    @Column(name = "image", length = 128, nullable = false)
    private String image;

    private boolean enabled;


    @Transient
    private boolean hasChildren;

    @OneToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    @OrderBy("name asc")
    private Set<Category>children = new HashSet<>();


    public Category(Integer id) {
        this.id = id;
    }

    public Category(String name) {
        this.name = name;
    }

    public Category(Integer id, String name, String alias) {
        this.id = id;
        this.name = name;
        this.alias = alias;
    }

    public Category(String name, String alias, String image) {
        this.name = name;
        this.alias = alias;
        this.image = image;
    }

    public Category(String name, String alias, String image, Category parent) {
        this(name, alias, image);
        this.parent = parent;
    }

    public static Category copyParentIdAndName(Category category){
        Category copiedCategory = new Category();
        copiedCategory.setId(category.getId());
        copiedCategory.setName(category.getName());

        return copiedCategory;
    }

    public static Category copyParentIdAndName(Integer id, String name){
        Category copiedCategory = new Category();
        copiedCategory.setId(id);
        copiedCategory.setName(name);

        return copiedCategory;
    }

    public static Category copyFull(Category category){
        Category copiedCategory = new Category();
        copiedCategory.setId(category.getId());
        copiedCategory.setName(category.getName());
        copiedCategory.setAlias(category.getAlias());
        copiedCategory.setImage(category.getImage());
        copiedCategory.setEnabled(category.isEnabled());

        return copiedCategory;
    }

    public static Category copyFull(Category category, String name){
        Category copiedCategory = Category.copyFull(category);
        copiedCategory.setName(name);
        return copiedCategory;
    }

    /**
     * This data is not mapped to the database
     */
    @Transient
    public String getImagePath(){
        if (this.id == null || image == null || image.isEmpty()) return "/images/default-product.png";
        return "/category-images/" + this.id + "/" + this.image;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
