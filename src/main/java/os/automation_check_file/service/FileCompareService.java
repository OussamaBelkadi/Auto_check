package os.automation_check_file.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface FileCompareService{
    Set<String> extractSegment(MultipartFile file) throws IOException;
    List<String> extractHeadersFromRow20(MultipartFile file) throws IOException;

}
