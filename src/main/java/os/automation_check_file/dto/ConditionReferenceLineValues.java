package os.automation_check_file.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class ConditionReferenceLineValues {

        private String oracleReconciliationReference;
        private String oracleSegment21;
        private String oracleSegment22;
        private String oracleSegment23;
        private String oracleSegment24;
        private String oracleSegment25;

        private String oracleAttributeNr09;
        private String oracleAttributeNr07;
        private String oracleAttributeNr08;
        private String oracleAttributeNr01;
        private String oracleAttributeNr02;
        private String oracleAttributeNr05;
        private String oracleAttributeNr06;
        private String oracleAttributeNr03;
        private String oracleAttributeNr04;
        private String oracleAttributeNr10;

        private String oracleCreationDate;
        private String oracleCurrencyConversionDate;
        private String oracleAccountingDate;

        private String oracleAttributeDate01;
        private String oracleAttributeDate02;
        private String oracleAttributeDate03;
        private String oracleAttributeDate04;
        private String oracleAttributeDate05;
        private String oracleAttributeDate06;
        private String oracleAttributeDate07;
        private String oracleAttributeDate08;
        private String oracleAttributeDate09;
        private String oracleAttributeDate10;

        private String oracleGlobalAttributeDate01;
        private String oracleGlobalAttributeDate02;
        private String oracleGlobalAttributeDate03;
        private String oracleGlobalAttributeDate04;
        private String oracleGlobalAttributeDate05;

        private String oracleCurrencyConversionType;
    }
