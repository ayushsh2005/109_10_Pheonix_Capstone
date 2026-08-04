import { useState, useEffect, useCallback } from 'react';
import { Plus, Search, TrendingUp, Pencil, Trash2 } from 'lucide-react';
import GlassCard from '../components/ui/GlassCard';
import Button from '../components/ui/Button';
import Badge from '../components/ui/Badge';
import EmptyState from '../components/ui/EmptyState';
import ConfirmDialog from '../components/ui/ConfirmDialog';
import InvestmentForm from '../components/forms/InvestmentForm';
import { SkeletonTable } from '../components/ui/Skeleton';
import { getInvestments, createInvestment, updateInvestment, deleteInvestment } from '../api/services/investments';
import { getCustomers } from '../api/services/customers';
import { formatCurrencyPrecise, formatCurrency, formatDate, formatReturnPct, calcInvestmentMetrics } from '../utils/formatters';
import { useToast } from '../context/ToastContext';

export default function InvestmentsPage() {
  const toast = useToast();

  const [investments, setInvestments] = useState([]);
  const [customers,   setCustomers]   = useState([]);
  const [loading,     setLoading]     = useState(true);
  const [error,       setError]       = useState(null);
  const [search,      setSearch]      = useState('');
  const [filterType,  setFilterType]  = useState('');

  const [editInv,      setEditInv]      = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [saving,       setSaving]       = useState(false);
  const [deleting,     setDeleting]     = useState(false);
  const [createOpen,   setCreateOpen]   = useState(false);
  const [newCustId,    setNewCustId]    = useState('');

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [invs, custs] = await Promise.all([getInvestments(), getCustomers()]);
      setInvestments(invs);
      setCustomers(custs);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const ASSET_TYPES = [...new Set(investments.map(i => i.assetType))].filter(Boolean);

  const filtered = investments.filter(inv => {
    const matchSearch = !search ||
      inv.assetName.toLowerCase().includes(search.toLowerCase()) ||
      (inv.ticker || '').toLowerCase().includes(search.toLowerCase());
    const matchType = !filterType || inv.assetType === filterType;
    return matchSearch && matchType;
  });

  const handleUpdate = async (data) => {
    setSaving(true);
    try {
      const updated = await updateInvestment(editInv.id, data);
      setInvestments(prev => prev.map(i => i.id === editInv.id ? updated : i));
      setEditInv(null);
      toast.success('Investment updated successfully');
    } catch (e) {
      toast.error(e.message, 'Failed to update investment');
    } finally {
      setSaving(false);
    }
  };

  const handleCreate = async (data) => {
    setSaving(true);
    try {
      const created = await createInvestment(newCustId, data);
      setInvestments(prev => [...prev, created]);
      setCreateOpen(false);
      setNewCustId('');
      toast.success('Investment added successfully');
    } catch (e) {
      toast.error(e.message, 'Failed to add investment');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
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

  // Compute totals
  const totalValue   = investments.reduce((s, i) => s + i.quantity * i.currentPrice, 0);
  const totalCost    = investments.reduce((s, i) => s + i.quantity * i.purchasePrice, 0);
  const totalPL      = totalValue - totalCost;

  return (
    <>
      {/* Header */}
      <div className="page-header">
        <div className="page-header-left">
          <h1 className="page-title">
            Investments
            {!loading && (
              <span style={{ fontSize: 14, fontWeight: 500, color: 'var(--text-secondary)', marginLeft: 10 }}>
                {investments.length} positions
              </span>
            )}
          </h1>
          <p className="page-subtitle">All investment positions across all customers</p>
        </div>
        <div className="page-header-actions">
          <Button
            variant="primary"
            icon={<Plus size={15} />}
            onClick={() => { setNewCustId(''); setCreateOpen(true); }}
          >
            Add Investment
          </Button>
        </div>
      </div>

      {/* Summary Bar */}
      {!loading && investments.length > 0 && (
        <GlassCard style={{ marginBottom: 20, padding: '14px 24px', display: 'flex', flexWrap: 'wrap', gap: 28 }}>
          <SummaryItem label="Total Market Value" value={formatCurrency(totalValue)} />
          <SummaryItem label="Total Cost Basis"   value={formatCurrency(totalCost)} />
          <SummaryItem label="Total Gain / Loss"  value={(totalPL >= 0 ? '+' : '') + formatCurrency(totalPL)} positive={totalPL >= 0} />
          <SummaryItem label="Overall Return"     value={formatReturnPct(totalCost > 0 ? (totalPL / totalCost) * 100 : 0)} positive={totalPL >= 0} />
        </GlassCard>
      )}

      {/* Filters */}
      <div style={{ display: 'flex', gap: 10, marginBottom: 20, flexWrap: 'wrap' }}>
        <div className="search-bar">
          <Search size={15} className="search-bar-icon" />
          <input
            type="search"
            placeholder="Search by asset name or ticker…"
            value={search}
            onChange={e => setSearch(e.target.value)}
            aria-label="Search investments"
          />
        </div>
        <select
          className="form-select"
          style={{ width: 160, height: 36, padding: '0 36px 0 12px', fontSize: 13.5 }}
          value={filterType}
          onChange={e => setFilterType(e.target.value)}
          aria-label="Filter by asset type"
        >
          <option value="">All types</option>
          {ASSET_TYPES.map(t => <option key={t} value={t}>{t}</option>)}
        </select>
      </div>

      {error && (
        <GlassCard style={{ marginBottom: 20, padding: '14px 20px', borderColor: 'var(--danger)' }}>
          <p style={{ color: 'var(--danger)', fontSize: 14 }}>{error}</p>
        </GlassCard>
      )}

      <GlassCard>
        {loading && <SkeletonTable rows={5} />}

        {!loading && filtered.length === 0 && (
          <EmptyState
            icon={<TrendingUp size={26} />}
            title="No investments found"
            description={search || filterType ? 'Try adjusting your filters.' : 'Investments will appear here once added via customer profiles.'}
          />
        )}

        {!loading && filtered.length > 0 && (
          <div className="data-table-wrap" style={{ border: 'none', borderRadius: 0 }}>
            <table className="data-table">
              <thead>
                <tr>
                  <th>Asset</th>
                  <th>Type</th>
                  <th>Ticker</th>
                  <th>Qty</th>
                  <th>Buy Price</th>
                  <th>Curr. Price</th>
                  <th>Market Value</th>
                  <th>P/L</th>
                  <th>Return</th>
                  <th>Purchased</th>
                  <th style={{ textAlign: 'right' }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map(inv => {
                  const m = calcInvestmentMetrics(inv);
                  return (
                    <tr key={inv.id}>
                      <td style={{ fontWeight: 600 }}>{inv.assetName}</td>
                      <td><Badge label={inv.assetType} /></td>
                      <td style={{ fontWeight: 500, color: 'var(--text-secondary)' }}>{inv.ticker || '—'}</td>
                      <td>{inv.quantity}</td>
                      <td>{formatCurrencyPrecise(inv.purchasePrice)}</td>
                      <td>{formatCurrencyPrecise(inv.currentPrice)}</td>
                      <td style={{ fontWeight: 600 }}>{formatCurrency(m.currentValue)}</td>
                      <td className={m.profitLoss >= 0 ? 'perf-positive' : 'perf-negative'} style={{ fontWeight: 600 }}>
                        {m.profitLoss >= 0 ? '+' : ''}{formatCurrency(m.profitLoss)}
                      </td>
                      <td className={m.returnPct >= 0 ? 'perf-positive' : 'perf-negative'} style={{ fontWeight: 600 }}>
                        {formatReturnPct(m.returnPct)}
                      </td>
                      <td style={{ color: 'var(--text-secondary)' }}>{formatDate(inv.purchaseDate)}</td>
                      <td>
                        <div className="col-actions">
                          <Button
                            variant="ghost"
                            size="sm"
                            icon={<Pencil size={13} />}
                            onClick={() => setEditInv(inv)}
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

      <InvestmentForm
        open={Boolean(editInv)}
        onClose={() => setEditInv(null)}
        onSubmit={handleUpdate}
        initial={editInv}
        loading={saving}
      />

      <InvestmentForm
        open={createOpen}
        onClose={() => { setCreateOpen(false); setNewCustId(''); }}
        onSubmit={handleCreate}
        loading={saving}
        customers={customers}
        customerId={newCustId}
        onCustomerChange={setNewCustId}
      />

      <ConfirmDialog
        open={Boolean(deleteTarget)}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleDelete}
        title="Delete Investment"
        message={`Remove ${deleteTarget?.assetName}? This cannot be undone.`}
        confirmLabel="Delete"
        loading={deleting}
      />
    </>
  );
}

function SummaryItem({ label, value, positive }) {
  return (
    <div>
      <div style={{ fontSize: 11, fontWeight: 600, color: 'var(--text-tertiary)', textTransform: 'uppercase', letterSpacing: '0.5px', marginBottom: 4 }}>{label}</div>
      <div style={{ fontSize: 16, fontWeight: 700, color: positive === undefined ? 'var(--text-primary)' : positive ? 'var(--success)' : 'var(--danger)' }}>
        {value}
      </div>
    </div>
  );
}
