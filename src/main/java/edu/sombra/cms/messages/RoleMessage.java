package edu.sombra.cms.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RoleMessage implements ThrowMessage {

    NOT_FOUND("Role not found", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus status;

}
