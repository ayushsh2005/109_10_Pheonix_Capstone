/* Store uses module-level mutable state — isolateModules gives a fresh copy per test */
let mockStore;

beforeEach(() => {
  jest.isolateModules(() => {
    ({ mockStore } = require('./store'));
  });
});

describe('mockStore.getCustomers', () => {
  it('returns an array of customers', () => {
    const result = mockStore.getCustomers();
    expect(Array.isArray(result)).toBe(true);
    expect(result.length).toBeGreaterThan(0);
  });
  it('each customer has P&L fields', () => {
    const c = mockStore.getCustomers()[0];
    expect(c).toHaveProperty('totalInvested');
    expect(c).toHaveProperty('currentValue');
    expect(c).toHaveProperty('profitLoss');
    expect(c).toHaveProperty('returnPercentage');
  });
});

describe('mockStore.getCustomer', () => {
  it('returns a customer by id', () => {
    const id = mockStore.getCustomers()[0].id;
    expect(mockStore.getCustomer(id).id).toBe(id);
  });
  it('returns null for unknown id', () => {
    expect(mockStore.getCustomer('INVALID')).toBeNull();
  });
});

describe('mockStore.createCustomer', () => {
  it('assigns a CUS-prefixed id', () => {
    const c = mockStore.createCustomer({ name: 'Test', email: 't@test.com', riskProfile: 'Low', investmentGoal: 'Growth' });
    expect(c.id).toMatch(/^CUS/);
  });
  it('sets status to Active', () => {
    const c = mockStore.createCustomer({ name: 'Test', email: 't@test.com', riskProfile: 'Low', investmentGoal: 'Growth' });
    expect(c.status).toBe('Active');
  });
  it('new customer appears in getCustomers', () => {
    const c = mockStore.createCustomer({ name: 'New', email: 'n@test.com', riskProfile: 'Low', investmentGoal: 'Growth' });
    expect(mockStore.getCustomers().some(x => x.id === c.id)).toBe(true);
  });
});

describe('mockStore.updateCustomer', () => {
  it('updates the customer name', () => {
    const id = mockStore.getCustomers()[0].id;
    const updated = mockStore.updateCustomer(id, { name: 'Updated Name' });
    expect(updated.name).toBe('Updated Name');
  });
});

describe('mockStore.deleteCustomer', () => {
  it('removes customer from list', () => {
    const id = mockStore.getCustomers()[0].id;
    mockStore.deleteCustomer(id);
    expect(mockStore.getCustomers().some(c => c.id === id)).toBe(false);
  });
  it('removes their investments too', () => {
    const id = mockStore.getCustomers()[0].id;
    mockStore.deleteCustomer(id);
    expect(mockStore.getInvestmentsByCustomer(id)).toHaveLength(0);
  });
});

describe('mockStore.createInvestment', () => {
  it('assigns an INV-prefixed id', () => {
    const customerId = mockStore.getCustomers()[0].id;
    const inv = mockStore.createInvestment(customerId, {
      assetName: 'TCS', assetType: 'Stock', ticker: 'TCS',
      quantity: 10, purchasePrice: 3000, currentPrice: 3500, purchaseDate: '2025-01-01',
    });
    expect(inv.id).toMatch(/^INV/);
  });
  it('records a Buy trade', () => {
    const customerId = mockStore.getCustomers()[0].id;
    const inv = mockStore.createInvestment(customerId, {
      assetName: 'Infosys', assetType: 'Stock', ticker: 'INFY',
      quantity: 5, purchasePrice: 1500, currentPrice: 1600, purchaseDate: '2025-01-01',
    });
    const trades = mockStore.getTrades(customerId);
    expect(trades.some(t => t.investmentId === inv.id && t.tradeType === 'Buy')).toBe(true);
  });
});

describe('mockStore.sellInvestment', () => {
  it('calculates realisedPL correctly', () => {
    const customerId = mockStore.getCustomers()[0].id;
    const inv = mockStore.createInvestment(customerId, {
      assetName: 'HDFC', assetType: 'Stock', ticker: 'HDFC',
      quantity: 10, purchasePrice: 1000, currentPrice: 1200, purchaseDate: '2025-01-01',
    });
    // (1500 - 1000) * 5 = 2500
    const result = mockStore.sellInvestment(inv.id, 5, 1500, '2025-06-01');
    expect(result.realisedPL).toBe(2500);
  });
  it('partial sell reduces investment quantity', () => {
    const customerId = mockStore.getCustomers()[0].id;
    const inv = mockStore.createInvestment(customerId, {
      assetName: 'Wipro', assetType: 'Stock', ticker: 'WIPRO',
      quantity: 10, purchasePrice: 500, currentPrice: 600, purchaseDate: '2025-01-01',
    });
    mockStore.sellInvestment(inv.id, 4, 600, '2025-06-01');
    const remaining = mockStore.getInvestmentsByCustomer(customerId).find(i => i.id === inv.id);
    expect(remaining.quantity).toBe(6);
  });
  it('full sell removes investment', () => {
    const customerId = mockStore.getCustomers()[0].id;
    const inv = mockStore.createInvestment(customerId, {
      assetName: 'Zomato', assetType: 'Stock', ticker: 'ZOM',
      quantity: 5, purchasePrice: 200, currentPrice: 250, purchaseDate: '2025-01-01',
    });
    mockStore.sellInvestment(inv.id, 5, 250, '2025-06-01');
    expect(mockStore.getInvestmentsByCustomer(customerId).some(i => i.id === inv.id)).toBe(false);
  });
  it('throws for unknown investment id', () => {
    expect(() => mockStore.sellInvestment('INVALID', 1, 100, '2025-01-01')).toThrow('Investment not found');
  });
});

describe('mockStore.getCustomerPerformance', () => {
  it('returns object with required fields', () => {
    const id = mockStore.getCustomers()[0].id;
    const perf = mockStore.getCustomerPerformance(id, '6M');
    expect(perf).toHaveProperty('totalInvested');
    expect(perf).toHaveProperty('currentValue');
    expect(perf).toHaveProperty('profitLoss');
    expect(perf).toHaveProperty('performanceSeries');
  });
  it('returns 6 data points for 6M', () => {
    const id = mockStore.getCustomers()[0].id;
    expect(mockStore.getCustomerPerformance(id, '6M').performanceSeries).toHaveLength(6);
  });
  it('returns 12 data points for 1Y', () => {
    const id = mockStore.getCustomers()[0].id;
    expect(mockStore.getCustomerPerformance(id, '1Y').performanceSeries).toHaveLength(12);
  });
  it('returns 15 data points for 1M', () => {
    const id = mockStore.getCustomers()[0].id;
    expect(mockStore.getCustomerPerformance(id, '1M').performanceSeries).toHaveLength(15);
  });
});

describe('mockStore.getPortfolio', () => {
  it('returns null for customer with no portfolio', () => {
    const c = mockStore.createCustomer({ name: 'New', email: 'n@t.com', riskProfile: 'Low', investmentGoal: 'Growth' });
    expect(mockStore.getPortfolio(c.id)).toBeNull();
  });
});

describe('mockStore.getInvestmentsByCustomer - no portfolio', () => {
  it('returns empty array for customer with no portfolio', () => {
    const c = mockStore.createCustomer({ name: 'NoPF', email: 'x@t.com', riskProfile: 'Low', investmentGoal: 'Growth' });
    expect(mockStore.getInvestmentsByCustomer(c.id)).toEqual([]);
  });
});

describe('mockStore.createInvestment - new portfolio', () => {
  it('creates a new portfolio for a customer who has none', () => {
    const c = mockStore.createCustomer({ name: 'Fresh', email: 'f@t.com', riskProfile: 'Low', investmentGoal: 'Growth' });
    const inv = mockStore.createInvestment(c.id, {
      assetName: 'NFLX', assetType: 'Stock', ticker: 'NFLX',
      quantity: 3, purchasePrice: 5000, currentPrice: 5500, purchaseDate: '2025-01-01',
    });
    expect(inv.id).toMatch(/^INV/);
    expect(mockStore.getPortfolio(c.id)).not.toBeNull();
  });
  it('records Buy trade with no purchaseDate provided (uses today)', () => {
    const id = mockStore.getCustomers()[0].id;
    const inv = mockStore.createInvestment(id, {
      assetName: 'AMZN', assetType: 'Stock', ticker: 'AMZN',
      quantity: 2, purchasePrice: 15000, currentPrice: 16000,
    });
    const trades = mockStore.getTrades(id);
    const buyTrade = trades.find(t => t.investmentId === inv.id);
    expect(buyTrade.tradeDate).toBeTruthy();
  });
});

describe('mockStore.sellInvestment - trade date default', () => {
  it('uses today as tradeDate when none provided', () => {
    const id = mockStore.getCustomers()[0].id;
    const inv = mockStore.createInvestment(id, {
      assetName: 'META', assetType: 'Stock', ticker: 'META',
      quantity: 5, purchasePrice: 3000, currentPrice: 3200, purchaseDate: '2025-01-01',
    });
    const result = mockStore.sellInvestment(inv.id, 2, 3200, null);
    expect(result.realisedPL).toBe(400);
  });
});

describe('mockStore.deleteCustomer - no portfolio', () => {
  it('deletes customer who has no portfolio', () => {
    const c = mockStore.createCustomer({ name: 'NoPF2', email: 'np@t.com', riskProfile: 'Low', investmentGoal: 'Growth' });
    mockStore.deleteCustomer(c.id);
    expect(mockStore.getCustomer(c.id)).toBeNull();
  });
});

describe('mockStore.getCustomerPerformance - all ranges', () => {
  it('returns 12 data points for 3M range', () => {
    const id = mockStore.getCustomers()[0].id;
    expect(mockStore.getCustomerPerformance(id, '3M').performanceSeries).toHaveLength(12);
  });
  it('returns 24 data points for All range', () => {
    const id = mockStore.getCustomers()[0].id;
    expect(mockStore.getCustomerPerformance(id, 'All').performanceSeries).toHaveLength(24);
  });
});

describe('mockStore.getDashboard', () => {
  it('returns the dashboard object', () => {
    expect(mockStore.getDashboard()).toBeTruthy();
  });
});

describe('mockStore.getSuggestions', () => {
  it('returns an array', () => {
    expect(Array.isArray(mockStore.getSuggestions())).toBe(true);
  });
});

describe('mockStore.getSuggestionsByCustomer', () => {
  it('returns only suggestions for the given customer', () => {
    const suggestions = mockStore.getSuggestions();
    if (suggestions.length === 0) return;
    const customerId = suggestions[0].customerId;
    const result = mockStore.getSuggestionsByCustomer(customerId);
    expect(result.every(s => s.customerId === customerId)).toBe(true);
  });
});

