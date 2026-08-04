import GlassCard from './GlassCard';
import { TrendingUp, TrendingDown } from 'lucide-react';

export default function StatCard({ label, value, icon, delta, deltaLabel, iconBg, iconColor }) {
  const isPositive = delta > 0;
  const isNeutral  = delta === 0;

  return (
    <GlassCard style={{ borderLeft: `3px solid ${iconColor || 'var(--primary)'}` }}>
      <div className="stat-card">
        <div className="stat-card-header">
          <span className="stat-card-label">{label}</span>
          {icon && (
            <div className="stat-card-icon" style={{ background: iconBg || 'var(--primary-subtle)', color: iconColor || 'var(--primary)' }}>
              {icon}
            </div>
          )}
        </div>

        <div className="stat-card-value">{value}</div>

        {delta !== undefined && (
          <div className="stat-card-footer">
            <span className={`stat-card-delta ${isNeutral ? '' : isPositive ? 'up' : 'down'}`}>
              {isNeutral ? null : isPositive ? <TrendingUp size={12} /> : <TrendingDown size={12} />}
              {isPositive ? '+' : ''}{delta}%
            </span>
            {deltaLabel && <span className="stat-card-period">{deltaLabel}</span>}
          </div>
        )}
      </div>
    </GlassCard>
  );
}
