import { render, screen } from '@testing-library/react';
import Badge from './Badge';

describe('Badge — label', () => {
  it('renders label text', () => {
    render(<Badge label="Active" />);
    expect(screen.getByText('Active')).toBeInTheDocument();
  });
  it('renders with badge class', () => {
    const { container } = render(<Badge label="Active" />);
    expect(container.firstChild).toHaveClass('badge');
  });
});

describe('Badge — variants', () => {
  it('applies success class for Active label', () => {
    const { container } = render(<Badge label="Active" />);
    expect(container.firstChild.className).toContain('badge-success');
  });
  it('applies danger class for High label', () => {
    const { container } = render(<Badge label="High" />);
    expect(container.firstChild.className).toContain('badge-danger');
  });
  it('applies warning class for Medium label', () => {
    const { container } = render(<Badge label="Medium" />);
    expect(container.firstChild.className).toContain('badge-warning');
  });
  it('applies success class for Low label', () => {
    const { container } = render(<Badge label="Low" />);
    expect(container.firstChild.className).toContain('badge-success');
  });
  it('applies neutral class for unknown label', () => {
    const { container } = render(<Badge label="Unknown" />);
    expect(container.firstChild.className).toContain('badge-neutral');
  });
  it('variant prop overrides label-based class', () => {
    const { container } = render(<Badge label="Active" variant="danger" />);
    expect(container.firstChild.className).toContain('badge-danger');
  });
});

