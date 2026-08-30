

import java.math.BigDecimal;
import java.time.Instant;

public record ExampleDto(
        Long id,
        String name,
        String email,
        String subject,
        String message,
        String userType,
        String status,
        Instant createdAt
) {
}
