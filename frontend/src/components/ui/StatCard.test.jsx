import { render, screen, waitFor } from '@testing-library/react';
import StatCard from './StatCard';

describe('StatCard — render', () => {
  it('renders label', () => {
    render(<StatCard label="Total Customers" value={5} />);
    expect(screen.getByText('Total Customers')).toBeInTheDocument();
  });
  it('renders value', () => {
    render(<StatCard label="Total" value="₹5,000" />);
    expect(screen.getByText('₹5,000')).toBeInTheDocument();
  });
  it('renders delta when provided', async () => {
    render(<StatCard label="Total" value={5} delta={8.3} deltaLabel="vs last quarter" />);
    // count-up animation: wait for final value
    await waitFor(() => expect(screen.getByText(/8%/)).toBeInTheDocument(), { timeout: 1000 });
  });
  it('renders deltaLabel when provided', () => {
    render(<StatCard label="Total" value={5} delta={8.3} deltaLabel="vs last quarter" />);
    expect(screen.getByText('vs last quarter')).toBeInTheDocument();
  });
  it('does not render delta section when delta is undefined', () => {
    render(<StatCard label="Total" value={5} />);
    expect(screen.queryByText(/%/)).not.toBeInTheDocument();
  });
  it('shows negative delta with - prefix', async () => {
    render(<StatCard label="Total" value={5} delta={-5} />);
    await waitFor(() => expect(screen.getByText(/-\d+%/)).toBeInTheDocument(), { timeout: 1000 });
  });
  it('renders neutral delta (zero) without icon', () => {
    render(<StatCard label="Total" value={5} delta={0} />);
    expect(screen.getByText(/0%/)).toBeInTheDocument();
  });
  it('renders icon when icon prop provided', () => {
    render(<StatCard label="Total" value={5} icon={<span data-testid="icon" />} />);
    expect(screen.getByTestId('icon')).toBeInTheDocument();
  });
});

