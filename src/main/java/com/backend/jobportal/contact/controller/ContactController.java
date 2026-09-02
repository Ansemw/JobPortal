package com.backend.jobportal.contact.controller;


import com.backend.jobportal.contact.dto.ContactDto;
import com.backend.jobportal.contact.service.IContactService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final IContactService contactService;



    @PostMapping(path = "/public", version = "1.0")
    public ResponseEntity<String> saveContact(@RequestBody @Valid ContactDto contactDto) {


        boolean isSaved=contactService.saveContact(contactDto);
        if (isSaved) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Contact saved successfully");
        }
        else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save contact");
        }

    }

    @GetMapping(path = "/open", version = "1.0")
    public ResponseEntity<String> fetchOpenContacts(@RequestParam @Validated @NotBlank(message = "Status can't be blank") String status) {
        return ResponseEntity.ok("These are the contacts with given status "+ status);
    }
}
