package com.backend.repository;

import com.backend.entity.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class CustomerRepository {

    private final JdbcTemplate jdbc;

    public CustomerRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Customer> mapper = (rs, rowNum) -> {
        Customer c = new Customer();
        c.setId(rs.getLong("id"));
        c.setName(rs.getString("name"));
        c.setEmail(rs.getString("email"));
        c.setPhone(rs.getString("phone"));
        c.setRiskProfile(rs.getString("risk_profile"));
        c.setInvestmentGoal(rs.getString("investment_goal"));
        Timestamp ts = rs.getTimestamp("created_date");
        c.setCreatedDate(ts != null ? ts.toLocalDateTime() : null);
        return c;
    };

    public List<Customer> findAll() {
        return jdbc.query("SELECT * FROM customer", mapper);
    }

    public Optional<Customer> findById(Long id) {
        return jdbc.query("SELECT * FROM customer WHERE id = ?", mapper, id)
                   .stream().findFirst();
    }

    public Optional<Customer> findByEmail(String email) {
        return jdbc.query("SELECT * FROM customer WHERE email = ?", mapper, email)
                   .stream().findFirst();
    }

    public boolean existsByEmail(String email) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM customer WHERE email = ?", Integer.class, email);
        return count != null && count > 0;
    }

    public long count() {
        Long count = jdbc.queryForObject("SELECT COUNT(*) FROM customer", Long.class);
        return count != null ? count : 0L;
    }

    public Customer save(Customer customer) {
        if (customer.getId() == null) {
            customer.setCreatedDate(LocalDateTime.now());
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO customer (name, email, phone, risk_profile, investment_goal, created_date) VALUES (?,?,?,?,?,?)",
                        new String[]{"id"});
                ps.setString(1, customer.getName());
                ps.setString(2, customer.getEmail());
                ps.setString(3, customer.getPhone());
                ps.setString(4, customer.getRiskProfile());
                ps.setString(5, customer.getInvestmentGoal());
                ps.setTimestamp(6, Timestamp.valueOf(customer.getCreatedDate()));
                return ps;
            }, keyHolder);
            customer.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        } else {
            jdbc.update(
                    "UPDATE customer SET name=?, email=?, phone=?, risk_profile=?, investment_goal=? WHERE id=?",
                    customer.getName(), customer.getEmail(), customer.getPhone(),
                    customer.getRiskProfile(), customer.getInvestmentGoal(), customer.getId());
        }
        return customer;
    }

    public void delete(Customer customer) {
        jdbc.update("DELETE FROM customer WHERE id = ?", customer.getId());
    }
}
