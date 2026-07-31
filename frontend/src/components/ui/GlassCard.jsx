/**
 * GlassCard — the primary surface container for the app.
 */
export default function GlassCard({ children, className = '', hoverable, clickable, style, onClick }) {
  const cls = [
    'glass-card',
    hoverable ? 'hoverable' : '',
    clickable ? 'clickable' : '',
    className,
  ].filter(Boolean).join(' ');

  return (
    <div className={cls} style={style} onClick={onClick}>
      {children}
    </div>
  );
}
