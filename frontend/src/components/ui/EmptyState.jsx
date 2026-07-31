import Button from './Button';

export default function EmptyState({ icon, title, description, action, actionLabel }) {
  return (
    <div className="empty-state">
      {icon && (
        <div className="empty-state-icon" aria-hidden="true">{icon}</div>
      )}
      <div className="empty-state-title">{title}</div>
      {description && <p className="empty-state-desc">{description}</p>}
      {action && actionLabel && (
        <Button variant="primary" onClick={action} style={{ marginTop: 4 }}>
          {actionLabel}
        </Button>
      )}
    </div>
  );
}
