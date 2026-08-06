import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import CustomerDetailPage from './CustomerDetailPage';

/* ── Mock all API services ────────────────────────────────────── */
jest.mock('../api/services/customers',    () => ({ getCustomer: jest.fn() }));
jest.mock('../api/services/investments',  () => ({
  getInvestmentsByCustomer: jest.fn(),
  createInvestment:         jest.fn(),
  updateInvestment:         jest.fn(),
  deleteInvestment:         jest.fn(),
}));
jest.mock('../api/services/suggestions',  () => ({ getSuggestionsByCustomer: jest.fn() }));
jest.mock('../api/services/aiSuggestions', () => ({ getAiSuggestions: jest.fn() }));
jest.mock('../api/services/performance',  () => ({ getCustomerPerformance: jest.fn() }));
jest.mock('../api/services/trades',       () => ({ getTrades: jest.fn(), sellInvestment: jest.fn() }));

/* ── Mock heavy chart components ──────────────────────────────── */
jest.mock('../components/charts/PerformanceChart', () => () => <div data-testid="performance-chart" />);
jest.mock('../components/charts/AllocationChart',  () => () => <div data-testid="allocation-chart" />);

/* ── Mock ToastContext ─────────────────────────────────────────── */
const mockToast = { success: jest.fn(), error: jest.fn() };
jest.mock('../context/ToastContext', () => ({ useToast: () => mockToast }));

import { getCustomer }                                         from '../api/services/customers';
import { getInvestmentsByCustomer, deleteInvestment }         from '../api/services/investments';
import { getSuggestionsByCustomer }                           from '../api/services/suggestions';
import { getAiSuggestions }                                   from '../api/services/aiSuggestions';
import { getCustomerPerformance }                             from '../api/services/performance';
import { getTrades }                                          from '../api/services/trades';

/* ── Shared test fixtures ─────────────────────────────────────── */
const mockCustomer = {
  id: 'CUS001', name: 'Alice Sharma', email: 'alice@test.com',
  phone: '9999999999', riskProfile: 'Moderate', investmentGoal: 'Growth',
  joinedDate: '2023-01-01', status: 'Active', notes: 'VIP client',
  targetAllocation: null,
};

const mockInvestment = {
  id: 'INV001', assetName: 'Reliance', assetType: 'Stocks', ticker: 'RELIANCE',
  quantity: 10, purchasePrice: 2000, currentPrice: 2500, purchaseDate: '2023-06-01',
};

const mockPerformance = {
  totalInvested: 20000, currentValue: 25000, profitLoss: 5000,
  returnPercentage: 25, performanceSeries: [],
};

const mockTrade = {
  id: 'TRD001', assetName: 'Reliance', tradeType: 'Buy',
  quantity: 10, price: 2000, tradeDate: '2023-06-01', realisedPL: null,
};

const mockSuggestion = {
  id: 'SUG001', type: 'Rebalance', severity: 'High', message: 'Rebalance portfolio',
};

/* ── Helper: render with router ──────────────────────────────── */
const renderPage = () =>
  render(
    <MemoryRouter initialEntries={['/customers/CUS001']}>
      <Routes>
        <Route path="/customers/:id" element={<CustomerDetailPage />} />
      </Routes>
    </MemoryRouter>
  );

/* ── Setup default mocks before each test ────────────────────── */
beforeEach(() => {
  jest.clearAllMocks();
  getCustomer.mockResolvedValue(mockCustomer);
  getInvestmentsByCustomer.mockResolvedValue([mockInvestment]);
  getSuggestionsByCustomer.mockResolvedValue([mockSuggestion]);
  getAiSuggestions.mockResolvedValue({
    customerId: 'CUS001', summary: 'AI summary', suggestions: ['AI suggestion'], riskLevel: 'MEDIUM', source: 'AI',
  });
  getCustomerPerformance.mockResolvedValue(mockPerformance);
  getTrades.mockResolvedValue([mockTrade]);
});

/* ── Tests ───────────────────────────────────────────────────── */
describe('CustomerDetailPage — loading state', () => {
  it('shows skeleton while data is loading', () => {
    getCustomer.mockReturnValue(new Promise(() => {})); // never resolves
    const { container } = renderPage();
    expect(container.firstChild).toBeTruthy();
  });
});

describe('CustomerDetailPage — error state', () => {
  it('shows error message when customer is not found', async () => {
    getCustomer.mockResolvedValue(null);
    renderPage();
    await waitFor(() => {
      expect(screen.getByText(/customer not found/i)).toBeInTheDocument();
    });
  });

  it('shows back button on error screen', async () => {
    getCustomer.mockResolvedValue(null);
    renderPage();
    await waitFor(() => {
      expect(screen.getByText(/back to customers/i)).toBeInTheDocument();
    });
  });
});

describe('CustomerDetailPage — customer info', () => {
  it('renders customer name', async () => {
    renderPage();
    await waitFor(() => {
      expect(screen.getByText('Alice Sharma')).toBeInTheDocument();
    });
  });

  it('renders customer email', async () => {
    renderPage();
    await waitFor(() => {
      expect(screen.getByText('alice@test.com')).toBeInTheDocument();
    });
  });

  it('renders risk profile badge', async () => {
    renderPage();
    await waitFor(() => {
      expect(screen.getByText('Moderate')).toBeInTheDocument();
    });
  });

  it('renders manager notes when present', async () => {
    renderPage();
    await waitFor(() => {
      expect(screen.getByText('VIP client')).toBeInTheDocument();
    });
  });

  it('does not render manager notes section when notes is empty', async () => {
    getCustomer.mockResolvedValue({ ...mockCustomer, notes: '' });
    renderPage();
    await waitFor(() => {
      expect(screen.queryByText(/manager notes/i)).not.toBeInTheDocument();
    });
  });
});

describe('CustomerDetailPage — P&L hero', () => {
  it('renders total invested value', async () => {
    renderPage();
    await waitFor(() => {
      expect(screen.getByText(/total invested/i)).toBeInTheDocument();
    });
  });

  it('renders current value label', async () => {
    renderPage();
    await waitFor(() => {
      expect(screen.getByText(/current value/i)).toBeInTheDocument();
    });
  });

  it('renders unrealised P/L label', async () => {
    renderPage();
    await waitFor(() => {
      expect(screen.getByText(/unrealised profit/i)).toBeInTheDocument();
    });
  });
});

describe('CustomerDetailPage — performance chart', () => {
  it('renders the performance chart', async () => {
    renderPage();
    await waitFor(() => {
      expect(screen.getByTestId('performance-chart')).toBeInTheDocument();
    });
  });

  it('renders all range selector buttons', async () => {
    renderPage();
    await waitFor(() => {
      ['1M', '3M', '6M', '1Y', 'All'].forEach(r => {
        expect(screen.getByText(r)).toBeInTheDocument();
      });
    });
  });
});

describe('CustomerDetailPage — holdings table', () => {
  it('renders investment asset name', async () => {
    renderPage();
    await waitFor(() => {
      expect(screen.getByText('Reliance')).toBeInTheDocument();
    });
  });

  it('renders Add Investment button', async () => {
    renderPage();
    await waitFor(() => {
      expect(screen.getByText(/add investment/i)).toBeInTheDocument();
    });
  });

  it('shows empty state when no investments', async () => {
    getInvestmentsByCustomer.mockResolvedValue([]);
    renderPage();
    await waitFor(() => {
      expect(screen.getByText(/no investments yet/i)).toBeInTheDocument();
    });
  });

  it('opens InvestmentForm when Add Investment is clicked', async () => {
    renderPage();
    await waitFor(() => screen.getByText(/add investment/i));
    fireEvent.click(screen.getByText(/add investment/i));
    expect(screen.getByRole('dialog')).toBeInTheDocument();
  });

  it('opens delete confirm dialog when trash icon clicked', async () => {
    renderPage();
    await waitFor(() => screen.getByLabelText(/delete reliance/i));
    fireEvent.click(screen.getByLabelText(/delete reliance/i));
    expect(screen.getAllByText(/delete investment/i).length).toBeGreaterThan(0);
  });

  it('calls deleteInvestment and removes row on confirm', async () => {
    deleteInvestment.mockResolvedValue();
    getCustomerPerformance.mockResolvedValue(mockPerformance);
    renderPage();
    await waitFor(() => screen.getByLabelText(/delete reliance/i));
    fireEvent.click(screen.getByLabelText(/delete reliance/i));
    // Click the danger confirm button (not the title)
    const confirmBtn = screen.getAllByText(/delete investment/i).find(
      el => el.tagName === 'BUTTON'
    );
    fireEvent.click(confirmBtn);
    await waitFor(() => {
      expect(deleteInvestment).toHaveBeenCalledWith('INV001');
    });
  });
});

describe('CustomerDetailPage — trade history', () => {
  it('renders Trade History section', async () => {
    renderPage();
    await waitFor(() => {
      expect(screen.getByText(/trade history/i)).toBeInTheDocument();
    });
  });

  it('shows transaction count', async () => {
    renderPage();
    await waitFor(() => {
      expect(screen.getByText(/1 transaction/i)).toBeInTheDocument();
    });
  });

  it('expands trade history on click', async () => {
    renderPage();
    await waitFor(() => screen.getByText(/trade history/i));
    fireEvent.click(screen.getByText(/trade history/i));
    await waitFor(() => {
      expect(screen.getAllByText('Reliance').length).toBeGreaterThan(0);
    });
  });
});

describe('CustomerDetailPage — suggestions', () => {
  it('renders suggestions section when suggestions exist', async () => {
    renderPage();
    await waitFor(() => {
      expect(screen.getByText('Insights & Suggestions')).toBeInTheDocument();
    });
  });

  it('renders suggestion message', async () => {
    renderPage();
    await waitFor(() => {
      expect(screen.getByText('Rebalance portfolio')).toBeInTheDocument();
    });
  });

  it('does not render suggestions section when list is empty', async () => {
    getSuggestionsByCustomer.mockResolvedValue([]);
    renderPage();
    await waitFor(() => {
      expect(screen.queryByText('Insights & Suggestions')).not.toBeInTheDocument();
    });
  });
});

