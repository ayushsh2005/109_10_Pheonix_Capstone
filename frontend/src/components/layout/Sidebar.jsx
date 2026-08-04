import { NavLink, useLocation } from 'react-router-dom';
import { LayoutDashboard, Users, TrendingUp, Lightbulb, ChevronLeft, ChevronRight } from 'lucide-react';

const NAV_ITEMS = [
  { to: '/',            icon: <LayoutDashboard size={17} />, label: 'Dashboard' },
  { to: '/customers',   icon: <Users          size={17} />, label: 'Customers' },
  { to: '/investments', icon: <TrendingUp     size={17} />, label: 'Investments' },
  { to: '/suggestions', icon: <Lightbulb     size={17} />, label: 'Suggestions' },
];

export default function Sidebar({ open, onClose, collapsed, onToggleCollapse }) {
  const location = useLocation();

  return (
    <>
      {/* Mobile overlay */}
      <div
        className={`sidebar-overlay ${open ? 'open' : ''}`}
        onClick={onClose}
        aria-hidden="true"
      />

      <aside
        className={`sidebar${open ? ' open' : ''}${collapsed ? ' collapsed' : ''}`}
        aria-label="Main navigation"
      >
        {/* Brand */}
        <div className="sidebar-brand">
          <div className="sidebar-logo" aria-hidden="true">PM</div>
          <div className="sidebar-brand-text">
            <div className="sidebar-brand-name">Portfolio Manager</div>
            <div className="sidebar-brand-sub">Team Phoenix</div>
          </div>
        </div>

        {/* Navigation */}
        <nav className="sidebar-nav" aria-label="Primary">
          <div className="sidebar-nav-section">Main Menu</div>
          {NAV_ITEMS.map(({ to, icon, label }) => {
            const isActive = to === '/'
              ? location.pathname === '/'
              : location.pathname.startsWith(to);
            return (
              <NavLink
                key={to}
                to={to}
                className={`sidebar-nav-item ${isActive ? 'active' : ''}`}
                onClick={() => open && onClose()}
                aria-current={isActive ? 'page' : undefined}
                title={collapsed ? label : undefined}
              >
                <span className="sidebar-nav-icon" aria-hidden="true">{icon}</span>
                <span className="sidebar-nav-label">{label}</span>
              </NavLink>
            );
          })}
        </nav>

        {/* Footer */}
        <div className="sidebar-footer">
          <div className="sidebar-user">
            <div className="sidebar-avatar" aria-hidden="true">IM</div>
            <div className="sidebar-user-info">
              <div className="sidebar-user-name">Investment Manager</div>
              <div className="sidebar-user-role">Administrator</div>
            </div>
          </div>

          {/* Collapse toggle — desktop only (hidden on mobile via CSS) */}
          <button
            className="sidebar-collapse-btn"
            onClick={onToggleCollapse}
            aria-label={collapsed ? 'Expand sidebar' : 'Collapse sidebar'}
            title={collapsed ? 'Expand sidebar' : 'Collapse sidebar'}
          >
            {collapsed ? <ChevronRight size={15} /> : <ChevronLeft size={15} />}
          </button>
        </div>
      </aside>
    </>
  );
}
