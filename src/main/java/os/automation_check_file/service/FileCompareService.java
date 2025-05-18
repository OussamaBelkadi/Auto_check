package os.automation_check_file.service;

import org.springframework.web.multipart.MultipartFile;
import os.automation_check_file.dto.CellInfo;
import os.automation_check_file.dto.MismatchRecord;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FileCompareService{
//    Set<String> extractSegment(MultipartFile file) throws IOException;
    List<CellInfo> extractHeadersFromRefDocRow20(MultipartFile file) throws IOException;
    List<CellInfo> extractHeadersFromInputFileRow(MultipartFile file) throws IOException;
    Map<String, Integer> fetchColumnFromDictionary();
    List<MismatchRecord> compareFiles(MultipartFile referenceFile, MultipartFile inputFile) throws IOException;
//    FileComparisonResult compareFileWithReference(MultipartFile inputFile) throws IOException;
}
