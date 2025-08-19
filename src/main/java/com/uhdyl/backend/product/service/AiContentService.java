package com.uhdyl.backend.product.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.uhdyl.backend.global.config.ai.AiProperties;
import com.uhdyl.backend.product.dto.request.ProductAiGenerateRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiContentService {

    private final RestTemplate restTemplate;
    private final AiProperties aiProperties;

    public record AiResult(String title, String description) {}
    public AiResult generateContent(ProductAiGenerateRequest request) {

        String toneInstruction = switch (request.tone()) {
            case "다정하게" -> "a friendly and caring tone";
            case "유쾌하게" -> "a witty and cheerful tone";
            case "장사꾼스럽게" -> "a savvy merchant's tone, highlighting value-for-money";
            default -> "a standard neutral tone";
        };

        String categoriesString = request.categories().stream()
                .map(Enum::name)
                .collect(Collectors.joining(", "));

        String koreanPrompt = String.format("""
        [SYSTEM]
        You are a professional copywriter in South Korea. Your specialty is writing compelling sales copy for 'ugly produce' (못난이 농산물).
        Your goal is to generate a product title and description in KOREAN.
        You must follow all instructions and output only a valid JSON object.
        
        [CONTEXT]
        The product has minor cosmetic flaws but is perfectly fresh and delicious. Your copy should emphasize its great taste and excellent value for money, turning its imperfections into a positive point.
        
        **[표현 가이드라인 (Guideline for Expression)]**
        - When describing cosmetic flaws, use natural Korean phrases.
        - GOOD examples: "못생겼지만 맛은 최고예요", "모양이 제멋대로", "개성있게 생긴", "약간의 흠집이 있지만", "정품과 맛은 똑같아요"
        - BAD examples: "주을 고려하더라도", "외관의 결함에도 불구하고" (Avoid overly literal or awkward translations)
        
        [PRODUCT INFORMATION]
        - Product Condition (includes 품종명): %s
        - Packaging Unit: %s
        - Total Price: %d KRW
        - Categories/Keywords: %s
        - Desired Tone of Voice: %s
        
        [INSTRUCTIONS]
        1.  Generate the response **in KOREAN**.
        2.  Create a catchy title (max 20 characters) that **must include the product variety name from "Product Condition" (상품의 품종명)**.
        3.  Write an appealing description (max 400 characters).
        4.  Use natural and persuasive Korean phrases suitable for selling food, based on the Guideline for Expression.
        5.  Output must be a single, raw JSON object with two keys: "title" and "description".
        6.  Do not include any other text, explanations, or markdown formatting like ```json.
        
        **[매우 중요한 규칙 (CRITICAL RULE)]**
        - **Do NOT create or use awkward, non-existent, or made-up Korean words, Stick to common and natural vocabulary.**
    """,
                request.condition(),
                request.pricePerWeight(),
                request.price(),
                categoriesString,
                toneInstruction
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<String> imageUrls = request.images();
            if (imageUrls != null && !imageUrls.isEmpty()) {
                ArrayNode base64ImagesNode = objectMapper.createArrayNode();
                for (String imageUrl : imageUrls) {
                    try {
                        String base64Image = urlToBase64(imageUrl);
                        base64ImagesNode.add(base64Image);
                    } catch (IOException e) {
                        log.warn("image URL -> base64 변환 실패: {}", imageUrl, e);
                    }
                }
                requestBody = objectMapper.createObjectNode()
                        .put("prompt", koreanPrompt)
                        .set("images", base64ImagesNode)
                        .toString();
            } else {
                requestBody = objectMapper.createObjectNode()
                        .put("prompt", koreanPrompt)
                        .toString();
            }
        } catch (Exception e) {
            throw new RuntimeException("JSON 생성 오류", e);
        }

        if (aiProperties.getUrl() == null || aiProperties.getUrl().isBlank()) {
            throw new IllegalStateException("AI 서버 URL이 설정되지 않았습니다(ai.server.url).");
        }

        String url = UriComponentsBuilder
                .fromUriString(aiProperties.getUrl())
                .pathSegment("generate")
                .build(true)
                .toUriString();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        String responseBody;
        try {
            responseBody = restTemplate.postForObject(url, entity, String.class);
        } catch (RestClientException ex) {
            throw new RuntimeException("AI 서버 호출 실패", ex);
        }

        if (responseBody == null) {
            throw new RuntimeException("AI 서버 응답이 비어 있습니다");
        }
        log.info("AI raw response: {}", responseBody);

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode responseNode = root.get("response");
            if (responseNode == null || responseNode.isNull()) {
                throw new RuntimeException("AI 응답에 'response' 필드가 없습니다.");
            }
            String responseStr = responseNode.isTextual() ? responseNode.asText() : responseNode.toString();

            String cleanJsonStr = responseStr.trim();
            if (cleanJsonStr.startsWith("```json")) {
                cleanJsonStr = cleanJsonStr.substring(7);
                if (cleanJsonStr.endsWith("```")) {
                    cleanJsonStr = cleanJsonStr.substring(0, cleanJsonStr.length() - 3);
                }
            }
            cleanJsonStr = cleanJsonStr.trim();

            JsonNode aiResponse = objectMapper.readTree(cleanJsonStr);
            String title = aiResponse.path("title").asText(null);
            String description = aiResponse.path("description").asText(null);
            if (title == null || title.isBlank() || description == null || description.isBlank()) {
                throw new RuntimeException("AI 응답 JSON에 title/description이 없거나 비어 있습니다.");
            }

            return new AiResult(title, description);

        } catch (Exception e) {
            log.error("AI 응답 파싱 오류. Raw Response: {}", responseBody, e);
            throw new RuntimeException("AI 응답 파싱 오류", e);
        }
    }

    public String urlToBase64(String imageUrl) throws IOException {
        validateExternalImageUrl(imageUrl);

        try {
            byte[] bytes = restTemplate.getForObject(imageUrl, byte[].class);
            if (bytes == null || bytes.length == 0) {
                throw new IOException("이미지 다운로드 실패: 빈 응답");
            }
            final int maxBytes = 5 * 1024 * 1024;
            if (bytes.length > maxBytes) {
                throw new IOException("이미지 용량 초과: " + bytes.length);
            }
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IllegalArgumentException e) {
            throw new IOException("잘못된 이미지 URL", e);
        }
    }
    private void validateExternalImageUrl(String imageUrl) {
        try {
            URI uri = new URI(imageUrl);
            String host = uri.getHost();
            if (host == null) throw new IllegalArgumentException("호스트가 없는 URL");

            java.net.InetAddress addr = java.net.InetAddress.getByName(host);
            if (addr.isAnyLocalAddress() || addr.isLoopbackAddress() || addr.isSiteLocalAddress()) {
                throw new IllegalArgumentException("허용되지 않은 내부/사설 네트워크 접근");
            }

        } catch (URISyntaxException | java.net.UnknownHostException e) {
            throw new IllegalArgumentException("유효하지 않은 이미지 URL", e);
        }
    }
}