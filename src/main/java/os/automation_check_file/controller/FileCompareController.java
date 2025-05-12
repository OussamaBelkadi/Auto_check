package os.automation_check_file.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import os.automation_check_file.dto.FileComparisonResult;
import os.automation_check_file.dto.FilesDto;
import os.automation_check_file.service.ExcelComparisonService;
import os.automation_check_file.service.FileCompareService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/fileCompare")
public class FileCompareController {
    private final FileCompareService fileCompareService;
    private final ExcelComparisonService comparisonService;

    public FileCompareController(FileCompareService fileCompareService, ExcelComparisonService comparisonService) {
        this.fileCompareService = fileCompareService;
        this.comparisonService = comparisonService;
    }

    @PostMapping("/segment")
    public ResponseEntity<Set<String>> getSegment(@RequestBody MultipartFile file) {
        try {
            Set<String> segments = fileCompareService.extractSegment(file);
            return ResponseEntity.ok(segments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/headers")
    public ResponseEntity<List<String>> getFileHeaders(@RequestBody MultipartFile file) {
        try {
            List<String> headers = fileCompareService.extractHeadersFromRow20(file);
            return ResponseEntity.ok(headers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PostMapping(value = "/values", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileComparisonResult> compareFiles(
            @ModelAttribute FilesDto filesDto) {
        try {
            FileComparisonResult result = comparisonService.compareFiles(
                    filesDto.getReferenceFile(),
                    filesDto.getInputFile()
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FileComparisonResult(false, null, "Error comparing files: " + e.getMessage()));
        }
    }
}

