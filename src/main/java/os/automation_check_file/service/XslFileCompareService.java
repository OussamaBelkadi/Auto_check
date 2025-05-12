package os.automation_check_file.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import os.automation_check_file.utils.FileHelper;

import java.io.IOException;
import java.util.*;

@Service
public class XslFileCompareService implements FileCompareService {
    @Value("${dictionary}")
    private String DICTIONARY_PATH;

    private final FileHelper fileHelper;

    public XslFileCompareService(FileHelper fileHelper) {
        this.fileHelper = fileHelper;
    }

    @Override
    public Set<String> extractSegment(MultipartFile file) throws IOException {
        Set<String> segment = new HashSet<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // Start from first row to last row
            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    Cell cell = row.getCell(35); // Get cell from column 35
                    if (cell != null) {
                        String value = getCellValueSegment(cell).trim();
                        if (!value.isEmpty()) {
                            segment.add(value);
                        }
                    }
                }
            }
        }

        return segment;
    }

    private String getCellValueSegment(Cell cell) {
        if (Objects.requireNonNull(cell.getCellType()) == CellType.NUMERIC) {// Handle numeric values without scientific notation
            double numValue = cell.getNumericCellValue();
            if (numValue == Math.floor(numValue)) {
                return String.format("%.0f", numValue);
            }
            return String.valueOf(numValue);
        }
        return "";
    }

    @Override
    public List<String> extractHeadersFromRow20(MultipartFile file) throws IOException {
        List<String> headersXslFile = new ArrayList<>();

        // Extract headers from Excel
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(18);
            if (headerRow != null) {
                headerRow.forEach(cell -> {
                    System.out.println(cell);
                    if (cell != null) {
                        String headerValue = getCellValue(cell).trim();
                        System.out.println(cell.getColumnIndex() + ":  " + headerValue);
                        if (!headerValue.isEmpty()) {
                            headersXslFile.add(headerValue);
                        }
                    }
                });
            }
        }

        // Create JSON object with headers as keys
        Map<String, String> headerMap = new HashMap<>();
        for (String header : headersXslFile) {
            headerMap.put(header, ""); // Empty value for now
        }

        // Write to JSON file
        fileHelper.writeJsonToFile(headerMap, DICTIONARY_PATH);

        return headersXslFile;
    }

    private String getCellValue(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            default -> "";
        };
    }
}
