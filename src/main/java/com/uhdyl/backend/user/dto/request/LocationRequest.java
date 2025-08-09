package com.uhdyl.backend.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record LocationRequest(
        @NotNull @JsonProperty("location_x") BigDecimal locationX,
        @NotNull @JsonProperty("location_y") BigDecimal locationY
){}