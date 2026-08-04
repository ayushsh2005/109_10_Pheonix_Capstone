import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import InvestmentForm from './InvestmentForm';

const defaultProps = { open: true, onClose: jest.fn(), onSubmit: jest.fn(), loading: false, customerName: 'Alice' };

beforeEach(() => jest.clearAllMocks());

describe('InvestmentForm - render', () => {
  it('renders nothing when open=false', () => {
    render(<InvestmentForm {...defaultProps} open={false} />);
    expect(screen.queryByRole('dialog')).not.toBeInTheDocument();
  });
  it('shows Add Investment title for new investment', () => {
    render(<InvestmentForm {...defaultProps} />);
    expect(screen.getAllByText('Add Investment').length).toBeGreaterThan(0);
  });
  it('shows Edit Investment title when initial provided', () => {
    render(<InvestmentForm {...defaultProps} initial={{ assetName: 'TCS', assetType: 'Stock', quantity: 10, purchasePrice: 3000, currentPrice: 3200, purchaseDate: '2025-01-01' }} />);
    expect(screen.getAllByText('Edit Investment').length).toBeGreaterThan(0);
  });
  it('shows customer name in subtitle', () => {
    render(<InvestmentForm {...defaultProps} />);
    expect(screen.getByText(/Alice/)).toBeInTheDocument();
  });
  it('pre-fills asset name when editing', () => {
    render(<InvestmentForm {...defaultProps} initial={{ assetName: 'TCS', assetType: 'Stock', quantity: 10, purchasePrice: 3000, currentPrice: 3200, purchaseDate: '2025-01-01' }} />);
    expect(screen.getByDisplayValue('TCS')).toBeInTheDocument();
  });
});

describe('InvestmentForm - validation', () => {
  it('shows asset name error on empty submit', async () => {
    render(<InvestmentForm {...defaultProps} />);
    fireEvent.click(screen.getByRole('button', { name: 'Add Investment' }));
    await waitFor(() => expect(screen.getByText('Asset name is required')).toBeInTheDocument());
  });
  it('shows asset type error on empty submit', async () => {
    render(<InvestmentForm {...defaultProps} />);
    fireEvent.click(screen.getByRole('button', { name: 'Add Investment' }));
    await waitFor(() => expect(screen.getByText('Asset type is required')).toBeInTheDocument());
  });
  it('shows quantity error on empty submit', async () => {
    render(<InvestmentForm {...defaultProps} />);
    fireEvent.click(screen.getByRole('button', { name: 'Add Investment' }));
    await waitFor(() => expect(screen.getByText('Quantity must be a positive number')).toBeInTheDocument());
  });
  it('does not call onSubmit when validation fails', () => {
    render(<InvestmentForm {...defaultProps} />);
    fireEvent.click(screen.getByRole('button', { name: 'Add Investment' }));
    expect(defaultProps.onSubmit).not.toHaveBeenCalled();
  });
});

describe('InvestmentForm - submit', () => {
  it('calls onSubmit with numeric values on valid submit', async () => {
    const { container } = render(<InvestmentForm {...defaultProps} />);
    fireEvent.change(screen.getByPlaceholderText('e.g. Apple Inc.'), { target: { value: 'Reliance' } });
    fireEvent.change(screen.getByRole('combobox'), { target: { value: 'Stock' } });
    fireEvent.change(screen.getByPlaceholderText('100'), { target: { value: '10' } });
    fireEvent.change(screen.getByPlaceholderText('150.00'), { target: { value: '2000' } });
    fireEvent.change(screen.getByPlaceholderText('165.00'), { target: { value: '2500' } });
    fireEvent.change(container.querySelector('input[type="date"]'), { target: { value: '2025-01-01' } });
    fireEvent.click(screen.getByRole('button', { name: 'Add Investment' }));
    await waitFor(() => expect(defaultProps.onSubmit).toHaveBeenCalledWith(
      expect.objectContaining({ assetName: 'Reliance', quantity: 10, purchasePrice: 2000 })
    ));
  });
  it('calls onClose when Cancel clicked', () => {
    render(<InvestmentForm {...defaultProps} />);
    fireEvent.click(screen.getByRole('button', { name: 'Cancel' }));
    expect(defaultProps.onClose).toHaveBeenCalledTimes(1);
  });
});
