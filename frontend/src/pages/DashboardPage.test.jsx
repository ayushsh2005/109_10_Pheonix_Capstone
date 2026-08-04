import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import DashboardPage from './DashboardPage';

jest.mock('../api/services/dashboard', () => ({ getDashboard: jest.fn() }));
jest.mock('../components/charts/AllocationChart',  () => () => <div data-testid="allocation-chart" />);
jest.mock('../components/charts/PerformanceChart', () => () => <div data-testid="performance-chart" />);
jest.mock('../context/ToastContext', () => ({ useToast: () => ({ success: jest.fn(), error: jest.fn() }) }));

import { getDashboard } from '../api/services/dashboard';

const mockDashboard = {
  summary: {
    totalCustomers:    5,
    totalAssetsManaged: 5000000,
    portfolioValue:    4800000,
    returnPercentage:  15.5,
  },
  allocation: [
    { assetType: 'Stocks', value: 3000000, percentage: 60 },
    { assetType: 'Bonds',  value: 2000000, percentage: 40 },
  ],
  performanceSeries: [
    { label: 'Jan', value: 4000000 },
    { label: 'Feb', value: 4800000 },
  ],
};

const renderPage = () =>
  render(<MemoryRouter><DashboardPage /></MemoryRouter>);

beforeEach(() => {
  jest.clearAllMocks();
  getDashboard.mockResolvedValue(mockDashboard);
});

describe('DashboardPage — greeting', () => {
  it('renders a greeting with "Manager"', async () => {
    renderPage();
    await waitFor(() => expect(screen.getByText(/manager/i)).toBeInTheDocument());
  });

  it('renders today\'s date', async () => {
    renderPage();
    await waitFor(() => {
      const year = new Date().getFullYear().toString();
      expect(screen.getByText(new RegExp(year))).toBeInTheDocument();
    });
  });
});

describe('DashboardPage — KPI stat cards', () => {
  it('renders Total Customers label', async () => {
    renderPage();
    await waitFor(() => expect(screen.getByText(/total customers/i)).toBeInTheDocument());
  });

  it('renders Assets Managed label', async () => {
    renderPage();
    await waitFor(() => expect(screen.getByText(/assets managed/i)).toBeInTheDocument());
  });

  it('renders Portfolio Value label', async () => {
    renderPage();
    await waitFor(() => expect(screen.getByText('Portfolio Value')).toBeInTheDocument());
  });

  it('renders Total Return label', async () => {
    renderPage();
    await waitFor(() => expect(screen.getByText(/total return/i)).toBeInTheDocument());
  });
});

describe('DashboardPage — charts', () => {
  it('renders allocation chart', async () => {
    renderPage();
    await waitFor(() => expect(screen.getByTestId('allocation-chart')).toBeInTheDocument());
  });

  it('renders performance chart', async () => {
    renderPage();
    await waitFor(() => expect(screen.getByTestId('performance-chart')).toBeInTheDocument());
  });

  it('renders Asset Allocation section title', async () => {
    renderPage();
    await waitFor(() => expect(screen.getByText(/asset allocation/i)).toBeInTheDocument());
  });

  it('renders Portfolio Performance section title', async () => {
    renderPage();
    await waitFor(() => expect(screen.getByText(/portfolio performance/i)).toBeInTheDocument());
  });
});

describe('DashboardPage — error state', () => {
  it('shows error message when API fails', async () => {
    getDashboard.mockRejectedValue(new Error('Server error'));
    renderPage();
    await waitFor(() => expect(screen.getByText(/failed to load dashboard/i)).toBeInTheDocument());
  });
});

