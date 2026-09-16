package com.backend.jobportal.contact.controller;


import com.backend.jobportal.contact.dto.ContactDto;
import com.backend.jobportal.contact.dto.ContactResponseDto;
import com.backend.jobportal.contact.service.IContactService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<String> fetchContacts(@RequestParam @Validated @NotBlank(message = "Status can't be blank") String status) {
        return ResponseEntity.ok("These are the contacts with given status "+ status);
    }

    @GetMapping(path = "/admin", version = "1.0")
    public ResponseEntity<List<ContactResponseDto>> fetchNewContacts() {

        List<ContactResponseDto> contactsDto = contactService.fetchNewContacts();
        return ResponseEntity.status(HttpStatus.OK).body(contactsDto);
    }

    @GetMapping(path = "/sort/admin", version = "1.0")
    public ResponseEntity<List<ContactResponseDto>> fetchNewContactsSorted(@RequestParam(defaultValue = "createdAt") String sortBy
            ,@RequestParam(defaultValue = "asc") String sortOrder) {

        List<ContactResponseDto> contactsDto = contactService.fetchNewContactsSorted(sortBy, sortOrder);
        return ResponseEntity.status(HttpStatus.OK).body(contactsDto);
    }

    @GetMapping(path = "/page/admin", version = "1.0")
    public ResponseEntity<Page<ContactResponseDto>> fetchPaginatedContactsSorted( @RequestParam(defaultValue = "0") int pageNumber
                                                                                 ,@RequestParam(defaultValue = "10") int pageSize
                                                                                 ,@RequestParam(defaultValue = "createdAt") String sortBy
                                                                                 ,@RequestParam(defaultValue = "asc") String sortOrder) {

        Page<ContactResponseDto> contactsDto = contactService.fetchPaginatedContactsSorted(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.status(HttpStatus.OK).body(contactsDto);
    }

    @PatchMapping("/{id}/status/admin")
    public ResponseEntity<String> closeContactMessage(@PathVariable Long id) {

        boolean isUpdated = contactService.closeContactMessage(id);
        if (isUpdated) {
            return ResponseEntity.status(HttpStatus.OK).body("Contact message closed successfully");
        }else  {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to close contact");
        }

    }

}
