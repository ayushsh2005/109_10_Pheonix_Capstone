import { render, screen, fireEvent } from '@testing-library/react';
import { ThemeProvider, useTheme } from './ThemeContext';

function ThemeConsumer() {
  const { theme, toggle } = useTheme();
  return <button onClick={toggle}>Theme: {theme}</button>;
}
const renderWith = () => render(<ThemeProvider><ThemeConsumer /></ThemeProvider>);

describe('useTheme - guard', () => {
  it('throws when used outside ThemeProvider', () => {
    const Broken = () => { useTheme(); return null; };
    expect(() => render(<Broken />)).toThrow('useTheme must be used within ThemeProvider');
  });
});
describe('ThemeProvider - initial theme', () => {
  it('renders with a valid initial theme', () => {
    renderWith();
    expect(screen.getByRole('button').textContent).toMatch(/Theme: (light|dark)/);
  });
  it('sets data-theme on documentElement', () => {
    renderWith();
    expect(['light', 'dark']).toContain(document.documentElement.getAttribute('data-theme'));
  });
});
describe('ThemeProvider - toggle', () => {
  it('toggles from light to dark', () => {
    localStorage.setItem('pm_theme', 'light');
    renderWith();
    fireEvent.click(screen.getByRole('button'));
    expect(screen.getByRole('button').textContent).toBe('Theme: dark');
  });
  it('toggles from dark to light', () => {
    localStorage.setItem('pm_theme', 'dark');
    renderWith();
    fireEvent.click(screen.getByRole('button'));
    expect(screen.getByRole('button').textContent).toBe('Theme: light');
  });
  it('updates data-theme after toggle', () => {
    localStorage.setItem('pm_theme', 'light');
    renderWith();
    fireEvent.click(screen.getByRole('button'));
    expect(document.documentElement.getAttribute('data-theme')).toBe('dark');
  });
  it('persists theme to localStorage', () => {
    localStorage.setItem('pm_theme', 'light');
    renderWith();
    fireEvent.click(screen.getByRole('button'));
    expect(localStorage.getItem('pm_theme')).toBe('dark');
  });
});
describe('ThemeProvider - localStorage failure', () => {
  it('falls back to light when localStorage throws', () => {
    jest.spyOn(Storage.prototype, 'getItem').mockImplementation(() => { throw new Error('blocked'); });
    render(<ThemeProvider><ThemeConsumer /></ThemeProvider>);
    expect(screen.getByRole('button').textContent).toMatch(/Theme: (light|dark)/);
    jest.restoreAllMocks();
  });
  it('falls back silently when localStorage.setItem throws on toggle', () => {
    localStorage.setItem('pm_theme', 'light');
    jest.spyOn(Storage.prototype, 'setItem').mockImplementation(() => { throw new Error('blocked'); });
    render(<ThemeProvider><ThemeConsumer /></ThemeProvider>);
    fireEvent.click(screen.getByRole('button'));
    expect(screen.getByRole('button').textContent).toBe('Theme: dark');
    jest.restoreAllMocks();
  });
});

describe('ThemeProvider - localStorage restore', () => {
  it('restores dark theme on mount', () => {
    localStorage.setItem('pm_theme', 'dark');
    renderWith();
    expect(screen.getByRole('button').textContent).toBe('Theme: dark');
  });
  it('restores light theme on mount', () => {
    localStorage.setItem('pm_theme', 'light');
    renderWith();
    expect(screen.getByRole('button').textContent).toBe('Theme: light');
  });
});
