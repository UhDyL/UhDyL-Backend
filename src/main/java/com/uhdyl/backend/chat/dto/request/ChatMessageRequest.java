package com.uhdyl.backend.chat.dto.request;

import lombok.Getter;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

@Getter
public class ChatMessageRequest {

    private String message;
    private String imageUrl;
    private String publicId;

    public void setMessage(String message) {
        this.message = Jsoup.clean(message, Safelist.basic());
    }
}
