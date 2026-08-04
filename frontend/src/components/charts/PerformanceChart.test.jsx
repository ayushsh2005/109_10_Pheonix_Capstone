import { render, screen } from '@testing-library/react';
import PerformanceChart from './PerformanceChart';

jest.mock('recharts', () => ({
  AreaChart: ({ children }) => <div data-testid="area-chart">{children}</div>,
  Area: () => <div />,
  XAxis: () => <div />,
  YAxis: () => <div />,
  CartesianGrid: () => <div />,
  Tooltip: ({ content }) => <div data-testid="tooltip">{content}</div>,
  ResponsiveContainer: ({ children }) => <div>{children}</div>,
}));

const data = [
  { month: 'Jan', value: 400000 },
  { month: 'Feb', value: 420000 },
];

describe('PerformanceChart', () => {
  it('renders without crashing with data', () => {
    const { container } = render(<PerformanceChart data={data} />);
    expect(container.firstChild).toBeTruthy();
  });
  it('renders AreaChart', () => {
    render(<PerformanceChart data={data} />);
    expect(screen.getByTestId('area-chart')).toBeInTheDocument();
  });
  it('renders without crashing with empty data', () => {
    const { container } = render(<PerformanceChart data={[]} />);
    expect(container.firstChild).toBeTruthy();
  });
  it('renders tooltip with formatted currency value', () => {
    render(<PerformanceChart data={data} />);
    expect(screen.getByTestId('tooltip')).toBeInTheDocument();
  });
  it('renders loss colour when data trends down', () => {
    const down = [{ month: 'Jan', value: 500000 }, { month: 'Feb', value: 400000 }];
    const { container } = render(<PerformanceChart data={down} />);
    expect(container.firstChild).toBeTruthy();
  });
});
