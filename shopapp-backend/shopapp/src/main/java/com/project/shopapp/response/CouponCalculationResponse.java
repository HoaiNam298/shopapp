package com.project.shopapp.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponCalculationResponse {

    @JsonProperty("result")
    private Double result;

    @JsonProperty("error_message")
    private String errorMessage;
}
