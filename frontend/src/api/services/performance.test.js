jest.mock('../client', () => ({}));
jest.mock('../config', () => ({ USE_MOCK: true, delay: () => Promise.resolve() }));
jest.mock('../mock/store', () => ({
  mockStore: { getCustomerPerformance: jest.fn() },
}));

import { getCustomerPerformance } from './performance';
import { mockStore } from '../mock/store';

const mockPerf = { totalInvested: 10000, currentValue: 12000, profitLoss: 2000, returnPercentage: 20, performanceSeries: [] };

beforeEach(() => jest.clearAllMocks());

describe('getCustomerPerformance', () => {
  it('returns performance data for a customer', async () => {
    mockStore.getCustomerPerformance.mockReturnValue(mockPerf);
    const result = await getCustomerPerformance('CUS001');
    expect(result).toEqual(mockPerf);
    expect(mockStore.getCustomerPerformance).toHaveBeenCalledWith('CUS001', '6M');
  });
  it('passes the range parameter to the store', async () => {
    mockStore.getCustomerPerformance.mockReturnValue(mockPerf);
    await getCustomerPerformance('CUS001', '1Y');
    expect(mockStore.getCustomerPerformance).toHaveBeenCalledWith('CUS001', '1Y');
  });
  it('defaults range to 6M when not specified', async () => {
    mockStore.getCustomerPerformance.mockReturnValue(mockPerf);
    await getCustomerPerformance('CUS001');
    expect(mockStore.getCustomerPerformance).toHaveBeenCalledWith('CUS001', '6M');
  });
  it('returns object with required fields', async () => {
    mockStore.getCustomerPerformance.mockReturnValue(mockPerf);
    const result = await getCustomerPerformance('CUS001');
    expect(result).toHaveProperty('totalInvested');
    expect(result).toHaveProperty('currentValue');
    expect(result).toHaveProperty('profitLoss');
    expect(result).toHaveProperty('performanceSeries');
  });
});

