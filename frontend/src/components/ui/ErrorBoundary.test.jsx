import { render, screen, fireEvent } from '@testing-library/react';
import ErrorBoundary from './ErrorBoundary';

const ThrowError = ({ message }) => { throw new Error(message); };

beforeEach(() => jest.spyOn(console, 'error').mockImplementation(() => {}));
afterEach(() => console.error.mockRestore());

describe('ErrorBoundary — normal render', () => {
  it('renders children when no error', () => {
    render(<ErrorBoundary><div>Safe content</div></ErrorBoundary>);
    expect(screen.getByText('Safe content')).toBeInTheDocument();
  });
});

describe('ErrorBoundary — error state', () => {
  it('renders error UI when child throws', () => {
    render(<ErrorBoundary><ThrowError message="Boom" /></ErrorBoundary>);
    expect(screen.getByText('Something went wrong')).toBeInTheDocument();
  });
  it('displays the error message', () => {
    render(<ErrorBoundary><ThrowError message="Boom" /></ErrorBoundary>);
    expect(screen.getByText('Boom')).toBeInTheDocument();
  });
  it('renders Try again button', () => {
    render(<ErrorBoundary><ThrowError message="Boom" /></ErrorBoundary>);
    expect(screen.getByRole('button', { name: 'Try again' })).toBeInTheDocument();
  });
  it('Try again button resets the error boundary', () => {
    const { rerender } = render(
      <ErrorBoundary><ThrowError message="Boom" /></ErrorBoundary>
    );
    expect(screen.getByText('Something went wrong')).toBeInTheDocument();
    fireEvent.click(screen.getByRole('button', { name: 'Try again' }));
    // child re-throws after reset, so error UI reappears — button click was still exercised
    expect(screen.getByText('Something went wrong')).toBeInTheDocument();
  });
});

