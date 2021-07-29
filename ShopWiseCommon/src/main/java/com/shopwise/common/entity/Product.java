package com.shopwise.common.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Entity
@Table(name = "products")
@NoArgsConstructor
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 256, nullable = false,unique = true)
    private String name;

    @Column(length = 256, nullable = false,unique = true)
    private String alias;

    @Column(length = 512, nullable = false)
    private String summary;

    @Column(length = 4096, nullable = false)
    private String description;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    private boolean enabled;

    @Column(nullable = false)
    private int stock;

    private float cost;
    private float price;

    @Column(name = "discount_percentage")
    private float discountPercentage;

    private float length;
    private float width;
    private float height;
    private float weight;

    @Column(name = "main_image", nullable = false)
    private String mainImage;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonBackReference
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    @JsonBackReference
    private Brand brand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true) //within the ProductImage entity
    @JsonBackReference
    private Set<ProductImage>images = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<ProductDetail> details = new ArrayList<>();

    @Transient
    public boolean isInStock(){
        return stock > 0;
    }

    /**
     * This data is not mapped to the database
     */
    @Transient
    public String getMainImagePath(){
        if (this.id == null || mainImage == null || mainImage.isEmpty()) return "/images/default-product.png";
        return "/product-images/" + this.id + "/" + this.mainImage;
    }

//    /**
//     * This data is not mapped to the database
//     */
    @Transient
    public String getDate(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return updatedAt != null ? format.format(updatedAt) : format.format(createdAt);
    }

    public void addExtraImage(String imageName){
        this.images.add(new ProductImage(imageName, this));
    }

    public void addDetail(String name, String value) {
        this.details.add(new ProductDetail(name, value, this));
    }

    public void addDetail(Integer id, String name, String value) {
        this.details.add(new ProductDetail(id, name, value, this));
    }

    public boolean containsImageName(String imageName) {

        for (ProductImage image : images) {
            if (image.getName().equals(imageName)) {
                return true;
            }
        }

        return false;
    }

    @Transient
    public String getShortenedName() {
        if (name.length() > 70) {
            return name.substring(0, 70).concat("...");
        }
        return name;
    }

    @Transient
    public String getShortestName() {
        if (name.length() > 30) {
            return name.substring(0, 30).concat("...");
        }
        return name;
    }

    @Transient
    public float getDiscountPrice() {
        if (discountPercentage > 0) {
            return price * ((100 - discountPercentage) / 100);
        }
        return this.price;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", summary='" + summary + '\'' +
                ", createdAt=" + createdAt +
                ", stock=" + stock +
                ", price=" + price +

                '}';
    }


}
