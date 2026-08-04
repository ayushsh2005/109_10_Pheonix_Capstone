import { render, screen, fireEvent } from '@testing-library/react';
import Button from './Button';

describe('Button — render', () => {
  it('renders children text', () => {
    render(<Button>Click me</Button>);
    expect(screen.getByText('Click me')).toBeInTheDocument();
  });
  it('renders as a button element', () => {
    render(<Button>Submit</Button>);
    expect(screen.getByRole('button')).toBeInTheDocument();
  });
  it('applies primary class by default', () => {
    const { container } = render(<Button>Go</Button>);
    expect(container.firstChild.className).toContain('btn-primary');
  });
  it('applies secondary class when variant=secondary', () => {
    const { container } = render(<Button variant="secondary">Go</Button>);
    expect(container.firstChild.className).toContain('btn-secondary');
  });
  it('applies sm class when size=sm', () => {
    const { container } = render(<Button size="sm">Go</Button>);
    expect(container.firstChild.className).toContain('btn-sm');
  });
});

describe('Button — interaction', () => {
  it('calls onClick when clicked', () => {
    const fn = jest.fn();
    render(<Button onClick={fn}>Go</Button>);
    fireEvent.click(screen.getByRole('button'));
    expect(fn).toHaveBeenCalledTimes(1);
  });
  it('is disabled when loading=true', () => {
    render(<Button loading>Go</Button>);
    expect(screen.getByRole('button')).toBeDisabled();
  });
  it('is disabled when disabled=true', () => {
    render(<Button disabled>Go</Button>);
    expect(screen.getByRole('button')).toBeDisabled();
  });
  it('applies btn-icon class when only icon provided (no children)', () => {
    const { container } = render(<Button icon={<span />} />);
    expect(container.firstChild.className).toContain('btn-icon');
  });
  it('shows loading spinner when loading=true', () => {
    const { container } = render(<Button loading>Go</Button>);
    expect(container.querySelector('.btn-loading-spinner')).toBeInTheDocument();
  });
});

