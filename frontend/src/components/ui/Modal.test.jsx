import { render, screen, fireEvent } from '@testing-library/react';
import Modal from './Modal';

describe('Modal — render', () => {
  it('renders nothing when open=false', () => {
    render(<Modal open={false} title="Test"><p>Content</p></Modal>);
    expect(screen.queryByText('Test')).not.toBeInTheDocument();
  });
  it('renders title when open=true', () => {
    render(<Modal open title="My Modal"><p>Body</p></Modal>);
    expect(screen.getByText('My Modal')).toBeInTheDocument();
  });
  it('renders children', () => {
    render(<Modal open title="T"><p>Modal body</p></Modal>);
    expect(screen.getByText('Modal body')).toBeInTheDocument();
  });
  it('renders subtitle when provided', () => {
    render(<Modal open title="T" subtitle="Sub"><p>X</p></Modal>);
    expect(screen.getByText('Sub')).toBeInTheDocument();
  });
  it('renders footer when provided', () => {
    render(<Modal open title="T" footer={<button>Save</button>}><p>X</p></Modal>);
    expect(screen.getByRole('button', { name: 'Save' })).toBeInTheDocument();
  });
  it('renders close button', () => {
    render(<Modal open title="T"><p>X</p></Modal>);
    expect(screen.getByLabelText('Close modal')).toBeInTheDocument();
  });
});

describe('Modal — interaction', () => {
  it('calls onClose when X button is clicked', () => {
    const fn = jest.fn();
    render(<Modal open title="T" onClose={fn}><p>X</p></Modal>);
    fireEvent.click(screen.getByLabelText('Close modal'));
    expect(fn).toHaveBeenCalledTimes(1);
  });
  it('calls onClose when backdrop is clicked', () => {
    const fn = jest.fn();
    render(<Modal open title="T" onClose={fn}><p>X</p></Modal>);
    fireEvent.click(screen.getByRole('dialog').parentElement);
    expect(fn).toHaveBeenCalledTimes(1);
  });
  it('renders with size=lg applies wider max-width', () => {
    render(<Modal open title="T" size="lg"><p>X</p></Modal>);
    expect(screen.getByRole('dialog').style.maxWidth).toBe('680px');
  });
  it('renders with size=sm applies narrower max-width', () => {
    render(<Modal open title="T" size="sm"><p>X</p></Modal>);
    expect(screen.getByRole('dialog').style.maxWidth).toBe('380px');
  });
});

