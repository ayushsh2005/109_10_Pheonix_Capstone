import { useState, useEffect, useCallback, useMemo } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Plus, Pencil, Trash2, TrendingUp, TrendingDown, DollarSign, ChevronRight, History, Target } from 'lucide-react';
import GlassCard from '../components/ui/GlassCard';
import Button from '../components/ui/Button';
import Badge from '../components/ui/Badge';
import ConfirmDialog from '../components/ui/ConfirmDialog';
import InvestmentForm from '../components/forms/InvestmentForm';
import SellForm from '../components/forms/SellForm';
import EmptyState from '../components/ui/EmptyState';
import { SkeletonCard } from '../components/ui/Skeleton';
import { Skeleton } from '../components/ui/Skeleton';
import PerformanceChart from '../components/charts/PerformanceChart';
import AllocationChart from '../components/charts/AllocationChart';
import { getCustomer } from '../api/services/customers';
import { getInvestmentsByCustomer, createInvestment, updateInvestment, deleteInvestment } from '../api/services/investments';
import { getSuggestionsByCustomer } from '../api/services/suggestions';
import { getCustomerPerformance } from '../api/services/performance';
import { getTrades, sellInvestment } from '../api/services/trades';
import { formatCurrency, formatCurrencyPrecise, formatDate, formatReturnPct, formatPL, getInitials, calcInvestmentMetrics } from '../utils/formatters';
import { useToast } from '../context/ToastContext';

export default function CustomerDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const toast = useToast();

  const [customer,         setCustomer]         = useState(null);
  const [investments,      setInvestments]      = useState([]);
  const [suggestions,      setSuggestions]      = useState([]);
  const [performance,      setPerformance]      = useState(null);
  const [trades,           setTrades]           = useState([]);
  const [loading,          setLoading]          = useState(true);
  const [error,            setError]            = useState(null);
  const [perfLoading,      setPerfLoading]      = useState(false);
  const [range,            setRange]            = useState('6M');
  const [tradeHistoryOpen, setTradeHistoryOpen] = useState(false);

  const [formOpen,     setFormOpen]     = useState(false);
  const [editInv,      setEditInv]      = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [sellTarget,   setSellTarget]   = useState(null);
  const [saving,       setSaving]       = useState(false);
  const [deleting,     setDeleting]     = useState(false);
  const [selling,      setSelling]      = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [cust, invs, suggs, trds] = await Promise.all([
        getCustomer(id),
        getInvestmentsByCustomer(id),
        getSuggestionsByCustomer(id),
        getTrades(id),
      ]);
      if (!cust) { setError('Customer not found'); setLoading(false); return; }
      setCustomer(cust);
      setInvestments(invs);
      setSuggestions(suggs);
      setTrades(trds);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => { load(); }, [load]);

  useEffect(() => {
    if (!id) return;
    setPerfLoading(true);
    getCustomerPerformance(id, range)
      .then(setPerformance)
      .finally(() => setPerfLoading(false));
  }, [id, range]);

  const stats = performance ?? { totalInvested: 0, currentValue: 0, profitLoss: 0, returnPercentage: 0 };

  const customerAllocation = useMemo(() => {
    if (!investments.length) return [];
    const groups = {};
    investments.forEach(inv => {
      const val = inv.quantity * inv.currentPrice;
      groups[inv.assetType] = (groups[inv.assetType] || 0) + val;
    });
    const total = Object.values(groups).reduce((s, v) => s + v, 0);
    return Object.entries(groups).map(([assetType, value]) => ({
      assetType, value,
      percentage: total > 0 ? Math.round((value / total) * 100) : 0,
    }));
  }, [investments]);

  const handleCreateInvestment = async (data) => {
    setSaving(true);
    try {
      const created = await createInvestment(id, data);
      setInvestments(prev => [...prev, created]);
      const [newPerf, newTrades] = await Promise.all([getCustomerPerformance(id, range), getTrades(id)]);
      setPerformance(newPerf);
      setTrades(newTrades);
      setFormOpen(false);
      toast.success('Investment added successfully');
    } catch (e) {
      toast.error(e.message, 'Failed to add investment');
    } finally {
      setSaving(false);
    }
  };

  const handleUpdateInvestment = async (data) => {
    setSaving(true);
    try {
      const updated = await updateInvestment(editInv.id, data);
      setInvestments(prev => prev.map(i => i.id === editInv.id ? updated : i));
      setEditInv(null);
      setFormOpen(false);
      toast.success('Investment updated successfully');
    } catch (e) {
      toast.error(e.message, 'Failed to update investment');
    } finally {
      setSaving(false);
    }
  };

  const handleDeleteInvestment = async () => {
    setDeleting(true);
    try {
      await deleteInvestment(deleteTarget.id);
      setInvestments(prev => prev.filter(i => i.id !== deleteTarget.id));
      setDeleteTarget(null);
      const newPerf = await getCustomerPerformance(id, range);
      setPerformance(newPerf);
      toast.success('Investment removed');
    } catch (e) {
      toast.error(e.message, 'Failed to delete investment');
    } finally {
      setDeleting(false);
    }
  };

  const handleSell = async ({ quantity, sellPrice, tradeDate }) => {
    setSelling(true);
    try {
      await sellInvestment(sellTarget.id, { quantity, sellPrice, tradeDate });
      if (quantity >= sellTarget.quantity) {
        setInvestments(prev => prev.filter(i => i.id !== sellTarget.id));
      } else {
        setInvestments(prev => prev.map(i => i.id === sellTarget.id ? { ...i, quantity: i.quantity - quantity } : i));
      }
      const [newPerf, newTrades] = await Promise.all([getCustomerPerformance(id, range), getTrades(id)]);
      setPerformance(newPerf);
      setTrades(newTrades);
      setSellTarget(null);
      toast.success(`Sold ${quantity} x ${sellTarget.assetName}`);
    } catch (e) {
      toast.error(e.message, 'Failed to record sell trade');
    } finally {
      setSelling(false);
    }
  };

  if (loading) {
    return (
      <div>
        <div style={{ marginBottom: 20 }}><SkeletonCard rows={3} /></div>
        <div className="charts-grid">{[1,2].map(i => <GlassCard key={i}><SkeletonCard rows={2} /></GlassCard>)}</div>
      </div>
    );
  }

  if (error) {
    return (
      <div style={{ textAlign: 'center', padding: 40 }}>
        <p style={{ color: 'var(--danger)', marginBottom: 16 }}>{error}</p>
        <Button variant="secondary" onClick={() => navigate('/customers')}>Back to Customers</Button>
      </div>
    );
  }

  return (
    <>
      <div style={{ marginBottom: 20 }}>
        <Button variant="ghost" size="sm" icon={<ArrowLeft size={15} />} onClick={() => navigate('/customers')}>Back to Customers</Button>
      </div>

      {/* P&L Hero Card */}
      <GlassCard style={{ marginBottom: 20 }}>
        <div className="pl-hero">
          <div style={{ display: 'flex', alignItems: 'flex-start', gap: 16, flexWrap: 'wrap' }}>
            <div className="detail-avatar" aria-hidden="true">{getInitials(customer.name)}</div>
            <div style={{ flex: 1, minWidth: 200 }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 10, flexWrap: 'wrap', marginBottom: 8 }}>
                <h2 style={{ fontSize: 20, fontWeight: 700, color: 'var(--text-primary)', letterSpacing: '-0.3px' }}>{customer.name}</h2>
                <Badge label={customer.status === 'Archived' ? 'Archived' : 'Active'} variant={customer.status === 'Archived' ? 'warning' : 'success'} />
                <Badge label={customer.riskProfile} />
              </div>
              <div className="detail-info-grid">
                <InfoItem label="Customer ID" value={customer.id} />
                <InfoItem label="Email"       value={customer.email} />
                <InfoItem label="Phone"       value={customer.phone || 'Not provided'} />
                <InfoItem label="Goal"        value={customer.investmentGoal} />
                <InfoItem label="Joined"      value={formatDate(customer.joinedDate)} />
              </div>
              {customer.notes && (
                <div style={{ marginTop: 12, padding: '8px 12px', background: 'var(--bg-base)', borderRadius: 'var(--r-md)', borderLeft: '3px solid var(--info)' }}>
                  <div style={{ fontSize: 10, fontWeight: 700, color: 'var(--info)', textTransform: 'uppercase', letterSpacing: '0.6px', marginBottom: 4 }}>Manager Notes</div>
                  <p style={{ fontSize: 13, color: 'var(--text-secondary)', lineHeight: 1.6, margin: 0 }}>{customer.notes}</p>
                </div>
              )}
            </div>
          </div>

          <div className="pl-divider" />

          <div className="pl-kpi">
            <div>
              <div style={{ display: 'flex', alignItems: 'baseline', gap: 14, flexWrap: 'wrap' }}>
                <span className={`pl-amount ${stats.profitLoss >= 0 ? 'pl-positive' : 'pl-negative'}`}>{formatPL(stats.profitLoss)}</span>
                <span className={`pl-pct ${stats.returnPercentage >= 0 ? 'pl-positive' : 'pl-negative'}`}>{formatReturnPct(stats.returnPercentage)}</span>
              </div>
              <div className="pl-label">Unrealised Profit / Loss</div>
            </div>
            <div className="pl-support">
              <div><div className="pl-support-label">Total Invested</div><div className="pl-support-value">{formatCurrency(stats.totalInvested)}</div></div>
              <div><div className="pl-support-label">Current Value</div><div className="pl-support-value">{formatCurrency(stats.currentValue)}</div></div>
            </div>
          </div>
        </div>
      </GlassCard>

      {/* Performance Chart with Range Selector */}
      <GlassCard style={{ marginBottom: 20 }}>
        <div className="chart-card">
          <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', marginBottom: 16, flexWrap: 'wrap', gap: 10 }}>
            <div>
              <div className="chart-title">Portfolio Performance</div>
              <div className="chart-subtitle">Value over time for this client</div>
            </div>
            <div className="range-selector">
              {['1M','3M','6M','1Y','All'].map(r => (
                <button key={r} className={`range-btn ${range === r ? 'active' : ''}`} onClick={() => setRange(r)}>{r}</button>
              ))}
            </div>
          </div>
          {perfLoading
            ? <Skeleton height={220} style={{ borderRadius: 'var(--r-md)' }} />
            : <PerformanceChart data={performance?.performanceSeries ?? []} />}
        </div>
      </GlassCard>

      {/* Allocation Section */}
      {investments.length > 0 && (
        <div className="charts-grid" style={{ marginBottom: 20 }}>
          <GlassCard>
            <div className="chart-card">
              <div className="chart-title">Asset Allocation</div>
              <div className="chart-subtitle">Current holdings distribution</div>
              <AllocationChart data={customerAllocation} />
            </div>
          </GlassCard>
          <GlassCard>
            <div className="chart-card">
              <div className="chart-title">Target vs Actual</div>
              <div className="chart-subtitle">{customer.targetAllocation ? 'Allocation deviation from set targets' : 'Edit profile to define target percentages'}</div>
              {customer.targetAllocation
                ? <AllocationCompare actual={customerAllocation} target={customer.targetAllocation} />
                : <EmptyState icon={<Target size={22} />} title="No target allocation set" description="Edit this client profile to define target percentages per asset class." />}
            </div>
          </GlassCard>
        </div>
      )}

      {/* Holdings Table */}
      <GlassCard style={{ marginBottom: 20 }}>
        <div style={{ padding: '16px 20px 12px', display: 'flex', alignItems: 'center', justifyContent: 'space-between', borderBottom: '1px solid var(--border)' }}>
          <div>
            <div style={{ fontWeight: 700, fontSize: 15, color: 'var(--text-primary)' }}>Holdings</div>
            <div style={{ fontSize: 12, color: 'var(--text-secondary)', marginTop: 2 }}>{investments.length} active position{investments.length !== 1 ? 's' : ''}</div>
          </div>
          <Button variant="primary" size="sm" icon={<Plus size={14} />} onClick={() => { setEditInv(null); setFormOpen(true); }}>Add Investment</Button>
        </div>
        {investments.length === 0
          ? <EmptyState icon={<TrendingUp size={24} />} title="No investments yet" description="Add the first investment position for this customer." />
          : (
            <div className="data-table-wrap" style={{ borderRadius: 0, border: 'none' }}>
              <table className="data-table">
                <thead><tr><th>Asset</th><th>Type</th><th>Ticker</th><th>Qty</th><th>Buy Price</th><th>Curr. Price</th><th>Market Value</th><th>P/L</th><th>Return</th><th>Date</th><th style={{ textAlign: 'right' }}>Actions</th></tr></thead>
                <tbody>
                  {investments.map(inv => {
                    const m = calcInvestmentMetrics(inv);
                    return (
                      <tr key={inv.id}>
                        <td style={{ fontWeight: 600 }}>{inv.assetName}</td>
                        <td><Badge label={inv.assetType} /></td>
                        <td style={{ fontWeight: 500, color: 'var(--text-secondary)' }}>{inv.ticker || 'N/A'}</td>
                        <td>{inv.quantity}</td>
                        <td>{formatCurrencyPrecise(inv.purchasePrice)}</td>
                        <td>{formatCurrencyPrecise(inv.currentPrice)}</td>
                        <td style={{ fontWeight: 600 }}>{formatCurrency(m.currentValue)}</td>
                        <td className={m.profitLoss >= 0 ? 'perf-positive' : 'perf-negative'} style={{ fontWeight: 600 }}>{formatPL(m.profitLoss)}</td>
                        <td className={m.returnPct >= 0 ? 'perf-positive' : 'perf-negative'} style={{ fontWeight: 600 }}>
                          {m.returnPct >= 0 ? <TrendingUp size={12} style={{ marginRight: 3, display: 'inline' }} /> : <TrendingDown size={12} style={{ marginRight: 3, display: 'inline' }} />}
                          {formatReturnPct(m.returnPct)}
                        </td>
                        <td style={{ color: 'var(--text-secondary)' }}>{formatDate(inv.purchaseDate)}</td>
                        <td>
                          <div className="col-actions">
                            <Button variant="ghost" size="sm" icon={<Pencil size={13} />} onClick={() => { setEditInv(inv); setFormOpen(true); }} aria-label={`Edit ${inv.assetName}`} />
                            <Button variant="ghost" size="sm" icon={<DollarSign size={13} />} onClick={() => setSellTarget(inv)} aria-label={`Sell ${inv.assetName}`} style={{ color: 'var(--success)' }} />
                            <Button variant="ghost" size="sm" icon={<Trash2 size={13} />} onClick={() => setDeleteTarget(inv)} aria-label={`Delete ${inv.assetName}`} style={{ color: 'var(--danger)' }} />
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
      </GlassCard>

      {/* Trade History */}
      <GlassCard style={{ marginBottom: 20 }}>
        <div
          style={{ padding: '14px 20px', display: 'flex', alignItems: 'center', justifyContent: 'space-between', cursor: 'pointer', borderBottom: tradeHistoryOpen && trades.length ? '1px solid var(--border)' : 'none' }}
          onClick={() => setTradeHistoryOpen(o => !o)}
          role="button" aria-expanded={tradeHistoryOpen}
        >
          <div>
            <div style={{ fontWeight: 700, fontSize: 15, color: 'var(--text-primary)' }}>Trade History</div>
            <div style={{ fontSize: 12, color: 'var(--text-secondary)', marginTop: 2 }}>{trades.length} transaction{trades.length !== 1 ? 's' : ''}</div>
          </div>
          <ChevronRight size={16} style={{ color: 'var(--text-tertiary)', transform: tradeHistoryOpen ? 'rotate(90deg)' : 'none', transition: 'transform var(--t-fast)' }} aria-hidden />
        </div>
        {tradeHistoryOpen && (
          trades.length === 0
            ? <EmptyState icon={<History size={22} />} title="No trade history" description="Buy and sell records appear here after transactions." />
            : (
              <div className="data-table-wrap" style={{ border: 'none', borderRadius: 0 }}>
                <table className="data-table">
                  <thead><tr><th>Date</th><th>Asset</th><th>Type</th><th>Qty</th><th>Price</th><th>Total</th><th>Realised P/L</th></tr></thead>
                  <tbody>
                    {[...trades].sort((a, b) => new Date(b.tradeDate) - new Date(a.tradeDate)).map(trade => (
                      <tr key={trade.id}>
                        <td style={{ color: 'var(--text-secondary)' }}>{formatDate(trade.tradeDate)}</td>
                        <td style={{ fontWeight: 600 }}>{trade.assetName}</td>
                        <td><Badge label={trade.tradeType} variant={trade.tradeType === 'Buy' ? 'success' : 'danger'} /></td>
                        <td>{trade.quantity}</td>
                        <td>{formatCurrencyPrecise(trade.price)}</td>
                        <td>{formatCurrency(trade.quantity * trade.price)}</td>
                        <td className={trade.realisedPL !== null ? (trade.realisedPL >= 0 ? 'perf-positive' : 'perf-negative') : ''} style={{ fontWeight: trade.realisedPL !== null ? 600 : 400 }}>
                          {trade.realisedPL !== null ? formatPL(trade.realisedPL) : 'N/A'}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )
        )}
      </GlassCard>

      {/* Suggestions */}
      {suggestions.length > 0 && (
        <GlassCard>
          <div style={{ padding: '16px 20px', borderBottom: '1px solid var(--border)' }}>
            <div style={{ fontWeight: 700, fontSize: 15, color: 'var(--text-primary)' }}>Insights &amp; Suggestions</div>
          </div>
          {suggestions.map(s => <SuggestionRow key={s.id} suggestion={s} />)}
        </GlassCard>
      )}

      <InvestmentForm open={formOpen} onClose={() => { setFormOpen(false); setEditInv(null); }} onSubmit={editInv ? handleUpdateInvestment : handleCreateInvestment} initial={editInv} customerName={customer?.name} loading={saving} />
      <SellForm open={Boolean(sellTarget)} onClose={() => setSellTarget(null)} onSubmit={handleSell} investment={sellTarget} loading={selling} />
      <ConfirmDialog open={Boolean(deleteTarget)} onClose={() => setDeleteTarget(null)} onConfirm={handleDeleteInvestment} title="Delete Investment" message={`Remove ${deleteTarget?.assetName} from this portfolio? This cannot be undone.`} confirmLabel="Delete Investment" loading={deleting} />
    </>
  );
}

function InfoItem({ label, value }) {
  return (
    <div style={{ paddingBottom: 4 }}>
      <div className="detail-info-label">{label}</div>
      <div className="detail-info-value">{value}</div>
    </div>
  );
}

const ALLOC_COLORS = { Stocks: '#E60000', Stock: '#E60000', Bonds: '#007AFF', Bond: '#007AFF', ETF: '#34C759', Cash: '#FF9F0A', Others: '#8E8E93' };

function AllocationCompare({ actual, target }) {
  const allTypes = [...new Set([...actual.map(a => a.assetType), ...Object.keys(target)])];
  return (
    <div className="alloc-compare">
      {allTypes.map(type => {
        const act    = actual.find(a => a.assetType === type)?.percentage ?? 0;
        const tgt    = target[type] ?? 0;
        const diff   = act - tgt;
        const color  = ALLOC_COLORS[type] || '#8E8E93';
        const status = Math.abs(diff) <= 3 ? 'ok' : diff > 0 ? 'over' : 'under';
        return (
          <div key={type} className="alloc-compare-row">
            <div className="alloc-compare-label">{type}</div>
            <div className="alloc-bar-wrap">
              <div className="alloc-bar-actual" style={{ width: `${Math.min(act, 100)}%`, background: color }} />
              {tgt > 0 && <div className="alloc-bar-target" style={{ left: `${Math.min(tgt, 100)}%` }} />}
            </div>
            <div className="alloc-compare-pct" style={{ color }}>{act}%</div>
            {tgt > 0 && (
              <span className={`alloc-compare-badge alloc-${status}`}>
                {diff > 0 ? '+' : ''}{diff.toFixed(0)}% {status === 'ok' ? 'OK' : status === 'over' ? 'Over' : 'Under'}
              </span>
            )}
          </div>
        );
      })}
    </div>
  );
}

const SEV_BG    = { High: 'var(--danger-bg)',  Medium: 'var(--warning-bg)', Low: 'var(--success-bg)' };
const SEV_COLOR = { High: 'var(--danger)',      Medium: 'var(--warning)',    Low: 'var(--success)'    };

function SuggestionRow({ suggestion }) {
  return (
    <div style={{ padding: '14px 20px', display: 'flex', gap: 12, alignItems: 'flex-start', borderBottom: '1px solid var(--border)' }}>
      <div style={{ width: 36, height: 36, borderRadius: 'var(--r-md)', background: SEV_BG[suggestion.severity] || 'var(--info-bg)', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0, color: SEV_COLOR[suggestion.severity] || 'var(--info)' }}>
        <span style={{ fontSize: 15 }}>{suggestion.severity === 'High' ? 'H' : suggestion.severity === 'Medium' ? 'M' : 'L'}</span>
      </div>
      <div>
        <div style={{ display: 'flex', gap: 8, alignItems: 'center', marginBottom: 4 }}>
          <span style={{ fontWeight: 700, fontSize: 13 }}>{suggestion.type}</span>
          <Badge label={suggestion.severity} variant={suggestion.severity} />
        </div>
        <p style={{ fontSize: 13.5, color: 'var(--text-secondary)', lineHeight: 1.6, margin: 0 }}>{suggestion.message}</p>
      </div>
    </div>
  );
}
