package com.backend.jobportal.contact.service.impl;

import com.backend.jobportal.contact.dto.ContactDto;
import com.backend.jobportal.contact.repository.ContactRepository;
import com.backend.jobportal.contact.service.IContactService;
import com.backend.jobportal.entity.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ContactServiceImpl implements IContactService {


    private final ContactRepository contactRepository;

    // Injects the repository used to talk to the contacts table.
    @Autowired
    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public List<ContactDto> getAllContacts() {
        List<Contact> contacts = contactRepository.findAll();
        List<ContactDto> dto = contacts.stream().map(this::transformToDto).toList();
        return dto;
    }

    @Override
    public boolean saveContact(ContactDto contactDto) {

        boolean result = false;
        Contact contact = contactRepository.save(transformToEntity(contactDto));
        if (contact != null && contact.getId() != null) {
            result = true;
        }
        return result;
    }

    private Contact transformToEntity(ContactDto contactDto) {
        Contact contact = new Contact();
        contact.setEmail(contactDto.email());
        contact.setMessage(contactDto.message());
        contact.setName(contactDto.name());
        contact.setSubject(contactDto.subject());
        contact.setUserType(contactDto.userType());
        contact.setCreatedAt(Instant.now());
        contact.setCreatedBy("System");
        contact.setStatus("NEW");
        return contact;
    }

    private ContactDto transformToDto(Contact contact) {
        return new ContactDto(
                contact.getEmail(),
                contact.getMessage(),
                contact.getName(),
                contact.getSubject(),
                contact.getUserType()
        );
    }
}
