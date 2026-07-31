import { Component } from 'react';
import { AlertTriangle } from 'lucide-react';

export default class ErrorBoundary extends Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, info) {
    console.error('[ErrorBoundary]', error, info);
  }

  render() {
    if (!this.state.hasError) return this.props.children;
    return (
      <div className="error-boundary">
        <div style={{ width: 56, height: 56, background: 'var(--danger-bg)', borderRadius: 'var(--r-xl)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--danger)' }}>
          <AlertTriangle size={24} />
        </div>
        <div>
          <div style={{ fontSize: 18, fontWeight: 700, color: 'var(--text-primary)', marginBottom: 8 }}>
            Something went wrong
          </div>
          <div style={{ fontSize: 14, color: 'var(--text-secondary)', maxWidth: 360 }}>
            {this.state.error?.message || 'An unexpected error occurred. Please refresh the page.'}
          </div>
        </div>
        <button
          className="btn btn-secondary"
          onClick={() => this.setState({ hasError: false, error: null })}
        >
          Try again
        </button>
      </div>
    );
  }
}
