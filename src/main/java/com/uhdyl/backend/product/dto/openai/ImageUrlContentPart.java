package com.uhdyl.backend.product.dto.openai;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ImageUrlContentPart(
        @JsonProperty("image_url") ImageUrl imageUrl
) implements ContentPart {
    @Override
    public String type() { return "image_url"; }
}