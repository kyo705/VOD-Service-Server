package com.ktube.vod.identification;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class RequestIdentificationDto {

    @NotEmpty
    private String identificationCode;
}
