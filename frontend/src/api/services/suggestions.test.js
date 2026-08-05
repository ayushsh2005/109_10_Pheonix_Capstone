jest.mock('../client', () => ({}));
jest.mock('../config', () => ({ USE_MOCK: true, delay: () => Promise.resolve() }));
jest.mock('../mock/store', () => ({
  mockStore: {
    getSuggestions:           jest.fn(),
    getSuggestionsByCustomer: jest.fn(),
  },
}));

import { getSuggestions, getSuggestionsByCustomer } from './suggestions';
import { mockStore } from '../mock/store';

const mockSugg = { id: 'SUG001', type: 'Diversification', severity: 'High', message: 'Rebalance', customerId: 'CUS001' };

beforeEach(() => jest.clearAllMocks());

describe('getSuggestions', () => {
  it('returns all suggestions', async () => {
    mockStore.getSuggestions.mockReturnValue([mockSugg]);
    expect(await getSuggestions()).toEqual([mockSugg]);
    expect(mockStore.getSuggestions).toHaveBeenCalledTimes(1);
  });
  it('returns empty array when no suggestions', async () => {
    mockStore.getSuggestions.mockReturnValue([]);
    expect(await getSuggestions()).toEqual([]);
  });
});

describe('getSuggestionsByCustomer', () => {
  it('returns suggestions for a specific customer', async () => {
    mockStore.getSuggestionsByCustomer.mockReturnValue([mockSugg]);
    const result = await getSuggestionsByCustomer('CUS001');
    expect(result).toEqual([mockSugg]);
    expect(mockStore.getSuggestionsByCustomer).toHaveBeenCalledWith('CUS001');
  });
  it('returns empty array when customer has no suggestions', async () => {
    mockStore.getSuggestionsByCustomer.mockReturnValue([]);
    expect(await getSuggestionsByCustomer('CUS999')).toEqual([]);
  });
});

