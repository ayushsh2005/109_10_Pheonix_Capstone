jest.mock('../client', () => ({}));
jest.mock('../config', () => ({ USE_MOCK: true, delay: () => Promise.resolve() }));
jest.mock('../mock/store', () => ({
  mockStore: {
    getTrades:       jest.fn(),
    sellInvestment:  jest.fn(),
  },
}));

import { getTrades, sellInvestment } from './trades';
import { mockStore } from '../mock/store';

const mockTrade = { id: 'TRD001', tradeType: 'Buy', assetName: 'Reliance', quantity: 10, price: 2000, tradeDate: '2025-01-01', realisedPL: null };

beforeEach(() => jest.clearAllMocks());

describe('getTrades', () => {
  it('returns trades for a customer', async () => {
    mockStore.getTrades.mockReturnValue([mockTrade]);
    const result = await getTrades('CUS001');
    expect(result).toEqual([mockTrade]);
    expect(mockStore.getTrades).toHaveBeenCalledWith('CUS001');
  });
  it('returns empty array when customer has no trades', async () => {
    mockStore.getTrades.mockReturnValue([]);
    expect(await getTrades('CUS999')).toEqual([]);
  });
});

describe('sellInvestment', () => {
  it('returns realisedPL on successful sell', async () => {
    mockStore.sellInvestment.mockReturnValue({ realisedPL: 2000 });
    const payload = { quantity: 5, sellPrice: 2400, tradeDate: '2025-06-01' };
    const result = await sellInvestment('INV001', payload);
    expect(result.realisedPL).toBe(2000);
    expect(mockStore.sellInvestment).toHaveBeenCalledWith('INV001', 5, 2400, '2025-06-01');
  });
  it('passes correct arguments to store', async () => {
    mockStore.sellInvestment.mockReturnValue({ realisedPL: 500 });
    await sellInvestment('INV002', { quantity: 2, sellPrice: 1500, tradeDate: '2025-03-01' });
    expect(mockStore.sellInvestment).toHaveBeenCalledWith('INV002', 2, 1500, '2025-03-01');
  });
});

