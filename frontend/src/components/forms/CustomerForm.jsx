import { useState, useEffect } from 'react';
import Modal from '../ui/Modal';
import Button from '../ui/Button';
import { AlertCircle } from 'lucide-react';

const RISK_PROFILES = ['Conservative', 'Moderate', 'Aggressive'];
const GOALS = ['Retirement Planning', 'Wealth Growth', 'Capital Appreciation', 'Income Generation', 'Education', 'Emergency Fund'];

const EMPTY = { name: '', email: '', phone: '', riskProfile: '', investmentGoal: '' };

function validate(f) {
  const errs = {};
  if (!f.name.trim())               errs.name = 'Name is required';
  else if (f.name.trim().length < 2) errs.name = 'Name must be at least 2 characters';

  const emailRe = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!f.email.trim())              errs.email = 'Email is required';
  else if (!emailRe.test(f.email))  errs.email = 'Enter a valid email address';

  if (f.phone && !/^[\d\s\+\-\(\)]{7,20}$/.test(f.phone))
    errs.phone = 'Enter a valid phone number';

  if (!f.riskProfile)    errs.riskProfile = 'Risk profile is required';
  if (!f.investmentGoal) errs.investmentGoal = 'Investment goal is required';

  return errs;
}

export default function CustomerForm({ open, onClose, onSubmit, initial, loading }) {
  const [fields, setFields] = useState(EMPTY);
  const [errors, setErrors] = useState({});
  const [touched, setTouched] = useState({});

  useEffect(() => {
    if (open) {
      setFields(initial ? { name: initial.name || '', email: initial.email || '', phone: initial.phone || '', riskProfile: initial.riskProfile || '', investmentGoal: initial.investmentGoal || '' } : EMPTY);
      setErrors({});
      setTouched({});
    }
  }, [open, initial]);

  const set = (key, val) => {
    setFields(f => ({ ...f, [key]: val }));
    if (touched[key]) {
      const errs = validate({ ...fields, [key]: val });
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
      onSubmit(fields);
    }
  };

  const isEdit = Boolean(initial);

  return (
    <Modal
      open={open}
      onClose={onClose}
      title={isEdit ? 'Edit Customer' : 'Add Customer'}
      subtitle={isEdit ? 'Update customer information' : 'Create a new investor profile'}
      footer={
        <>
          <Button variant="secondary" onClick={onClose} disabled={loading}>Cancel</Button>
          <Button variant="primary" onClick={handleSubmit} loading={loading} type="submit">
            {isEdit ? 'Save Changes' : 'Create Customer'}
          </Button>
        </>
      }
    >
      <form onSubmit={handleSubmit} noValidate>
        <div className="form-grid">
          <Field label="Full Name" required error={errors.name}>
            <input
              className={`form-input ${errors.name ? 'error' : ''}`}
              type="text"
              placeholder="James Anderson"
              value={fields.name}
              onChange={e => set('name', e.target.value)}
              onBlur={() => blur('name')}
              maxLength={100}
              autoFocus
            />
          </Field>

          <Field label="Email Address" required error={errors.email}>
            <input
              className={`form-input ${errors.email ? 'error' : ''}`}
              type="email"
              placeholder="james@example.com"
              value={fields.email}
              onChange={e => set('email', e.target.value)}
              onBlur={() => blur('email')}
              maxLength={254}
            />
          </Field>

          <Field label="Phone Number" error={errors.phone}>
            <input
              className={`form-input ${errors.phone ? 'error' : ''}`}
              type="tel"
              placeholder="+91 98765 43210"
              value={fields.phone}
              onChange={e => set('phone', e.target.value)}
              onBlur={() => blur('phone')}
              maxLength={25}
            />
          </Field>

          <Field label="Risk Profile" required error={errors.riskProfile}>
            <select
              className={`form-select ${errors.riskProfile ? 'error' : ''}`}
              value={fields.riskProfile}
              onChange={e => set('riskProfile', e.target.value)}
              onBlur={() => blur('riskProfile')}
            >
              <option value="">Select risk profile</option>
              {RISK_PROFILES.map(r => <option key={r} value={r}>{r}</option>)}
            </select>
          </Field>
        </div>

        <Field label="Investment Goal" required error={errors.investmentGoal}>
          <select
            className={`form-select ${errors.investmentGoal ? 'error' : ''}`}
            value={fields.investmentGoal}
            onChange={e => set('investmentGoal', e.target.value)}
            onBlur={() => blur('investmentGoal')}
          >
            <option value="">Select investment goal</option>
            {GOALS.map(g => <option key={g} value={g}>{g}</option>)}
          </select>
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
