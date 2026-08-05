import { useEffect, useRef, useState } from 'react';
import GlassCard from './GlassCard';
import { TrendingUp, TrendingDown } from 'lucide-react';

function useCountUp(target, duration = 600) {
  const [display, setDisplay] = useState(target);
  const raf = useRef(null);
  const started = useRef(false);

  useEffect(() => {
    if (typeof target !== 'number') return;
    // Start from 0 and animate to target
    setDisplay(0);
    started.current = false;
    const startTime = Date.now();
    const animate = () => {
      const elapsed = Date.now() - startTime;
      const progress = Math.min(elapsed / duration, 1);
      const ease = progress === 1 ? 1 : 1 - Math.pow(2, -10 * progress);
      const value = Math.round(target * ease);
      setDisplay(value);
      if (progress < 1) raf.current = requestAnimationFrame(animate);
    };
    cancelAnimationFrame(raf.current);
    raf.current = requestAnimationFrame(animate);
    return () => cancelAnimationFrame(raf.current);
  }, [target, duration]);

  return display;
}

export default function StatCard({ label, value, icon, delta, deltaLabel, iconBg, iconColor }) {
  const isPositive = delta > 0;
  const isNeutral  = delta === 0;
  const animatedDelta = useCountUp(typeof delta === 'number' ? Math.abs(delta) : 0);

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
              {isPositive ? '+' : isNeutral ? '' : '-'}{animatedDelta}%
            </span>
            {deltaLabel && <span className="stat-card-period">{deltaLabel}</span>}
          </div>
        )}
      </div>
    </GlassCard>
  );
}
