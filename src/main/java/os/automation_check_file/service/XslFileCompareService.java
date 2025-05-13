package os.automation_check_file.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import os.automation_check_file.entity.Segment;
import os.automation_check_file.repository.SegmentRepository;
import os.automation_check_file.utils.FileHelper;

import java.io.IOException;
import java.util.*;

@Service
public class XslFileCompareService implements FileCompareService {
    private final FileHelper fileHelper;
    private final SegmentRepository segmentRepository;
    @Value("${dictionary}")
    private String DICTIONARY_PATH;

    public XslFileCompareService(FileHelper fileHelper, SegmentRepository segmentRepository) {
        this.fileHelper = fileHelper;
        this.segmentRepository = segmentRepository;
    }

    @Override
    public Set<String> extractSegment(MultipartFile file) throws IOException {
        Set<String> segmentRef = new HashSet<>();
        Set<Segment> segments = new HashSet<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // Start from first row to last row
            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {

                Row row = sheet.getRow(rowIndex);
                System.out.println(extractAllFileContents(file));
                if (row != null) {
                    Cell cell = row.getCell(35); // Get cell from column 35
                    if (cell != null) {
                        String value = getCellValueSegment(cell).trim();
                        if (!value.isEmpty()) {
                            int segmentRef03 = Integer.parseInt(value);
                            if (!segmentRepository.existsSegmentBySegmentRef03(segmentRef03)) {
                                segments.add(Segment.builder().erp(Segment.Erp.Oracle).segmentRef03(Integer.parseInt(value)).build());
                            }
                            segmentRef.add(value);
                        }
                    }
                }
            }
        }
        segmentRepository.saveAll(segments);
        return segmentRef;
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

//    private String getRowContent(Row row) {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < row.getLastCellNum(); i++) {
//            Cell cell = row.getCell(i);
//            if (cell != null) {
//                sb.append(getCellValue(cell).trim());
//            }
//            sb.append(" ");
//        }
//        return sb.toString().trim();
//    }

    private List<List<String>> extractAllFileContents(MultipartFile file) throws IOException {
        List<List<String>> allContents = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            int lengthSheet = workbook.getNumberOfSheets();
            for (int i = 0; i < lengthSheet; i++) {
                Sheet sheet = workbook.getSheetAt(i);

                // Iterate through all rows
                for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    if (row != null) {
                        List<String> rowContents = getRowContentTest(row);
                        allContents.add(rowContents);
                    }
                }
            }
            System.out.println(workbook.getNumberOfSheets() + " sheets in workbook");

        }
        return allContents;
    }

    private List<String> getRowContentTest(Row row) {
        List<String> rowContents = new ArrayList<>();
        int lastCell = row.getLastCellNum();

        for (int cellNum = 0; cellNum < lastCell; cellNum++) {
            Cell cell = row.getCell(cellNum, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null) {
                rowContents.add(getCellValueTest(cell));
            } else {
                rowContents.add(""); // Add empty string for null cells
            }
        }
        return rowContents;
    }
    private String getCellValueTest(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toString();
                }
                double value = cell.getNumericCellValue();
                yield value == Math.floor(value) ?
                        String.format("%.0f", value) :
                        String.valueOf(value);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue();
                } catch (Exception e) {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            default -> "";
        };
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
