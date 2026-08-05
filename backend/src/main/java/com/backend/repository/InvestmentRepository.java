package com.backend.repository;

import com.backend.entity.Customer;
import com.backend.entity.Investment;
import com.backend.entity.Portfolio;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class InvestmentRepository {

    private final JdbcTemplate jdbc;

    public InvestmentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * All SELECT queries JOIN portfolio so the RowMapper can populate
     * investment.getPortfolio().getId() and investment.getPortfolio().getCustomer().getId()
     * without extra round-trips, preserving the service/test code that relies on
     * this object graph.
     */
    private static final String BASE_SELECT =
            "SELECT i.*, p.customer_id FROM investment i JOIN portfolio p ON i.portfolio_id = p.id";

    private final RowMapper<Investment> mapper = (rs, rowNum) -> {
        Investment inv = new Investment();
        inv.setId(rs.getLong("id"));
        inv.setAssetName(rs.getString("asset_name"));
        inv.setAssetType(rs.getString("asset_type"));
        inv.setTicker(rs.getString("ticker"));
        inv.setQuantity(rs.getBigDecimal("quantity"));
        inv.setPurchasePrice(rs.getBigDecimal("purchase_price"));
        inv.setCurrentPrice(rs.getBigDecimal("current_price"));
        Date purchaseDate = rs.getDate("purchase_date");
        inv.setPurchaseDate(purchaseDate != null ? purchaseDate.toLocalDate() : null);

        Portfolio p = new Portfolio();
        p.setId(rs.getLong("portfolio_id"));
        Customer c = new Customer();
        c.setId(rs.getLong("customer_id"));
        p.setCustomer(c);
        inv.setPortfolio(p);

        return inv;
    };

    public List<Investment> findAll() {
        return jdbc.query(BASE_SELECT, mapper);
    }

    public Optional<Investment> findById(Long id) {
        return jdbc.query(BASE_SELECT + " WHERE i.id = ?", mapper, id)
                   .stream().findFirst();
    }

    public List<Investment> findByPortfolioId(Long portfolioId) {
        return jdbc.query(BASE_SELECT + " WHERE i.portfolio_id = ?", mapper, portfolioId);
    }

    public List<Investment> findByPortfolioCustomerId(Long customerId) {
        return jdbc.query(BASE_SELECT + " WHERE p.customer_id = ?", mapper, customerId);
    }

    public Investment save(Investment investment) {
        if (investment.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO investment (portfolio_id, asset_name, asset_type, ticker, quantity, purchase_price, current_price, purchase_date) VALUES (?,?,?,?,?,?,?,?)",
                        new String[]{"id"});
                ps.setLong(1, investment.getPortfolio().getId());
                ps.setString(2, investment.getAssetName());
                ps.setString(3, investment.getAssetType());
                ps.setString(4, investment.getTicker());
                ps.setBigDecimal(5, investment.getQuantity());
                ps.setBigDecimal(6, investment.getPurchasePrice());
                ps.setBigDecimal(7, investment.getCurrentPrice());
                ps.setDate(8, investment.getPurchaseDate() != null
                        ? Date.valueOf(investment.getPurchaseDate()) : null);
                return ps;
            }, keyHolder);
            investment.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        } else {
            jdbc.update(
                    "UPDATE investment SET portfolio_id=?, asset_name=?, asset_type=?, ticker=?, quantity=?, purchase_price=?, current_price=?, purchase_date=? WHERE id=?",
                    investment.getPortfolio().getId(),
                    investment.getAssetName(), investment.getAssetType(), investment.getTicker(),
                    investment.getQuantity(), investment.getPurchasePrice(), investment.getCurrentPrice(),
                    investment.getPurchaseDate() != null ? Date.valueOf(investment.getPurchaseDate()) : null,
                    investment.getId());
        }
        return investment;
    }

    public void delete(Investment investment) {
        jdbc.update("DELETE FROM investment WHERE id = ?", investment.getId());
    }
}
