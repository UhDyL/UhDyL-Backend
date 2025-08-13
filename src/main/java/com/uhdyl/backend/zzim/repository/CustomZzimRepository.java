package com.uhdyl.backend.zzim.repository;

import com.uhdyl.backend.global.response.GlobalPageResponse;
import com.uhdyl.backend.zzim.dto.response.ZzimResponse;
import org.springframework.data.domain.Pageable;

public interface CustomZzimRepository {
    GlobalPageResponse<ZzimResponse> findByUser(Long userId, Pageable pageable);
}
