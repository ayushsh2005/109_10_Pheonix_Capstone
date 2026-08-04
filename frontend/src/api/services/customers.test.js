jest.mock('../client', () => ({}));
jest.mock('../config', () => ({ USE_MOCK: true, delay: () => Promise.resolve() }));
jest.mock('../mock/store', () => ({
  mockStore: {
    getCustomers:   jest.fn(),
    getCustomer:    jest.fn(),
    createCustomer: jest.fn(),
    updateCustomer: jest.fn(),
    deleteCustomer: jest.fn(),
    getPortfolio:   jest.fn(),
  },
}));

import { getCustomers, getCustomer, createCustomer, updateCustomer, deleteCustomer, getPortfolio } from './customers';
import { mockStore } from '../mock/store';

const mockCustomer = { id: 'CUS001', name: 'Alice', email: 'alice@test.com' };

beforeEach(() => jest.clearAllMocks());

describe('getCustomers', () => {
  it('returns customers from mock store', async () => {
    mockStore.getCustomers.mockReturnValue([mockCustomer]);
    const result = await getCustomers();
    expect(result).toEqual([mockCustomer]);
    expect(mockStore.getCustomers).toHaveBeenCalledTimes(1);
  });
});

describe('getCustomer', () => {
  it('returns a single customer by id', async () => {
    mockStore.getCustomer.mockReturnValue(mockCustomer);
    const result = await getCustomer('CUS001');
    expect(result).toEqual(mockCustomer);
    expect(mockStore.getCustomer).toHaveBeenCalledWith('CUS001');
  });
  it('returns null for unknown id', async () => {
    mockStore.getCustomer.mockReturnValue(null);
    expect(await getCustomer('UNKNOWN')).toBeNull();
  });
});

describe('createCustomer', () => {
  it('returns the created customer', async () => {
    const payload = { name: 'Bob', email: 'bob@test.com' };
    mockStore.createCustomer.mockReturnValue({ id: 'CUS002', ...payload });
    const result = await createCustomer(payload);
    expect(result.id).toBe('CUS002');
    expect(mockStore.createCustomer).toHaveBeenCalledWith(payload);
  });
});

describe('updateCustomer', () => {
  it('returns the updated customer', async () => {
    const updated = { ...mockCustomer, name: 'Updated' };
    mockStore.updateCustomer.mockReturnValue(updated);
    const result = await updateCustomer('CUS001', { name: 'Updated' });
    expect(result.name).toBe('Updated');
    expect(mockStore.updateCustomer).toHaveBeenCalledWith('CUS001', { name: 'Updated' });
  });
});

describe('deleteCustomer', () => {
  it('calls deleteCustomer on store', async () => {
    mockStore.deleteCustomer.mockReturnValue(undefined);
    await deleteCustomer('CUS001');
    expect(mockStore.deleteCustomer).toHaveBeenCalledWith('CUS001');
  });
});

describe('getPortfolio', () => {
  it('returns portfolio for customer', async () => {
    const portfolio = { id: 'PORT001', customerId: 'CUS001' };
    mockStore.getPortfolio.mockReturnValue(portfolio);
    const result = await getPortfolio('CUS001');
    expect(result).toEqual(portfolio);
    expect(mockStore.getPortfolio).toHaveBeenCalledWith('CUS001');
  });
});

