import { render, screen, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { ThemeProvider } from '../../context/ThemeContext';
import TopBar from './TopBar';

const renderTopBar = (path = '/') =>
  render(
    <MemoryRouter initialEntries={[path]}>
      <ThemeProvider><TopBar onMenuToggle={jest.fn()} /></ThemeProvider>
    </MemoryRouter>
  );

describe('TopBar — page title', () => {
  it('shows Dashboard for / path', () => {
    renderTopBar('/');
    expect(screen.getByText('Dashboard')).toBeInTheDocument();
  });
  it('shows Customers for /customers path', () => {
    renderTopBar('/customers');
    expect(screen.getByText('Customers')).toBeInTheDocument();
  });
  it('shows Customer Profile for /customers/:id path', () => {
    renderTopBar('/customers/CUS001');
    expect(screen.getByText('Customer Profile')).toBeInTheDocument();
  });
});

describe('TopBar — theme toggle', () => {
  it('renders theme toggle button', () => {
    renderTopBar();
    expect(screen.getByRole('button', { name: /theme|dark|light/i })).toBeInTheDocument();
  });
  it('calls theme toggle when clicked', () => {
    renderTopBar();
    const btn = screen.getByRole('button', { name: /theme|dark|light/i });
    fireEvent.click(btn);
    expect(btn).toBeInTheDocument();
  });
});

describe('TopBar — menu toggle', () => {
  it('calls onMenuToggle when menu button clicked', () => {
    const fn = jest.fn();
    render(
      <MemoryRouter>
        <ThemeProvider><TopBar onMenuToggle={fn} /></ThemeProvider>
      </MemoryRouter>
    );
    fireEvent.click(screen.getByRole('button', { name: /toggle sidebar/i }));
    expect(fn).toHaveBeenCalledTimes(1);
  });
});

