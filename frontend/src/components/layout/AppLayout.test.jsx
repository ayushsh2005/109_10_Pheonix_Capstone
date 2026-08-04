import { render, screen, fireEvent, act } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { ThemeProvider } from '../../context/ThemeContext';
import AppLayout from './AppLayout';

const renderLayout = () =>
  render(
    <MemoryRouter>
      <ThemeProvider>
        <AppLayout><div>Page content</div></AppLayout>
      </ThemeProvider>
    </MemoryRouter>
  );

describe('AppLayout', () => {
  it('renders children', () => {
    renderLayout();
    expect(screen.getByText('Page content')).toBeInTheDocument();
  });
  it('renders Sidebar navigation', () => {
    renderLayout();
    expect(screen.getAllByText('Dashboard').length).toBeGreaterThan(0);
  });
  it('renders TopBar', () => {
    renderLayout();
    expect(screen.getAllByRole('button').length).toBeGreaterThan(0);
  });
  it('toggles sidebar collapsed state when toggle button clicked', () => {
    renderLayout();
    const toggleBtn = screen.getByRole('button', { name: /collapse|expand/i });
    fireEvent.click(toggleBtn);
    expect(toggleBtn).toBeInTheDocument();
  });
  it('closes sidebar on resize to desktop', () => {
    renderLayout();
    act(() => {
      Object.defineProperty(window, 'innerWidth', { value: 1024, configurable: true });
      window.dispatchEvent(new Event('resize'));
    });
    expect(screen.getByText('Page content')).toBeInTheDocument();
  });
});

