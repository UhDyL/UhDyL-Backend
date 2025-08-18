package com.uhdyl.backend.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record LocationResponse(
        @NotNull @JsonProperty("location_x") BigDecimal locationX,
        @NotNull @JsonProperty("location_y") BigDecimal locationY
){}
