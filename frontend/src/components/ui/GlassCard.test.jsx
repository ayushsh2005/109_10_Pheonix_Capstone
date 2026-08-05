import { render, screen, fireEvent } from '@testing-library/react';
import GlassCard from './GlassCard';

describe('GlassCard — render', () => {
  it('renders children', () => {
    render(<GlassCard>Content</GlassCard>);
    expect(screen.getByText('Content')).toBeInTheDocument();
  });
  it('always has glass-card class', () => {
    const { container } = render(<GlassCard>X</GlassCard>);
    expect(container.firstChild).toHaveClass('glass-card');
  });
  it('adds hoverable class when hoverable=true', () => {
    const { container } = render(<GlassCard hoverable>X</GlassCard>);
    expect(container.firstChild).toHaveClass('hoverable');
  });
  it('adds clickable class when clickable=true', () => {
    const { container } = render(<GlassCard clickable>X</GlassCard>);
    expect(container.firstChild).toHaveClass('clickable');
  });
  it('appends custom className', () => {
    const { container } = render(<GlassCard className="custom">X</GlassCard>);
    expect(container.firstChild).toHaveClass('custom');
  });
  it('calls onClick when clicked', () => {
    const fn = jest.fn();
    render(<GlassCard onClick={fn}>X</GlassCard>);
    fireEvent.click(screen.getByText('X'));
    expect(fn).toHaveBeenCalledTimes(1);
  });
});

