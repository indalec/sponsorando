package com.sponsorando.app.models;

public class CurrencyDTO {
    private String code;
    private String symbol;

    public CurrencyDTO() {
    }

    public CurrencyDTO(String code, String symbol) {
        this.code = code;
        this.symbol = symbol;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}

