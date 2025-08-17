package com.uhdyl.backend.product.service;

import com.uhdyl.backend.global.exception.BusinessException;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.global.response.GlobalPageResponse;
import com.uhdyl.backend.image.domain.Image;
import com.uhdyl.backend.product.domain.Category;
import com.uhdyl.backend.product.domain.Product;
import com.uhdyl.backend.product.dto.request.ProductAiGenerateRequest;
import com.uhdyl.backend.product.dto.request.ProductCreateWithAiContentRequest;
import com.uhdyl.backend.product.dto.response.AiGeneratedContentResponse;
import com.uhdyl.backend.product.dto.response.MyProductListResponse;
import com.uhdyl.backend.product.dto.response.ProductCreateResponse;
import com.uhdyl.backend.product.dto.response.ProductDetailResponse;
import com.uhdyl.backend.product.dto.response.ProductListResponse;
import com.uhdyl.backend.product.dto.response.SalesStatsResponse;
import com.uhdyl.backend.product.repository.ProductRepository;
import com.uhdyl.backend.user.domain.User;
import com.uhdyl.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AiContentService aiContentService;

    /**
     * 1단계: AI 글 생성 (트랜잭션 없음, 저장하지 않고 반환만)
     */
    public AiGeneratedContentResponse generateAiContent(Long userId, ProductAiGenerateRequest request) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(ExceptionType.USER_NOT_FOUND);
        }

        try {
            AiContentService.AiResult aiResult = aiContentService.generateContent(
                    request.breed(),
                    request.price(),
                    request.tone(),
                    request.images()
            );

            return new AiGeneratedContentResponse(
                    aiResult.title(),
                    aiResult.description(),
                    request.breed(),
                    request.price(),
                    request.images(),
                    request.categories(),
                    request.tone()
            );

        } catch (Exception e) {
            log.error("AI 콘텐츠 생성 실패 - userId: {}, breed: {}, price: {}",
                    userId, request.breed(), request.price(), e);
            throw new BusinessException(ExceptionType.AI_GENERATION_FAILED);
        }
    }

    /**
     * 2단계: AI로 생성된 글로 상품 등록 (사용자가 수정했을 수도 있음)
     */
    @Transactional
    public ProductCreateResponse createProductWithGeneratedContent(Long userId, ProductCreateWithAiContentRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND));

        if (request.title() == null || request.title().isBlank()) {
            throw new BusinessException(ExceptionType.INVALID_INPUT);
        }
        if (request.description() == null || request.description().isBlank()) {
            throw new BusinessException(ExceptionType.INVALID_INPUT);
        }

        Product product = Product.builder()
                .name(request.breed())
                .title(request.title())
                .description(request.description())
                .isSale(true)
                .price(request.price())
                .categories(request.categories())
                .user(user)
                .build();

        if (request.images() != null && !request.images().isEmpty()) {
            long order = 0;
            for (String imageUrl : request.images()) {
                if (imageUrl == null || imageUrl.isBlank()) continue;
                product.addImage(new Image(imageUrl, order++, null));
            }
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

        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    public MyProductListResponse getMyProducts(Long userId, Pageable pageable){
        if(!userRepository.existsById(userId))
            throw new BusinessException(ExceptionType.USER_NOT_FOUND);

        return productRepository.getMyProducts(userId, pageable);
    }

    public SalesStatsResponse getSalesStats(Long userId) {
        if(!userRepository.existsById(userId))
            throw new BusinessException(ExceptionType.USER_NOT_FOUND);

        return productRepository.getSalesStats(userId);
    }

    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    @Transactional
    public void completeProduct(Long userId, Long productId) {
        Product product = productRepository.findByIdAndUser_Id(productId, userId)
                .orElseThrow(() -> new BusinessException(ExceptionType.CANT_UPDATE_PRODUCT));

        if (!product.isSale()) {
            throw new BusinessException(ExceptionType.CANT_UPDATE_PRODUCT);
        }

        product.markSaleCompleted();
        productRepository.save(product);
    }

    @Recover
    public void recover(Exception e, Long userId, Long productId) {
        throw new BusinessException(ExceptionType.PRODUCT_COMPLETE_CONFLICT);
    }

    @Transactional(readOnly = true)
    public GlobalPageResponse<ProductListResponse> getProductsByCategory(Long userId, Category category, Pageable pageable){
        if(!userRepository.existsById(userId))
            throw new BusinessException(ExceptionType.USER_NOT_FOUND);

        return productRepository.getProductsByCategory(category, pageable);
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetail(Long userId, Long productId) {
        if(!userRepository.existsById(userId))
            throw new BusinessException(ExceptionType.USER_NOT_FOUND);

        return productRepository.getProductDetail(productId);
    }
}
