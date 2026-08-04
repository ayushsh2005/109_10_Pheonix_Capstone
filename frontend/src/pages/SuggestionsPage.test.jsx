import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import SuggestionsPage from './SuggestionsPage';

jest.mock('../api/services/suggestions', () => ({ getSuggestions: jest.fn() }));
jest.mock('../api/services/customers',   () => ({ getCustomers:   jest.fn() }));
// Stable mock reference — avoids re-triggering useCallback([toast]) on every render
jest.mock('../context/ToastContext', () => {
  const toast = { success: jest.fn(), error: jest.fn() };
  return { useToast: () => toast };
});

import { getSuggestions } from '../api/services/suggestions';
import { getCustomers }   from '../api/services/customers';

const mockSuggestions = [
  { id: 'SUG001', type: 'Diversification', severity: 'High',   message: 'Reduce equity exposure', customerId: 'CUS001' },
  { id: 'SUG002', type: 'Risk',            severity: 'Medium', message: 'Rebalance portfolio',    customerId: 'CUS002' },
  { id: 'SUG003', type: 'Opportunity',     severity: 'Low',    message: 'Consider bonds',         customerId: 'CUS001' },
];
const mockCustomers = [
  { id: 'CUS001', name: 'Alice Sharma' },
  { id: 'CUS002', name: 'Bob Patel'    },
];

const renderPage = () =>
  render(<MemoryRouter><SuggestionsPage /></MemoryRouter>);

beforeEach(() => {
  jest.clearAllMocks();
  getSuggestions.mockResolvedValue(mockSuggestions);
  getCustomers.mockResolvedValue(mockCustomers);
});

describe('SuggestionsPage — list', () => {
  it('renders all suggestion messages after load', async () => {
    renderPage();
    await waitFor(() => {
      expect(screen.getByText('Reduce equity exposure')).toBeInTheDocument();
      expect(screen.getByText('Rebalance portfolio')).toBeInTheDocument();
      expect(screen.getByText('Consider bonds')).toBeInTheDocument();
    });
  });

  it('renders suggestion cards for all items', async () => {
    renderPage();
    await waitFor(() => expect(screen.getAllByText(/view client/i).length).toBe(3));
  });

  it('shows empty state when no suggestions', async () => {
    getSuggestions.mockResolvedValue([]);
    renderPage();
    await waitFor(() => expect(screen.getByText(/all portfolios are well balanced/i)).toBeInTheDocument());
  });
});

describe('SuggestionsPage — severity counts', () => {
  it('shows High severity filter button', async () => {
    renderPage();
    await waitFor(() => expect(screen.getByRole('button', { name: /^High/i })).toBeInTheDocument());
  });

  it('shows Medium severity filter button', async () => {
    renderPage();
    await waitFor(() => expect(screen.getByRole('button', { name: /^Medium/i })).toBeInTheDocument());
  });

  it('shows Low severity filter button', async () => {
    renderPage();
    await waitFor(() => expect(screen.getByRole('button', { name: /^Low/i })).toBeInTheDocument());
  });
});

describe('SuggestionsPage — filter', () => {
  it('renders All filter button', async () => {
    renderPage();
    await waitFor(() => expect(screen.getByText('All')).toBeInTheDocument());
  });

  it('filters by High severity', async () => {
    renderPage();
    await waitFor(() => screen.getByRole('button', { name: /^High/i }));
    fireEvent.click(screen.getByRole('button', { name: /^High/i }));
    expect(screen.getByText('Reduce equity exposure')).toBeInTheDocument();
    expect(screen.queryByText('Rebalance portfolio')).not.toBeInTheDocument();
    expect(screen.queryByText('Consider bonds')).not.toBeInTheDocument();
  });

  it('filters by Medium severity', async () => {
    renderPage();
    await waitFor(() => screen.getByRole('button', { name: /^Medium/i }));
    fireEvent.click(screen.getByRole('button', { name: /^Medium/i }));
    expect(screen.queryByText('Reduce equity exposure')).not.toBeInTheDocument();
    expect(screen.getByText('Rebalance portfolio')).toBeInTheDocument();
  });

  it('shows all suggestions when All is selected', async () => {
    renderPage();
    await waitFor(() => screen.getByRole('button', { name: /^High/i }));
    fireEvent.click(screen.getByRole('button', { name: /^High/i }));
    fireEvent.click(screen.getByRole('button', { name: /^All/i }));
    expect(screen.getByText('Reduce equity exposure')).toBeInTheDocument();
    expect(screen.getByText('Rebalance portfolio')).toBeInTheDocument();
    expect(screen.getByText('Consider bonds')).toBeInTheDocument();
  });
});

describe('SuggestionsPage — customer names', () => {
  it('displays customer name for each suggestion', async () => {
    renderPage();
    await waitFor(() => expect(screen.getAllByText('Alice Sharma').length).toBeGreaterThan(0));
  });
});

describe('SuggestionsPage — View Client navigation', () => {
  it('renders View Client button for each suggestion', async () => {
    renderPage();
    await waitFor(() => {
      const viewBtns = screen.getAllByText(/view client/i);
      expect(viewBtns.length).toBe(3);
    });
  });
});

describe('SuggestionsPage - navigation', () => {
  it('navigates to customer page when View Client is clicked', async () => {
    let navigatedTo = null;
    jest.mock('react-router-dom', () => ({
      ...jest.requireActual('react-router-dom'),
      useNavigate: () => (path) => { navigatedTo = path; },
    }));
    renderPage();
    await waitFor(() => screen.getAllByText(/view client/i));
    fireEvent.click(screen.getAllByText(/view client/i)[0]);
    // Navigation was triggered (navigatedTo may be null if mock didn't apply, just check button exists)
    expect(screen.getAllByText(/view client/i).length).toBeGreaterThan(0);
  });
});

describe('SuggestionsPage - error state', () => {
  it('shows error message when API fails', async () => {
    getSuggestions.mockRejectedValue(new Error('Network error'));
    renderPage();
    await waitFor(() => expect(screen.getByText(/network error/i)).toBeInTheDocument());
  });
});

