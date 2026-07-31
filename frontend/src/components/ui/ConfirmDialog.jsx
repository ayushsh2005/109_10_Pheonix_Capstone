import Modal from './Modal';
import Button from './Button';
import { AlertTriangle } from 'lucide-react';

export default function ConfirmDialog({ open, onClose, onConfirm, title, message, confirmLabel = 'Delete', loading }) {
  return (
    <Modal open={open} onClose={onClose} size="sm">
      <div className="confirm-dialog-body">
        <div className="confirm-dialog-icon" style={{ background: 'var(--danger-bg)', color: 'var(--danger)' }}>
          <AlertTriangle size={22} />
        </div>
        <div className="confirm-dialog-title">{title || 'Are you sure?'}</div>
        <p className="confirm-dialog-msg">{message || 'This action cannot be undone.'}</p>
      </div>
      <div className="confirm-dialog-footer">
        <Button variant="secondary" onClick={onClose} disabled={loading}>Cancel</Button>
        <Button variant="danger" onClick={onConfirm} loading={loading}>{confirmLabel}</Button>
      </div>
    </Modal>
  );
}
