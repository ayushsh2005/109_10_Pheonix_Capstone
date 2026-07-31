import client from '../client';
import { USE_MOCK, delay } from '../config';
import { mockStore } from '../mock/store';

export async function getCustomerPerformance(customerId) {
  if (USE_MOCK) { await delay(300); return mockStore.getCustomerPerformance(customerId); }
  const { data } = await client.get(`/customers/${customerId}/performance`);
  return data;
}
