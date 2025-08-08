package com.uhdyl.backend.user.dto.response;

import java.math.BigDecimal;

public record LocationResponse(
        BigDecimal location_x,
        BigDecimal location_y
){}
