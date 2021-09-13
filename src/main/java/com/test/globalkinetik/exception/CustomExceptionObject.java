package com.test.globalkinetik.exception;

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
public class CustomExceptionObject {
    private String timestamp;
    private int status;
    private String error;
    private String path;
}
