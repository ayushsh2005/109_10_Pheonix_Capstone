import { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Plus, Pencil, Trash2, TrendingUp, TrendingDown } from 'lucide-react';
import GlassCard from '../components/ui/GlassCard';
import Button from '../components/ui/Button';
import Badge from '../components/ui/Badge';
import StatCard from '../components/ui/StatCard';
import ConfirmDialog from '../components/ui/ConfirmDialog';
import InvestmentForm from '../components/forms/InvestmentForm';
import EmptyState from '../components/ui/EmptyState';
import { SkeletonCard } from '../components/ui/Skeleton';
import { getCustomer } from '../api/services/customers';
import { getInvestmentsByCustomer, createInvestment, updateInvestment, deleteInvestment } from '../api/services/investments';
import { getSuggestionsByCustomer } from '../api/services/suggestions';
import { formatCurrency, formatCurrencyPrecise, formatDate, formatReturnPct, getInitials, calcInvestmentMetrics, calcPortfolioStats } from '../utils/formatters';
import { useToast } from '../context/ToastContext';

export default function CustomerDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const toast = useToast();

  const [customer,    setCustomer]    = useState(null);
  const [investments, setInvestments] = useState([]);
  const [suggestions, setSuggestions] = useState([]);
  const [loading,     setLoading]     = useState(true);
  const [error,       setError]       = useState(null);

  // Form/dialog state
  const [formOpen,      setFormOpen]      = useState(false);
  const [editInv,       setEditInv]       = useState(null);
  const [deleteTarget,  setDeleteTarget]  = useState(null);
  const [saving,        setSaving]        = useState(false);
  const [deleting,      setDeleting]      = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [cust, invs, suggs] = await Promise.all([
        getCustomer(id),
        getInvestmentsByCustomer(id),
        getSuggestionsByCustomer(id),
      ]);
      if (!cust) { setError('Customer not found'); setLoading(false); return; }
      setCustomer(cust);
      setInvestments(invs);
      setSuggestions(suggs);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => { load(); }, [load]);

  const stats = calcPortfolioStats(investments);

  const handleCreateInvestment = async (data) => {
    setSaving(true);
    try {
      const created = await createInvestment(id, data);
      setInvestments(prev => [...prev, created]);
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
      toast.success('Investment removed');
    } catch (e) {
      toast.error(e.message, 'Failed to delete investment');
    } finally {
      setDeleting(false);
    }
  };

  if (loading) {
    return (
      <div>
        <div style={{ marginBottom: 20 }}>
          <SkeletonCard rows={2} />
        </div>
        <div className="stats-grid">
          {[1,2,3,4].map(i => <GlassCard key={i}><SkeletonCard rows={1} /></GlassCard>)}
        </div>
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
      {/* Back button */}
      <div style={{ marginBottom: 20 }}>
        <Button
          variant="ghost"
          size="sm"
          icon={<ArrowLeft size={15} />}
          onClick={() => navigate('/customers')}
        >
          Back to Customers
        </Button>
      </div>

      {/* Profile Card */}
      <GlassCard style={{ marginBottom: 20 }}>
        <div className="detail-profile">
          <div className="detail-avatar" aria-hidden="true">{getInitials(customer.name)}</div>
          <div style={{ flex: 1 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10, flexWrap: 'wrap', marginBottom: 8 }}>
              <h2 style={{ fontSize: 20, fontWeight: 700, color: 'var(--text-primary)', letterSpacing: '-0.3px' }}>{customer.name}</h2>
              <Badge label={customer.status || 'Active'} variant="Active" />
            </div>
            <div className="detail-info-grid">
              <InfoItem label="Customer ID"    value={customer.id} />
              <InfoItem label="Email"          value={customer.email} />
              <InfoItem label="Phone"          value={customer.phone || '—'} />
              <InfoItem label="Risk Profile"   value={<Badge label={customer.riskProfile} />} />
              <InfoItem label="Goal"           value={customer.investmentGoal} />
              <InfoItem label="Joined"         value={formatDate(customer.joinedDate)} />
            </div>
          </div>
        </div>
      </GlassCard>

      {/* Portfolio Stats */}
      <div className="stats-grid" style={{ marginBottom: 20 }}>
        <StatCard
          label="Total Invested"
          value={formatCurrency(stats.totalInvested)}
          iconBg="var(--info-bg)"
          iconColor="var(--info)"
        />
        <StatCard
          label="Current Value"
          value={formatCurrency(stats.totalCurrentValue)}
          iconBg="var(--primary-subtle)"
          iconColor="var(--primary)"
        />
        <StatCard
          label="Profit / Loss"
          value={
            <span className={stats.profitLoss >= 0 ? 'perf-positive' : 'perf-negative'}>
              {stats.profitLoss >= 0 ? '+' : ''}{formatCurrency(stats.profitLoss)}
            </span>
          }
          iconBg={stats.profitLoss >= 0 ? 'var(--success-bg)' : 'var(--danger-bg)'}
          iconColor={stats.profitLoss >= 0 ? 'var(--success)' : 'var(--danger)'}
        />
        <StatCard
          label="Return Rate"
          value={
            <span className={stats.returnPct >= 0 ? 'perf-positive' : 'perf-negative'}>
              {formatReturnPct(stats.returnPct)}
            </span>
          }
          iconBg={stats.returnPct >= 0 ? 'var(--success-bg)' : 'var(--danger-bg)'}
          iconColor={stats.returnPct >= 0 ? 'var(--success)' : 'var(--danger)'}
        />
      </div>

      {/* Investments Table */}
      <GlassCard style={{ marginBottom: 20 }}>
        <div style={{ padding: '16px 20px 12px', display: 'flex', alignItems: 'center', justifyContent: 'space-between', borderBottom: '1px solid var(--border)' }}>
          <div>
            <div style={{ fontWeight: 700, fontSize: 15, color: 'var(--text-primary)' }}>Investments</div>
            <div style={{ fontSize: 12, color: 'var(--text-secondary)', marginTop: 2 }}>{investments.length} position{investments.length !== 1 ? 's' : ''}</div>
          </div>
          <Button
            variant="primary"
            size="sm"
            icon={<Plus size={14} />}
            onClick={() => { setEditInv(null); setFormOpen(true); }}
          >
            Add Investment
          </Button>
        </div>

        {investments.length === 0 ? (
          <EmptyState
            icon={<TrendingUp size={24} />}
            title="No investments yet"
            description="Add the first investment position for this customer."
          />
        ) : (
          <div className="data-table-wrap" style={{ borderRadius: 0, border: 'none' }}>
            <table className="data-table">
              <thead>
                <tr>
                  <th>Asset</th>
                  <th>Type</th>
                  <th>Ticker</th>
                  <th>Qty</th>
                  <th>Purchase Price</th>
                  <th>Current Price</th>
                  <th>P/L</th>
                  <th>Return</th>
                  <th>Date</th>
                  <th style={{ textAlign: 'right' }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {investments.map(inv => {
                  const m = calcInvestmentMetrics(inv);
                  return (
                    <tr key={inv.id}>
                      <td style={{ fontWeight: 600 }}>{inv.assetName}</td>
                      <td><Badge label={inv.assetType} /></td>
                      <td style={{ fontFamily: 'var(--font)', fontWeight: 500, color: 'var(--text-secondary)' }}>{inv.ticker || '—'}</td>
                      <td>{inv.quantity}</td>
                      <td>{formatCurrencyPrecise(inv.purchasePrice)}</td>
                      <td>{formatCurrencyPrecise(inv.currentPrice)}</td>
                      <td className={m.profitLoss >= 0 ? 'perf-positive' : 'perf-negative'} style={{ fontWeight: 600 }}>
                        {m.profitLoss >= 0 ? '+' : ''}{formatCurrency(m.profitLoss)}
                      </td>
                      <td className={m.returnPct >= 0 ? 'perf-positive' : 'perf-negative'} style={{ fontWeight: 600 }}>
                        {m.returnPct >= 0 ? <TrendingUp size={12} style={{ marginRight: 3, display: 'inline' }} /> : <TrendingDown size={12} style={{ marginRight: 3, display: 'inline' }} />}
                        {formatReturnPct(m.returnPct)}
                      </td>
                      <td style={{ color: 'var(--text-secondary)' }}>{formatDate(inv.purchaseDate)}</td>
                      <td>
                        <div className="col-actions">
                          <Button
                            variant="ghost"
                            size="sm"
                            icon={<Pencil size={13} />}
                            onClick={() => { setEditInv(inv); setFormOpen(true); }}
                            aria-label={`Edit ${inv.assetName}`}
                          />
                          <Button
                            variant="ghost"
                            size="sm"
                            icon={<Trash2 size={13} />}
                            onClick={() => setDeleteTarget(inv)}
                            aria-label={`Delete ${inv.assetName}`}
                            style={{ color: 'var(--danger)' }}
                          />
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

      {/* Suggestions */}
      {suggestions.length > 0 && (
        <GlassCard>
          <div style={{ padding: '16px 20px', borderBottom: '1px solid var(--border)' }}>
            <div style={{ fontWeight: 700, fontSize: 15, color: 'var(--text-primary)' }}>Insights &amp; Suggestions</div>
          </div>
          {suggestions.map(s => (
            <SuggestionRow key={s.id} suggestion={s} />
          ))}
        </GlassCard>
      )}

      {/* Investment Form */}
      <InvestmentForm
        open={formOpen}
        onClose={() => { setFormOpen(false); setEditInv(null); }}
        onSubmit={editInv ? handleUpdateInvestment : handleCreateInvestment}
        initial={editInv}
        customerName={customer?.name}
        loading={saving}
      />

      {/* Delete Confirm */}
      <ConfirmDialog
        open={Boolean(deleteTarget)}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleDeleteInvestment}
        title="Delete Investment"
        message={`Remove ${deleteTarget?.assetName} from this portfolio? This cannot be undone.`}
        confirmLabel="Delete Investment"
        loading={deleting}
      />
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

const SEV_COLOR = { High: 'var(--danger-bg)', Medium: 'var(--warning-bg)', Low: 'var(--success-bg)' };
const SEV_ICON_COLOR = { High: 'var(--danger)', Medium: 'var(--warning)', Low: 'var(--success)' };

function SuggestionRow({ suggestion }) {
  return (
    <div style={{ padding: '14px 20px', display: 'flex', gap: 12, alignItems: 'flex-start', borderBottom: '1px solid var(--border)' }}>
      <div style={{ width: 36, height: 36, borderRadius: 'var(--r-md)', background: SEV_COLOR[suggestion.severity] || 'var(--info-bg)', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
        <span style={{ fontSize: 16, color: SEV_ICON_COLOR[suggestion.severity] || 'var(--info)' }}>
          {suggestion.severity === 'High' ? '⚠' : suggestion.severity === 'Medium' ? '◈' : '✓'}
        </span>
      </div>
      <div>
        <div style={{ display: 'flex', gap: 8, alignItems: 'center', marginBottom: 4 }}>
          <span style={{ fontWeight: 700, fontSize: 13 }}>{suggestion.type}</span>
          <Badge label={suggestion.severity} variant={suggestion.severity} />
        </div>
        <p style={{ fontSize: 13.5, color: 'var(--text-secondary)', lineHeight: 1.6 }}>{suggestion.message}</p>
      </div>
    </div>
  );
}
