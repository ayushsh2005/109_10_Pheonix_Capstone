import client from '../client';
import { USE_MOCK, delay } from '../config';
import { mockStore } from '../mock/store';

export async function getSuggestions() {
  if (USE_MOCK) { await delay(); return mockStore.getSuggestions(); }
  const { data } = await client.get('/suggestions');
  return data;
}

export async function getSuggestionsByCustomer(customerId) {
  if (USE_MOCK) { await delay(200); return mockStore.getSuggestionsByCustomer(customerId); }
  const { data } = await client.get(`/customers/${customerId}/suggestions`);
  return data;
}
