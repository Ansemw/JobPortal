package com.backend.jobportal.contact.repository;

import com.backend.jobportal.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findContactsByStatus(String status);
    List<Contact> findContactsByStatusOrderByCreatedAtDesc(String status);
    List<Contact> findContactsByStatus(String status, Sort  sort);
    Page<Contact> findContactsByStatus(String status, Pageable pageable);

    @Modifying
    @Query( "Update Contact c set c.status = :status, c.updatedAt = CURRENT_TIMESTAMP, c.updatedBy=:updatedBy where c.id = :id")
    int updateStatusById(@Param("id") Long id, @Param("status") String status, @Param("updatedBy") String updatedBy);


}
