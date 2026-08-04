jest.mock('../client', () => ({}));
jest.mock('../config', () => ({ USE_MOCK: true, delay: () => Promise.resolve() }));
jest.mock('../mock/store', () => ({
  mockStore: { getDashboard: jest.fn() },
}));

import { getDashboard } from './dashboard';
import { mockStore } from '../mock/store';

const mockData = { summary: { totalCustomers: 5, portfolioValue: 5000000 } };

beforeEach(() => jest.clearAllMocks());

describe('getDashboard', () => {
  it('returns dashboard data from mock store', async () => {
    mockStore.getDashboard.mockReturnValue(mockData);
    const result = await getDashboard();
    expect(result).toEqual(mockData);
    expect(mockStore.getDashboard).toHaveBeenCalledTimes(1);
  });
  it('returns data with summary field', async () => {
    mockStore.getDashboard.mockReturnValue(mockData);
    const result = await getDashboard();
    expect(result).toHaveProperty('summary');
  });
});

