# Agent Changes

> Based on `backend-frontend-data-mismatches.md`. All changes follow the recommended fix order.
> Backend stack: Spring Boot + **JdbcTemplate** (no JPA). Frontend: React 18 + Vite.

---

## Backend Changes

### 1. `schema.sql` — New columns + trade table
- Added `status VARCHAR(20) DEFAULT 'Active'` to `customer` table (fix 3.2)
- Added `notes VARCHAR(1000)` to `customer` table (fix 2.1)
- Added `target_allocation TEXT` to `customer` table (fix 2.1)
- Created new `trade` table with columns: `id, portfolio_id, customer_id, investment_id, asset_name, asset_type, ticker, trade_type, quantity, price, trade_date, realised_pl` (fix 3.1)

---

### 2. `entity/Customer.java`
- Added fields: `status`, `notes`, `targetAllocation` (raw JSON string) with getters/setters.

### 3. `entity/Trade.java` *(new)*
- New entity representing a trade record (Buy/Sell).
- Fields: `id, portfolioId, customerId, investmentId, assetName, assetType, ticker, tradeType, quantity, price, tradeDate, realisedPL`.

---

### 4. `dto/CustomerRequestDTO.java`
- Added `notes` (`String`, max 1000 chars) field.
- Added `targetAllocation` (`Map<String, Object>`) field — accepts `{Stocks, Bonds, Cash, Others}` percentages from the form.

### 5. `dto/CustomerResponseDTO.java`
- Added `totalInvestment`, `currentValue`, `profitLoss`, `returnPercentage` fields — fixes the P/L always-₹0 bug on Customers list page (fix 1.4).
- Added `notes` and `targetAllocation` (`Map<String, Object>`) fields (fix 2.1).
- Updated constructor to accept all new fields.

### 6. `dto/PortfolioPerformanceDTO.java`
- **Renamed** `totalInvestment` → `totalInvested` — fixes "Total Invested" always-₹0 on Customer Detail page (fix 1.1).
- Added `List<PerformancePointDTO> performanceSeries` field — fixes always-empty performance chart (fix 1.2).

### 7. `dto/TradeDTO.java` *(new)*
- Response DTO for trade records: mirrors `Trade` entity fields.
- `realisedPL` field name matches what `CustomerDetailPage.jsx` reads.

### 8. `dto/SellRequestDTO.java` *(new)*
- Request DTO for `POST /investments/{id}/sell`: `quantity`, `sellPrice`, `tradeDate`.

---

### 9. `repository/CustomerRepository.java`
- Updated `RowMapper` to read `status`, `notes`, `target_allocation` columns.
- Updated `INSERT` SQL and `UPDATE` SQL to include the three new columns.
- Added `updateStatus(Long id, String status)` method for archive/restore.

### 10. `repository/TradeRepository.java` *(new)*
- JdbcTemplate repository for the `trade` table.
- `findByCustomerId(Long)` — lists trades for a customer ordered by date desc.
- `save(Trade)` — inserts a new trade row.

---

### 11. `service/CustomerService.java`
- Injected `ObjectMapper` to serialise/deserialise `targetAllocation` JSON.
- Extracted `applyRequest()` helper to apply DTO fields to entity.
- `toResponseDTO()` now computes `totalInvestment`, `currentValue`, `profitLoss`, `returnPercentage` (same logic as `PortfolioService`).
- Added `archiveCustomer(Long id)` — sets status to `"Archived"`.
- Added `restoreCustomer(Long id)` — sets status back to `"Active"`.

### 12. `service/PortfolioService.java`
- `getPerformance(Long customerId)` signature changed to `getPerformance(Long customerId, String range)`.
- Added `buildCustomerPerformanceSeries(investments, range)` — buckets portfolio value by month for the requested range (1M / 3M / 6M / 1Y / ALL), populating `performanceSeries` in the response (fixes 1.2 & 1.3).

### 13. `service/TradeService.java` *(new)*
- `getTradesByCustomer(Long customerId)` — returns trade history.
- `sellInvestment(Long investmentId, SellRequestDTO)` — validates quantity, reduces/removes investment holding, computes `realisedPL = (sellPrice − purchasePrice) × qty`, inserts Sell trade record.
- `recordBuy(Investment, Long customerId)` — records a Buy trade; called by `InvestmentService`.

### 14. `service/InvestmentService.java`
- Injected `TradeService` (via `@Lazy` to break circular dependency).
- `addInvestment()` now calls `tradeService.recordBuy()` after saving the new investment, so Buy trades are automatically recorded.

---

### 15. `controller/CustomerController.java`
- `getPerformance` now accepts `@RequestParam(defaultValue = "6M") String range` and passes it to `portfolioService.getPerformance(id, range)` (fix 1.3).
- Added `POST /{id}/archive` endpoint → `customerService.archiveCustomer()`.
- Added `POST /{id}/restore` endpoint → `customerService.restoreCustomer()`.

### 16. `controller/TradeController.java` *(new)*
- `GET /customers/{customerId}/trades` — returns customer trade history.
- `POST /investments/{investmentId}/sell` — records a sell transaction.

---

## Frontend Changes

### 17. `api/services/customers.js`
- **Fix 4.1**: `getPortfolio()` URL changed from `/customers/${id}/performance` → `/customers/${id}/portfolio` (was returning the wrong shape).
- Added `archiveCustomer(id)` — calls `POST /customers/{id}/archive`.
- Added `restoreCustomer(id)` — calls `POST /customers/{id}/restore`.

---

### 18. `src/index.css` — Enhanced micro-interactions & animations

**New keyframes added:**
- `slideInUp`, `slideInRight`, `slideInLeft` — directional page/card entrances
- `popIn`, `bounceIn` — spring-style modal & icon pop
- `glowPulse`, `glowPulseGreen` — periodic glow rings on active elements
- `barGrow` — allocation bar animated grow from left
- `dotBounce` — three-dot loading indicator
- `floatUp` — gentle float for suggestion cards
- `avatarPop` — avatar scale bounce on hover
- `countUp` — number entrance animation
- `chartDraw` — SVG stroke-dashoffset line draw for Recharts lines
- `valueFlash` — background flash when a metric value updates
- `progressBar` — horizontal loading bar
- `shimmerRow` — button shimmer sweep

**Micro-interactions added:**
- **Glass cards** — steeper lift (`translateY(-6px) scale(1.01)`) on hover for clickable cards
- **Customer avatar** — `avatarPop` bounce on parent card hover
- **Detail avatar** — `bounceIn` spring entrance
- **Sidebar nav item** — radial gradient overlay on hover; active item pulses with `glowPulse`
- **Stat cards** — lift + shadow deepens on hover; positive-KPI cards get a green glow ring
- **Primary button** — shimmer sweep animation across surface; `translateY(-2px)` lift on hover; `translateY(0)` snap on press
- **All buttons** — SVG icon micro-bounce on `:active`
- **Form inputs** — `translateY(-2px)` lift on focus; green border when valid/filled
- **Table rows** — `translateX(2px)` nudge + left red border accent on hover; deeper stagger delays
- **Allocation bars** — `barGrow` from zero width on mount, staggered per row
- **P/L hero block** — `slideInUp` entrance, `countUp` on amount, `slideInRight` on percentage badge
- **Suggestion cards** — `floatUp` staggered entrance
- **Range selector buttons** — spring scale on active
- **Confirm dialog icon** — `bounceIn` with delay
- **Toasts** — spring `toastIn` animation
- **Recharts line path** — stroke-dashoffset draw animation
- **Sidebar logo** — rotate + scale on hover
- **Skeleton** — smoother 3-stop shimmer gradient
- **Tooltip** — CSS-only tooltip via `[data-tooltip]` attribute with fade+slide reveal
- **Loading dots** — `.loading-dots` utility component style
- **Page loading bar** — `.page-loading-bar` fixed top bar utility
- **Focus ring** — `:focus-visible` accessible outline using `--primary` colour
