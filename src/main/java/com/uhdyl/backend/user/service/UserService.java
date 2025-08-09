package com.uhdyl.backend.user.service;


import com.uhdyl.backend.global.exception.BusinessException;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.token.repository.RefreshTokenRepository;
import com.uhdyl.backend.user.domain.User;
import com.uhdyl.backend.user.domain.UserRole;
import com.uhdyl.backend.user.dto.response.LocationResponse;
import com.uhdyl.backend.user.repository.UserRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public boolean isFarmer(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new BusinessException(ExceptionType.USER_NOT_FOUND));

        return user.getRole().equals(UserRole.FARMER);
    }

    @Transactional
    public void logout(Long userId){
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Transactional
    public void saveLocation(Long userId, BigDecimal locationX, BigDecimal locationY) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new BusinessException(ExceptionType.USER_NOT_FOUND));

        if (!isFarmer(userId)){
            throw new BusinessException(ExceptionType.USER_NOT_FARMER);
        }

        user.updateLocation(locationX, locationY);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public LocationResponse getLocation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND));

        if (!isFarmer(userId)){
            throw new BusinessException(ExceptionType.USER_NOT_FARMER);
        }

        if (user.getLocationX() == null || user.getLocationY() == null) {
            throw new BusinessException(ExceptionType.LOCATION_NOT_FOUND);
        }

        return new LocationResponse(user.getLocationX(), user.getLocationY());
    }
}
