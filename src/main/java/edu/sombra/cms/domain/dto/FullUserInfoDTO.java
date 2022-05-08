package edu.sombra.cms.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FullUserInfoDTO {

    private long id;
    private String username;
    private String email;
    private String fullName;

}
