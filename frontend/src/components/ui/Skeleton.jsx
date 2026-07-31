/**
 * Skeleton — placeholder shapes for loading states.
 */
export function Skeleton({ width = '100%', height = 14, circle, style }) {
  return (
    <div
      className={`skeleton ${circle ? 'skeleton-circle' : ''}`}
      style={{ width, height, ...style }}
      aria-hidden="true"
    />
  );
}

export function SkeletonCard({ rows = 3 }) {
  return (
    <div style={{ padding: '20px 24px', display: 'flex', flexDirection: 'column', gap: 12 }}>
      <Skeleton height={22} width="60%" />
      {Array.from({ length: rows }).map((_, i) => (
        <Skeleton key={i} height={14} width={`${70 + i * 10}%`} />
      ))}
    </div>
  );
}

export function SkeletonTable({ rows = 5 }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
      {Array.from({ length: rows }).map((_, i) => (
        <div key={i} style={{ display: 'flex', gap: 16, padding: '14px 16px', alignItems: 'center' }}>
          <Skeleton height={14} width="18%" />
          <Skeleton height={14} width="14%" />
          <Skeleton height={14} width="10%" />
          <Skeleton height={14} width="12%" />
          <Skeleton height={14} width="10%" />
        </div>
      ))}
    </div>
  );
}
