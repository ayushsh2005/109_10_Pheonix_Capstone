package com.backend.repository;

import com.backend.entity.Customer;
import com.backend.entity.Portfolio;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Repository
public class PortfolioRepository {

    private final JdbcTemplate jdbc;

    public PortfolioRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Portfolio> mapper = (rs, rowNum) -> {
        Portfolio p = new Portfolio();
        p.setId(rs.getLong("id"));
        Timestamp ts = rs.getTimestamp("created_date");
        p.setCreatedDate(ts != null ? ts.toLocalDateTime() : null);
        Customer c = new Customer();
        c.setId(rs.getLong("customer_id"));
        p.setCustomer(c);
        return p;
    };

    public Optional<Portfolio> findById(Long id) {
        return jdbc.query("SELECT * FROM portfolio WHERE id = ?", mapper, id)
                   .stream().findFirst();
    }

    public Optional<Portfolio> findByCustomerId(Long customerId) {
        return jdbc.query("SELECT * FROM portfolio WHERE customer_id = ?", mapper, customerId)
                   .stream().findFirst();
    }

    public Portfolio save(Portfolio portfolio) {
        if (portfolio.getId() == null) {
            portfolio.setCreatedDate(LocalDateTime.now());
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO portfolio (customer_id, created_date) VALUES (?,?)",
                        new String[]{"id"});
                ps.setLong(1, portfolio.getCustomer().getId());
                ps.setTimestamp(2, Timestamp.valueOf(portfolio.getCreatedDate()));
                return ps;
            }, keyHolder);
            portfolio.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        } else {
            jdbc.update("UPDATE portfolio SET customer_id=? WHERE id=?",
                    portfolio.getCustomer().getId(), portfolio.getId());
        }
        return portfolio;
    }
}
