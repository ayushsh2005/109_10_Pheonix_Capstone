import { useLocation } from 'react-router-dom';
import { Menu, Sun, Moon } from 'lucide-react';
import { useTheme } from '../../context/ThemeContext';

const PAGE_META = {
  '/':            { title: 'Dashboard',   parent: null },
  '/customers':   { title: 'Customers',   parent: null },
  '/investments': { title: 'Investments', parent: null },
  '/suggestions': { title: 'Suggestions', parent: null },
};

function getPageMeta(pathname) {
  if (PAGE_META[pathname]) return PAGE_META[pathname];
  if (pathname.startsWith('/customers/'))
    return { title: 'Customer Profile', parent: 'Customers' };
  return { title: 'Portfolio Manager', parent: null };
}

export default function TopBar({ onMenuToggle }) {
  const { theme, toggle } = useTheme();
  const { pathname } = useLocation();
  const { title, parent } = getPageMeta(pathname);

  return (
    <header className="topbar">
      <button
        className="topbar-menu-btn icon-btn"
        onClick={onMenuToggle}
        aria-label="Toggle sidebar"
      >
        <Menu size={20} />
      </button>

      <div className="topbar-breadcrumb">
        {parent && (
          <>
            <span className="topbar-breadcrumb-parent">{parent}</span>
            <span className="topbar-breadcrumb-sep" aria-hidden>/</span>
          </>
        )}
        <span className="topbar-title">{title}</span>
      </div>

      <div className="topbar-actions">
        <button
          className="icon-btn"
          onClick={toggle}
          aria-label={`Switch to ${theme === 'dark' ? 'light' : 'dark'} mode`}
          title={`Switch to ${theme === 'dark' ? 'light' : 'dark'} mode`}
        >
          {theme === 'dark' ? <Sun size={18} /> : <Moon size={18} />}
        </button>
      </div>
    </header>
  );
}
