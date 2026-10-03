package com.backend.jobportal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;

// Reference entity for the generator. Real entities live in com.backend.jobportal.entity and
// extend BaseEntity, which supplies created_at/created_by/updated_at/updated_by via JPA auditing —
// never redeclare those columns on the entity itself.
@Getter
@Setter
@Entity
@Table(name = "examples")
public class Example extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // Owning side of a many-to-one relationship (see Job.company).
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 500)
    @Column(name = "website", length = 500)
    private String website;

    @Column(name = "rating", precision = 3, scale = 2)
    private BigDecimal rating;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "deadline")
    private Instant deadline;

    // Enum-like String column — the DTO restricts it with a @Pattern regex.
    @Size(max = 20)
    @NotNull
    @ColumnDefault("'DRAFT'")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

}
