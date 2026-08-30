package com.backend.jobportal.contact.service;

import com.backend.jobportal.contact.dto.ContactDto;

import java.util.List;

public interface IContactService {

    // Returns every contact in the system as a list of DTOs.
    public List<ContactDto> getAllContacts();

    public boolean saveContact(ContactDto contactDto);
}
