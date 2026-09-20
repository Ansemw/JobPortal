package com.backend.jobportal.contact.service.impl;

import com.backend.jobportal.constants.ApplicationConstant;
import com.backend.jobportal.contact.dto.ContactDto;
import com.backend.jobportal.contact.dto.ContactResponseDto;
import com.backend.jobportal.contact.repository.ContactRepository;
import com.backend.jobportal.contact.service.IContactService;
import com.backend.jobportal.entity.Contact;
import com.backend.jobportal.util.ApplicationUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
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
    @Transactional
    public boolean saveContact(ContactDto contactDto) {

        boolean result = false;
        Contact contact = contactRepository.save(transformToEntity(contactDto));
        if (contact != null && contact.getId() != null) {
            result = true;
        }
        return result;
    }

    @Override
    public List<ContactResponseDto> fetchNewContacts() {
        //List<Contact> contacts= contactRepository.findContactsByStatus(ApplicationConstant.STATUS_NEW);
        List<Contact> contacts = contactRepository.findContactsByStatusOrderByCreatedAtDesc(ApplicationConstant.STATUS_NEW);
        List<ContactResponseDto> contactsDto = contacts.stream().map(contact->transformToResponseDto(contact)).toList();
        return contactsDto;
    }


    @Override
    public List<ContactResponseDto> fetchNewContactsSorted(String sortBy, String sortOrder) {

        Sort sort = sortOrder.equalsIgnoreCase("desc")? Sort.by(sortBy).descending(): Sort.by(sortBy).ascending();
        List<Contact> contacts = contactRepository.findContactsByStatus(ApplicationConstant.STATUS_NEW, sort);
        List<ContactResponseDto> contactsDto = contacts.stream().map(contact->transformToResponseDto(contact)).toList();
        return contactsDto;
    }

    @Override
    public Page<ContactResponseDto> fetchPaginatedContactsSorted(int pageNumber, int pageSize, String sortBy, String sortOrder) {

        Sort sort = sortOrder.equalsIgnoreCase("desc")? Sort.by(sortBy).descending(): Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Contact> contacts = contactRepository.findContactsByStatus(ApplicationConstant.STATUS_NEW, pageable);
        Page<ContactResponseDto> contactsDto = contacts.map(contact->transformToResponseDto(contact));

        return contactsDto;
    }

    @Override
    @Transactional
    public boolean closeContactMessage(Long id) {

        int updatedRows = contactRepository.updateStatusById(id, ApplicationConstant.STATUS_CLOSED, ApplicationUtility.getLoggedInUser());

        return updatedRows > 0;
        /* Contact contact = contactRepository.findById(id).orElse(null);
        if(contact==null) return false;

        else{
            contact.setStatus(ApplicationConstant.STATUS_CLOSED);
            contactRepository.save(contact);
            return true;
        }*/
    }

    private Contact transformToEntity(ContactDto contactDto) {
        Contact contact = new Contact();
        contact.setEmail(contactDto.email());
        contact.setMessage(contactDto.message());
        contact.setName(contactDto.name());
        contact.setSubject(contactDto.subject());
        contact.setUserType(contactDto.userType());
        /*
        contact.setCreatedAt(Instant.now());
        contact.setCreatedBy("System");
        */
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

    private ContactResponseDto transformToResponseDto(Contact contact) {
        return new ContactResponseDto(
                contact.getId(),
                contact.getName(),
                contact.getEmail(),
                contact.getUserType(),
                contact.getSubject(),
                contact.getMessage(),
                contact.getStatus(),
                contact.getCreatedAt()
        );
    }
}
