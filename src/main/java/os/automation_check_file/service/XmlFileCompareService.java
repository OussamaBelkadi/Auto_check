package os.automation_check_file.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class XmlFileCompareService implements FileCompareService{
    private static final String pathFile = "./src/main/resources/dictionary.json";


    @Override
    public List<String> extractHeadersFromRow20(MultipartFile file) throws IOException {
        List<String> headersXmlFile = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())){
            System.out.println(workbook);
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(18);
            if (headerRow != null) {
                headerRow.forEach(cell->{
                    if (cell != null) {
                        String headerValue = getCellValue(cell).trim();
                        if (!headerValue.isEmpty()) {
                            headersXmlFile.add(headerValue);
                        }
                    }
                });

            }
        }
        File fileDictionary = new File(pathFile);
        if (fileDictionary.createNewFile()) {
            FileWriter fileWriter = new FileWriter(fileDictionary);
            fileWriter.write(String.join(",", headersXmlFile));
            fileWriter.close();
        }
        return headersXmlFile;
    }

    private String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            default: return "";
        }
    }
}
