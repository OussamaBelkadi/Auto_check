package os.automation_check_file.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;
@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Segment implements Serializable {
    public enum Erp {Oracle, Sap}
    @Id
    @GeneratedValue
    private UUID id;
    private int segmentRef03;

    @Enumerated(EnumType.STRING)
    private Erp erp;
}