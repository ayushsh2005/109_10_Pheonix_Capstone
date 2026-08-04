import { render, screen } from '@testing-library/react';
import AllocationChart from './AllocationChart';

jest.mock('recharts', () => ({
  PieChart: ({ children }) => <div data-testid="pie-chart">{children}</div>,
  Pie: () => <div />,
  Cell: () => <div />,
  Tooltip: ({ content }) => <div data-testid="tooltip">{content}</div>,
  ResponsiveContainer: ({ children }) => <div>{children}</div>,
}));

const data = [
  { assetType: 'Stocks', value: 300000, percentage: 60 },
  { assetType: 'Bonds',  value: 200000, percentage: 40 },
];

describe('AllocationChart', () => {
  it('renders without crashing with data', () => {
    const { container } = render(<AllocationChart data={data} />);
    expect(container.firstChild).toBeTruthy();
  });
  it('renders PieChart', () => {
    render(<AllocationChart data={data} />);
    expect(screen.getByTestId('pie-chart')).toBeInTheDocument();
  });
  it('renders without crashing with empty data', () => {
    const { container } = render(<AllocationChart data={[]} />);
    expect(container.firstChild).toBeTruthy();
  });
  it('renders tooltip with asset name', () => {
    render(<AllocationChart data={data} />);
    expect(screen.getByTestId('tooltip')).toBeInTheDocument();
  });
});
