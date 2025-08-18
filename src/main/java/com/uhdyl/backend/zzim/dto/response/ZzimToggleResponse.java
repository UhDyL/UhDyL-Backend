package com.uhdyl.backend.zzim.dto.response;

public record ZzimToggleResponse(
        ZzimResponse zzim,
        boolean isZzim
) {
    public static ZzimToggleResponse to(ZzimResponse zzim, boolean isZzim){
        return new ZzimToggleResponse(zzim, isZzim);
    }
}
