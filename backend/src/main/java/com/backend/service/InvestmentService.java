package com.backend.service;

import com.backend.dto.InvestmentRequestDTO;
import com.backend.dto.InvestmentResponseDTO;
import com.backend.entity.Investment;
import com.backend.entity.Portfolio;
import com.backend.exception.InvestmentNotFoundException;
import com.backend.exception.PortfolioNotFoundException;
import com.backend.repository.InvestmentRepository;
import com.backend.repository.PortfolioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final PortfolioRepository portfolioRepository;

    public InvestmentService(InvestmentRepository investmentRepository, PortfolioRepository portfolioRepository) {
        this.investmentRepository = investmentRepository;
        this.portfolioRepository = portfolioRepository;
    }

    @Transactional(readOnly = true)
    public List<InvestmentResponseDTO> getAllInvestments() {
        return investmentRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InvestmentResponseDTO> getInvestmentsByCustomer(Long customerId) {
        return investmentRepository.findByPortfolioCustomerId(customerId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public InvestmentResponseDTO addInvestment(Long customerId, InvestmentRequestDTO request) {
        Portfolio portfolio = portfolioRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found for customer id: " + customerId));

        Investment investment = new Investment();
        investment.setPortfolio(portfolio);
        applyRequest(investment, request);

        return toResponseDTO(investmentRepository.save(investment));
    }

    @Transactional
    public InvestmentResponseDTO updateInvestment(Long id, InvestmentRequestDTO request) {
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new InvestmentNotFoundException(id));

        applyRequest(investment, request);

        return toResponseDTO(investmentRepository.save(investment));
    }

    @Transactional
    public void deleteInvestment(Long id) {
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new InvestmentNotFoundException(id));
        investmentRepository.delete(investment);
    }

    private void applyRequest(Investment investment, InvestmentRequestDTO request) {
        investment.setAssetName(request.getAssetName());
        investment.setAssetType(request.getAssetType());
        investment.setTicker(request.getTicker());
        investment.setQuantity(request.getQuantity());
        investment.setPurchasePrice(request.getPurchasePrice());
        investment.setCurrentPrice(request.getCurrentPrice());
        investment.setPurchaseDate(request.getPurchaseDate());
    }

    private InvestmentResponseDTO toResponseDTO(Investment investment) {
        Portfolio portfolio = investment.getPortfolio();
        return new InvestmentResponseDTO(
                investment.getId(),
                portfolio.getId(),
                portfolio.getCustomer().getId(),
                investment.getAssetName(),
                investment.getAssetType(),
                investment.getTicker(),
                investment.getQuantity(),
                investment.getPurchasePrice(),
                investment.getCurrentPrice(),
                investment.getPurchaseDate()
        );
    }
}
