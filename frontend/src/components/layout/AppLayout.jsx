import { useState, useEffect } from 'react';
import Sidebar from './Sidebar';
import TopBar from './TopBar';
import ErrorBoundary from '../ui/ErrorBoundary';

const DESKTOP_BREAKPOINT = 768;

export default function AppLayout({ children }) {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  // Persist collapse state across sessions
  const [sidebarCollapsed, setSidebarCollapsed] = useState(() => {
    try { return localStorage.getItem('pm_sidebar_collapsed') === 'true'; }
    catch { return false; }
  });

  // Close mobile drawer when viewport grows to desktop
  useEffect(() => {
    const handleResize = () => {
      if (window.innerWidth > DESKTOP_BREAKPOINT) setSidebarOpen(false);
    };
    window.addEventListener('resize', handleResize, { passive: true });
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  const toggleCollapsed = () => {
    setSidebarCollapsed(prev => {
      const next = !prev;
      try { localStorage.setItem('pm_sidebar_collapsed', String(next)); } catch { void 0; }
      return next;
    });
  };

  return (
    <div className="app-shell">
      <Sidebar
        open={sidebarOpen}
        onClose={() => setSidebarOpen(false)}
        collapsed={sidebarCollapsed}
        onToggleCollapse={toggleCollapsed}
      />

      <main className={`app-main${sidebarCollapsed ? ' sidebar-collapsed' : ''}`}>
        <TopBar onMenuToggle={() => setSidebarOpen(o => !o)} />

        <div className="app-content">
          <ErrorBoundary>
            {children}
          </ErrorBoundary>
        </div>
      </main>
    </div>
  );
}
