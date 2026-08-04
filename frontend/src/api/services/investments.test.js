jest.mock('../client', () => ({}));
jest.mock('../config', () => ({ USE_MOCK: true, delay: () => Promise.resolve() }));
jest.mock('../mock/store', () => ({
  mockStore: {
    getInvestments:           jest.fn(),
    getInvestmentsByCustomer: jest.fn(),
    createInvestment:         jest.fn(),
    updateInvestment:         jest.fn(),
    deleteInvestment:         jest.fn(),
  },
}));

import { getInvestments, getInvestmentsByCustomer, createInvestment, updateInvestment, deleteInvestment } from './investments';
import { mockStore } from '../mock/store';

const mockInv = { id: 'INV001', assetName: 'Reliance', quantity: 10, purchasePrice: 2000, currentPrice: 2500 };

beforeEach(() => jest.clearAllMocks());

describe('getInvestments', () => {
  it('returns all investments', async () => {
    mockStore.getInvestments.mockReturnValue([mockInv]);
    expect(await getInvestments()).toEqual([mockInv]);
  });
});

describe('getInvestmentsByCustomer', () => {
  it('returns investments for a specific customer', async () => {
    mockStore.getInvestmentsByCustomer.mockReturnValue([mockInv]);
    const result = await getInvestmentsByCustomer('CUS001');
    expect(result).toEqual([mockInv]);
    expect(mockStore.getInvestmentsByCustomer).toHaveBeenCalledWith('CUS001');
  });
  it('returns empty array when customer has no investments', async () => {
    mockStore.getInvestmentsByCustomer.mockReturnValue([]);
    expect(await getInvestmentsByCustomer('CUS999')).toEqual([]);
  });
});

describe('createInvestment', () => {
  it('returns created investment with id', async () => {
    mockStore.createInvestment.mockReturnValue(mockInv);
    const result = await createInvestment('CUS001', mockInv);
    expect(result.id).toBe('INV001');
    expect(mockStore.createInvestment).toHaveBeenCalledWith('CUS001', mockInv);
  });
});

describe('updateInvestment', () => {
  it('returns updated investment', async () => {
    const updated = { ...mockInv, currentPrice: 3000 };
    mockStore.updateInvestment.mockReturnValue(updated);
    const result = await updateInvestment('INV001', { currentPrice: 3000 });
    expect(result.currentPrice).toBe(3000);
    expect(mockStore.updateInvestment).toHaveBeenCalledWith('INV001', { currentPrice: 3000 });
  });
});

describe('deleteInvestment', () => {
  it('calls deleteInvestment on store', async () => {
    mockStore.deleteInvestment.mockReturnValue(undefined);
    await deleteInvestment('INV001');
    expect(mockStore.deleteInvestment).toHaveBeenCalledWith('INV001');
  });
});

