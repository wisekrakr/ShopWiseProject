package com.shopwise.common.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 4285956468270828920L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 128, nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number",length = 20, unique = true)
    private String phoneNumber;

    @Column(length = 64, nullable = false)
    private String password;

    @Column(name = "first_name", length = 45, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 45, nullable = false)
    private String lastName;

    @Column(length = 64)
    private String avatar;

    @Column(name = "created_at")
    private Date createdAt = new Date();

    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public User(String email, String password, String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(String email, String phoneNumber, String password, String firstName, String lastName) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(String email, String phoneNumber, String password, String firstName, String lastName, Set<Role> roles) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
    }

    public void addRole(Role role){
        this.roles.add(role);
    }

    /**
     * This data is not mapped to the database
     */
    @Transient
    public String getAvatarPath(){
        if(id == null || avatar == null || avatar.isEmpty()) return "/images/default-avatar.png";
        return "/user-avatars/" + this.id + "/" + this.avatar;
    }

    /**
     * This data is not mapped to the database
     */
    @Transient
    public String getFullName(){
        return firstName + " " + lastName;
    }

    public boolean hasRole(String roleName){

        for (Role role : roles) {
            if (role.getName().equals(roleName)) return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", createdAt=" + createdAt +
                ", enabled=" + enabled +
                ", roles=" + roles +
                '}';
    }
}
