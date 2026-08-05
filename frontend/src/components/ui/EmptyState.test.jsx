import { render, screen, fireEvent } from '@testing-library/react';
import EmptyState from './EmptyState';

describe('EmptyState — render', () => {
  it('renders title', () => {
    render(<EmptyState title="No data" />);
    expect(screen.getByText('No data')).toBeInTheDocument();
  });
  it('renders description when provided', () => {
    render(<EmptyState title="Empty" description="Add items to get started" />);
    expect(screen.getByText('Add items to get started')).toBeInTheDocument();
  });
  it('does not render description when omitted', () => {
    render(<EmptyState title="Empty" />);
    expect(screen.queryByRole('paragraph')).not.toBeInTheDocument();
  });
  it('renders action button when action and actionLabel provided', () => {
    render(<EmptyState title="Empty" action={jest.fn()} actionLabel="Add Item" />);
    expect(screen.getByRole('button', { name: 'Add Item' })).toBeInTheDocument();
  });
  it('does not render button when action is missing', () => {
    render(<EmptyState title="Empty" actionLabel="Add Item" />);
    expect(screen.queryByRole('button')).not.toBeInTheDocument();
  });
  it('calls action when button is clicked', () => {
    const fn = jest.fn();
    render(<EmptyState title="Empty" action={fn} actionLabel="Add" />);
    fireEvent.click(screen.getByRole('button'));
    expect(fn).toHaveBeenCalledTimes(1);
  });
});

