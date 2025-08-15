package com.uhdyl.backend.zzim.repository;

import com.uhdyl.backend.global.response.GlobalPageResponse;
import com.uhdyl.backend.zzim.dto.response.ZzimResponse;
import com.uhdyl.backend.zzim.dto.response.ZzimToggleResponse;
import org.springframework.data.domain.Pageable;

public interface CustomZzimRepository {
    GlobalPageResponse<ZzimResponse> findAllByUser(Long userId, Pageable pageable);
    ZzimResponse findZzim(Long userId, Long productId);
}
