import client from '../client';
import { USE_MOCK, delay } from '../config';
import { mockStore } from '../mock/store';

export async function getCustomers(includeArchived = false) {
  if (USE_MOCK) { await delay(); return mockStore.getCustomers(includeArchived); }
  const { data } = await client.get('/customers', { params: includeArchived ? { status: 'all' } : {} });
  return data;
}

export async function getCustomer(id) {
  if (USE_MOCK) { await delay(200); return mockStore.getCustomer(id); }
  const { data } = await client.get(`/customers/${id}`);
  return data;
}

export async function createCustomer(payload) {
  if (USE_MOCK) { await delay(500); return mockStore.createCustomer(payload); }
  const { data } = await client.post('/customers', payload);
  return data;
}

export async function updateCustomer(id, payload) {
  if (USE_MOCK) { await delay(400); return mockStore.updateCustomer(id, payload); }
  const { data } = await client.put(`/customers/${id}`, payload);
  return data;
}

export async function deleteCustomer(id) {
  if (USE_MOCK) { await delay(400); mockStore.deleteCustomer(id); return; }
  await client.delete(`/customers/${id}`);
}

export async function archiveCustomer(id) {
  if (USE_MOCK) { await delay(400); mockStore.archiveCustomer(id); return; }
  await client.put(`/customers/${id}/archive`);
}

export async function getPortfolio(customerId) {
  if (USE_MOCK) { await delay(200); return mockStore.getPortfolio(customerId); }
  const { data } = await client.get(`/customers/${customerId}/portfolio`);
  return data;
}
