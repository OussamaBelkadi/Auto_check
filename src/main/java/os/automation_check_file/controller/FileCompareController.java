package os.automation_check_file.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import os.automation_check_file.service.FileCompareService;

import java.util.List;

@RestController
@RequestMapping("/fileCompare")
public class FileCompareController {
    private final FileCompareService fileCompareService;
    public FileCompareController(FileCompareService fileCompareService) {
        this.fileCompareService = fileCompareService;
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
}
