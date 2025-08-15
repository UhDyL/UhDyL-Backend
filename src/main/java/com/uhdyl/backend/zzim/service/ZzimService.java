package com.uhdyl.backend.zzim.service;

import com.uhdyl.backend.global.exception.BusinessException;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.global.response.GlobalPageResponse;
import com.uhdyl.backend.product.domain.Product;
import com.uhdyl.backend.product.repository.ProductRepository;
import com.uhdyl.backend.user.domain.User;
import com.uhdyl.backend.user.repository.UserRepository;
import com.uhdyl.backend.zzim.domain.Zzim;
import com.uhdyl.backend.zzim.dto.request.ZzimToggleRequest;
import com.uhdyl.backend.zzim.dto.response.ZzimResponse;
import com.uhdyl.backend.zzim.dto.response.ZzimToggleResponse;
import com.uhdyl.backend.zzim.repository.ZzimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ZzimService {

    private final ZzimRepository zzimRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public GlobalPageResponse<ZzimResponse> getZzims(Long userId, Pageable pageable){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new BusinessException(ExceptionType.USER_NOT_FOUND));

        return zzimRepository.findAllByUser(userId, pageable);
    }

    @Transactional
    public ZzimToggleResponse toggleZzim(Long userId, ZzimToggleRequest request){
        if(!userRepository.existsById(userId))
            throw new BusinessException(ExceptionType.USER_NOT_FOUND);
        if(!productRepository.existsById(request.productId()))
            throw new BusinessException(ExceptionType.PRODUCT_NOT_FOUND);

        if (zzimRepository.existsByUser_IdAndProduct_Id(userId, request.productId())){
            ZzimResponse zzimResponse = zzimRepository.findZzim(userId, request.productId());
            zzimRepository.deleteByUser_IdAndProduct_Id(userId, request.productId());
            return ZzimToggleResponse.to(zzimResponse, false);
        }

        User userProxy = userRepository.getReferenceById(userId);
        Product productProxy = productRepository.getReferenceById(request.productId());
        try {
            zzimRepository.save(Zzim.builder()
                    .user(userProxy)
                    .product(productProxy)
                    .build());
            ZzimResponse zzimResponse = zzimRepository.findZzim(userId, request.productId());
            return ZzimToggleResponse.to(zzimResponse, true);
        }
        catch (DataIntegrityViolationException e){
            throw new BusinessException(ExceptionType.ALREADY_ZZIMED);
        }
    }
}
