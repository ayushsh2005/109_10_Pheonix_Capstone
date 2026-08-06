package com.backend.repository;

import com.backend.entity.Trade;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;
import java.util.Objects;

@Repository
public class TradeRepository {

    private final JdbcTemplate jdbc;

    public TradeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Trade> mapper = (rs, rowNum) -> {
        Trade t = new Trade();
        t.setId(rs.getLong("id"));
        t.setPortfolioId(rs.getLong("portfolio_id"));
        t.setCustomerId(rs.getLong("customer_id"));
        long invId = rs.getLong("investment_id");
        t.setInvestmentId(rs.wasNull() ? null : invId);
        t.setAssetName(rs.getString("asset_name"));
        t.setAssetType(rs.getString("asset_type"));
        t.setTicker(rs.getString("ticker"));
        t.setTradeType(rs.getString("trade_type"));
        t.setQuantity(rs.getBigDecimal("quantity"));
        t.setPrice(rs.getBigDecimal("price"));
        Date d = rs.getDate("trade_date");
        t.setTradeDate(d != null ? d.toLocalDate() : null);
        t.setRealisedPL(rs.getBigDecimal("realised_pl"));
        return t;
    };

    public List<Trade> findByCustomerId(Long customerId) {
        return jdbc.query("SELECT * FROM trade WHERE customer_id = ? ORDER BY trade_date DESC", mapper, customerId);
    }

    public Trade save(Trade trade) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO trade (portfolio_id, customer_id, investment_id, asset_name, asset_type, ticker, trade_type, quantity, price, trade_date, realised_pl) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
                    new String[]{"id"});
            ps.setLong(1, trade.getPortfolioId());
            ps.setLong(2, trade.getCustomerId());
            if (trade.getInvestmentId() != null) {
                ps.setLong(3, trade.getInvestmentId());
            } else {
                ps.setNull(3, Types.BIGINT);
            }
            ps.setString(4, trade.getAssetName());
            ps.setString(5, trade.getAssetType());
            ps.setString(6, trade.getTicker());
            ps.setString(7, trade.getTradeType());
            ps.setBigDecimal(8, trade.getQuantity());
            ps.setBigDecimal(9, trade.getPrice());
            ps.setDate(10, Date.valueOf(trade.getTradeDate()));
            if (trade.getRealisedPL() != null) {
                ps.setBigDecimal(11, trade.getRealisedPL());
            } else {
                ps.setNull(11, Types.DECIMAL);
            }
            return ps;
        }, keyHolder);
        trade.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return trade;
    }
}
