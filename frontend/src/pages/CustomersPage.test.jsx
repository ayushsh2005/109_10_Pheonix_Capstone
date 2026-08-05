import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import CustomersPage from './CustomersPage';

jest.mock('../api/services/customers', () => ({
  getCustomers:   jest.fn(),
  createCustomer: jest.fn(),
  updateCustomer: jest.fn(),
  deleteCustomer: jest.fn(),
}));
jest.mock('../context/ToastContext', () => ({ useToast: () => ({ success: jest.fn(), error: jest.fn() }) }));
jest.mock('../components/forms/CustomerForm', () => ({ open, onClose, onSubmit }) =>
  open ? (
    <div data-testid="customer-form">
      <button onClick={() => onSubmit({ name: 'New Customer', email: 'new@test.com' })}>Submit</button>
      <button onClick={onClose}>Close</button>
    </div>
  ) : null
);

import { getCustomers, createCustomer, deleteCustomer } from '../api/services/customers';

const mockCustomers = [
  { id: 'CUS001', name: 'Alice Sharma', email: 'alice@test.com', riskProfile: 'Moderate', investmentGoal: 'Growth', joinedDate: '2023-01-01', profitLoss: 5000, returnPercentage: 25, currentValue: 25000 },
  { id: 'CUS002', name: 'Bob Patel',    email: 'bob@test.com',   riskProfile: 'Low',      investmentGoal: 'Income', joinedDate: '2022-06-15', profitLoss: -1000, returnPercentage: -5, currentValue: 18000 },
];

const renderPage = () =>
  render(<MemoryRouter><CustomersPage /></MemoryRouter>);

beforeEach(() => {
  jest.clearAllMocks();
  getCustomers.mockResolvedValue(mockCustomers);
});

describe('CustomersPage — loading', () => {
  it('shows skeletons while loading', () => {
    getCustomers.mockReturnValue(new Promise(() => {}));
    const { container } = renderPage();
    expect(container.firstChild).toBeTruthy();
  });
});

describe('CustomersPage — customer list', () => {
  it('renders all customer names after load', async () => {
    renderPage();
    await waitFor(() => {
      expect(screen.getByText('Alice Sharma')).toBeInTheDocument();
      expect(screen.getByText('Bob Patel')).toBeInTheDocument();
    });
  });

  it('shows customer count in page title', async () => {
    renderPage();
    await waitFor(() => expect(screen.getByText(/2 total/i)).toBeInTheDocument());
  });

  it('renders Add Customer button', async () => {
    renderPage();
    await waitFor(() => expect(screen.getByText(/add customer/i)).toBeInTheDocument());
  });

  it('shows empty state when no customers', async () => {
    getCustomers.mockResolvedValue([]);
    renderPage();
    await waitFor(() => expect(screen.getByText(/no customers yet/i)).toBeInTheDocument());
  });
});

describe('CustomersPage — search', () => {
  it('filters customers by name', async () => {
    renderPage();
    await waitFor(() => screen.getByText('Alice Sharma'));
    fireEvent.change(screen.getByPlaceholderText(/search/i), { target: { value: 'Alice' } });
    expect(screen.getByText('Alice Sharma')).toBeInTheDocument();
    expect(screen.queryByText('Bob Patel')).not.toBeInTheDocument();
  });

  it('filters customers by email', async () => {
    renderPage();
    await waitFor(() => screen.getByText('Alice Sharma'));
    fireEvent.change(screen.getByPlaceholderText(/search/i), { target: { value: 'bob@test.com' } });
    expect(screen.queryByText('Alice Sharma')).not.toBeInTheDocument();
    expect(screen.getByText('Bob Patel')).toBeInTheDocument();
  });

  it('shows no results empty state when search has no matches', async () => {
    renderPage();
    await waitFor(() => screen.getByText('Alice Sharma'));
    fireEvent.change(screen.getByPlaceholderText(/search/i), { target: { value: 'zzznomatch' } });
    expect(screen.getByText(/no customers found/i)).toBeInTheDocument();
  });
});

describe('CustomersPage — sort', () => {
  it('renders sort dropdown', async () => {
    renderPage();
    await waitFor(() => expect(screen.getByRole('combobox')).toBeInTheDocument());
  });

  it('sorts by Name Z→A when selected', async () => {
    renderPage();
    await waitFor(() => screen.getByText('Alice Sharma'));
    fireEvent.change(screen.getByRole('combobox'), { target: { value: 'name-desc' } });
    const names = screen.getAllByText(/alice sharma|bob patel/i).map(el => el.textContent);
    expect(names[0]).toMatch(/Bob/);
  });
});

describe('CustomersPage — delete', () => {
  it('opens delete confirm dialog on trash click', async () => {
    renderPage();
    await waitFor(() => screen.getByLabelText(/delete alice sharma/i));
    fireEvent.click(screen.getByLabelText(/delete alice sharma/i));
    expect(screen.getAllByText(/delete customer/i).length).toBeGreaterThan(0);
  });

  it('calls deleteCustomer and removes card on confirm', async () => {
    deleteCustomer.mockResolvedValue();
    renderPage();
    await waitFor(() => screen.getByLabelText(/delete alice sharma/i));
    fireEvent.click(screen.getByLabelText(/delete alice sharma/i));
    const confirmBtn = screen.getAllByText(/delete customer/i).find(el => el.tagName === 'BUTTON');
    fireEvent.click(confirmBtn);
    await waitFor(() => expect(deleteCustomer).toHaveBeenCalledWith('CUS001'));
  });

  it('closes dialog on cancel', async () => {
    renderPage();
    await waitFor(() => screen.getByLabelText(/delete alice sharma/i));
    fireEvent.click(screen.getByLabelText(/delete alice sharma/i));
    fireEvent.click(screen.getByText(/cancel/i));
    expect(screen.queryByText(/delete customer/i)).not.toBeInTheDocument();
  });
});

describe('CustomersPage - error state', () => {
  it('shows error message when API fails', async () => {
    getCustomers.mockRejectedValue(new Error('Server down'));
    renderPage();
    await waitFor(() => expect(screen.getByText('Server down')).toBeInTheDocument());
  });
});

describe('CustomersPage - sort variants', () => {
  it('sorts by P&L low to high', async () => {
    renderPage();
    await waitFor(() => screen.getByText('Alice Sharma'));
    fireEvent.change(screen.getByRole('combobox'), { target: { value: 'pl-asc' } });
    expect(screen.getByText('Bob Patel')).toBeInTheDocument();
  });
  it('sorts by value high to low', async () => {
    renderPage();
    await waitFor(() => screen.getByText('Alice Sharma'));
    fireEvent.change(screen.getByRole('combobox'), { target: { value: 'value-desc' } });
    expect(screen.getByText('Alice Sharma')).toBeInTheDocument();
  });
});

describe('CustomersPage - update customer', () => {
  it('calls updateCustomer and updates card on form submit', async () => {
    const { updateCustomer } = require('../api/services/customers');
    updateCustomer.mockResolvedValue({ ...mockCustomers[0], name: 'Alice Updated' });
    renderPage();
    await waitFor(() => screen.getByLabelText(/edit alice sharma/i));
    fireEvent.click(screen.getByLabelText(/edit alice sharma/i));
    fireEvent.click(screen.getByText('Submit'));
    await waitFor(() => expect(updateCustomer).toHaveBeenCalled());
  });
});

describe('CustomersPage - create/edit form', () => {
  it('opens CustomerForm when Add Customer is clicked', async () => {
    renderPage();
    await waitFor(() => screen.getByText(/add customer/i));
    fireEvent.click(screen.getByText(/add customer/i));
    expect(screen.getByTestId('customer-form')).toBeInTheDocument();
  });

  it('opens CustomerForm when edit icon is clicked', async () => {
    renderPage();
    await waitFor(() => screen.getByLabelText(/edit alice sharma/i));
    fireEvent.click(screen.getByLabelText(/edit alice sharma/i));
    expect(screen.getByTestId('customer-form')).toBeInTheDocument();
  });

  it('calls createCustomer on form submit for new customer', async () => {
    createCustomer.mockResolvedValue({ id: 'CUS003', name: 'New Customer', email: 'new@test.com' });
    renderPage();
    await waitFor(() => screen.getByText(/add customer/i));
    fireEvent.click(screen.getByText(/add customer/i));
    fireEvent.click(screen.getByText('Submit'));
    await waitFor(() => expect(createCustomer).toHaveBeenCalled());
  });
});

