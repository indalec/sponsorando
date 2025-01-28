package com.sponsorando.app.models;

import org.springframework.web.multipart.MultipartFile;

public class ImageForm {
    // Todo: Validation
    private MultipartFile url;
    //        @NotBlank(message = "Caption is mandatory")
    private String altText;

    public MultipartFile getUrl() {
        return url;
    }

    public void setUrl(MultipartFile url) {
        this.url = url;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }
}
