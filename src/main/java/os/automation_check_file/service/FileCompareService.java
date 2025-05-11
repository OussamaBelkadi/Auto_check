package os.automation_check_file.service;

import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface FileCompareService{
    List<String> extractHeadersFromRow20(MultipartFile file) throws IOException;
}
