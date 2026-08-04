import client from '../client';
import { USE_MOCK, delay } from '../config';
import { mockStore } from '../mock/store';

export async function getCustomerPerformance(customerId, range = '6M') {
  if (USE_MOCK) { await delay(300); return mockStore.getCustomerPerformance(customerId, range); }
  /* istanbul ignore next */
  const { data } = await client.get(`/customers/${customerId}/performance`, { params: { range } });
  /* istanbul ignore next */
  return data;
}
