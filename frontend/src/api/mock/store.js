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
const dashboard = clone(dashboardData);

let custSeq = customers.length + 1;
let invSeq  = investments.length + 1;
let portSeq = portfolios.length + 1;

const pad = (n) => String(n).padStart(3, '0');

export const mockStore = {
  /* ── Dashboard ─────────────────────────────────────────────── */
  getDashboard: () => clone(dashboard),

  /* ── Customers ─────────────────────────────────────────────── */
  getCustomers: () => clone(customers),

  getCustomer: (id) => clone(customers.find(c => c.id === id) ?? null),

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
    return clone(customers.find(c => c.id === id));
  },

  deleteCustomer: (id) => {
    const portfolio = portfolios.find(p => p.customerId === id);
    if (portfolio) {
      investments = investments.filter(inv => inv.portfolioId !== portfolio.id);
      portfolios  = portfolios.filter(p => p.id !== portfolio.id);
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
      allocation:  0, // recalculated on the fly
    };
    investments = [...investments, investment];
    return clone(investment);
  },

  updateInvestment: (id, data) => {
    investments = investments.map(inv => inv.id === id ? { ...inv, ...data } : inv);
    return clone(investments.find(inv => inv.id === id));
  },

  deleteInvestment: (id) => {
    investments = investments.filter(inv => inv.id !== id);
  },

  /* ── Suggestions ─────────────────────────────────────────────── */
  getSuggestions: () => clone(suggestions),

  getSuggestionsByCustomer: (customerId) =>
    clone(suggestions.filter(s => s.customerId === customerId)),
};
