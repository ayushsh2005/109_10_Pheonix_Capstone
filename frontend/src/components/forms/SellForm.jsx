import { useState, useEffect } from 'react';
import Modal from '../ui/Modal';
import Button from '../ui/Button';
import { AlertCircle } from 'lucide-react';
import { formatCurrencyPrecise } from '../../utils/formatters';

export default function SellForm({ open, onClose, onSubmit, investment, loading }) {
  const [fields, setFields] = useState({ quantity: '', sellPrice: '', tradeDate: '' });
  const [errors, setErrors] = useState({});

  const maxQty = investment?.quantity ?? 0;

  useEffect(() => {
    if (open && investment) {
      setFields({
        quantity:  String(maxQty),
        sellPrice: String(investment.currentPrice || ''),
        tradeDate: new Date().toISOString().split('T')[0],
      });
      setErrors({});
    }
  }, [open, investment, maxQty]);

  const validate = (f) => {
    const errs = {};
    const qty = Number(f.quantity);
    if (!f.quantity || isNaN(qty) || qty <= 0) errs.quantity = 'Enter a valid quantity';
    else if (!Number.isInteger(qty))           errs.quantity = 'Quantity must be a whole number';
    else if (qty > maxQty)                     errs.quantity = `Cannot exceed ${maxQty} units held`;
    const price = Number(f.sellPrice);
    if (!f.sellPrice || isNaN(price) || price <= 0) errs.sellPrice = 'Enter a valid sell price';
    if (!f.tradeDate) errs.tradeDate = 'Trade date is required';
    else if (new Date(f.tradeDate) > new Date())    errs.tradeDate = 'Date cannot be in the future';
    return errs;
  };

  const set = (key, val) => setFields(f => ({ ...f, [key]: val }));

  const handleSubmit = () => {
    const errs = validate(fields);
    setErrors(errs);
    if (!Object.keys(errs).length) {
      onSubmit({ quantity: Number(fields.quantity), sellPrice: Number(fields.sellPrice), tradeDate: fields.tradeDate });
    }
  };

  const qty   = Number(fields.quantity)   || 0;
  const price = Number(fields.sellPrice)  || 0;
  const realisedPL = investment ? (price - investment.purchasePrice) * qty : 0;
  const showPreview = investment && qty > 0 && price > 0;

  return (
    <Modal
      open={open}
      onClose={onClose}
      title="Record Sell Trade"
      subtitle={investment ? `${investment.assetName} · Holding: ${maxQty} units @ ${formatCurrencyPrecise(investment.currentPrice)}` : ''}
      footer={
        <>
          <Button variant="secondary" onClick={onClose} disabled={loading}>Cancel</Button>
          <Button variant="primary" onClick={handleSubmit} loading={loading}>Confirm Sale</Button>
        </>
      }
    >
      <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
        {showPreview && (
          <div style={{
            padding: '12px 16px', borderRadius: 'var(--r-md)',
            background: realisedPL >= 0 ? 'var(--success-bg)' : 'var(--danger-bg)',
            border: `1px solid ${realisedPL >= 0 ? 'rgba(52,199,89,0.3)' : 'rgba(255,59,48,0.3)'}`,
          }}>
            <div style={{ fontSize: 10, fontWeight: 600, color: 'var(--text-tertiary)', textTransform: 'uppercase', letterSpacing: '0.5px', marginBottom: 4 }}>
              Estimated Realised P&L
            </div>
            <div style={{ fontSize: 22, fontWeight: 800, color: realisedPL >= 0 ? 'var(--success)' : 'var(--danger)', letterSpacing: '-0.5px' }}>
              {realisedPL >= 0 ? '+' : ''}{new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(realisedPL)}
            </div>
          </div>
        )}

        <div className="form-grid">
          <Field label="Quantity to Sell" error={errors.quantity}>
            <input className={`form-input ${errors.quantity ? 'error' : ''}`} type="number" min="1" max={maxQty} step="1" value={fields.quantity} onChange={e => set('quantity', e.target.value)} />
          </Field>
          <Field label="Sell Price per Unit (₹)" error={errors.sellPrice}>
            <input className={`form-input ${errors.sellPrice ? 'error' : ''}`} type="number" min="0.01" step="0.01" value={fields.sellPrice} onChange={e => set('sellPrice', e.target.value)} />
          </Field>
        </div>

        <Field label="Trade Date" error={errors.tradeDate}>
          <input className={`form-input ${errors.tradeDate ? 'error' : ''}`} type="date" max={new Date().toISOString().split('T')[0]} value={fields.tradeDate} onChange={e => set('tradeDate', e.target.value)} />
        </Field>
      </div>
    </Modal>
  );
}

function Field({ label, error, children }) {
  return (
    <div className="form-group">
      <label className="form-label">{label}</label>
      {children}
      {error && <div className="form-error"><AlertCircle size={12} />{error}</div>}
    </div>
  );
}
