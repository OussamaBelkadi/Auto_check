//package os.automation_check_file.entity;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.Id;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@Data
//public class BaseFile {
//    @Id
//    @GeneratedValue
//    private UUID id;
//    @Column(updatable = false)
//    private LocalDateTime createTime;
//    @Column(insertable = false)
//    private LocalDateTime updateTime;
//
//    private String fileName;
//    private String filePath;
//}
