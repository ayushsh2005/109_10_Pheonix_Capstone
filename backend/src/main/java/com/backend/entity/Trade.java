package com.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Trade {

    private Long id;
    private Long portfolioId;
    private Long customerId;
    private Long investmentId;
    private String assetName;
    private String assetType;
    private String ticker;
    private String tradeType;   // "Buy" or "Sell"
    private BigDecimal quantity;
    private BigDecimal price;
    private LocalDate tradeDate;
    private BigDecimal realisedPL;

    public Trade() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPortfolioId() { return portfolioId; }
    public void setPortfolioId(Long portfolioId) { this.portfolioId = portfolioId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getInvestmentId() { return investmentId; }
    public void setInvestmentId(Long investmentId) { this.investmentId = investmentId; }

    public String getAssetName() { return assetName; }
    public void setAssetName(String assetName) { this.assetName = assetName; }

    public String getAssetType() { return assetType; }
    public void setAssetType(String assetType) { this.assetType = assetType; }

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public String getTradeType() { return tradeType; }
    public void setTradeType(String tradeType) { this.tradeType = tradeType; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public LocalDate getTradeDate() { return tradeDate; }
    public void setTradeDate(LocalDate tradeDate) { this.tradeDate = tradeDate; }

    public BigDecimal getRealisedPL() { return realisedPL; }
    public void setRealisedPL(BigDecimal realisedPL) { this.realisedPL = realisedPL; }
}
