package com.backend.scheduler;

import com.backend.entity.Investment;
import com.backend.repository.InvestmentRepository;
import com.backend.service.MarketPriceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Periodically refreshes {@code Investment.currentPrice} from Yahoo Finance.
 * Runs every {@code market.price.refresh-rate-ms} (default 5 minutes).
 * If a price can't be fetched for a ticker, the existing currentPrice is left
 * untouched (per MARKET_DATA_INTEGRATION_GEMINI.md section 11).
 */
@Component
public class MarketPriceScheduler {

    private static final Logger log = LoggerFactory.getLogger(MarketPriceScheduler.class);

    private final InvestmentRepository investmentRepository;
    private final MarketPriceService marketPriceService;

    public MarketPriceScheduler(InvestmentRepository investmentRepository, MarketPriceService marketPriceService) {
        this.investmentRepository = investmentRepository;
        this.marketPriceService = marketPriceService;
    }

    @Scheduled(fixedRateString = "${market.price.refresh-rate-ms:300000}")
    public void refreshPrices() {
        List<Investment> investments = investmentRepository.findAll();
        int updated = 0;
        for (Investment investment : investments) {
            String ticker = investment.getTicker();
            if (ticker == null || ticker.isBlank()) {
                continue;
            }
            Optional<BigDecimal> price = marketPriceService.getLatestPrice(ticker);
            if (price.isPresent()) {
                investment.setCurrentPrice(price.get());
                investmentRepository.save(investment);
                updated++;
            } else {
                log.debug("Keeping previous currentPrice for ticker {} (fetch failed)", ticker);
            }
        }
        log.info("Market price refresh complete: {}/{} investments updated", updated, investments.size());
    }
}
