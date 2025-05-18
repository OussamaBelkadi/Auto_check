package os.automation_check_file.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import os.automation_check_file.dto.FileComparisonResult;
import os.automation_check_file.dto.MismatchRecord;
import os.automation_check_file.utils.Constant;
import os.automation_check_file.utils.FileHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class ExcelComparisonService {
    @Value("${ref_dictionary_path}")
    private String DICTIONARY_PATH;

    private final FileHelper fileHelper;

    public ExcelComparisonService(FileHelper fileHelper) {
        this.fileHelper = fileHelper;
    }

//    public FileComparisonResult compareFiles(MultipartFile referenceFile, MultipartFile inputFile) throws IOException {
//        List<MismatchRecord> mismatches = new ArrayList<>();
//
//        // Load title mappings from dictionary
//        Map<String, String> titleMappings = fileHelper.readJsonFromFile(DICTIONARY_PATH, Map.class);
//
//        try (Workbook referenceWorkbook = new XSSFWorkbook(referenceFile.getInputStream());
//             Workbook inputWorkbook = new XSSFWorkbook(inputFile.getInputStream())) {
//
//            Sheet referenceSheet = referenceWorkbook.getSheetAt(0);
//            Sheet inputSheet = inputWorkbook.getSheetAt(0);
//
//            // Get reference headers and values
//            Map<String, String> referenceValues = extractReferenceValues(referenceSheet);
//            Map<String, Integer> inputHeaders = extractInputHeaders(inputSheet);
//
//            // Compare values
//            for (Map.Entry<String, String> entry : referenceValues.entrySet()) {
//                String referenceTitle = entry.getKey();
//                String referenceValue = entry.getValue();
//
//                // Skip if reference value is empty
//                if (referenceValue.trim().isEmpty()) {
//                    continue;
//                }
//
//                // Get corresponding input column title using mapping
//                String inputTitle = titleMappings.get(referenceTitle);
//                if (inputTitle == null || !inputHeaders.containsKey(inputTitle)) {
//                    continue;
//                }
//
//                // Get column index in input file
//                int inputColumnIndex = inputHeaders.get(inputTitle);
//
//                // Compare values in input file
//                compareValues(inputSheet, inputColumnIndex, referenceValue, inputTitle, mismatches);
//            }
//        }
//
//        boolean isValid = mismatches.isEmpty();
//        String message = isValid ? "No mismatches found" : "Found " + mismatches.size() + " mismatches";
//
//        return new FileComparisonResult(isValid, mismatches, message);
//    }

    private Map<String, String> extractReferenceValues(Sheet sheet) {
        Map<String, String> values = new HashMap<>();
        Row headerRow = sheet.getRow(Constant.INPUT_HEADER_ROW);
        Row valueRow = sheet.getRow(Constant.REFERENCE_VALUE_ROW);

        if (headerRow != null && valueRow != null) {
            for (Cell headerCell : headerRow) {
                if (headerCell != null) {
                    String header = getCellValue(headerCell).trim();
                    if (!header.isEmpty()) {
                        Cell valueCell = valueRow.getCell(headerCell.getColumnIndex());
                        String value = valueCell != null ? getCellValue(valueCell).trim() : "";
                        values.put(header, value);
                    }
                }
            }
        }
        return values;
    }

    private Map<String, Integer> extractInputHeaders(Sheet sheet) {
        Map<String, Integer> headers = new HashMap<>();
        Row headerRow = sheet.getRow(Constant.INPUT_HEADER_ROW);

        if (headerRow != null) {
            headerRow.forEach(cell -> {
                if (cell != null) {
                    String header = getCellValue(cell).trim();
                    if (!header.isEmpty()) {
                        headers.put(header, cell.getColumnIndex());
                    }
                }
            });
        }
        return headers;
    }

//    private void compareValues(Sheet inputSheet, int columnIndex, String referenceValue,
//                               String columnName, List<MismatchRecord> mismatches) {
//        for (int rowNum = Constant.INPUT_START_ROW; rowNum <= inputSheet.getLastRowNum(); rowNum++) {
//            Row row = inputSheet.getRow(rowNum);
//            if (row != null) {
//                Cell cell = row.getCell(columnIndex);
//                if (cell != null) {
//                    String inputValue = getCellValue(cell).trim();
//                    if (!inputValue.isEmpty() && !inputValue.equals(referenceValue)) {
//                        mismatches.add(new MismatchRecord(
//                                formatReferenceValue(referenceValue),
//                                formatActualValue(inputValue),
//                                String.format("Value mismatch in column %s", columnName)
//                        ));
//                    }
//                }
//            }
//        }
//    }
    private String formatReferenceValue(String value) {
        return value != null ? value.trim() : "";
    }

    private String formatActualValue(String value) {
        return value != null ? value.trim() : "";
    }

//    public List<Cell> getColumnCells(Sheet sheet, int columnIndex) {
//        List<Cell> cells = new ArrayList<>();
//        for (int rowNum = INPUT_START_ROW; rowNum <= sheet.getLastRowNum(); rowNum++) {
//            Row row = sheet.getRow(rowNum);
//            if (row != null) {
//                Cell cell = row.getCell(columnIndex);
//                if (cell != null) {
//                    cells.add(cell);
//                }
//            }
//        }
//    }

    private String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            default:
                return "";
        }
    }
}
