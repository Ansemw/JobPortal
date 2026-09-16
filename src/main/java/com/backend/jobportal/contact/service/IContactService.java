package com.backend.jobportal.contact.service;

import com.backend.jobportal.contact.dto.ContactDto;
import com.backend.jobportal.contact.dto.ContactResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IContactService {

    // Returns every contact in the system as a list of DTOs.
    public List<ContactDto> getAllContacts();

    public boolean saveContact(ContactDto contactDto);

    public List<ContactResponseDto> fetchNewContacts();

    public List<ContactResponseDto> fetchNewContactsSorted(String sortBy, String sortOrder);

    public Page<ContactResponseDto> fetchPaginatedContactsSorted(int pageNumber, int pageSize, String sortBy, String sortOrder);

    public boolean closeContactMessage(Long id);
}
