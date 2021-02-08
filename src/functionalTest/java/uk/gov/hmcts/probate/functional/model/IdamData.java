package uk.gov.hmcts.probate.functional.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class IdamData {

    private String email;

    private String forename;

    private String surname;

    private String password;

    private String userGroup;

    private List<Role> roles;
}
