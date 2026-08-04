import {
  formatCurrency,
  formatCurrencyPrecise,
  formatNumber,
  formatPercent,
  formatReturnPct,
  formatPL,
  formatDate,
  getInitials,
  calcInvestmentMetrics,
  calcPortfolioStats,
} from './formatters';

describe('formatCurrency', () => {
  it('formats a positive integer as INR', () => {
    expect(formatCurrency(1000)).toContain('1,000');
  });
  it('formats a large number with Indian grouping', () => {
    expect(formatCurrency(1000000)).toContain('10,00,000');
  });
  it('formats zero', () => {
    expect(formatCurrency(0)).toContain('0');
  });
  it('formats a negative value', () => {
    expect(formatCurrency(-500)).toContain('500');
  });
  it('handles null without throwing', () => {
    expect(() => formatCurrency(null)).not.toThrow();
  });
  it('handles undefined without throwing', () => {
    expect(() => formatCurrency(undefined)).not.toThrow();
  });
  it('includes the ₹ symbol', () => {
    expect(formatCurrency(100)).toMatch(/₹/);
  });
});

describe('formatCurrencyPrecise', () => {
  it('includes two decimal places', () => {
    expect(formatCurrencyPrecise(1000)).toMatch(/1,000\.00/);
  });
  it('rounds correctly to 2 decimals', () => {
    expect(formatCurrencyPrecise(99.999)).toMatch(/100\.00/);
  });
  it('handles zero', () => {
    expect(formatCurrencyPrecise(0)).toMatch(/0\.00/);
  });
});

describe('formatNumber', () => {
  it('formats with Indian thousands separator', () => {
    expect(formatNumber(100000)).toContain('1,00,000');
  });
  it('handles zero', () => {
    expect(formatNumber(0)).toBe('0');
  });
  it('handles null without throwing', () => {
    expect(() => formatNumber(null)).not.toThrow();
  });
});

describe('formatPercent', () => {
  it('formats a decimal (0-1 range) as percentage', () => {
    expect(formatPercent(0.5)).toContain('50');
  });
  it('formats an integer percentage (>1) as percentage', () => {
    expect(formatPercent(50)).toContain('50');
  });
  it('includes % symbol', () => {
    expect(formatPercent(0.25)).toContain('%');
  });
  it('handles null without throwing', () => {
    expect(() => formatPercent(null)).not.toThrow();
  });
});

describe('formatReturnPct', () => {
  it('adds + prefix for positive values', () => {
    expect(formatReturnPct(12.5)).toBe('+12.5%');
  });
  it('keeps - prefix for negative values', () => {
    expect(formatReturnPct(-8.3)).toBe('-8.3%');
  });
  it('formats zero as +0.0%', () => {
    expect(formatReturnPct(0)).toBe('+0.0%');
  });
  it('rounds to 1 decimal place', () => {
    expect(formatReturnPct(12.55)).toBe('+12.6%');
  });
  it('handles null as +0.0%', () => {
    expect(formatReturnPct(null)).toBe('+0.0%');
  });
  it('handles undefined as +0.0%', () => {
    expect(formatReturnPct(undefined)).toBe('+0.0%');
  });
});

describe('formatPL', () => {
  it('adds + prefix for profit', () => {
    expect(formatPL(500)).toMatch(/^\+/);
  });
  it('adds - prefix for loss', () => {
    expect(formatPL(-500)).toMatch(/^-/);
  });
  it('includes ₹ symbol', () => {
    expect(formatPL(1000)).toMatch(/₹/);
  });
  it('handles null as zero without throwing', () => {
    expect(() => formatPL(null)).not.toThrow();
  });
  it('formats zero with + prefix', () => {
    expect(formatPL(0)).toMatch(/^\+/);
  });
});

describe('formatDate', () => {
  it('formats ISO date string to readable format', () => {
    const result = formatDate('2025-01-15');
    expect(result).toContain('2025');
    expect(result).toContain('Jan');
    expect(result).toContain('15');
  });
  it('returns em dash for null', () => {
    expect(formatDate(null)).toBe('—');
  });
  it('returns em dash for undefined', () => {
    expect(formatDate(undefined)).toBe('—');
  });
  it('returns em dash for empty string', () => {
    expect(formatDate('')).toBe('—');
  });
});

describe('getInitials', () => {
  it('returns two uppercase initials for a full name', () => {
    expect(getInitials('John Doe')).toBe('JD');
  });
  it('returns one initial for a single word name', () => {
    expect(getInitials('Alice')).toBe('A');
  });
  it('only uses first two words for names with more than two words', () => {
    expect(getInitials('John Michael Doe')).toBe('JM');
  });
  it('uppercases initials for lowercase input', () => {
    expect(getInitials('john doe')).toBe('JD');
  });
  it('returns empty string for empty input', () => {
    expect(getInitials('')).toBe('');
  });
  it('returns empty string when called with no argument', () => {
    expect(getInitials()).toBe('');
  });
  it('handles extra spaces between words', () => {
    expect(getInitials('John  Doe')).toBe('JD');
  });
});

describe('calcInvestmentMetrics', () => {
  const inv = { quantity: 10, purchasePrice: 100, currentPrice: 150 };

  it('calculates totalCost correctly', () => {
    expect(calcInvestmentMetrics(inv).totalCost).toBe(1000);
  });
  it('calculates currentValue correctly', () => {
    expect(calcInvestmentMetrics(inv).currentValue).toBe(1500);
  });
  it('calculates profitLoss correctly', () => {
    expect(calcInvestmentMetrics(inv).profitLoss).toBe(500);
  });
  it('calculates returnPct correctly', () => {
    expect(calcInvestmentMetrics(inv).returnPct).toBeCloseTo(50);
  });
  it('calculates negative profitLoss for a loss position', () => {
    const losing = { quantity: 10, purchasePrice: 200, currentPrice: 150 };
    expect(calcInvestmentMetrics(losing).profitLoss).toBe(-500);
  });
  it('returns returnPct of 0 when totalCost is 0', () => {
    const zeroCost = { quantity: 0, purchasePrice: 0, currentPrice: 100 };
    expect(calcInvestmentMetrics(zeroCost).returnPct).toBe(0);
  });
});

describe('calcPortfolioStats', () => {
  const investments = [
    { quantity: 10, purchasePrice: 100, currentPrice: 120 },
    { quantity: 5,  purchasePrice: 200, currentPrice: 180 },
  ];

  it('calculates totalInvested correctly', () => {
    // (10*100) + (5*200) = 2000
    expect(calcPortfolioStats(investments).totalInvested).toBe(2000);
  });
  it('calculates totalCurrentValue correctly', () => {
    // (10*120) + (5*180) = 2100
    expect(calcPortfolioStats(investments).totalCurrentValue).toBe(2100);
  });
  it('calculates overall profitLoss correctly', () => {
    expect(calcPortfolioStats(investments).profitLoss).toBe(100);
  });
  it('calculates returnPct correctly', () => {
    // 100 / 2000 * 100 = 5%
    expect(calcPortfolioStats(investments).returnPct).toBeCloseTo(5);
  });
  it('returns all zeroes for an empty list', () => {
    const stats = calcPortfolioStats([]);
    expect(stats.totalInvested).toBe(0);
    expect(stats.totalCurrentValue).toBe(0);
    expect(stats.profitLoss).toBe(0);
    expect(stats.returnPct).toBe(0);
  });
  it('returns all zeroes when called with no argument', () => {
    expect(calcPortfolioStats().totalInvested).toBe(0);
  });
});

