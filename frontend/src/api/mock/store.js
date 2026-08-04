/**
 * In-memory mock data store — used when VITE_USE_MOCK=true.
 * Simulates full CRUD so the UI behaves like a real API.
 * Replace by removing the mock flag once the backend is ready.
 */
import customersData   from '../../../mock-data/customers.json';
import investmentsData from '../../../mock-data/investments.json';
import dashboardData   from '../../../mock-data/dashboard.json';
import suggestionsData from '../../../mock-data/suggestions.json';
import portfoliosData  from '../../../mock-data/portfolios.json';

const clone = (obj) => JSON.parse(JSON.stringify(obj));

let customers   = clone(customersData.customers);
let investments = clone(investmentsData.investments);
let portfolios  = clone(portfoliosData.portfolios);
let suggestions = clone(suggestionsData.suggestions);
let trades      = [];
const dashboard = clone(dashboardData);

let custSeq  = customers.length + 1;
let invSeq   = investments.length + 1;
let portSeq  = portfolios.length + 1;
let tradeSeq = 1;

const pad = (n) => String(n).padStart(3, '0');

/* Compute P&L for a customer from their investments */
const computePL = (customerId) => {
  const portfolio = portfolios.find(p => p.customerId === customerId);
  const invs = portfolio ? investments.filter(inv => inv.portfolioId === portfolio.id) : [];
  const totalInvested    = invs.reduce((s, i) => s + i.quantity * i.purchasePrice, 0);
  const currentValue     = invs.reduce((s, i) => s + i.quantity * i.currentPrice, 0);
  const profitLoss       = currentValue - totalInvested;
  const returnPercentage = totalInvested > 0 ? (profitLoss / totalInvested) * 100 : 0;
  return { totalInvested, currentValue, profitLoss, returnPercentage };
};

export const mockStore = {
  /* ── Dashboard ─────────────────────────────────────────────── */
  getDashboard: () => clone(dashboard),

  /* ── Customers ─────────────────────────────────────────────── */
  getCustomers: (includeArchived = false) => {
    const list = includeArchived ? customers : customers.filter(c => c.status !== 'Archived');
    return clone(list.map(c => ({ ...c, ...computePL(c.id) })));
  },

  getCustomer: (id) => {
    const c = customers.find(c => c.id === id);
    return c ? clone({ ...c, ...computePL(id) }) : null;
  },

  createCustomer: (data) => {
    const customer = {
      ...data,
      id:             `CUS${pad(custSeq++)}`,
      joinedDate:     new Date().toISOString().split('T')[0],
      portfolioValue: 0,
      status:         'Active',
    };
    customers = [...customers, customer];
    return clone(customer);
  },

  updateCustomer: (id, data) => {
    customers = customers.map(c => c.id === id ? { ...c, ...data } : c);
    const c = customers.find(c => c.id === id);
    return clone({ ...c, ...computePL(id) });
  },

  /* ── Hard delete: removes customer and all related data ─────────────────────────────────────────────── */
  deleteCustomer: (id) => {
    const portfolio = portfolios.find(p => p.customerId === id);
    if (portfolio) {
      investments = investments.filter(inv => inv.portfolioId !== portfolio.id);
      portfolios  = portfolios.filter(p => p.id !== portfolio.id);
      trades      = trades.filter(t => t.customerId !== id);
    }
    suggestions = suggestions.filter(s => s.customerId !== id);
    customers   = customers.filter(c => c.id !== id);
  },

  /* ── Portfolios ─────────────────────────────────────────────── */
  getPortfolio: (customerId) => {
    const p = portfolios.find(p => p.customerId === customerId);
    return p ? clone(p) : null;
  },

  /* ── Investments ─────────────────────────────────────────────── */
  getInvestments: () => clone(investments),

  getInvestmentsByCustomer: (customerId) => {
    const portfolio = portfolios.find(p => p.customerId === customerId);
    if (!portfolio) return [];
    return clone(investments.filter(inv => inv.portfolioId === portfolio.id));
  },

  createInvestment: (customerId, data) => {
    let portfolio = portfolios.find(p => p.customerId === customerId);
    if (!portfolio) {
      portfolio = {
        id:               `PORT${pad(portSeq++)}`,
        customerId,
        totalInvestment:  0,
        currentValue:     0,
        profitLoss:       0,
        returnPercentage: 0,
        lastUpdated:      new Date().toISOString().split('T')[0],
      };
      portfolios = [...portfolios, portfolio];
    }
    const investment = {
      ...data,
      id:          `INV${pad(invSeq++)}`,
      portfolioId: portfolio.id,
      allocation:  0,
    };
    investments = [...investments, investment];
    /* Record as a Buy trade */
    trades = [...trades, {
      id:          `TRD${pad(tradeSeq++)}`,
      portfolioId: portfolio.id,
      customerId,
      investmentId: investment.id,
      assetName:   data.assetName,
      assetType:   data.assetType,
      ticker:      data.ticker || '',
      tradeType:   'Buy',
      quantity:    data.quantity,
      price:       data.purchasePrice,
      tradeDate:   data.purchaseDate || new Date().toISOString().split('T')[0],
      realisedPL:  null,
    }];
    return clone(investment);
  },

  updateInvestment: (id, data) => {
    investments = investments.map(inv => inv.id === id ? { ...inv, ...data } : inv);
    return clone(investments.find(inv => inv.id === id));
  },

  deleteInvestment: (id) => {
    investments = investments.filter(inv => inv.id !== id);
  },

  /* ── Sell Trade ─────────────────────────────────────────────── */
  sellInvestment: (investmentId, quantity, sellPrice, tradeDate) => {
    const inv = investments.find(i => i.id === investmentId);
    if (!inv) throw new Error('Investment not found');
    const portfolio  = portfolios.find(p => p.id === inv.portfolioId);
    const customerId = portfolio?.customerId;
    const realisedPL = (sellPrice - inv.purchasePrice) * quantity;
    trades = [...trades, {
      id:          `TRD${pad(tradeSeq++)}`,
      portfolioId: inv.portfolioId,
      customerId,
      investmentId,
      assetName:   inv.assetName,
      assetType:   inv.assetType,
      ticker:      inv.ticker || '',
      tradeType:   'Sell',
      quantity,
      price:       sellPrice,
      tradeDate:   tradeDate || new Date().toISOString().split('T')[0],
      realisedPL,
    }];
    if (quantity >= inv.quantity) {
      investments = investments.filter(i => i.id !== investmentId);
    } else {
      investments = investments.map(i => i.id === investmentId ? { ...i, quantity: i.quantity - quantity } : i);
    }
    return clone({ realisedPL });
  },

  /* ── Trades ─────────────────────────────────────────────────── */
  getTrades: (customerId) => clone(trades.filter(t => t.customerId === customerId)),

  /* ── Suggestions ─────────────────────────────────────────────── */
  getSuggestions: () => clone(suggestions),

  getSuggestionsByCustomer: (customerId) =>
    clone(suggestions.filter(s => s.customerId === customerId)),

  /* ── Performance ─────────────────────────────────────────────── */
  getCustomerPerformance: (customerId, range = '6M') => {
    const { totalInvested, currentValue, profitLoss, returnPercentage } = computePL(customerId);
    const growth = returnPercentage / 100;
    const now    = new Date();

    const COUNTS = { '1M': 15, '3M': 12, '6M': 6, '1Y': 12, 'All': 24 };
    const count  = COUNTS[range] || 6;

    const makeLabel = (i) => {
      if (range === '1M') {
        const d = new Date(now); d.setDate(d.getDate() - (count - 1 - i) * 2);
        return `${d.getDate()}/${d.getMonth() + 1}`;
      }
      if (range === '3M') return `W${i + 1}`;
      const back = range === '1Y' ? 11 : range === 'All' ? 23 : 5;
      const d = new Date(now.getFullYear(), now.getMonth() - back + i, 1);
      return d.toLocaleString('en', { month: 'short' });
    };

    const performanceSeries = Array.from({ length: count }, (_, i) => ({
      month: makeLabel(i),
      value: Math.max(0, Math.round(totalInvested * (1 + growth * ((i + 1) / count) + Math.sin(i * 2.3) * 0.015))),
    }));

    return clone({ totalInvested, currentValue, profitLoss, returnPercentage, performanceSeries });
  },
};
