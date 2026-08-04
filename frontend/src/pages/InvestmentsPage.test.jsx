import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import InvestmentsPage from './InvestmentsPage';

jest.mock('../api/services/investments', () => ({
  getInvestments:   jest.fn(),
  createInvestment: jest.fn(),
  updateInvestment: jest.fn(),
  deleteInvestment: jest.fn(),
}));
jest.mock('../api/services/customers', () => ({ getCustomers: jest.fn() }));
jest.mock('../context/ToastContext', () => ({ useToast: () => ({ success: jest.fn(), error: jest.fn() }) }));
jest.mock('../components/forms/InvestmentForm', () => ({ open, onClose, onSubmit }) =>
  open ? (
    <div data-testid="investment-form">
      <button onClick={() => onSubmit({ assetName: 'New Asset', assetType: 'ETF', quantity: 5, purchasePrice: 1000, currentPrice: 1100, purchaseDate: '2025-01-01' })}>Submit</button>
      <button onClick={onClose}>Close</button>
    </div>
  ) : null
);

import { getInvestments, deleteInvestment, updateInvestment } from '../api/services/investments';
import { getCustomers } from '../api/services/customers';

const mockInvestments = [
  { id: 'INV001', assetName: 'Reliance', assetType: 'Stocks', ticker: 'RELIANCE', quantity: 10, purchasePrice: 2000, currentPrice: 2500, purchaseDate: '2023-06-01', portfolioId: 'PORT001' },
  { id: 'INV002', assetName: 'HDFC Bond', assetType: 'Bonds',  ticker: 'HDFCBND', quantity: 5,  purchasePrice: 1000, currentPrice: 950,  purchaseDate: '2023-03-15', portfolioId: 'PORT002' },
];
const mockCustomers = [{ id: 'CUS001', name: 'Alice Sharma' }];

const renderPage = () =>
  render(<MemoryRouter><InvestmentsPage /></MemoryRouter>);

beforeEach(() => {
  jest.clearAllMocks();
  getInvestments.mockResolvedValue(mockInvestments);
  getCustomers.mockResolvedValue(mockCustomers);
});

describe('InvestmentsPage — list', () => {
  it('renders investment asset names after load', async () => {
    renderPage();
    await waitFor(() => {
      expect(screen.getByText('Reliance')).toBeInTheDocument();
      expect(screen.getByText('HDFC Bond')).toBeInTheDocument();
    });
  });

  it('shows total investment count', async () => {
    renderPage();
    await waitFor(() => expect(screen.getByText(/2 positions/i)).toBeInTheDocument());
  });

  it('shows empty state when no investments', async () => {
    getInvestments.mockResolvedValue([]);
    renderPage();
    await waitFor(() => expect(screen.getByText(/no investments/i)).toBeInTheDocument());
  });
});

describe('InvestmentsPage — search', () => {
  it('filters by asset name', async () => {
    renderPage();
    await waitFor(() => screen.getByText('Reliance'));
    fireEvent.change(screen.getByPlaceholderText(/search/i), { target: { value: 'Reliance' } });
    expect(screen.getByText('Reliance')).toBeInTheDocument();
    expect(screen.queryByText('HDFC Bond')).not.toBeInTheDocument();
  });

  it('filters by ticker', async () => {
    renderPage();
    await waitFor(() => screen.getByText('Reliance'));
    fireEvent.change(screen.getByPlaceholderText(/search/i), { target: { value: 'HDFCBND' } });
    expect(screen.queryByText('Reliance')).not.toBeInTheDocument();
    expect(screen.getByText('HDFC Bond')).toBeInTheDocument();
  });
});

describe('InvestmentsPage — delete', () => {
  it('opens confirm dialog when trash icon is clicked', async () => {
    renderPage();
    await waitFor(() => screen.getAllByLabelText(/delete/i));
    fireEvent.click(screen.getAllByLabelText(/delete/i)[0]);
    expect(screen.getAllByText(/delete investment/i).length).toBeGreaterThan(0);
  });

  it('calls deleteInvestment on confirm', async () => {
    deleteInvestment.mockResolvedValue();
    renderPage();
    await waitFor(() => screen.getAllByLabelText(/delete/i));
    fireEvent.click(screen.getAllByLabelText(/delete/i)[0]);
    fireEvent.click(screen.getByRole('button', { name: 'Delete' }));
    await waitFor(() => expect(deleteInvestment).toHaveBeenCalled());
  });
});

describe('InvestmentsPage — edit form', () => {
  it('opens InvestmentForm when edit icon is clicked', async () => {
    renderPage();
    await waitFor(() => screen.getAllByLabelText(/edit/i));
    fireEvent.click(screen.getAllByLabelText(/edit/i)[0]);
    expect(screen.getByTestId('investment-form')).toBeInTheDocument();
  });

  it('calls updateInvestment on form submit', async () => {
    updateInvestment.mockResolvedValue({ id: 'INV001', assetName: 'Reliance', assetType: 'Stocks', quantity: 10, purchasePrice: 2000, currentPrice: 2600, purchaseDate: '2023-06-01' });
    renderPage();
    await waitFor(() => screen.getAllByLabelText(/edit/i));
    fireEvent.click(screen.getAllByLabelText(/edit/i)[0]);
    fireEvent.click(screen.getByText('Submit'));
    await waitFor(() => expect(updateInvestment).toHaveBeenCalled());
  });
});

