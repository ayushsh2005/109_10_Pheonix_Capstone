package com.backend.repository;

import com.backend.entity.Investment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    List<Investment> findByPortfolioId(Long portfolioId);

    List<Investment> findByPortfolioCustomerId(Long customerId);
}
