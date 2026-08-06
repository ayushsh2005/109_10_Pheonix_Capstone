jest.mock('../client', () => ({}));
jest.mock('../config', () => ({ USE_MOCK: true, delay: () => Promise.resolve() }));
jest.mock('../mock/store', () => ({
  mockStore: {
    getMarketPrice: jest.fn(),
  },
}));

import { getMarketPrice } from './market';
import { mockStore } from '../mock/store';

beforeEach(() => jest.clearAllMocks());

describe('getMarketPrice', () => {
  it('returns the price payload for a ticker', async () => {
    const payload = { ticker: 'RELIANCE', price: 2500, success: true, message: null };
    mockStore.getMarketPrice.mockReturnValue(payload);
    expect(await getMarketPrice('RELIANCE')).toEqual(payload);
    expect(mockStore.getMarketPrice).toHaveBeenCalledWith('RELIANCE');
  });

  it('returns a failure payload when the ticker is unknown', async () => {
    const payload = { ticker: 'UNKNOWN', price: null, success: false, message: 'Market data temporarily unavailable for UNKNOWN' };
    mockStore.getMarketPrice.mockReturnValue(payload);
    expect(await getMarketPrice('UNKNOWN')).toEqual(payload);
  });
});
