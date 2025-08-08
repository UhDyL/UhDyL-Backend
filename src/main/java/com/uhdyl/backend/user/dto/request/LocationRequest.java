package com.uhdyl.backend.user.dto.request;

import java.math.BigDecimal;

public record LocationRequest(
    BigDecimal location_x,
    BigDecimal location_y
){}