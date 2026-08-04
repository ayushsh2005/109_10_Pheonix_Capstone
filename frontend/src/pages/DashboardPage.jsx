import { useState, useEffect } from 'react';
import { Users, TrendingUp, IndianRupee, BarChart2 } from 'lucide-react';
import StatCard from '../components/ui/StatCard';
import GlassCard from '../components/ui/GlassCard';
import AllocationChart from '../components/charts/AllocationChart';
import PerformanceChart from '../components/charts/PerformanceChart';
import { Skeleton } from '../components/ui/Skeleton';
import { getDashboard } from '../api/services/dashboard';
import { formatCurrency, formatReturnPct } from '../utils/formatters';
import { useToast } from '../context/ToastContext';

export default function DashboardPage() {
  const [data, setData]       = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError]     = useState(null);
  const toast = useToast();

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    getDashboard()
      .then(d => { if (!cancelled) { setData(d); setLoading(false); } })
      .catch(e => {
        if (!cancelled) {
          setError(e.message);
          setLoading(false);
          toast.error(e.message, 'Failed to load dashboard');
        }
      });
    return () => { cancelled = true; };
  }, [toast]);

  const greeting = (() => {
    const h = new Date().getHours();
    if (h < 12) return 'Good morning';
    if (h < 17) return 'Good afternoon';
    return 'Good evening';
  })();

  if (error) {
    return (
      <div style={{ padding: 40, textAlign: 'center', color: 'var(--danger)' }}>
        Failed to load dashboard: {error}
      </div>
    );
  }

  const s = data?.summary;

  return (
    <>
      {/* Header */}
      <div className="page-header">
        <div className="page-header-left">
          <h1 className="page-title">{greeting}, Manager</h1>
          <p className="page-subtitle">
            {new Date().toLocaleDateString('en-GB', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' })}
          </p>
        </div>
      </div>

      {/* KPI Stats */}
      <div className="stats-grid">
        <StatCard
          label="Total Customers"
          value={loading ? <Skeleton width={80} height={26} /> : (s?.totalCustomers ?? '—')}
          icon={<Users size={18} />}
          iconBg="var(--info-bg)"
          iconColor="var(--info)"
          delta={loading ? undefined : 8.3}
          deltaLabel="vs last quarter"
        />
        <StatCard
          label="Assets Managed"
          value={loading ? <Skeleton width={120} height={26} /> : formatCurrency(s?.totalAssetsManaged)}
          icon={<IndianRupee size={18} />}
          iconBg="var(--primary-subtle)"
          iconColor="var(--primary)"
          delta={loading ? undefined : 12.1}
          deltaLabel="vs last quarter"
        />
        <StatCard
          label="Portfolio Value"
          value={loading ? <Skeleton width={120} height={26} /> : formatCurrency(s?.portfolioValue)}
          icon={<TrendingUp size={18} />}
          iconBg="var(--success-bg)"
          iconColor="var(--success)"
          delta={loading ? undefined : s?.returnPercentage}
          deltaLabel="overall return"
        />
        <StatCard
          label="Total Return"
          value={loading ? <Skeleton width={80} height={26} /> : formatReturnPct(s?.returnPercentage)}
          icon={<BarChart2 size={18} />}
          iconBg="var(--warning-bg)"
          iconColor="var(--warning)"
          delta={loading ? undefined : 2.4}
          deltaLabel="vs last month"
        />
      </div>

      {/* Charts */}
      <div className="charts-grid">
        {/* Allocation */}
        <GlassCard>
          <div className="chart-card">
            <div className="chart-title">Asset Allocation</div>
            <div className="chart-subtitle">Distribution across asset classes</div>
            {loading
              ? <Skeleton height={200} style={{ borderRadius: 'var(--r-lg)' }} />
              : <AllocationChart data={data?.allocation ?? []} />
            }
          </div>
        </GlassCard>

        {/* Performance Trend */}
        <GlassCard>
          <div className="chart-card">
            <div className="chart-title">Portfolio Performance</div>
            <div className="chart-subtitle">Total portfolio value over time</div>
            {loading
              ? <Skeleton height={220} style={{ borderRadius: 'var(--r-lg)' }} />
              : <PerformanceChart data={data?.performanceTrend ?? []} />
            }
          </div>
        </GlassCard>
      </div>

      {/* Summary row */}
      {!loading && s && (
        <GlassCard>
          <div className="dashboard-summary-grid">
            <SummaryItem label="Invested Capital" value={formatCurrency(s.totalAssetsManaged)} />
            <SummaryItem label="Current Value"    value={formatCurrency(s.portfolioValue)} />
            <SummaryItem
              label="Total Profit / Loss"
              value={formatCurrency(s.overallProfitLoss)}
              positive={s.overallProfitLoss >= 0}
            />
            <SummaryItem
              label="Return Rate"
              value={formatReturnPct(s.returnPercentage)}
              positive={s.returnPercentage >= 0}
            />
          </div>
        </GlassCard>
      )}
    </>
  );
}

function SummaryItem({ label, value, positive }) {
  return (
    <div className="dashboard-summary-item">
      <div style={{ fontSize: 11, fontWeight: 600, color: 'var(--text-tertiary)', textTransform: 'uppercase', letterSpacing: '0.5px', marginBottom: 6 }}>
        {label}
      </div>
      <div
        className="dashboard-summary-value"
        style={{ fontSize: 18, fontWeight: 700, color: positive === undefined ? 'var(--text-primary)' : positive ? 'var(--success)' : 'var(--danger)' }}
      >
        {value}
      </div>
    </div>
  );
}
