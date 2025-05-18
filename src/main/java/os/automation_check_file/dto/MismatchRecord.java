package os.automation_check_file.dto;

import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MismatchRecord {
    private String headerValue;
    private Set<String> expectedValue;
    private String actualValue;
    private int rowNumber;
    private int columnNumber;

}