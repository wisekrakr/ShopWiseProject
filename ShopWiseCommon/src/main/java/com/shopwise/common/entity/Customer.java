package com.shopwise.common.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Entity
@NoArgsConstructor
@Table(name = "customers")
@Getter
@Setter
public class Customer implements Serializable {
    @Serial
    private static final long serialVersionUID = -3750320196713987482L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 45)
    private String email;

    @Column(nullable = false, length = 64)
    private String password;

    @Column(name = "first_name",nullable = false, length = 45)
    private String firstName;

    @Column(name = "last_name",nullable = false, length = 45)
    private String lastName;

    @Column(name = "phone_number",nullable = false, length = 15)
    private String phoneNumber;

    @Column(name = "address_line",nullable = false, length = 64)
    private String addressLine;

    @Column(name = "address_line_2", length = 64)
    private String addressLine2;

    @Column(nullable = false, length = 45)
    private String city;

    @Column(nullable = false, length = 64)
    private String state;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @Column(name = "postal_code",nullable = false, length = 10)
    private String postalCode;

    @Column(name = "created_at")
    private Date createdAt;

    private boolean enabled;

    @Column(name = "verification_code",length = 64)
    private String verificationCode;


    @Override
    public String toString() {
        return "Customer [id=" + id + ", email=" + email + ", firstName=" + firstName + ", lastName=" + lastName + "]";
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
