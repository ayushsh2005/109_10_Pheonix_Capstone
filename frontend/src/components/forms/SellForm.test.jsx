import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import SellForm from './SellForm';

const investment = { id: 'INV001', assetName: 'Reliance', quantity: 10, purchasePrice: 2000, currentPrice: 2500 };
const defaultProps = { open: true, onClose: jest.fn(), onSubmit: jest.fn(), loading: false, investment };

beforeEach(() => jest.clearAllMocks());

describe('SellForm — render', () => {
  it('renders nothing when open=false', () => {
    render(<SellForm {...defaultProps} open={false} />);
    expect(screen.queryByText('Record Sell Trade')).not.toBeInTheDocument();
  });
  it('shows Record Sell Trade title', () => {
    render(<SellForm {...defaultProps} />);
    expect(screen.getByText('Record Sell Trade')).toBeInTheDocument();
  });
  it('shows asset name in subtitle', () => {
    render(<SellForm {...defaultProps} />);
    expect(screen.getByText(/Reliance/)).toBeInTheDocument();
  });
  it('pre-fills quantity with full holding', () => {
    render(<SellForm {...defaultProps} />);
    expect(screen.getByDisplayValue('10')).toBeInTheDocument();
  });
  it('pre-fills sell price with current price', () => {
    render(<SellForm {...defaultProps} />);
    expect(screen.getByDisplayValue('2500')).toBeInTheDocument();
  });
  it('renders Confirm Sale button', () => {
    render(<SellForm {...defaultProps} />);
    expect(screen.getByRole('button', { name: 'Confirm Sale' })).toBeInTheDocument();
  });
});

describe('SellForm — P&L preview', () => {
  it('shows P&L preview when quantity and price are set', () => {
    render(<SellForm {...defaultProps} />);
    expect(screen.getByText(/Estimated Realised P&L/i)).toBeInTheDocument();
  });
  it('shows positive P&L when sell price > purchase price', () => {
    render(<SellForm {...defaultProps} />);
    // (2500 - 2000) * 10 = +5000
    expect(screen.getByText(/\+/)).toBeInTheDocument();
  });
});

describe('SellForm — validation', () => {
  it('shows error when quantity exceeds holding', async () => {
    render(<SellForm {...defaultProps} />);
    const qtyInput = screen.getByDisplayValue('10');
    fireEvent.change(qtyInput, { target: { value: '99' } });
    fireEvent.click(screen.getByRole('button', { name: 'Confirm Sale' }));
    await waitFor(() => expect(screen.getByText(/Cannot exceed 10 units/)).toBeInTheDocument());
  });
  it('shows error when sell price is 0', async () => {
    render(<SellForm {...defaultProps} />);
    fireEvent.change(screen.getByDisplayValue('2500'), { target: { value: '0' } });
    fireEvent.click(screen.getByRole('button', { name: 'Confirm Sale' }));
    await waitFor(() => expect(screen.getByText(/valid sell price/i)).toBeInTheDocument());
  });
  it('does not call onSubmit when validation fails', () => {
    render(<SellForm {...defaultProps} />);
    fireEvent.change(screen.getByDisplayValue('2500'), { target: { value: '-1' } });
    fireEvent.click(screen.getByRole('button', { name: 'Confirm Sale' }));
    expect(defaultProps.onSubmit).not.toHaveBeenCalled();
  });
});

describe('SellForm — submit', () => {
  it('calls onSubmit with numeric values on valid submit', async () => {
    render(<SellForm {...defaultProps} />);
    fireEvent.click(screen.getByRole('button', { name: 'Confirm Sale' }));
    await waitFor(() => expect(defaultProps.onSubmit).toHaveBeenCalledWith(
      expect.objectContaining({ quantity: 10, sellPrice: 2500 })
    ));
  });
  it('calls onClose when Cancel is clicked', () => {
    render(<SellForm {...defaultProps} />);
    fireEvent.click(screen.getByRole('button', { name: 'Cancel' }));
    expect(defaultProps.onClose).toHaveBeenCalledTimes(1);
  });
});

