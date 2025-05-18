package os.automation_check_file.service;

import jakarta.annotation.PostConstruct;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import os.automation_check_file.dto.CellInfo;
import os.automation_check_file.dto.MismatchRecord;
import os.automation_check_file.dto.ReferenceLineValues;
import os.automation_check_file.utils.Constant;
import os.automation_check_file.utils.FileHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class XslFileCompareService implements FileCompareService {
    private final FileHelper fileHelper;
//    private final SegmentRepository segmentRepository;
    private final Map<String, Integer> COLUMN_MAPPINGS_REF = new HashMap<>();
    private final Map<String, Integer> COLUMN_MAPPINGS = new HashMap<>();
    private final List<String> segmentNames = List.of(
            "ORACLE-SEG01-SOCIETE", "ORACLE-SEG02-ETABLISSEMENT", "ORACLE-SEG03-COMPTE",
            "ORACLE-SEG04-TIERS-CONTREPARTI", "ORACLE-SEG05-PORTEFEUILLE", "ORACLE-SEG06-CENTRE-COUT",
            "ORACLE-SEG07-PROJET", "ORACLE-SEG08-LOCAL-IMMEUBLE", "ORACLE-SEG09-CODE-TAXE",
            "ORACLE-SEG10-TRAITE-AUTRE-TIER", "ORACLE-SEG11-TYPE-SUPPORT", "ORACLE-SEG12-PRODUIT",
            "ORACLE-SEG13-RISQUE", "ORACLE-SEG14-CAT-MIN", "ORACLE-SEG15-LOBS2",
            "ORACLE-SEG16-RACHETABLE", "ORACLE-SEG17-EXERC-SURVENANCE", "ORACLE-SEG18-ZONE-GEOGRAPHIQUE",
            "ORACLE-SEG19-MODE-GESTION", "ORACLE-SEG20-CANAL-DISTRIB", "ORACLE-SEGMENT21",
            "ORACLE-SEGMENT22", "ORACLE-SEGMENT23", "ORACLE-SEGMENT24", "ORACLE-SEGMENT25"
    );

    @Value("${dictionary_path}")
    private String DICTIONARY_PATH;

    @Value("${ref_dictionary_path}")
    private String REF_DICTIONARY_PATH;

    public XslFileCompareService(FileHelper fileHelper) {
        this.fileHelper = fileHelper;
//        this.segmentRepository = segmentRepository;
    }

    private void validateBijectiveControl(Row row, int rowIndex, List<MismatchRecord> mismatchRecords) {
        String segment17Value = getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG17-EXERC-SURVENANCE"));
        String attrib14Value = getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-ATTRIB-14"));

//        Integer segment17Column = COLUMN_MAPPINGS.get("ORACLE-SEG17-EXERC-SURVENANCE");
        Integer attrib14Column = COLUMN_MAPPINGS.get("ORACLE-ATTRIB-14");

        if (!segment17Value.isEmpty() && attrib14Value.isEmpty()) {
            MismatchRecord mismatchRecord = MismatchRecord.builder()
                    .rowNumber(rowIndex)
                    .columnNumber(attrib14Column )
                    .headerValue("ORACLE-ATTRIB-14")
                    .actualValue( attrib14Value )
                    .expectedValue(Set.of("ORACLE-ATTRIB-14 fields must be filled "))
                    .build();
            mismatchRecords.add(mismatchRecord);
        }
    }

    private void validateAndGetReconciliationReference(Row row, int rowIndex, List<MismatchRecord> mismatchRecords) {
        String segment03Value = getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG03-COMPTE"));
        String reconciliationReference = getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-RECONCIL-REFERENCE"));
        Integer columnNumber = COLUMN_MAPPINGS.get("ORACLE-RECONCIL-REFERENCE");
        if (columnNumber == null) {
            System.err.println("Column mapping for 'ORACLE-RECONCIL-REFERENCE' is missing.");
//            return reconciliationReference; // Return the value even if the column mapping is missing
        }

        if (segment03Value.endsWith("10") && reconciliationReference.isEmpty()) {
            MismatchRecord mismatchRecord = MismatchRecord.builder()
                    .rowNumber(rowIndex)
                    .columnNumber(columnNumber)
                    .headerValue("ORACLE-RECONCIL-REFERENCE")
                    .actualValue("")
                    .expectedValue(Set.of("Ce champ ne dois etre pas nul si le segment 03 ce termine par 10"))
                    .build();
            mismatchRecords.add(mismatchRecord);
        }
    }


    private void validateSegmentsNotNull(Row row, int rowIndex, List<MismatchRecord> mismatchRecords) {
        // List of segment names to validate

        String segmentReference = getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEGMENT21"));

        for (String segmentName : segmentNames) {
            String segmentValue = getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get(segmentName));
            Integer columnNumber = COLUMN_MAPPINGS.get(segmentName);

            if (!segmentReference.isEmpty() && segmentValue.isEmpty()) {
                MismatchRecord mismatchRecord = MismatchRecord.builder()
                        .rowNumber(rowIndex)
                        .columnNumber(columnNumber)
                        .headerValue(segmentName)
                        .actualValue("")
                        .expectedValue(Set.of("Ce champ ne dois etre pas nul"))
                        .build();
                mismatchRecords.add(mismatchRecord);
            }
        }
    }
//    private ConditionReferenceLineValues extractConditionReferenceLineValues(Row row) {
//        ConditionReferenceLineValues conditionValues = new ConditionReferenceLineValues();
//
//        conditionValues.setOracleReconciliationReference(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-RECONCIL-REFERENCE"))
//        );
//        conditionValues.setOracleSegment21(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG21"))
//        );
//        conditionValues.setOracleSegment22(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG22"))
//        );
//        conditionValues.setOracleSegment23(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG23"))
//        );
//        conditionValues.setOracleSegment24(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG24"))
//        );
//        conditionValues.setOracleSegment25(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG25"))
//        );
//        conditionValues.setOracleAttributeNr09(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-ATTRIBUTE-NR-09"))
//        );
//        conditionValues.setOracleAttributeNr07(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-ATTRIBUTE-NR-07"))
//        );
//        conditionValues.setOracleAttributeNr08(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-ATTRIBUTE-NR-08"))
//        );
//        conditionValues.setOracleAttributeNr01(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-ATTRIBUTE-NR-01"))
//        );
//        conditionValues.setOracleAttributeNr02(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-ATTRIBUTE-NR-02"))
//        );
//        conditionValues.setOracleAttributeNr05(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-ATTRIBUTE-NR-05"))
//        );
//        conditionValues.setOracleAttributeNr06(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-ATTRIBUTE-NR-06"))
//        );
//        conditionValues.setOracleAttributeNr03(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-ATTRIBUTE-NR-03"))
//        );
//        conditionValues.setOracleAttributeNr04(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-ATTRIBUTE-NR-04"))
//        );
//        conditionValues.setOracleAttributeNr10(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-ATTRIBUTE-NR-10"))
//        );
//        conditionValues.setOracleCreationDate(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-CREATION-DATE"))
//        );
//        conditionValues.setOracleCurrencyConversionDate(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-CURRENCY-CONV-DATE"))
//        );
//        conditionValues.setOracleAccountingDate(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-ACCOUNTING-DATE"))
//        );
//        conditionValues.setOracleAttributeDate01(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-ATTRIBUTE-DATE-01"))
//        );
//        conditionValues.setOracleAttributeDate02(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-ATTRIBUTE-DATE-02"))
//        );
//        conditionValues.setOracleAttributeDate03(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-ATTRIBUTE-DATE-03"))
//        );
//        conditionValues.setOracleAttributeDate04(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-ATTRIBUTE-DATE-04"))
//        );
//        conditionValues.setOracleAttributeDate05(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-ATTRIBUTE-DATE-05"))
//        );
//        conditionValues.setOracleAttributeDate06(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-ATTRIBUTE-DATE-06"))
//        );
//        conditionValues.setOracleAttributeDate07(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-ATTRIBUTE-DATE-07"))
//        );
//        conditionValues.setOracleAttributeDate08(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-ATTRIBUTE-DATE-08"))
//        );
//        conditionValues.setOracleAttributeDate09(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-ATTRIBUTE-DATE-09"))
//        );
//        conditionValues.setOracleAttributeDate10(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-ATTRIBUTE-DATE-10"))
//        );
//        conditionValues.setOracleGlobalAttributeDate01(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-GLOB-ATTRIB-DATE-01"))
//        );
//        conditionValues.setOracleGlobalAttributeDate02(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-GLOB-ATTRIB-DATE-02"))
//        );
//        conditionValues.setOracleGlobalAttributeDate03(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-GLOB-ATTRIB-DATE-03"))
//        );
//        conditionValues.setOracleGlobalAttributeDate04(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-GLOB-ATTRIB-DATE-04"))
//        );
//        conditionValues.setOracleGlobalAttributeDate05(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-GLOB-ATTRIB-DATE-05"))
//        );
//        conditionValues.setOracleCurrencyConversionType(
//                getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-CURRENCY-CONVERSION-TYPE"))
//        );
//
//        return conditionValues;
//    }

    @Override
    public List<MismatchRecord> compareFiles(MultipartFile referenceFile, MultipartFile inputFile) throws IOException {
        this.fetchColumnFromDictionary();
        List<ReferenceLineValues> referenceData = loadReferenceDataAsObjects(referenceFile);
        List<MismatchRecord> mismatchRecords = new ArrayList<>();

        try (Workbook inputWorkbook = new XSSFWorkbook(inputFile.getInputStream())) {
            Sheet inputSheet = inputWorkbook.getSheetAt(0);

            for (int rowIndex = 2; rowIndex <= inputSheet.getLastRowNum(); rowIndex++) {
                Row row = inputSheet.getRow(rowIndex);
                if (row == null) continue;
                validateAndGetReconciliationReference(row, rowIndex + 1, mismatchRecords);
                validateBijectiveControl(row, rowIndex + 1, mismatchRecords);
                validateSegmentsNotNull(row, rowIndex + 1, mismatchRecords);
                String segment03Value = getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG03-COMPTE"));
                if (segment03Value.isEmpty()) {
//                    errors.add(String.format("Ligne %d : Valeur segment03 manquante", rowIndex + 1));
                    continue;
                }

                List<ReferenceLineValues> matchingReferences = getPossibilitiesForSegment03(segment03Value, referenceData);
                if (matchingReferences.isEmpty()) {
//                    errors.add(String.format("Ligne %d : Aucune référence trouvée pour segment03=%s", rowIndex + 1, segment03Value));
                    continue;
                }

                mismatchRecords.addAll(checkRowValuesAgainstReferences(row, matchingReferences, rowIndex + 1));
            }
        }
        return mismatchRecords;
    }

    private String getCellValueFromRowOrEmpty(Row row, Integer columnIndex) {
        if (columnIndex == null || row == null) {
            return "";
        }

        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield new SimpleDateFormat("dd/MM/yyyy").format(cell.getDateCellValue());
                }
                double numericValue = cell.getNumericCellValue();
                // Check if the value is an integer
                yield (numericValue == Math.floor(numericValue))
                        ? String.format("%.0f", numericValue)
                        : BigDecimal.valueOf(numericValue).toPlainString();
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    try {
                        double formulaValue = cell.getNumericCellValue();
                        yield (formulaValue == Math.floor(formulaValue))
                                ? String.format("%.0f", formulaValue)
                                : BigDecimal.valueOf(formulaValue).toPlainString();
                    } catch (IllegalStateException ex) {
                        yield "";
                    }
                }
            }
            default -> "";
        };
    }

    private List<ReferenceLineValues> loadReferenceDataAsObjects(MultipartFile referenceFile) throws IOException {
        List<ReferenceLineValues> referenceData = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(referenceFile.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = 22; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;
                ReferenceLineValues lineValues = ReferenceLineValues.builder()
                        .segment01(getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS_REF.get("ORACLE-SEGMENT01")))
                        .segment02(getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS_REF.get("ORACLE-SEGMENT02")))
                        .segment03(getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS_REF.get("ORACLE-SEGMENT03")))
                        .segment04(getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS_REF.get("ORACLE-SEGMENT04")))
                        .segment05(getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS_REF.get("ORACLE-SEGMENT05")))
                        .segment06(getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS_REF.get("ORACLE-SEGMENT06")))
                        .segment07(getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS_REF.get("ORACLE-SEGMENT07")))
                        .segment08(getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS_REF.get("ORACLE-SEGMENT08")))
                        .segment09(getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS_REF.get("ORACLE-SEGMENT09")))
                        .segment10(getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS_REF.get("ORACLE-SEGMENT10")))
                        .segment11(getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS_REF.get("ORACLE-SEGMENT11")))
                        .segment12(getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS_REF.get("ORACLE-SEGMENT12")))
                        .segment13(getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS_REF.get("ORACLE-SEGMENT13")))
                        .segment14(getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS_REF.get("ORACLE-SEGMENT14")))
                        .segment15(getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS_REF.get("ORACLE-SEGMENT15")))
                        .segment16(getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS_REF.get("ORACLE-SEGMENT16")))
                        .segment17(getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS_REF.get("ORACLE-SEGMENT17")))
                        .segment18(getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS_REF.get("ORACLE-SEGMENT18")))
                        .segment19(getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS_REF.get("ORACLE-SEGMENT19")))
                        .segment20(getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS_REF.get("ORACLE-SEGMENT20")))
                        .statusCode(getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS_REF.get("ORACLE-STATUS-CODE")))
                        .journalSource(getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS_REF.get("ORACLE-JOURNAL-SOURCE")))
                        .journalCategory(getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS_REF.get("ORACLE-JOURNAL-CATEGORY")))
                        .actualFlag(getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS_REF.get("ORACLE-ACTUAL-FLAG")))

                        .build();
                referenceData.add(lineValues);
            }
        }

        return referenceData;
    }


    private List<MismatchRecord> checkRowValuesAgainstReferences(Row row, List<ReferenceLineValues> references, int rowNum) {
        List<String> errors = new ArrayList<>();
        Map<String, String> columnValues = new LinkedHashMap<>();
        List<MismatchRecord> mismatchRecords = new ArrayList<>();

        // Extract all values from the row
        columnValues.put("ORACLE-SEG01-SOCIETE", getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG01-SOCIETE")));
        columnValues.put("ORACLE-SEG02-ETABLISSEMENT", getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG02-ETABLISSEMENT")));
        columnValues.put("ORACLE-SEG03-COMPTE", getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG03-COMPTE")));
        columnValues.put("ORACLE-SEG04-TIERS-CONTREPARTI", getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG04-TIERS-CONTREPARTI")));
        columnValues.put("ORACLE-SEG05-PORTEFEUILLE", getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG05-PORTEFEUILLE")));
        columnValues.put("ORACLE-SEG06", getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG06")));
        columnValues.put("ORACLE-SEG07", getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG07")));
        columnValues.put("ORACLE-SEG08", getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG08")));
        columnValues.put("ORACLE-SEG09", getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG09")));
        columnValues.put("ORACLE-SEG10", getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG10")));
        columnValues.put("ORACLE-SEG11", getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG11")));
        columnValues.put("ORACLE-SEG12", getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG12")));
        columnValues.put("ORACLE-SEG13", getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG13")));
        columnValues.put("ORACLE-SEG14", getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG14")));
        columnValues.put("ORACLE-SEG15", getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG15")));
        columnValues.put("ORACLE-SEG16", getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG16")));
        columnValues.put("ORACLE-SEG17", getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG17")));
        columnValues.put("ORACLE-SEG18", getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG18")));
        columnValues.put("ORACLE-SEG19", getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG19")));
        columnValues.put("ORACLE-SEG20", getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-SEG20")));
        columnValues.put("ORACLE-STATUS-CODE", getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-STATUS-CODE")));
        columnValues.put("ORACLE-JOURNAL-SOURCE", getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-JOURNAL-SOURCE")));
        columnValues.put("ORACLE-JOURNAL-CATEGORY", getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-JOURNAL-CATEGORY")));
        columnValues.put("ORACLE-ACTUAL-FLAG", getCellValueFromRowOrEmpty(row, COLUMN_MAPPINGS.get("ORACLE-ACTUAL-FLAG")));

        for (Map.Entry<String, String> entry : columnValues.entrySet()) {
            String columnName = entry.getKey();
            String actualValue = entry.getValue();
            if (actualValue.isEmpty()) continue;

            Set<String> possibleValues = getReferenceValues(references, columnName);
            if (!possibleValues.contains(actualValue)) {
                errors.add(String.format("Ligne %d, Colonne %s : Valeur '%s' invalide. Valeurs possibles : %s",
                        rowNum, columnName, actualValue, String.join(", ", possibleValues)));
                MismatchRecord mismatchRecord = MismatchRecord.builder()
                        .columnNumber(COLUMN_MAPPINGS.get(columnName))
                        .headerValue(columnName)
                        .actualValue(actualValue)
                        .expectedValue(possibleValues)
                        .rowNumber(rowNum)
                        .build();
                mismatchRecords.add(mismatchRecord);
            }
//            System.out.println("---------");
//            System.out.println(actualValue);
//            System.out.println("---------");

        }

        return mismatchRecords;
    }

    private Set<String> getReferenceValues(List<ReferenceLineValues> references, String columnName) {
        return references.stream()
                .map(ref -> switch (columnName) {
                    case "ORACLE-SEG01-SOCIETE" -> ref.getSegment01();
                    case "ORACLE-SEG02-ETABLISSEMENT" -> ref.getSegment02();
                    case "ORACLE-SEG03-COMPTE" -> ref.getSegment03();
                    case "ORACLE-SEG04-TIERS-CONTREPARTI" -> ref.getSegment04();
                    case "ORACLE-SEG05-PORTEFEUILLE" -> ref.getSegment05();
                    case "ORACLE-SEG06" -> ref.getSegment06();
                    case "ORACLE-SEG07" -> ref.getSegment07();
                    case "ORACLE-SEG08" -> ref.getSegment08();
                    case "ORACLE-SEG09" -> ref.getSegment09();
                    case "ORACLE-SEG10" -> ref.getSegment10();
                    case "ORACLE-SEG11" -> ref.getSegment11();
                    case "ORACLE-SEG12" -> ref.getSegment12();
                    case "ORACLE-SEG13" -> ref.getSegment13();
                    case "ORACLE-SEG14" -> ref.getSegment14();
                    case "ORACLE-SEG15" -> ref.getSegment15();
                    case "ORACLE-SEG16" -> ref.getSegment16();
                    case "ORACLE-SEG17" -> ref.getSegment17();
                    case "ORACLE-SEG18" -> ref.getSegment18();
                    case "ORACLE-SEG19" -> ref.getSegment19();
                    case "ORACLE-SEG20" -> ref.getSegment20();
                    case "ORACLE-STATUS-CODE" -> ref.getStatusCode();
                    case "ORACLE-JOURNAL-SOURCE" -> ref.getJournalSource();
                    case "ORACLE-JOURNAL-CATEGORY" -> ref.getJournalCategory();
                    case "ORACLE-ACTUAL-FLAG" -> ref.getActualFlag();
                    default -> "";
                })
                .filter(value -> !value.isEmpty())
                .collect(Collectors.toSet());
    }


    public List<ReferenceLineValues> getPossibilitiesForSegment03(String segment03, List<ReferenceLineValues> referenceData) {
        return referenceData.stream()
                .filter(ref -> segment03.equals(ref.getSegment03()))
                .collect(Collectors.toList());
    }



    @Override
    public Map<String, Integer> fetchColumnFromDictionary() {
        try {
            Map<String, String> dictionary = fileHelper.readJsonFromFile(DICTIONARY_PATH, Map.class);

            // Process each entry in the dictionary
            dictionary.forEach((columnName, indexStr) -> {
                try {
                    int columnIndex = Integer.parseInt(indexStr);
                    // Only add mappings for columns we want to track
                    if (isRequiredColumn(columnName)) {
                        COLUMN_MAPPINGS.put(columnName, columnIndex);
                    }
                } catch (NumberFormatException ignored) {
                    // Skip entries where the index is not a valid number
                }
            });
            if (COLUMN_MAPPINGS.isEmpty()) {
                throw new IllegalStateException("No valid column mappings found in dictionary file");
            }
            return COLUMN_MAPPINGS;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load dictionary file", e);
        }
    }

    @PostConstruct
    public void initializeColumnMappings() {
        try {
            Map<String, String> dictionary = fileHelper.readJsonFromFile(REF_DICTIONARY_PATH, Map.class);

            // Process each entry in the dictionary
            dictionary.forEach((columnName, indexStr) -> {
                try {
                    int columnIndex = Integer.parseInt(indexStr);
                    // Only add mappings for columns we want to track
                    if (isRequiredColumn(columnName)) {
                        COLUMN_MAPPINGS_REF.put(columnName, columnIndex);
                    }
                } catch (NumberFormatException ignored) {
                    // Skip entries where the index is not a valid number
                }
            });
            if (COLUMN_MAPPINGS_REF.isEmpty()) {
                throw new IllegalStateException("No valid column mappings found in dictionary file");
            }
//            System.out.println(COLUMN_MAPPINGS_REF);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load dictionary file", e);
        }
    }

    private boolean isRequiredColumn(String columnName) {
        return columnName.startsWith("ORACLE-SEGMENT") || columnName.startsWith("ORACLE-RECONCIL") || columnName.startsWith("ORACLE-ATTRIB") || columnName.startsWith("ORACLE-SEG") || columnName.equals("ORACLE-STATUS-CODE") || columnName.equals("ORACLE-JOURNAL-SOURCE") || columnName.equals("ORACLE-JOURNAL-CATEGORY") || columnName.equals("ORACLE-ACTUAL-FLAG");
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


    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield new SimpleDateFormat("dd/MM/yyyy").format(cell.getDateCellValue());
                }
                double value = cell.getNumericCellValue();
                yield value == Math.floor(value) ? String.format("%.0f", value) : String.valueOf(value);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    try {
                        yield String.valueOf(cell.getNumericCellValue());
                    } catch (IllegalStateException ex) {
                        yield "";
                    }
                }
            }
            default -> "";
        };
    }

    @Override
    public List<CellInfo> extractHeadersFromRefDocRow20(MultipartFile file) throws IOException {
        List<CellInfo> headersXslFile = new ArrayList<>();

        // Extract headers from Excel
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(Constant.INDEX_SHEET);
            Row headerRow = sheet.getRow(Constant.REFERENCE_DOC_HEADER_ROW);
            if (headerRow != null) {
//                System.out.println(headerRow.getCell(Constant.INDEX_CELL));
                headerRow.forEach(cell -> {
                    if (cell != null) {
                        String headerValue = getCellValue(cell).trim();
                        int headerPosition = cell.getColumnIndex();
                        if (!headerValue.isEmpty()) {
                            CellInfo cellInfo = CellInfo.builder().position(headerPosition).headerValue(headerValue).build();
                            headersXslFile.add(cellInfo);
                        }
                    }
                });
            }
        }

        // Create JSON object with headers as keys
//        List<Map<String, String>> headerMap = new ArrayList<>();
        Map<String, String> cellMap = new HashMap<>();
        for (CellInfo header : headersXslFile) {
            cellMap.put(header.getHeaderValue(), String.valueOf(header.getPosition()));
        }
//        headerMap.add(cellMap);
        // Write to JSON file
        fileHelper.writeJsonToFile(cellMap, REF_DICTIONARY_PATH);

        return headersXslFile;
    }

    @Override
//    public List<CellInfo> extractHeadersFromInputFileRow(MultipartFile file) throws IOException {
//        List<CellInfo> headersXslFile = new ArrayList<>();
//
//        // Extract headers from Excel
//        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
//            Sheet sheet = workbook.getSheetAt(Constant.INDEX_SHEET);
//            Row headerRow = sheet.getRow(Constant.REFERENCE_DOC_HEADER_ROW);
//            if (headerRow != null) {
//                System.out.println(headerRow.getCell(Constant.INDEX_CELL));
//                headerRow.forEach(cell -> {
//                    if (cell != null) {
//                        String headerValue = getCellValue(cell).trim();
//                        int headerPosition = cell.getColumnIndex();
//                        if (!headerValue.isEmpty()) {
//                            CellInfo cellInfo = CellInfo.builder().position(headerPosition).headerValue(headerValue).build();
//                            headersXslFile.add(cellInfo);
//                        }
//                    }
//                });
//            }
//        }
//
//        // Create JSON object with headers as keys
//        List<Map<String, String>> headerMap = new ArrayList<>();
//        Map<String, String> cellMap = new HashMap<>();
//        for (CellInfo header : headersXslFile) {
//            cellMap.put(header.getHeaderValue(), String.valueOf(header.getPosition()));
//        }
//        headerMap.add(cellMap);
//        // Write to JSON file
//        fileHelper.writeJsonToFile(cellMap, DICTIONARY_PATH);
//
//        return headersXslFile;
//    }

    public List<CellInfo> extractHeadersFromInputFileRow(MultipartFile file) throws IOException {
        List<CellInfo> headersXslFile = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(Constant.INDEX_SHEET);

            for (Row row : sheet) {
                System.out.println("Row: " + row.getRowNum());

                for (Cell cell : row) {
                    String cellValue = getCellValue(cell).trim();
                    int colIndex = cell.getColumnIndex();

                    System.out.println(" - Col " + colIndex + ": " + cellValue);

                    // Only for header row
                    if (row.getRowNum() == Constant.INPUT_DOC_HEADER_ROW && !cellValue.isEmpty()) {
                        CellInfo cellInfo = CellInfo.builder().position(colIndex).headerValue(cellValue).build();
                        headersXslFile.add(cellInfo);
                    }
                }
            }
        }

        // Convert headers to a JSON file
        Map<String, String> cellMap = new HashMap<>();
        for (CellInfo header : headersXslFile) {
            cellMap.put(header.getHeaderValue(), String.valueOf(header.getPosition()));
        }

        fileHelper.writeJsonToFile(cellMap, DICTIONARY_PATH);

        return headersXslFile;
    }


}
