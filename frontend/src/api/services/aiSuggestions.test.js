jest.mock('../client', () => ({}));
jest.mock('../config', () => ({ USE_MOCK: true, delay: () => Promise.resolve() }));
jest.mock('../mock/store', () => ({
  mockStore: {
    getAiSuggestions: jest.fn(),
  },
}));

import { getAiSuggestions } from './aiSuggestions';
import { mockStore } from '../mock/store';

beforeEach(() => jest.clearAllMocks());

describe('getAiSuggestions', () => {
  it('returns AI suggestion payload for a customer', async () => {
    const payload = {
      customerId: 'CUS001',
      summary: 'Well diversified portfolio.',
      suggestions: ['Consider rebalancing.'],
      riskLevel: 'MEDIUM',
      source: 'AI',
    };
    mockStore.getAiSuggestions.mockReturnValue(payload);
    expect(await getAiSuggestions('CUS001')).toEqual(payload);
    expect(mockStore.getAiSuggestions).toHaveBeenCalledWith('CUS001');
  });
});
