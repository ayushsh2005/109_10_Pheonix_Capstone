import { render } from '@testing-library/react';
import { Skeleton, SkeletonCard, SkeletonTable } from './Skeleton';

describe('Skeleton', () => {
  it('renders with skeleton class', () => {
    const { container } = render(<Skeleton />);
    expect(container.firstChild).toHaveClass('skeleton');
  });
  it('applies circle class when circle=true', () => {
    const { container } = render(<Skeleton circle />);
    expect(container.firstChild).toHaveClass('skeleton-circle');
  });
  it('sets aria-hidden=true', () => {
    const { container } = render(<Skeleton />);
    expect(container.firstChild).toHaveAttribute('aria-hidden', 'true');
  });
  it('applies custom height', () => {
    const { container } = render(<Skeleton height={40} />);
    expect(container.firstChild.style.height).toBe('40px');
  });
});

describe('SkeletonCard', () => {
  it('renders default 3 skeleton rows', () => {
    const { container } = render(<SkeletonCard />);
    expect(container.querySelectorAll('.skeleton').length).toBeGreaterThanOrEqual(3);
  });
  it('renders correct number of rows', () => {
    const { container } = render(<SkeletonCard rows={5} />);
    expect(container.querySelectorAll('.skeleton').length).toBe(6);
  });
});

describe('SkeletonTable', () => {
  it('renders without crashing', () => {
    const { container } = render(<SkeletonTable />);
    expect(container.firstChild).toBeTruthy();
  });
});

