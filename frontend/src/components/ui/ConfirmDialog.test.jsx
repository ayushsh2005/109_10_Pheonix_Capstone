import { render, screen, fireEvent } from '@testing-library/react';
import ConfirmDialog from './ConfirmDialog';

const defaultProps = { open: true, onClose: jest.fn(), onConfirm: jest.fn(), title: 'Delete Item', message: 'Are you sure?' };

beforeEach(() => jest.clearAllMocks());

describe('ConfirmDialog — render', () => {
  it('renders nothing when open=false', () => {
    render(<ConfirmDialog {...defaultProps} open={false} />);
    expect(screen.queryByText('Delete Item')).not.toBeInTheDocument();
  });
  it('renders title', () => {
    render(<ConfirmDialog {...defaultProps} />);
    expect(screen.getByText('Delete Item')).toBeInTheDocument();
  });
  it('renders message', () => {
    render(<ConfirmDialog {...defaultProps} />);
    expect(screen.getByText('Are you sure?')).toBeInTheDocument();
  });
  it('renders confirm button with confirmLabel', () => {
    render(<ConfirmDialog {...defaultProps} confirmLabel="Yes, Delete" />);
    expect(screen.getByRole('button', { name: 'Yes, Delete' })).toBeInTheDocument();
  });
  it('renders Cancel button', () => {
    render(<ConfirmDialog {...defaultProps} />);
    expect(screen.getByRole('button', { name: 'Cancel' })).toBeInTheDocument();
  });
});

describe('ConfirmDialog — interaction', () => {
  it('calls onConfirm when confirm button clicked', () => {
    render(<ConfirmDialog {...defaultProps} confirmLabel="Delete" />);
    fireEvent.click(screen.getByRole('button', { name: 'Delete' }));
    expect(defaultProps.onConfirm).toHaveBeenCalledTimes(1);
  });
  it('calls onClose when Cancel is clicked', () => {
    render(<ConfirmDialog {...defaultProps} />);
    fireEvent.click(screen.getByRole('button', { name: 'Cancel' }));
    expect(defaultProps.onClose).toHaveBeenCalledTimes(1);
  });
  it('renders default title when title not provided', () => {
    render(<ConfirmDialog open onClose={jest.fn()} onConfirm={jest.fn()} />);
    expect(screen.getByText('Are you sure?')).toBeInTheDocument();
  });
  it('renders default message when message not provided', () => {
    render(<ConfirmDialog open onClose={jest.fn()} onConfirm={jest.fn()} />);
    expect(screen.getByText('This action cannot be undone.')).toBeInTheDocument();
  });
});

