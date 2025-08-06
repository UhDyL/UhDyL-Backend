package com.uhdyl.backend.user.dto.request;

import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class LocationRequest {
    private BigDecimal location_x;
    private BigDecimal location_y;
}
