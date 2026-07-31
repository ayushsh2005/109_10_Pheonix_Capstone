const VARIANTS = {
  success:  'badge-success',
  warning:  'badge-warning',
  danger:   'badge-danger',
  error:    'badge-danger',
  info:     'badge-info',
  neutral:  'badge-neutral',
  primary:  'badge-primary',
  // Semantic mappings
  Active:        'badge-success',
  Conservative:  'badge-info',
  Moderate:      'badge-warning',
  Aggressive:    'badge-danger',
  High:          'badge-danger',
  Medium:        'badge-warning',
  Low:           'badge-success',
  Diversification: 'badge-info',
  Risk:          'badge-danger',
  Opportunity:   'badge-success',
  Stock:         'badge-primary',
  Bond:          'badge-info',
  ETF:           'badge-success',
  Cash:          'badge-neutral',
  Others:        'badge-neutral',
};

export default function Badge({ label, variant }) {
  const cls = VARIANTS[variant] || VARIANTS[label] || 'badge-neutral';
  return <span className={`badge ${cls}`}>{label}</span>;
}
