package com.backend.dto;

import java.math.BigDecimal;

/**
 * Response payload for GET /market/{ticker}.
 * When the upstream Yahoo Finance call fails, {@code success} is false and
 * {@code message} explains the failure; {@code price} will be null.
 */
public class MarketPriceDTO {

    private String ticker;
    private BigDecimal price;
    private boolean success;
    private String message;

    public MarketPriceDTO() {
    }

    public MarketPriceDTO(String ticker, BigDecimal price, boolean success, String message) {
        this.ticker = ticker;
        this.price = price;
        this.success = success;
        this.message = message;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
