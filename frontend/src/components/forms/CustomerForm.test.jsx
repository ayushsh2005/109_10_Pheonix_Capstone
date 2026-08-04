import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import CustomerForm from './CustomerForm';

const defaultProps = { open: true, onClose: jest.fn(), onSubmit: jest.fn(), loading: false };

beforeEach(() => jest.clearAllMocks());

describe('CustomerForm — render', () => {
  it('renders nothing when open=false', () => {
    render(<CustomerForm {...defaultProps} open={false} />);
    expect(screen.queryByText('Add Customer')).not.toBeInTheDocument();
  });
  it('shows Add Customer title for new customer', () => {
    render(<CustomerForm {...defaultProps} />);
    expect(screen.getByText('Add Customer')).toBeInTheDocument();
  });
  it('shows Edit Customer title when initial is provided', () => {
    render(<CustomerForm {...defaultProps} initial={{ name: 'Alice', email: 'a@a.com', riskProfile: 'Low', investmentGoal: 'Growth' }} />);
    expect(screen.getByText('Edit Customer')).toBeInTheDocument();
  });
  it('pre-fills name field when editing', () => {
    render(<CustomerForm {...defaultProps} initial={{ name: 'Alice', email: 'a@a.com', riskProfile: 'Low', investmentGoal: 'Growth' }} />);
    expect(screen.getByDisplayValue('Alice')).toBeInTheDocument();
  });
});

describe('CustomerForm — validation', () => {
  it('shows name error when submitting with empty name', async () => {
    render(<CustomerForm {...defaultProps} />);
    fireEvent.click(screen.getByText('Create Customer'));
    await waitFor(() => expect(screen.getByText('Name is required')).toBeInTheDocument());
  });
  it('shows email error when submitting with empty email', async () => {
    render(<CustomerForm {...defaultProps} />);
    fireEvent.click(screen.getByText('Create Customer'));
    await waitFor(() => expect(screen.getByText('Email is required')).toBeInTheDocument());
  });
  it('shows invalid email error for bad email format', async () => {
    render(<CustomerForm {...defaultProps} />);
    fireEvent.change(screen.getByPlaceholderText('james@example.com'), { target: { value: 'notanemail' } });
    fireEvent.blur(screen.getByPlaceholderText('james@example.com'));
    await waitFor(() => expect(screen.getByText('Enter a valid email address')).toBeInTheDocument());
  });
  it('shows risk profile error when not selected', async () => {
    render(<CustomerForm {...defaultProps} />);
    fireEvent.click(screen.getByText('Create Customer'));
    await waitFor(() => expect(screen.getByText('Risk profile is required')).toBeInTheDocument());
  });
  it('does not call onSubmit when validation fails', () => {
    render(<CustomerForm {...defaultProps} />);
    fireEvent.click(screen.getByText('Create Customer'));
    expect(defaultProps.onSubmit).not.toHaveBeenCalled();
  });
});

describe('CustomerForm — submit', () => {
  const fillForm = () => {
    fireEvent.change(screen.getByPlaceholderText('James Anderson'), { target: { value: 'Bob' } });
    fireEvent.change(screen.getByPlaceholderText('james@example.com'), { target: { value: 'bob@test.com' } });
    fireEvent.change(screen.getAllByRole('combobox')[0], { target: { value: 'Moderate' } });
    fireEvent.change(screen.getAllByRole('combobox')[1], { target: { value: 'Wealth Growth' } });
  };

  it('calls onSubmit with correct payload on valid submit', async () => {
    render(<CustomerForm {...defaultProps} />);
    fillForm();
    fireEvent.click(screen.getByText('Create Customer'));
    await waitFor(() => expect(defaultProps.onSubmit).toHaveBeenCalledWith(
      expect.objectContaining({ name: 'Bob', email: 'bob@test.com', riskProfile: 'Moderate' })
    ));
  });
  it('calls onClose when Cancel is clicked', () => {
    render(<CustomerForm {...defaultProps} />);
    fireEvent.click(screen.getByText('Cancel'));
    expect(defaultProps.onClose).toHaveBeenCalledTimes(1);
  });
});

describe('CustomerForm — target allocation', () => {
  it('shows allocation total when values are entered', async () => {
    render(<CustomerForm {...defaultProps} />);
    const inputs = screen.getAllByPlaceholderText('0');
    fireEvent.change(inputs[0], { target: { value: '60' } });
    await waitFor(() => expect(screen.getByText(/Total: 60%/)).toBeInTheDocument());
  });
  it('shows Valid when allocation sums to 100', async () => {
    render(<CustomerForm {...defaultProps} />);
    const inputs = screen.getAllByPlaceholderText('0');
    fireEvent.change(inputs[0], { target: { value: '100' } });
    await waitFor(() => expect(screen.getByText(/Valid/)).toBeInTheDocument());
  });
});

