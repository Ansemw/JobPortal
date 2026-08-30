package com.backend.jobportal.contact.controller;


import com.backend.jobportal.contact.dto.ContactDto;
import com.backend.jobportal.contact.service.IContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final IContactService contactService;

    // Handles GET requests for the list of all contacts and returns them as DTOs.
    @GetMapping(version = "1.0")
    public ResponseEntity<List<ContactDto>> getAllContacts() {
        List<ContactDto> contacts = contactService.getAllContacts();
        return ResponseEntity.ok().body(contacts);
    }

    @PostMapping(version = "1.0")
    public ResponseEntity<String> saveContact(@RequestBody ContactDto contactDto) {

        boolean isSaved=contactService.saveContact(contactDto);
        if (isSaved) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Contact saved successfully");
        }
        else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save contact");
        }

    }
}
