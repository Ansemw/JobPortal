package com.backend.jobportal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "users")
@Getter
@Setter
public class JobPortalUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "NAME", nullable = false)
    private String name;

    @Size(max = 255)
    @NotNull
    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Size(max = 500)
    @NotNull
    @Column(name = "PASSWORD_HASH", nullable = false, length = 500)
    private String passwordHash;

    @Size(max = 20)
    @Column(name = "MOBILE_NUMBER", unique = true, length = 20)
    private String mobileNumber;


    @NotNull
    @JoinColumn(name = "ROLE_ID", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Company company;



}
