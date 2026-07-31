import { useState, useEffect, useCallback } from 'react';
import { Lightbulb, AlertTriangle, TrendingUp, Shuffle } from 'lucide-react';
import GlassCard from '../components/ui/GlassCard';
import Badge from '../components/ui/Badge';
import EmptyState from '../components/ui/EmptyState';
import { SkeletonCard } from '../components/ui/Skeleton';
import { getSuggestions } from '../api/services/suggestions';
import { getCustomers } from '../api/services/customers';
import { useToast } from '../context/ToastContext';

const TYPE_ICONS = {
  Diversification: <Shuffle    size={18} />,
  Risk:            <AlertTriangle size={18} />,
  Opportunity:     <TrendingUp  size={18} />,
};

const SEV_BG    = { High: 'var(--danger-bg)',  Medium: 'var(--warning-bg)', Low: 'var(--success-bg)'  };
const SEV_COLOR = { High: 'var(--danger)',     Medium: 'var(--warning)',    Low: 'var(--success)'     };

const SEV_ORDER = { High: 0, Medium: 1, Low: 2 };

export default function SuggestionsPage() {
  const [suggestions, setSuggestions] = useState([]);
  const [customers,   setCustomers]   = useState([]);
  const [loading,     setLoading]     = useState(true);
  const [error,       setError]       = useState(null);
  const [filter,      setFilter]      = useState('All');
  const toast = useToast();

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [suggs, custs] = await Promise.all([getSuggestions(), getCustomers()]);
      setSuggestions(suggs);
      setCustomers(custs);
    } catch (e) {
      setError(e.message);
      toast.error(e.message, 'Failed to load suggestions');
    } finally {
      setLoading(false);
    }
  }, [toast]);

  useEffect(() => { load(); }, [load]);

  const custMap = Object.fromEntries(customers.map(c => [c.id, c.name]));

  const sorted = [...suggestions].sort((a, b) =>
    (SEV_ORDER[a.severity] ?? 9) - (SEV_ORDER[b.severity] ?? 9)
  );

  const filtered = filter === 'All'
    ? sorted
    : sorted.filter(s => s.severity === filter || s.type === filter);

  const counts = {
    High:   suggestions.filter(s => s.severity === 'High').length,
    Medium: suggestions.filter(s => s.severity === 'Medium').length,
    Low:    suggestions.filter(s => s.severity === 'Low').length,
  };

  return (
    <>
      {/* Header */}
      <div className="page-header">
        <div className="page-header-left">
          <h1 className="page-title">Suggestions</h1>
          <p className="page-subtitle">AI-driven insights and portfolio recommendations</p>
        </div>
      </div>

      {/* Severity summary */}
      {!loading && suggestions.length > 0 && (
        <div style={{ display: 'flex', gap: 12, marginBottom: 20, flexWrap: 'wrap' }}>
          {['All', 'High', 'Medium', 'Low'].map(sev => (
            <button
              key={sev}
              className={`btn ${filter === sev ? 'btn-primary' : 'btn-secondary'} btn-sm`}
              onClick={() => setFilter(sev)}
              aria-pressed={filter === sev}
            >
              {sev}
              {sev !== 'All' && counts[sev] > 0 && (
                <span style={{
                  marginLeft: 6, background: filter === sev ? 'rgba(255,255,255,0.3)' : SEV_BG[sev],
                  color: filter === sev ? '#fff' : SEV_COLOR[sev],
                  borderRadius: 'var(--r-full)', padding: '1px 7px', fontSize: 10, fontWeight: 700,
                }}>
                  {counts[sev]}
                </span>
              )}
            </button>
          ))}
        </div>
      )}

      {/* Error */}
      {error && (
        <GlassCard style={{ marginBottom: 20, padding: '14px 20px', borderColor: 'var(--danger)' }}>
          <p style={{ color: 'var(--danger)', fontSize: 14 }}>{error}</p>
        </GlassCard>
      )}

      {/* Loading */}
      {loading && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          {[1, 2, 3].map(i => <GlassCard key={i}><SkeletonCard rows={2} /></GlassCard>)}
        </div>
      )}

      {/* Empty */}
      {!loading && filtered.length === 0 && (
        <GlassCard>
          <EmptyState
            icon={<Lightbulb size={26} />}
            title="No suggestions"
            description={filter === 'All' ? 'All portfolios are well balanced.' : `No ${filter.toLowerCase()} priority suggestions.`}
          />
        </GlassCard>
      )}

      {/* Suggestion cards */}
      {!loading && filtered.length > 0 && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          {filtered.map(s => (
            <GlassCard key={s.id} hoverable>
              <div style={{ padding: '18px 20px', display: 'flex', alignItems: 'flex-start', gap: 14 }}>
                {/* Icon */}
                <div style={{
                  width: 44, height: 44, borderRadius: 'var(--r-md)',
                  background: SEV_BG[s.severity] || 'var(--info-bg)',
                  color: SEV_COLOR[s.severity] || 'var(--info)',
                  display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0,
                }}>
                  {TYPE_ICONS[s.type] ?? <Lightbulb size={18} />}
                </div>

                {/* Content */}
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8, flexWrap: 'wrap', marginBottom: 6 }}>
                    <span style={{ fontWeight: 700, fontSize: 14, color: 'var(--text-primary)' }}>{s.type}</span>
                    <Badge label={s.severity} variant={s.severity} />
                    <Badge label={s.type} />
                  </div>
                  <p style={{ fontSize: 13.5, color: 'var(--text-secondary)', lineHeight: 1.65, marginBottom: 8 }}>
                    {s.message}
                  </p>
                  {s.customerId && custMap[s.customerId] && (
                    <div style={{ fontSize: 12, color: 'var(--text-tertiary)', display: 'flex', alignItems: 'center', gap: 5 }}>
                      <span style={{ fontSize: 11 }}>👤</span>
                      {custMap[s.customerId]}
                    </div>
                  )}
                </div>
              </div>
            </GlassCard>
          ))}
        </div>
      )}
    </>
  );
}
