import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import { ToastProvider, useToast } from './ToastContext';

function ToastTrigger({ action }) {
  const toast = useToast();
  return <button onClick={() => toast[action]('Test message', 'Test Title')}>Trigger</button>;
}
const renderWith = (action) =>
  render(<ToastProvider><ToastTrigger action={action} /></ToastProvider>);

describe('useToast - guard', () => {
  it('throws when used outside ToastProvider', () => {
    const Broken = () => { useToast(); return null; };
    expect(() => render(<Broken />)).toThrow('useToast must be used within ToastProvider');
  });
});
describe('ToastProvider - success', () => {
  it('renders toast with message', async () => {
    renderWith('success');
    fireEvent.click(screen.getByText('Trigger'));
    await waitFor(() => expect(screen.getByText('Test message')).toBeInTheDocument());
  });
  it('renders title when provided', async () => {
    renderWith('success');
    fireEvent.click(screen.getByText('Trigger'));
    await waitFor(() => expect(screen.getByText('Test Title')).toBeInTheDocument());
  });
  it('renders dismiss button', async () => {
    renderWith('success');
    fireEvent.click(screen.getByText('Trigger'));
    await waitFor(() => expect(screen.getByLabelText('Dismiss notification')).toBeInTheDocument());
  });
});
describe('ToastProvider - variants', () => {
  it('renders error toast', async () => {
    renderWith('error');
    fireEvent.click(screen.getByText('Trigger'));
    await waitFor(() => expect(screen.getByText('Test message')).toBeInTheDocument());
  });
  it('renders warning toast', async () => {
    renderWith('warning');
    fireEvent.click(screen.getByText('Trigger'));
    await waitFor(() => expect(screen.getByText('Test message')).toBeInTheDocument());
  });
  it('renders info toast', async () => {
    renderWith('info');
    fireEvent.click(screen.getByText('Trigger'));
    await waitFor(() => expect(screen.getByText('Test message')).toBeInTheDocument());
  });
});
describe('ToastProvider - string form', () => {
  it('renders toast when called with plain string', async () => {
    function StringTrigger() {
      const toast = useToast();
      return <button onClick={() => toast.info('Simple message')}>Go</button>;
    }
    render(<ToastProvider><StringTrigger /></ToastProvider>);
    fireEvent.click(screen.getByText('Go'));
    await waitFor(() => expect(screen.getByText('Simple message')).toBeInTheDocument());
  });
});

describe('ToastProvider - dismiss', () => {
  it('removes toast after dismiss click', async () => {
    jest.useFakeTimers();
    renderWith('success');
    fireEvent.click(screen.getByText('Trigger'));
    await waitFor(() => screen.getByLabelText('Dismiss notification'));
    fireEvent.click(screen.getByLabelText('Dismiss notification'));
    act(() => jest.advanceTimersByTime(300));
    await waitFor(() => expect(screen.queryByText('Test message')).not.toBeInTheDocument());
    jest.useRealTimers();
  });
  it('supports multiple simultaneous toasts', async () => {
    renderWith('success');
    fireEvent.click(screen.getByText('Trigger'));
    fireEvent.click(screen.getByText('Trigger'));
    await waitFor(() => expect(screen.getAllByText('Test message').length).toBe(2));
  });
});
