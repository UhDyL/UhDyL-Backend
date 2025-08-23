package com.uhdyl.backend.product.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uhdyl.backend.global.config.ai.AiProperties;
import com.uhdyl.backend.product.dto.request.ProductAiGenerateRequest;
import com.uhdyl.backend.product.dto.openai.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiContentService {

    private final RestTemplate restTemplate;
    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

        String systemPrompt = """
        You are a professional copywriter in South Korea. Your specialty is writing compelling sales copy for 'ugly produce' (못난이 농산물).
        Your goal is to generate a product title and description in KOREAN.
        You must follow all instructions and output only a valid JSON object with two keys: "title" and "description".
        Do not include any other text, explanations, or markdown formatting like ```json.
        
        **[표현 가이드라인 (Guideline for Expression)]**
        - When describing cosmetic flaws, use natural Korean phrases.
        - GOOD examples: "못생겼지만 맛은 최고예요", "모양이 제멋대로", "개성있게 생긴", "약간의 흠집이 있지만", "정품과 맛은 똑같아요"
        - BAD examples: "주을 고려하더라도", "외관의 결함에도 불구하고" (Avoid overly literal or awkward translations)
        
        **[매우 중요한 규칙 (CRITICAL RULE)]**
        - **Do NOT create or use awkward, non-existent, or made-up Korean words, Stick to common and natural vocabulary.**
        """;

        String userPrompt = String.format("""
        Based on the product information below, please generate a title and description according to the system instructions.
        
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
        """,
                request.condition(),
                request.pricePerWeight(),
                request.price(),
                categoriesString,
                toneInstruction
        );

        List<Message> messages = new ArrayList<>();
        messages.add(Message.of("system", systemPrompt));

        List<ContentPart> userMessageParts = new ArrayList<>();
        userMessageParts.add(new TextContentPart(userPrompt));

        if (request.images() != null && !request.images().isEmpty()) {
            for (String imageUrl : request.images()) {
                try {
                    String dataUri = createDataUriFromUrl(imageUrl);
                    userMessageParts.add(new ImageUrlContentPart(new ImageUrl(dataUri)));
                } catch (IOException e) {
                    log.warn("image URL -> Data URI 변환 실패: {}", imageUrl, e);
                }
            }
        }
        messages.add(new Message("user", userMessageParts));

        OpenAiChatRequest openAiRequest = new OpenAiChatRequest(aiProperties.model(), messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(aiProperties.apiKey());

        String url = aiProperties.baseUrl() + "/v1/chat/completions";

        HttpEntity<OpenAiChatRequest> entity = new HttpEntity<>(openAiRequest, headers);

        OpenAiChatResponse response;
        try {
            response = restTemplate.postForObject(url, entity, OpenAiChatResponse.class);
        } catch (RestClientException ex) {
            log.error("OpenAI API 호출 실패", ex);
            throw new RuntimeException("AI 서버 호출 실패", ex);
        }

        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            throw new RuntimeException("AI 서버 응답이 비어 있습니다");
        }

        try {
            Object contentObj = response.choices().get(0).message().content();
            String rawContent = (contentObj instanceof String)
                    ? (String) contentObj
                    : contentObj.toString();
            String jsonContent = extractJsonOnly(rawContent);
            log.debug("AI content response (truncated): {}", jsonContent.length() > 500 ? jsonContent.substring(0,500) + "..." : jsonContent);

            JsonNode aiResponse = objectMapper.readTree(jsonContent);
            String title = aiResponse.path("title").asText(null);
            String description = aiResponse.path("description").asText(null);

            if (title == null || title.isBlank() || description == null || description.isBlank()) {
                throw new RuntimeException("AI 응답 JSON에 title/description이 없거나 비어 있습니다.");
            }
            return new AiResult(title, description);

        } catch (Exception e) {
            log.error("AI 응답 파싱 오류. Raw Response Content: {}", response.choices().get(0).message().content(), e);
            throw new RuntimeException("AI 응답 파싱 오류", e);
        }
    }

    /**
     * [변경] 이미지를 Base64로 인코딩하고 OpenAI가 인식하는 Data URI 형식으로 변환합니다.
     * @param imageUrl 이미지 URL
     * @return Data URI (e.g., "data:image/jpeg;base64,iVBORw0KGgo...")
     * @throws IOException 이미지 다운로드/변환 실패 시
     */
    public String createDataUriFromUrl(String imageUrl) throws IOException {

        byte[] imageBytes = restTemplate.getForObject(imageUrl, byte[].class);
        if (imageBytes == null || imageBytes.length == 0) {
            throw new IOException("이미지 다운로드 실패: 빈 응답");
        }
        final int maxBytes = 5 * 1024 * 1024;
        if (imageBytes.length > maxBytes) {
            throw new IOException("이미지 용량 초과(5MB Max): " + imageBytes.length);
        }

        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        String mimeType = detectMimeType(imageUrl);

        return "data:" + mimeType + ";base64," + base64Image;
    }

    private String detectMimeType(String url) {
        String lowerCaseUrl = url.toLowerCase();
        if (lowerCaseUrl.endsWith(".png")) {
            return "image/png";
        } else if (lowerCaseUrl.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerCaseUrl.endsWith(".webp")) {
            return "image/webp";
        }
        return "image/jpeg";
    }

    private static String extractJsonOnly(String s) {
        if (s == null) return "";
        int start = s.indexOf('{');
        int end = s.lastIndexOf('}');
        return (start >= 0 && end > start) ? s.substring(start, end + 1) : s.trim();
    }
}