import {
  AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
} from 'recharts';
import { formatCurrency } from '../../utils/formatters';

const CustomTooltip = ({ active, payload, label, color }) => {
  if (!active || !payload?.length) return null;
  return (
    <div style={{
      background: 'var(--bg-elevated)',
      border: '1px solid var(--border-strong)',
      borderRadius: 'var(--r-md)',
      padding: '10px 14px',
      boxShadow: 'var(--shadow-md)',
    }}>
      <div style={{ fontSize: 12, color: 'var(--text-secondary)', marginBottom: 4 }}>{label}</div>
      <div style={{ fontWeight: 700, fontSize: 14, color }}>
        {formatCurrency(payload[0].value)}
      </div>
    </div>
  );
};

const formatYAxis = (value) => {
  if (value >= 10_000_000) return `₹${(value / 10_000_000).toFixed(1)}Cr`;
  if (value >= 100_000)    return `₹${(value / 100_000).toFixed(1)}L`;
  if (value >= 1_000)      return `₹${(value / 1_000).toFixed(0)}K`;
  return `₹${value}`;
};

export default function PerformanceChart({ data = [] }) {
  // Derive colour from the trend: green = overall profit, red = loss
  const isProfit = data.length < 2 || data[data.length - 1].value >= data[0].value;
  const COLOR    = isProfit ? 'var(--success)' : 'var(--danger)';
  const COLOR_HEX = isProfit ? '#34C759' : '#FF3B30'; // needed for SVG gradient stops

  return (
    <ResponsiveContainer width="100%" height={220}>
      <AreaChart data={data} margin={{ top: 4, right: 4, left: 4, bottom: 0 }}>
        <defs>
          <linearGradient id="perfGradient" x1="0" y1="0" x2="0" y2="1">
            <stop offset="5%"  stopColor={COLOR_HEX} stopOpacity={0.25} />
            <stop offset="95%" stopColor={COLOR_HEX} stopOpacity={0.02} />
          </linearGradient>
        </defs>
        <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" vertical={false} />
        <XAxis
          dataKey="month"
          tick={{ fontSize: 11, fill: 'var(--text-secondary)', fontFamily: 'var(--font)' }}
          axisLine={false}
          tickLine={false}
        />
        <YAxis
          tickFormatter={formatYAxis}
          tick={{ fontSize: 11, fill: 'var(--text-secondary)', fontFamily: 'var(--font)' }}
          axisLine={false}
          tickLine={false}
          width={56}
        />
        <Tooltip
          content={<CustomTooltip color={COLOR_HEX} />}
          cursor={{ stroke: COLOR_HEX, strokeWidth: 1, strokeDasharray: '4 2' }}
        />
        <Area
          type="monotone"
          dataKey="value"
          stroke={COLOR_HEX}
          strokeWidth={2}
          fill="url(#perfGradient)"
          dot={false}
          activeDot={{ r: 4, fill: COLOR_HEX, stroke: 'var(--bg-elevated)', strokeWidth: 2 }}
        />
      </AreaChart>
    </ResponsiveContainer>
  );
}
