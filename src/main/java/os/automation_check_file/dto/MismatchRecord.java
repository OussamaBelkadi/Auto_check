package os.automation_check_file.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MismatchRecord {
    private String referenceValue;
    private String actualValue;
    private int position;
    private String columnName;
}