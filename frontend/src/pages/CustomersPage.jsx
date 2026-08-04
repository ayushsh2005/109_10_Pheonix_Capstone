import { useState, useEffect, useCallback } from 'react';
import { UserPlus, Search, Users, Pencil, Trash2, ChevronRight } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import GlassCard from '../components/ui/GlassCard';
import Button from '../components/ui/Button';
import Badge from '../components/ui/Badge';
import EmptyState from '../components/ui/EmptyState';
import ConfirmDialog from '../components/ui/ConfirmDialog';
import CustomerForm from '../components/forms/CustomerForm';
import { Skeleton } from '../components/ui/Skeleton';
import { getCustomers, createCustomer, updateCustomer, archiveCustomer } from '../api/services/customers';
import { formatCurrency, formatDate, formatReturnPct, getInitials } from '../utils/formatters';
import { useToast } from '../context/ToastContext';

export default function CustomersPage() {
  const navigate = useNavigate();
  const toast = useToast();

  const [customers, setCustomers] = useState([]);
  const [loading, setLoading]     = useState(true);
  const [error, setError]         = useState(null);
  const [search, setSearch]       = useState('');
  const [sortBy, setSortBy]       = useState('name-asc');
  const [showArchived, setShowArchived] = useState(false);

  // Form state
  const [formOpen, setFormOpen]   = useState(false);
  const [editTarget, setEditTarget] = useState(null);
  const [saving, setSaving]       = useState(false);

  // Archive state
  const [archiveTarget, setArchiveTarget] = useState(null);
  const [archiving, setArchiving] = useState(false);

  const load = useCallback(() => {
    setLoading(true);
    setError(null);
    getCustomers(showArchived)
      .then(d => { setCustomers(d); setLoading(false); })
      .catch(e => { setError(e.message); setLoading(false); });
  }, [showArchived]);

  useEffect(() => { load(); }, [load]);

  const filtered = customers.filter(c =>
    c.name.toLowerCase().includes(search.toLowerCase()) ||
    c.email.toLowerCase().includes(search.toLowerCase()) ||
    c.id.toLowerCase().includes(search.toLowerCase())
  );

  const sorted = [...filtered].sort((a, b) => {
    switch (sortBy) {
      case 'name-asc':   return a.name.localeCompare(b.name);
      case 'name-desc':  return b.name.localeCompare(a.name);
      case 'pl-desc':    return (b.profitLoss ?? 0) - (a.profitLoss ?? 0);
      case 'pl-asc':     return (a.profitLoss ?? 0) - (b.profitLoss ?? 0);
      case 'value-desc': return (b.currentValue ?? 0) - (a.currentValue ?? 0);
      default:           return 0;
    }
  });

  const handleCreate = async (data) => {
    setSaving(true);
    try {
      const created = await createCustomer(data);
      setCustomers(cs => [...cs, created]);
      setFormOpen(false);
      toast.success('Customer created successfully', created.name);
    } catch (e) {
      toast.error(e.message, 'Failed to create customer');
    } finally {
      setSaving(false);
    }
  };

  const handleUpdate = async (data) => {
    setSaving(true);
    try {
      const updated = await updateCustomer(editTarget.id, data);
      setCustomers(cs => cs.map(c => c.id === editTarget.id ? updated : c));
      setEditTarget(null);
      setFormOpen(false);
      toast.success('Customer updated successfully');
    } catch (e) {
      toast.error(e.message, 'Failed to update customer');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    setArchiving(true);
    try {
      await archiveCustomer(archiveTarget.id);
      setCustomers(cs => showArchived
        ? cs.map(c => c.id === archiveTarget.id ? { ...c, status: 'Archived' } : c)
        : cs.filter(c => c.id !== archiveTarget.id)
      );
      setArchiveTarget(null);
      toast.success(`${archiveTarget.name} archived successfully`);
    } catch (e) {
      toast.error(e.message, 'Failed to archive customer');
    } finally {
      setArchiving(false);
    }
  };

  const openEdit = (e, customer) => {
    e.stopPropagation();
    setEditTarget(customer);
    setFormOpen(true);
  };

  const openDelete = (e, customer) => {
    e.stopPropagation();
    setArchiveTarget(customer);
  };

  return (
    <>
      {/* Header */}
      <div className="page-header">
        <div className="page-header-left">
          <h1 className="page-title">
            Customers
            {!loading && (
              <span style={{ fontSize: 14, fontWeight: 500, color: 'var(--text-secondary)', marginLeft: 10 }}>
                {customers.length} total
              </span>
            )}
          </h1>
          <p className="page-subtitle">Manage your investor profiles</p>
        </div>
        <div className="page-header-actions">
          <button
            className={`btn btn-sm ${showArchived ? 'btn-secondary' : 'btn-ghost'}`}
            style={showArchived ? { color: 'var(--warning)', borderColor: 'var(--warning)' } : {}}
            onClick={() => setShowArchived(v => !v)}
          >
            {showArchived ? 'Hide Archived' : 'Show Archived'}
          </button>
          <Button
            variant="primary"
            icon={<UserPlus size={15} />}
            onClick={() => { setEditTarget(null); setFormOpen(true); }}
          >
            Add Customer
          </Button>
        </div>
      </div>

      {/* Search + Sort */}
      <div style={{ display: 'flex', gap: 10, marginBottom: 20, flexWrap: 'wrap' }}>
        <div className="search-bar">
          <Search size={15} className="search-bar-icon" />
          <input
            type="search"
            placeholder="Search by name, email or ID…"
            value={search}
            onChange={e => setSearch(e.target.value)}
            aria-label="Search customers"
          />
        </div>
        <select
          className="form-select"
          value={sortBy}
          onChange={e => setSortBy(e.target.value)}
          style={{ width: 180, height: 36, padding: '0 36px 0 12px', fontSize: 13.5 }}
          aria-label="Sort customers"
        >
          <option value="name-asc">Name A → Z</option>
          <option value="name-desc">Name Z → A</option>
          <option value="pl-desc">P&amp;L High → Low</option>
          <option value="pl-asc">P&amp;L Low → High</option>
          <option value="value-desc">Value High → Low</option>
        </select>
      </div>

      {/* Error */}
      {error && (
        <GlassCard style={{ marginBottom: 20, padding: '14px 20px', borderColor: 'var(--danger)' }}>
          <p style={{ color: 'var(--danger)', fontSize: 14 }}>{error}</p>
        </GlassCard>
      )}

      {/* Loading */}
      {loading && (
        <div className="customer-grid">
          {[1, 2, 3].map(i => (
            <GlassCard key={i}><div style={{ padding: 20 }}><Skeleton height={20} width="70%" style={{ marginBottom: 10 }} /><Skeleton height={14} width="50%" style={{ marginBottom: 6 }} /><Skeleton height={14} width="60%" /></div></GlassCard>
          ))}
        </div>
      )}

      {/* Empty */}
      {!loading && !error && sorted.length === 0 && (
        <GlassCard>
          <EmptyState
            icon={<Users size={26} />}
            title={search ? 'No customers found' : showArchived ? 'No archived customers' : 'No customers yet'}
            description={search ? `No results for "${search}"` : 'Add your first customer to get started.'}
            action={!search ? () => { setEditTarget(null); setFormOpen(true); } : undefined}
            actionLabel="Add Customer"
          />
        </GlassCard>
      )}

      {/* Customer Cards */}
      {!loading && sorted.length > 0 && (
        <div className="customer-grid">
          {sorted.map(customer => (
            <GlassCard
              key={customer.id}
              className="customer-card"
              clickable
              onClick={() => navigate(`/customers/${customer.id}`)}
            >
              <div className="customer-card-header">
                <div className="customer-avatar" aria-hidden="true">
                  {getInitials(customer.name)}
                </div>
                <div className="min-w-0">
                  <div className="customer-name truncate">{customer.name}</div>
                  <div className="customer-id">{customer.id} · {customer.email}</div>
                </div>
              </div>

              <div className="customer-meta">
                <div className="customer-meta-item" style={{ gridColumn: 'span 2' }}>
                  <div className="customer-meta-label">P / L</div>
                  <div className={`customer-meta-value ${(customer.profitLoss ?? 0) >= 0 ? 'perf-positive' : 'perf-negative'}`} style={{ fontSize: 15, fontWeight: 700 }}>
                    {(customer.profitLoss ?? 0) >= 0 ? '+' : ''}{formatCurrency(customer.profitLoss ?? 0)}
                    <span style={{ fontSize: 12, fontWeight: 600, marginLeft: 6, opacity: 0.85 }}>({formatReturnPct(customer.returnPercentage ?? 0)})</span>
                  </div>
                </div>
                <div className="customer-meta-item">
                  <div className="customer-meta-label">Current Value</div>
                  <div className="customer-meta-value">{formatCurrency(customer.currentValue ?? customer.portfolioValue)}</div>
                </div>
                <div className="customer-meta-item">
                  <div className="customer-meta-label">Joined</div>
                  <div className="customer-meta-value">{formatDate(customer.joinedDate)}</div>
                </div>
                <div className="customer-meta-item">
                  <div className="customer-meta-label">Risk Profile</div>
                  <div style={{ marginTop: 2 }}><Badge label={customer.riskProfile} /></div>
                </div>
                <div className="customer-meta-item">
                  <div className="customer-meta-label">Goal</div>
                  <div className="customer-meta-value" style={{ fontSize: 12 }}>{customer.investmentGoal}</div>
                </div>
              </div>

              <div className="customer-card-footer">
                <div className="flex gap-2">
                  <Button
                    variant="ghost"
                    size="sm"
                    icon={<Pencil size={13} />}
                    onClick={e => openEdit(e, customer)}
                    aria-label={`Edit ${customer.name}`}
                  />
                  <Button
                    variant="ghost"
                    size="sm"
                    icon={<Trash2 size={13} />}
                    onClick={e => openDelete(e, customer)}
                    aria-label={`Delete ${customer.name}`}
                    style={{ color: 'var(--danger)' }}
                  />
                </div>
                <ChevronRight size={16} style={{ color: 'var(--text-tertiary)' }} aria-hidden />
              </div>
            </GlassCard>
          ))}
        </div>
      )}

      {/* Forms & Dialogs */}
      <CustomerForm
        open={formOpen}
        onClose={() => { setFormOpen(false); setEditTarget(null); }}
        onSubmit={editTarget ? handleUpdate : handleCreate}
        initial={editTarget}
        loading={saving}
      />

      <ConfirmDialog
        open={Boolean(archiveTarget)}
        onClose={() => setArchiveTarget(null)}
        onConfirm={handleDelete}
        title="Archive Customer"
        message={`Archive ${archiveTarget?.name}? Their data and trade history will be preserved. You can view archived clients by toggling 'Show Archived'.`}
        confirmLabel="Archive"
        loading={archiving}
      />
    </>
  );
}
