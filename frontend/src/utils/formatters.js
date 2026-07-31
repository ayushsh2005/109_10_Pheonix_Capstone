/**
 * Formatting utilities — pure functions, no side effects.
 * Region: India (INR / ₹)
 */

const INR = new Intl.NumberFormat('en-IN', {
  style:    'currency',
  currency: 'INR',
  maximumFractionDigits: 0,
});

const INR_PRECISE = new Intl.NumberFormat('en-IN', {
  style:    'currency',
  currency: 'INR',
  minimumFractionDigits: 2,
  maximumFractionDigits: 2,
});

const NUM = new Intl.NumberFormat('en-IN');

const PCT = new Intl.NumberFormat('en-IN', {
  style:                 'percent',
  minimumFractionDigits: 1,
  maximumFractionDigits: 2,
});

/** Format a number as INR currency (no paise). */
export const formatCurrency = (value) => INR.format(value ?? 0);

/** Format a number as INR with paise. */
export const formatCurrencyPrecise = (value) => INR_PRECISE.format(value ?? 0);

/** Format a number with Indian thousands separator. */
export const formatNumber = (value) => NUM.format(value ?? 0);

/** Format a decimal (0–1) or percentage integer as "XX.X%". */
export const formatPercent = (value) => {
  const v = value ?? 0;
  // accept both 0.168 and 16.8 styles
  const normalised = Math.abs(v) <= 1 ? v : v / 100;
  return PCT.format(normalised);
};

/** Format a raw percentage number like 16.8 → "+16.8%" */
export const formatReturnPct = (value) => {
  const v = value ?? 0;
  const sign = v >= 0 ? '+' : '';
  return `${sign}${v.toFixed(1)}%`;
};

/** Format a profit/loss amount with sign. */
export const formatPL = (value) => {
  const v = value ?? 0;
  const sign = v >= 0 ? '+' : '';
  return `${sign}${formatCurrency(v)}`;
};

/** Format an ISO date string to "15 Jan 2025". */
export const formatDate = (iso) => {
  if (!iso) return '—';
  return new Date(iso).toLocaleDateString('en-GB', {
    day:   'numeric',
    month: 'short',
    year:  'numeric',
  });
};

/** Get initials from a full name (up to 2 chars). */
export const getInitials = (name = '') =>
  name
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map(w => w[0].toUpperCase())
    .join('');

/**
 * Calculate investment P&L metrics from raw investment fields.
 */
export const calcInvestmentMetrics = (inv) => {
  const totalCost    = inv.quantity * inv.purchasePrice;
  const currentValue = inv.quantity * inv.currentPrice;
  const profitLoss   = currentValue - totalCost;
  const returnPct    = totalCost > 0 ? (profitLoss / totalCost) * 100 : 0;
  return { totalCost, currentValue, profitLoss, returnPct };
};

/**
 * Compute aggregated portfolio stats from a list of investments.
 */
export const calcPortfolioStats = (investmentList = []) => {
  const totalInvested    = investmentList.reduce((s, i) => s + i.quantity * i.purchasePrice, 0);
  const totalCurrentValue= investmentList.reduce((s, i) => s + i.quantity * i.currentPrice,  0);
  const profitLoss       = totalCurrentValue - totalInvested;
  const returnPct        = totalInvested > 0 ? (profitLoss / totalInvested) * 100 : 0;
  return { totalInvested, totalCurrentValue, profitLoss, returnPct };
};
