package com.uhdyl.backend.product.service;

import com.uhdyl.backend.global.exception.BusinessException;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.image.domain.Image;
import com.uhdyl.backend.product.domain.Product;
import com.uhdyl.backend.product.dto.request.ProductCreateRequest;
import com.uhdyl.backend.product.dto.response.MyProductListResponse;
import com.uhdyl.backend.product.dto.response.ProductCreateResponse;
import com.uhdyl.backend.product.repository.ProductRepository;
import com.uhdyl.backend.user.domain.User;
import com.uhdyl.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AiContentService aiContentService;

    @Transactional
    public ProductCreateResponse createProduct(Long userId, ProductCreateRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND));

        var aiResult = aiContentService.generateContent(
                request.breed(),
                request.price(),
                request.tone(),
                request.images());

        String title = aiResult.title();
        String description = aiResult.description();

        Product product = Product.builder()
                .name(request.breed())
                .title(title)
                .description(description)
                .isSale(true) // true = 거래 가능
                .price(String.valueOf(request.price()))
                .category(request.category())
                .user(user)
                .build();

        long order = 0;
        for (String imageUrl : request.images()){
            product.addImage(new Image(imageUrl, order++, null));
        }

        Product saved = productRepository.save(product);

        return new ProductCreateResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getPrice(),
                saved.getImages().stream().map(Image::getImageUrl).toList(),
                saved.isSale()
        );
    }

    @Transactional
    public void deleteProduct(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ExceptionType.PRODUCT_NOT_FOUND));

        if (!product.getUser().getId().equals(userId)) {
            throw new BusinessException(ExceptionType.CANT_DELETE_PRODUCT);
        }

        user.removeProduct(product);
    }

    @Transactional(readOnly = true)
    public MyProductListResponse getMyProducts(Long userId, Pageable pageable){
        if(!userRepository.existsById(userId))
            throw new BusinessException(ExceptionType.USER_NOT_FOUND);

        return productRepository.getMyProducts(userId, pageable);
    }
}
