
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExampleServiceImpl implements IExampleService {


    private final ExampleRepository exampleRepository;

    // Injects the repository used to talk to the companies table.
    @Autowired
    public ExampleServiceImpl(ExampleRepository exampleRepository) {
        this.exampleRepository = exampleRepository;
    }

    @Override
    public List<ExampleDto> getAllExamples() {
        List<Example> contacts = contactRepository.findAll();
        List<ExampleDto> dto = contacts.stream().map(this::transformToDto).toList();
        return dto;
    }
   
    private ExampleDto transformToDto(Example example) {
        return new ExampleDto(
                example.getId(),
                example.getName(),
                example.getLogo(),
                example.getIndustry(),
                example.getSize(),
                example.getRating(),
                example.getLocations(),
                example.getFounded(),
                example.getDescription(),
                example.getEmployees(),
                example.getWebsite(),
                example.getCreatedAt()
        );
    }
}
