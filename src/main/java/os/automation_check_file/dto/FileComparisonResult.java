package os.automation_check_file.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileComparisonResult {
    private boolean isValid;
    private List<MismatchRecord> mismatches;
    private String message;
}