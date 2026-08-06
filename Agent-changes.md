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

---

## Market Data & Gemini AI Integration

> Based on `MARKET_DATA_INTEGRATION_GEMINI.md`. Implements Phase 1 (Yahoo Finance market
> data) and Phase 2 (Gemini AI suggestions), following the existing layered
> package structure (`controller/`, `service/`, `dto/`, plus new `client/` and
> `scheduler/` packages) rather than the doc's suggested `market/`/`ai/`
> feature-folder layout, to stay consistent with the rest of the codebase.

### Status: ✅ Implemented (Phase 1 & 2) — Phase 3 (caching/real-time/advanced analytics) not started

### Backend — Market Data (Yahoo Finance)

- **`client/YahooFinanceClient.java`** *(new)* — Calls the key-free Yahoo Finance
  `chart` endpoint via `java.net.http.HttpClient`, with configurable timeout and
  retry count. Parses `chart.result[0].meta.regularMarketPrice` from the JSON
  response. Returns `Optional.empty()` on any failure (timeout, non-200,
  malformed body) instead of throwing.
- **`service/MarketPriceService.java`** *(new)* — Converts DB tickers (e.g.
  `RELIANCE`) to Yahoo's NSE format (`RELIANCE.NS`) and delegates to
  `YahooFinanceClient`.
- **`controller/MarketController.java`** *(new)* — `GET /market/{ticker}` →
  `MarketPriceDTO`. Returns HTTP 503 with `success:false` when the price can't
  be fetched (per the doc's error-handling rules).
- **`dto/MarketPriceDTO.java`** *(new)* — `ticker, price, success, message`.
- **`scheduler/MarketPriceScheduler.java`** *(new)* — `@Scheduled` job (default
  every 5 minutes, configurable via `market.price.refresh-rate-ms`) that walks
  every `Investment`, refreshes `currentPrice` from Yahoo Finance, and **leaves
  the existing price untouched** when a fetch fails, so portfolio valuations
  never blank out on an upstream outage.
- **`BackendApplication.java`** — added `@EnableScheduling` to activate the
  scheduler.
- **`application.properties`** — added `market.yahoo.base-url`,
  `market.request.timeout-seconds` (default 5s), `market.request.retries`
  (default 2), `market.price.refresh-rate-ms` (default 300000ms / 5 min).

### Backend — Gemini AI Suggestions

- **`client/GeminiClient.java`** *(new)* — Calls the Gemini `generateContent`
  REST API (`gemini-2.0-flash` model) via `java.net.http.HttpClient`.
  `isConfigured()` returns false when `gemini.api.key` is blank, so callers can
  skip straight to the rule-based fallback without making a network call.
- **`service/AiSuggestionService.java`** *(new)* — Builds a prompt from the
  customer's risk profile, investment goal, and current holdings; asks Gemini
  to respond with strict JSON (`summary`, `suggestions[]`, `riskLevel`); parses
  the response (stripping markdown code fences defensively). **Falls back** to
  the existing rule-based `SuggestionService` whenever Gemini is unconfigured,
  times out, errors, or returns something unparseable — response `source` is
  `"AI"` or `"RULE_BASED"` accordingly so the frontend can label it.
- **`controller/AiSuggestionController.java`** *(new)* — `GET
  /customers/{customerId}/ai-suggestions` → `AiSuggestionResponseDTO`. Kept as
  a **new, separate endpoint** from the existing `GET
  /customers/{id}/suggestions` (rule-based) so the existing `SuggestionService`
  /`SuggestionController` and their tests are untouched.
- **`dto/AiSuggestionResponseDTO.java`** *(new)* — `customerId, summary,
  suggestions[], riskLevel, source`.
- **`application.properties`** — added `gemini.api.key` (reads
  `GEMINI_API_KEY` env var, blank by default ⇒ AI disabled, rule-based fallback
  always used), `gemini.api.url`, `gemini.request.timeout-seconds`.

  ⚠️ **Action needed:** set the `GEMINI_API_KEY` environment variable (or
  `gemini.api.key` in `application.properties`) to enable real AI-generated
  suggestions. Without it, `/ai-suggestions` transparently returns rule-based
  results with `source: "RULE_BASED"`.

### Frontend

- **`api/services/market.js`** *(new)* — `getMarketPrice(ticker)`, calls
  `GET /market/{ticker}` (or mock store in mock mode).
- **`api/services/aiSuggestions.js`** *(new)* — `getAiSuggestions(customerId)`,
  calls `GET /customers/{id}/ai-suggestions` (or mock store in mock mode).
- **`api/mock/store.js`** — added `getMarketPrice()` (echoes an investment's
  `currentPrice` by ticker) and `getAiSuggestions()` (derives a mocked
  AI-style summary/suggestions/riskLevel from existing mock suggestions data)
  so the UI works end-to-end with `VITE_USE_MOCK=true`.
- **`pages/CustomerDetailPage.jsx`** — added an **"AI Portfolio Insights"**
  card above the existing rule-based "Insights & Suggestions" card, with a
  "Generate Insights" button that calls `getAiSuggestions` on demand (not
  auto-loaded, to avoid unnecessary Gemini calls), showing the risk badge,
  summary paragraph, suggestion bullets, and whether the result came from
  Gemini or the rule-based fallback.
- Added Jest tests: `market.test.js`, `aiSuggestions.test.js`.

### Not yet implemented (Phase 3 / future work from the design doc)

- Redis caching of market prices, WebSocket-based real-time price pushes,
  broker API integration.
- `portfolio_suggestion` history table (persisting past AI suggestions).
- News sentiment analysis / risk scoring / personalized recommendations.
- A dedicated "Live Price" UI affordance on the Investments page wired to
  `getMarketPrice` (service exists; not yet surfaced in a component).

### Gemini end-to-end verification (post-implementation)

After the user added a real `GEMINI_API_KEY` to a root-level `.env` file, the
integration was verified end-to-end by actually running the backend and
calling `GET /customers/1/ai-suggestions`:

- **Security fix**: an external edit had put the raw API key directly into
  the git-tracked `application.properties` (instead of the
  `${GEMINI_API_KEY:}` placeholder). Reverted immediately — secrets must
  never live in tracked files.
- **`config/DotenvEnvironmentPostProcessor.java`** *(new)* — loads a root-level
  `.env` file (checks `./.env` then `../.env`, since Maven runs from
  `backend/`) into the Spring `Environment` so `${GEMINI_API_KEY:}` resolves
  without exporting an OS environment variable. Logs
  `[dotenv] Loaded N properties from <path> (keys: [...])` to stdout on
  startup (never logs the value).
- **Bug found & fixed**: the processor was initially registered via
  `META-INF/spring/org.springframework.boot.env.EnvironmentPostProcessor.imports`
  implementing `org.springframework.boot.env.EnvironmentPostProcessor`. In
  **Spring Boot 4.1.0 this interface/registration moved** to
  `org.springframework.boot.EnvironmentPostProcessor`, registered via
  `META-INF/spring.factories` (confirmed by inspecting Boot's own jar — its
  built-in post processors are listed there under the new key). The old
  registration silently never ran, so `GEMINI_API_KEY` was never loaded and
  every `/ai-suggestions` call fell back to `RULE_BASED` with **no log
  output at all** (since `GeminiClient` was never even called). Fixed by
  switching the import to `org.springframework.boot.EnvironmentPostProcessor`
  and registering via `META-INF/spring.factories`.
- **Confirmed working after the fix**: startup log shows
  `[dotenv] Loaded 1 property ... (keys: [GEMINI_API_KEY])`, and calling
  `/customers/1/ai-suggestions` produced a real
  `com.backend.client.GeminiClient : Gemini API returned status 429: ...`
  warning (Gemini was actually called), followed by a graceful fallback —
  the response still returned `"source": "RULE_BASED"` because the
  provided key's Google Cloud project has **zero free-tier quota**
  (`RESOURCE_EXHAUSTED`, `generate_content_free_tier_requests`, limit 0) for
  `gemini-2.0-flash`. This proves both (a) the wiring/fallback logic works
  exactly as designed, and (b) the key itself is valid but needs billing
  enabled or a different key/model with available quota to actually produce
  `source: "AI"`.
- Full backend test suite re-run after the fix — still green.

