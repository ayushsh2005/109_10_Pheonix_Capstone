import { useState, useEffect } from 'react';
import Modal from '../ui/Modal';
import Button from '../ui/Button';
import { AlertCircle } from 'lucide-react';

const ASSET_TYPES = ['Stock', 'Bond', 'ETF', 'Cash', 'Others'];

const EMPTY = {
  assetName:     '',
  assetType:     '',
  ticker:        '',
  quantity:      '',
  purchasePrice: '',
  currentPrice:  '',
  purchaseDate:  '',
};

function validate(f) {
  const errs = {};
  if (!f.assetName.trim())    errs.assetName = 'Asset name is required';
  if (!f.assetType)           errs.assetType = 'Asset type is required';

  const qty = Number(f.quantity);
  if (f.quantity === '' || isNaN(qty) || qty <= 0)
    errs.quantity = 'Quantity must be a positive number';
  if (qty > 1_000_000)
    errs.quantity = 'Quantity exceeds maximum limit';

  const pp = Number(f.purchasePrice);
  if (f.purchasePrice === '' || isNaN(pp) || pp <= 0)
    errs.purchasePrice = 'Purchase price must be positive';

  const cp = Number(f.currentPrice);
  if (f.currentPrice === '' || isNaN(cp) || cp <= 0)
    errs.currentPrice = 'Current price must be positive';

  if (!f.purchaseDate)
    errs.purchaseDate = 'Purchase date is required';
  else if (new Date(f.purchaseDate) > new Date())
    errs.purchaseDate = 'Purchase date cannot be in the future';

  return errs;
}

export default function InvestmentForm({ open, onClose, onSubmit, initial, loading, customerName }) {
  const [fields, setFields] = useState(EMPTY);
  const [errors, setErrors] = useState({});
  const [touched, setTouched] = useState({});

  useEffect(() => {
    if (open) {
      if (initial) {
        setFields({
          assetName:     initial.assetName     || '',
          assetType:     initial.assetType     || '',
          ticker:        initial.ticker        || '',
          quantity:      String(initial.quantity      || ''),
          purchasePrice: String(initial.purchasePrice || ''),
          currentPrice:  String(initial.currentPrice  || ''),
          purchaseDate:  initial.purchaseDate  || '',
        });
      } else {
        setFields(EMPTY);
      }
      setErrors({});
      setTouched({});
    }
  }, [open, initial]);

  const set = (key, val) => {
    // Sanitise ticker to uppercase letters only
    const safeVal = key === 'ticker' ? val.toUpperCase().replace(/[^A-Z0-9.]/g, '') : val;
    setFields(f => ({ ...f, [key]: safeVal }));
    if (touched[key]) {
      const errs = validate({ ...fields, [key]: safeVal });
      setErrors(e => ({ ...e, [key]: errs[key] }));
    }
  };

  const blur = (key) => {
    setTouched(t => ({ ...t, [key]: true }));
    const errs = validate(fields);
    setErrors(e => ({ ...e, [key]: errs[key] }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const allTouched = Object.fromEntries(Object.keys(fields).map(k => [k, true]));
    setTouched(allTouched);
    const errs = validate(fields);
    setErrors(errs);
    if (Object.keys(errs).length === 0) {
      onSubmit({
        ...fields,
        quantity:      Number(fields.quantity),
        purchasePrice: Number(fields.purchasePrice),
        currentPrice:  Number(fields.currentPrice),
      });
    }
  };

  const isEdit = Boolean(initial);
  const today = new Date().toISOString().split('T')[0];

  return (
    <Modal
      open={open}
      onClose={onClose}
      title={isEdit ? 'Edit Investment' : 'Add Investment'}
      subtitle={customerName ? `Customer: ${customerName}` : 'Record a new investment position'}
      footer={
        <>
          <Button variant="secondary" onClick={onClose} disabled={loading}>Cancel</Button>
          <Button variant="primary" onClick={handleSubmit} loading={loading}>
            {isEdit ? 'Save Changes' : 'Add Investment'}
          </Button>
        </>
      }
    >
      <form onSubmit={handleSubmit} noValidate>
        <div className="form-grid">
          <Field label="Asset Name" required error={errors.assetName}>
            <input
              className={`form-input ${errors.assetName ? 'error' : ''}`}
              type="text"
              placeholder="e.g. Apple Inc."
              value={fields.assetName}
              onChange={e => set('assetName', e.target.value)}
              onBlur={() => blur('assetName')}
              maxLength={100}
              autoFocus
            />
          </Field>

          <Field label="Asset Type" required error={errors.assetType}>
            <select
              className={`form-select ${errors.assetType ? 'error' : ''}`}
              value={fields.assetType}
              onChange={e => set('assetType', e.target.value)}
              onBlur={() => blur('assetType')}
            >
              <option value="">Select type</option>
              {ASSET_TYPES.map(t => <option key={t} value={t}>{t}</option>)}
            </select>
          </Field>

          <Field label="Ticker Symbol" error={errors.ticker}>
            <input
              className="form-input"
              type="text"
              placeholder="AAPL"
              value={fields.ticker}
              onChange={e => set('ticker', e.target.value)}
              maxLength={10}
            />
          </Field>

          <Field label="Quantity" required error={errors.quantity}>
            <input
              className={`form-input ${errors.quantity ? 'error' : ''}`}
              type="number"
              placeholder="100"
              value={fields.quantity}
              onChange={e => set('quantity', e.target.value)}
              onBlur={() => blur('quantity')}
              min="1"
              step="1"
            />
          </Field>

          <Field label="Purchase Price (₹)" required error={errors.purchasePrice}>
            <input
              className={`form-input ${errors.purchasePrice ? 'error' : ''}`}
              type="number"
              placeholder="150.00"
              value={fields.purchasePrice}
              onChange={e => set('purchasePrice', e.target.value)}
              onBlur={() => blur('purchasePrice')}
              min="0.01"
              step="0.01"
            />
          </Field>

          <Field label="Current Price (₹)" required error={errors.currentPrice}>
            <input
              className={`form-input ${errors.currentPrice ? 'error' : ''}`}
              type="number"
              placeholder="165.00"
              value={fields.currentPrice}
              onChange={e => set('currentPrice', e.target.value)}
              onBlur={() => blur('currentPrice')}
              min="0.01"
              step="0.01"
            />
          </Field>
        </div>

        <Field label="Purchase Date" required error={errors.purchaseDate}>
          <input
            className={`form-input ${errors.purchaseDate ? 'error' : ''}`}
            type="date"
            value={fields.purchaseDate}
            onChange={e => set('purchaseDate', e.target.value)}
            onBlur={() => blur('purchaseDate')}
            max={today}
          />
        </Field>
      </form>
    </Modal>
  );
}

function Field({ label, required, error, children }) {
  return (
    <div className="form-group">
      <label className="form-label">
        {label}{required && <span className="required">*</span>}
      </label>
      {children}
      {error && (
        <div className="form-error"><AlertCircle size={12} />{error}</div>
      )}
    </div>
  );
}
