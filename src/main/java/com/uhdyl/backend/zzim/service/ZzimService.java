package com.uhdyl.backend.zzim.service;

import com.uhdyl.backend.global.exception.BusinessException;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.global.response.GlobalPageResponse;
import com.uhdyl.backend.product.domain.Product;
import com.uhdyl.backend.product.repository.ProductRepository;
import com.uhdyl.backend.user.domain.User;
import com.uhdyl.backend.user.repository.UserRepository;
import com.uhdyl.backend.zzim.domain.Zzim;
import com.uhdyl.backend.zzim.dto.response.ZzimResponse;
import com.uhdyl.backend.zzim.repository.ZzimRepository;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public void createZzim(Long userId, Long productId){

        if(!userRepository.existsById(userId))
            throw new BusinessException(ExceptionType.USER_NOT_FOUND);
        User userProxy = userRepository.getReferenceById(userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new BusinessException(ExceptionType.PRODUCT_NOT_FOUND));

        if(zzimRepository.existsByUser_IdAndProduct_Id(userId, productId))
            throw new BusinessException(ExceptionType.ALREADY_ZZIMED);

        Zzim zzim = Zzim.builder()
                .user(userProxy)
                .product(product)
                .build();
        zzimRepository.save(zzim);
    }

    public GlobalPageResponse<ZzimResponse> getZzims(Long userId, Pageable pageable){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new BusinessException(ExceptionType.USER_NOT_FOUND));

        return zzimRepository.findByUser(userId, pageable);
    }

    @Transactional
    public void deleteZzim(Long userId, Long zzimId){
        if(!userRepository.existsById(userId))
            throw new BusinessException(ExceptionType.USER_NOT_FOUND);

        Zzim zzim = zzimRepository.findById(zzimId)
                .orElseThrow(()-> new BusinessException(ExceptionType.ZZIM_NOT_FOUND));
        if(!zzim.getUser().getId().equals(userId))
            throw new BusinessException(ExceptionType.ZZIM_ACCESS_DENIED);

        zzimRepository.deleteById(zzimId);
    }
}
