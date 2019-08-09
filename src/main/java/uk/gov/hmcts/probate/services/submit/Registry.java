package uk.gov.hmcts.probate.services.submit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Registry {

    private String name;

    private Long id;

    private String address;

    private String email;
}
