package com.test.globalkinetik.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author S'phokuhle on 9/13/2021
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "Authentication Response")
public class AuthenticationResponse {
    @ApiModelProperty(value = "User's identity")
    private String id;

    @ApiModelProperty(value = "User's authentication token")
    private String token;
}
