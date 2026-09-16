package com.backend.jobportal.contact.repository;

import com.backend.jobportal.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findContactsByStatus(String status);
    List<Contact> findContactsByStatusOrderByCreatedAtDesc(String status);
    List<Contact> findContactsByStatus(String status, Sort  sort);
    Page<Contact> findContactsByStatus(String status, Pageable pageable);


}
