package com.uhdyl.backend.product.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Service
@RequiredArgsConstructor
public class AiContentService {

    private final RestTemplate restTemplate;

    public record AiResult(String title, String description) {}

    public AiResult generateContent(String breed, int price, String tone, List<String> imageUrls) {
        String englishPrompt = String.format("Generate a product title and description based on the following information. Product: %s, Price: %d won, Tone: %s. The title should be within 20 characters and the description should be within 100 characters.", breed, price, tone);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestBody;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            if (imageUrls != null && !imageUrls.isEmpty()) {
                ArrayNode base64ImagesNode = objectMapper.createArrayNode();
                for (String imageUrl : imageUrls) {
                    try {
                        String base64Image = urlToBase64(imageUrl);
                        base64ImagesNode.add(base64Image);
                    } catch (IOException e) {
                    }
                }
                requestBody = objectMapper.createObjectNode()
                        .put("prompt", englishPrompt)
                        .set("images", base64ImagesNode)
                        .toString();
            } else {
                requestBody = objectMapper.createObjectNode()
                        .put("prompt", englishPrompt)
                        .toString();
            }
        } catch (Exception e) {
            throw new RuntimeException("JSON 생성 오류", e);
        }

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        String url = "http://202.31.135.91:8000/generate";
        String responseBody = restTemplate.postForObject(url, entity, String.class);
        String englishAiResponse;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(responseBody);
            englishAiResponse = root.get("response").asText();
        } catch (Exception e) {
            throw new RuntimeException("AI 응답 파싱 오류", e);
        }

        String translationPrompt = String.format("Translate the following into Korean and provide the output in the format '제목: [translated title]\n설명: [translated description]': %s", englishAiResponse);
        String translationRequestBody;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            translationRequestBody = objectMapper.createObjectNode()
                    .put("prompt", translationPrompt)
                    .toString();
        } catch (Exception e) {
            throw new RuntimeException("Translation JSON 생성 오류", e);
        }
        HttpEntity<String> translationEntity = new HttpEntity<>(translationRequestBody, headers);
        String translatedResponseBody = restTemplate.postForObject(url, translationEntity, String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(translatedResponseBody);
            String koreanResponse = root.get("response").asText();
            String title = koreanResponse.split("제목:")[1].split("설명:")[0].trim();
            String description = koreanResponse.split("설명:")[1].trim();

            return new AiResult(title, description);

        } catch (Exception e) {
            throw new RuntimeException("번역된 AI 응답 파싱 오류", e);
        }
    }

    public String urlToBase64(String imageUrl) throws IOException {
        try (InputStream is = new URI(imageUrl).toURL().openStream();
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            return Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}