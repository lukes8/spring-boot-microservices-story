package com.lukestories.microservices.user_ws.web.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;

@Entity
@Table(name = "ROLE") @Data
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME")
    private String name;
    // ROLE_ADMIN, ROLE_USER
    // READ, WRITE, DELETE

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "roles_authorities", joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
                                           inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id") )
    private Collection<AuthorityEntity> authorities;
}
