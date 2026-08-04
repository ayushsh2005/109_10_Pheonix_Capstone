import { render, screen, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import Sidebar from './Sidebar';

const renderSidebar = (props = {}) =>
  render(
    <MemoryRouter>
      <Sidebar open={false} onClose={jest.fn()} collapsed={false} onToggleCollapse={jest.fn()} {...props} />
    </MemoryRouter>
  );

describe('Sidebar — nav links', () => {
  it('renders Dashboard link', () => {
    renderSidebar();
    expect(screen.getByText('Dashboard')).toBeInTheDocument();
  });
  it('renders Customers link', () => {
    renderSidebar();
    expect(screen.getByText('Customers')).toBeInTheDocument();
  });
  it('renders Investments link', () => {
    renderSidebar();
    expect(screen.getByText('Investments')).toBeInTheDocument();
  });
  it('renders Suggestions link', () => {
    renderSidebar();
    expect(screen.getByText('Suggestions')).toBeInTheDocument();
  });
});

describe('Sidebar — collapse toggle', () => {
  it('calls onToggleCollapse when toggle button is clicked', () => {
    const fn = jest.fn();
    renderSidebar({ onToggleCollapse: fn });
    fireEvent.click(screen.getByRole('button', { name: /collapse|expand/i }));
    expect(fn).toHaveBeenCalledTimes(1);
  });
});

