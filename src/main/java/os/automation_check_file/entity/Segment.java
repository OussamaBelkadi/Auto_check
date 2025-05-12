package os.automation_check_file.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import os.automation_check_file.ErpEnum;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Segment {
    @Id
    @GeneratedValue
    private UUID id;
    private Integer segmentRef03;
    private ErpEnum erp;
}
