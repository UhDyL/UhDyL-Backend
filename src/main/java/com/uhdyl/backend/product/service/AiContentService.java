package com.uhdyl.backend.product.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.uhdyl.backend.global.config.ai.AiProperties;
import com.uhdyl.backend.product.domain.Category;
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
                .map(Category::name)
                .collect(Collectors.joining(", "));

        if (request.condition() == null || request.weight() == null ||
                request.quantityPerWeight() == null || request.price() == null) {
            throw new IllegalArgumentException("필수 필드가 누락되었습니다");
        }

        String koreanPrompt = String.format("""
        You are a professional copywriter specializing in marketing agricultural products in South Korea.
        Based on the information below, generate a compelling product title and description **in Korean**.
        The product is so-called 'ugly produce', which has minor cosmetic flaws but is perfectly fine to eat. Emphasize its good value and taste.

        **Product Information:**
        - Condition: %s
        - Weight Unit: %s
        - Quantity per Unit: %s
        - Price: %d won
        - Keywords: %s
        - Desired Tone: %s

        **Constraints:**
        - Language: Korean
        - Title character limit: 20
        - Description character limit: 100
        - Output Format: A single JSON object with two keys: "title" and "description". Do not include any other text or markdown formatting.
        """,
                request.condition(),
                request.weight(),
                request.quantityPerWeight(),
                request.price(),
                categoriesString,
                toneInstruction);

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

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(responseBody);

            String responseStr = root.get("response").asText();
            JsonNode aiResponse = objectMapper.readTree(responseStr);

            String title = aiResponse.get("title").asText();
            String description = aiResponse.get("description").asText();

            return new AiResult(title, description);

        } catch (Exception e) {
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