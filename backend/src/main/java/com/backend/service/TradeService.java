package com.backend.service;

import com.backend.dto.SellRequestDTO;
import com.backend.dto.TradeDTO;
import com.backend.entity.Investment;
import com.backend.entity.Trade;
import com.backend.exception.InvestmentNotFoundException;
import com.backend.repository.InvestmentRepository;
import com.backend.repository.PortfolioRepository;
import com.backend.repository.TradeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TradeService {

    private final TradeRepository tradeRepository;
    private final InvestmentRepository investmentRepository;
    private final PortfolioRepository portfolioRepository;

    public TradeService(TradeRepository tradeRepository, InvestmentRepository investmentRepository,
                        PortfolioRepository portfolioRepository) {
        this.tradeRepository = tradeRepository;
        this.investmentRepository = investmentRepository;
        this.portfolioRepository = portfolioRepository;
    }

    @Transactional(readOnly = true)
    public List<TradeDTO> getTradesByCustomer(Long customerId) {
        return tradeRepository.findByCustomerId(customerId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TradeDTO sellInvestment(Long investmentId, SellRequestDTO request) {
        Investment investment = investmentRepository.findById(investmentId)
                .orElseThrow(() -> new InvestmentNotFoundException(investmentId));

        BigDecimal sellQty = request.getQuantity();
        BigDecimal heldQty = investment.getQuantity();

        if (sellQty.compareTo(heldQty) > 0) {
            throw new IllegalArgumentException(
                    "Cannot sell " + sellQty + " units; only " + heldQty + " held.");
        }

        BigDecimal realisedPL = request.getSellPrice()
                .subtract(investment.getPurchasePrice())
                .multiply(sellQty);

        // Reduce or remove the investment holding
        if (sellQty.compareTo(heldQty) == 0) {
            investmentRepository.delete(investment);
        } else {
            investment.setQuantity(heldQty.subtract(sellQty));
            investmentRepository.save(investment);
        }

        Long customerId = investment.getPortfolio().getCustomer().getId();
        Long portfolioId = investment.getPortfolio().getId();

        Trade trade = new Trade();
        trade.setPortfolioId(portfolioId);
        trade.setCustomerId(customerId);
        trade.setInvestmentId(investmentId);
        trade.setAssetName(investment.getAssetName());
        trade.setAssetType(investment.getAssetType());
        trade.setTicker(investment.getTicker());
        trade.setTradeType("Sell");
        trade.setQuantity(sellQty);
        trade.setPrice(request.getSellPrice());
        trade.setTradeDate(request.getTradeDate());
        trade.setRealisedPL(realisedPL);

        return toDTO(tradeRepository.save(trade));
    }

    /** Called by InvestmentService to record a Buy trade on investment creation. */
    @Transactional
    public void recordBuy(Investment investment, Long customerId) {
        Trade trade = new Trade();
        trade.setPortfolioId(investment.getPortfolio().getId());
        trade.setCustomerId(customerId);
        trade.setInvestmentId(investment.getId());
        trade.setAssetName(investment.getAssetName());
        trade.setAssetType(investment.getAssetType());
        trade.setTicker(investment.getTicker());
        trade.setTradeType("Buy");
        trade.setQuantity(investment.getQuantity());
        trade.setPrice(investment.getPurchasePrice());
        trade.setTradeDate(investment.getPurchaseDate() != null
                ? investment.getPurchaseDate()
                : java.time.LocalDate.now());
        trade.setRealisedPL(null);
        tradeRepository.save(trade);
    }

    private TradeDTO toDTO(Trade t) {
        return new TradeDTO(t.getId(), t.getPortfolioId(), t.getCustomerId(), t.getInvestmentId(),
                t.getAssetName(), t.getAssetType(), t.getTicker(), t.getTradeType(),
                t.getQuantity(), t.getPrice(), t.getTradeDate(), t.getRealisedPL());
    }
}
