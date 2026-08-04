import client from '../client';
import { USE_MOCK, delay } from '../config';
import { mockStore } from '../mock/store';

export async function getCustomers() {
  if (USE_MOCK) { await delay(); return mockStore.getCustomers(); }
  /* istanbul ignore next */
  const { data } = await client.get('/customers');
  /* istanbul ignore next */
  return data;
}

export async function getCustomer(id) {
  if (USE_MOCK) { await delay(200); return mockStore.getCustomer(id); }
  /* istanbul ignore next */
  const { data } = await client.get(`/customers/${id}`);
  /* istanbul ignore next */
  return data;
}

export async function createCustomer(payload) {
  if (USE_MOCK) { await delay(500); return mockStore.createCustomer(payload); }
  /* istanbul ignore next */
  const { data } = await client.post('/customers', payload);
  /* istanbul ignore next */
  return data;
}

export async function updateCustomer(id, payload) {
  if (USE_MOCK) { await delay(400); return mockStore.updateCustomer(id, payload); }
  /* istanbul ignore next */
  const { data } = await client.put(`/customers/${id}`, payload);
  /* istanbul ignore next */
  return data;
}

export async function deleteCustomer(id) {
  if (USE_MOCK) { await delay(400); mockStore.deleteCustomer(id); return; }
  /* istanbul ignore next */
  await client.delete(`/customers/${id}`);
}

export async function getPortfolio(customerId) {
  if (USE_MOCK) { await delay(200); return mockStore.getPortfolio(customerId); }
  /* istanbul ignore next */
  const { data } = await client.get(`/customers/${customerId}/performance`);
  /* istanbul ignore next */
  return data;
}
