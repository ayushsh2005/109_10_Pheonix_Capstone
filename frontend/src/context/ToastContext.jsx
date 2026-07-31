import { createContext, useContext, useState, useCallback, useRef } from 'react';
import { CheckCircle, XCircle, AlertTriangle, Info, X } from 'lucide-react';

const ToastContext = createContext(null);

function ToastContainer({ toasts, onRemove }) {
  return (
    <div className="toast-container" role="region" aria-label="Notifications" aria-live="polite">
      {toasts.map(t => (
        <Toast key={t.id} toast={t} onRemove={onRemove} />
      ))}
    </div>
  );
}

function Toast({ toast, onRemove }) {
  const icons = {
    success: <CheckCircle size={16} />,
    error:   <XCircle    size={16} />,
    warning: <AlertTriangle size={16} />,
    info:    <Info       size={16} />,
  };

  return (
    <div className={`toast ${toast.type} ${toast.removing ? 'removing' : ''}`} role="alert">
      <span className="toast-icon">{icons[toast.type] ?? icons.info}</span>
      <div className="toast-content">
        {toast.title   && <div className="toast-title">{toast.title}</div>}
        {toast.message && <div className="toast-message">{toast.message}</div>}
      </div>
      <button
        className="toast-close"
        onClick={() => onRemove(toast.id)}
        aria-label="Dismiss notification"
      >
        <X size={12} />
      </button>
    </div>
  );
}

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);
  const timers = useRef({});

  const removeToast = useCallback((id) => {
    setToasts(prev => prev.map(t => t.id === id ? { ...t, removing: true } : t));
    setTimeout(() => {
      setToasts(prev => prev.filter(t => t.id !== id));
    }, 260);
  }, []);

  const addToast = useCallback((options, duration = 4000) => {
    const id = `${Date.now()}-${Math.random()}`;
    const toast = typeof options === 'string'
      ? { id, type: 'info', message: options }
      : { id, type: 'info', ...options };

    setToasts(prev => [...prev, toast]);
    timers.current[id] = setTimeout(() => removeToast(id), duration);
    return id;
  }, [removeToast]);

  const toast = {
    success: (msg, title)  => addToast({ type: 'success', message: msg, title }),
    error:   (msg, title)  => addToast({ type: 'error',   message: msg, title }, 6000),
    warning: (msg, title)  => addToast({ type: 'warning', message: msg, title }),
    info:    (msg, title)  => addToast({ type: 'info',    message: msg, title }),
  };

  return (
    <ToastContext.Provider value={toast}>
      {children}
      <ToastContainer toasts={toasts} onRemove={removeToast} />
    </ToastContext.Provider>
  );
}

export const useToast = () => {
  const ctx = useContext(ToastContext);
  if (!ctx) throw new Error('useToast must be used within ToastProvider');
  return ctx;
};
