package os.automation_check_file.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
@Data
public class FilesDto {
    MultipartFile referenceFile;
    MultipartFile inputFile;
}
