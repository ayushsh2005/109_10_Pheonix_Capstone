/**
 * Button component — primary / secondary / ghost / danger variants.
 */
export default function Button({
  children,
  variant = 'primary',
  size,
  icon,
  loading,
  className = '',
  type = 'button',
  ...rest
}) {
  const cls = [
    'btn',
    `btn-${variant}`,
    size === 'sm' ? 'btn-sm' : size === 'lg' ? 'btn-lg' : '',
    icon && !children ? 'btn-icon' : '',
    className,
  ].filter(Boolean).join(' ');

  return (
    <button className={cls} type={type} disabled={loading || rest.disabled} {...rest}>
      {loading ? (
        <span className="btn-loading-spinner" aria-hidden />
      ) : (
        icon && <span aria-hidden>{icon}</span>
      )}
      {children}
    </button>
  );
}
