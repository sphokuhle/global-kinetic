package com.test.globalkinetik.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.test.globalkinetik.model.Users;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author S'phokuhle on 9/13/2021
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "User details")
public class UserDto {
    private String id;

    @ApiModelProperty(value = "Username")
    @NotNull(message = "username cannot be null")
    @NotEmpty(message = "username cannot be empty")
    private String username;

    @ApiModelProperty(value = "Phone number")
    private String phone;

    @ApiModelProperty(value = "Password")
    @NotNull(message = "password cannot be null")
    @NotEmpty(message = "password cannot be empty")
    private String password;

    public UserDto(Users user) {
        if(user.getId() != null) {
            this.id = String.valueOf(user.getId());
        }
        this.phone = user.getPhone();
    }
}
