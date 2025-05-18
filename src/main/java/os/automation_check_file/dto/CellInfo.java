package os.automation_check_file.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class CellInfo {
    private String headerValue;
    private int position;
}
