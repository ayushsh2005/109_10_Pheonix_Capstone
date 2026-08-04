import client from '../client';
import { USE_MOCK, delay } from '../config';
import { mockStore } from '../mock/store';

export async function getSuggestions() {
  if (USE_MOCK) { await delay(); return mockStore.getSuggestions(); }
  /* istanbul ignore next */
  const { data } = await client.get('/suggestions');
  /* istanbul ignore next */
  return data;
}

export async function getSuggestionsByCustomer(customerId) {
  if (USE_MOCK) { await delay(200); return mockStore.getSuggestionsByCustomer(customerId); }
  /* istanbul ignore next */
  const { data } = await client.get(`/customers/${customerId}/suggestions`);
  /* istanbul ignore next */
  return data;
}
