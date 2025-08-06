package com.uhdyl.backend.user.service;

import com.uhdyl.backend.global.exception.BusinessException;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.global.jwt.JwtHandler;
import com.uhdyl.backend.global.jwt.JwtUserClaim;
import com.uhdyl.backend.token.domain.Token;
import com.uhdyl.backend.token.repository.RefreshTokenRepository;
import com.uhdyl.backend.user.domain.User;
import com.uhdyl.backend.user.domain.UserRole;
import com.uhdyl.backend.user.dto.request.UserNicknameUpdateRequest;
import com.uhdyl.backend.user.dto.request.UserProfileUpdateRequest;
import com.uhdyl.backend.user.dto.response.UserProfileResponse;
import com.uhdyl.backend.user.repository.UserRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtHandler jwtHandler;

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

    @Transactional
    public void updateNickname(Long userId, UserNicknameUpdateRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND));

        if(userRepository.existsByNickname(request.nickname()))
            throw new BusinessException(ExceptionType.USER_NICKNAME_DUPLICATED);

        user.updateNickname(request.nickname());
    }

    public UserProfileResponse getProfile(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND));

        return UserProfileResponse.to(user);
    }


    @Transactional
    public void updateProfile(Long userId, UserProfileUpdateRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND));

        if(userRepository.existsByNickname(request.nickname().get()))
            throw new BusinessException(ExceptionType.USER_NICKNAME_DUPLICATED);

        user.updateProfile(request.profileImageUrl(), request.nickname());
    }

    @Transactional
    public Token completeRegistration(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND));

        if(!user.isBBatRegistered())
            throw new BusinessException(ExceptionType.BBAT_NOT_UPDATED);

        user.updateUserToFarmer();
        JwtUserClaim claim = new JwtUserClaim(userId, user.getRole());
        return jwtHandler.createTokens(claim);
    }
}
