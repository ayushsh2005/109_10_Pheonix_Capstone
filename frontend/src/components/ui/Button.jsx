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
  onClick,
  ...rest
}) {
  const cls = [
    'btn',
    `btn-${variant}`,
    size === 'sm' ? 'btn-sm' : size === 'lg' ? 'btn-lg' : '',
    icon && !children ? 'btn-icon' : '',
    className,
  ].filter(Boolean).join(' ');

  const handleClick = (e) => {
    // Ripple effect
    const btn = e.currentTarget;
    const rect = btn.getBoundingClientRect();
    const ripple = document.createElement('span');
    ripple.className = 'btn-ripple';
    ripple.style.left = `${e.clientX - rect.left}px`;
    ripple.style.top  = `${e.clientY - rect.top}px`;
    btn.appendChild(ripple);
    ripple.addEventListener('animationend', () => ripple.remove());
    onClick?.(e);
  };

  return (
    <button className={cls} type={type} disabled={loading || rest.disabled} onClick={handleClick} {...rest}>
      {loading ? (
        <span className="btn-loading-spinner" aria-hidden />
      ) : (
        icon && <span aria-hidden>{icon}</span>
      )}
      {children}
    </button>
  );
}
