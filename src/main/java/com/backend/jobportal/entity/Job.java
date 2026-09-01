package com.backend.jobportal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "jobs")
@Getter
@Setter
public class Job extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "TITLE", nullable = false)
    @Size(max = 255)
    private String title;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "LOCATION", nullable = false)
    private String location;

    @Column(name = "WORK_TYPE", nullable = false, length = 50)
    private String workType;

    @Column(name = "JOB_TYPE", nullable = false, length = 50)
    private String jobType;

    @Column(name = "CATEGORY", nullable = false, length = 100)
    private String category;

    @Column(name = "EXPERIENCE_LEVEL", nullable = false, length = 50)
    private String experienceLevel;

    @Column(name = "SALARY_MIN", nullable = false, precision = 12, scale = 2)
    private BigDecimal salaryMin;

    @Column(name = "SALARY_MAX", nullable = false, precision = 12, scale = 2)
    private BigDecimal salaryMax;

    @Column(name = "SALARY_CURRENCY", nullable = false, length = 10)
    private String salaryCurrency;

    @Column(name = "SALARY_PERIOD", nullable = false, length = 20)
    private String salaryPeriod;

    @Lob
    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Lob
    @Column(name = "REQUIREMENTS")
    private String requirements;

    @Lob
    @Column(name = "BENEFITS")
    private String benefits;

    @Column(name = "POSTED_DATE", nullable = false)
    private Instant postedDate;

    @Column(name = "APPLICATION_DEADLINE")
    private Instant applicationDeadline;

    @Column(name = "APPLICATIONS_COUNT")
    private Integer applicationsCount;

    @Column(name = "FEATURED")
    private Boolean featured;

    @Column(name = "URGENT")
    private Boolean urgent;

    @Column(name = "REMOTE")
    private Boolean remote;

    @Column(name = "STATUS", nullable = false, length = 20)
    private String status;

}
