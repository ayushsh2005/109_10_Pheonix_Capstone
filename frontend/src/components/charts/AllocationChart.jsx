import {
  PieChart, Pie, Cell, Tooltip, ResponsiveContainer,
} from 'recharts';
import { formatCurrency } from '../../utils/formatters';

const COLORS = ['#E60000', '#007AFF', '#34C759', '#FF9F0A', '#8E8E93'];

const CustomTooltip = ({ active, payload }) => {
  if (!active || !payload?.length) return null;
  const { name, payload: p } = payload[0];
  return (
    <div style={{
      background: 'var(--bg-elevated)',
      border: '1px solid var(--border-strong)',
      borderRadius: 'var(--r-md)',
      padding: '10px 14px',
      boxShadow: 'var(--shadow-md)',
    }}>
      <div style={{ fontWeight: 600, fontSize: 13, color: 'var(--text-primary)', marginBottom: 4 }}>{name}</div>
      <div style={{ fontSize: 12, color: 'var(--text-secondary)' }}>{p.percentage}% · {formatCurrency(p.value)}</div>
    </div>
  );
};

export default function AllocationChart({ data = [] }) {
  const chartData = data.map(d => ({ name: d.assetType, value: d.value, percentage: d.percentage }));

  const total = data.reduce((s, d) => s + d.value, 0);

  return (
    <div>
      <ResponsiveContainer width="100%" height={200}>
        <PieChart>
          <Pie
            data={chartData}
            cx="50%"
            cy="50%"
            innerRadius={60}
            outerRadius={90}
            paddingAngle={3}
            dataKey="value"
            strokeWidth={0}
          >
            {chartData.map((_, index) => (
              <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
            ))}
          </Pie>
          <Tooltip content={<CustomTooltip />} />
          {total > 0 && (
            <>
              <text x="50%" y="46%" textAnchor="middle" style={{ fontSize: 11, fill: 'var(--text-secondary)', fontFamily: 'var(--font)' }}>Total</text>
              <text x="50%" y="59%" textAnchor="middle" style={{ fontSize: 12, fontWeight: 700, fill: 'var(--text-primary)', fontFamily: 'var(--font)' }}>{formatCurrency(total)}</text>
            </>
          )}
        </PieChart>
      </ResponsiveContainer>

      {/* Legend */}
      <div className="allocation-legend">
        {data.map((item, i) => (
          <div className="allocation-legend-item" key={item.assetType}>
            <div className="allocation-dot" style={{ background: COLORS[i % COLORS.length] }} />
            <span className="allocation-label">{item.assetType}</span>
            <span className="allocation-pct">{item.percentage}%</span>
            <span className="allocation-val">{formatCurrency(item.value)}</span>
          </div>
        ))}
      </div>
    </div>
  );
}
