package os.automation_check_file.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import os.automation_check_file.dto.CellInfo;
import os.automation_check_file.dto.FileComparisonResult;
import os.automation_check_file.dto.FilesDto;
import os.automation_check_file.dto.MismatchRecord;
import os.automation_check_file.service.ExcelComparisonService;
import os.automation_check_file.service.FileCompareService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/fileCompare")
public class FileCompareController {
    private final FileCompareService fileCompareService;
    private final ExcelComparisonService comparisonService;

    public FileCompareController(FileCompareService fileCompareService, ExcelComparisonService comparisonService) {
        this.fileCompareService = fileCompareService;
        this.comparisonService = comparisonService;
    }
    @GetMapping()
    public ResponseEntity<String> getFiles() {
        return ResponseEntity.ok("Os  !!");
    }
    @PostMapping("/compare")
    public ResponseEntity<List<MismatchRecord>> compareFiles(
            @RequestParam("referenceFile") MultipartFile referenceFile,
            @RequestParam("inputFile") MultipartFile inputFile) {
        try {
            List<MismatchRecord> mismatches = fileCompareService.compareFiles(referenceFile, inputFile);
            return ResponseEntity.ok(mismatches);
        } catch (IOException e) {
            MismatchRecord errorRecord = MismatchRecord.builder()
                    .headerValue("Error")
                    .actualValue("Error processing files: " + e.getMessage())
                    .build();
            return ResponseEntity.status(500).body(List.of(errorRecord));
        }
    }


//    @PostMapping("/segment")
//    public ResponseEntity<Set<String>> getSegment(@RequestBody MultipartFile file) {
//        try {
//            Set<String> segments = fileCompareService.extractSegment(file);
//            return ResponseEntity.ok(segments);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }


    @PostMapping("/headers")
    public ResponseEntity<List<CellInfo>> getFileHeaders(@RequestBody MultipartFile file) {
        try {
            List<CellInfo> headers = fileCompareService.extractHeadersFromRefDocRow20(file);
            return ResponseEntity.ok(headers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/headers/inputFile")
    public ResponseEntity<List<CellInfo>> getInputFileHeaders(@RequestBody MultipartFile file) {
        try {
            List<CellInfo> headers = fileCompareService.extractHeadersFromInputFileRow(file);
            return ResponseEntity.ok(headers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/columnMappings")
    public ResponseEntity<Map<String, Integer>> getColumnMappings() {
        try {
            Map<String, Integer> mappings = fileCompareService.fetchColumnFromDictionary();
            return ResponseEntity.ok(mappings);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


//    @PostMapping(value = "/values", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<FileComparisonResult> compareFiles(
//            @ModelAttribute FilesDto filesDto) {
//        try {
//            FileComparisonResult result = comparisonService.compareFiles(
//                    filesDto.getReferenceFile(),
//                    filesDto.getInputFile()
//            );
//            return ResponseEntity.ok(result);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new FileComparisonResult(false, null, "Error comparing files: " + e.getMessage()));
//        }
//    }
}

