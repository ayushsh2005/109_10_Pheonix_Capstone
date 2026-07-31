import client from '../client';
import { USE_MOCK, delay } from '../config';
import { mockStore } from '../mock/store';

export async function getInvestments() {
  if (USE_MOCK) { await delay(); return mockStore.getInvestments(); }
  const { data } = await client.get('/investments');
  return data;
}

export async function getInvestmentsByCustomer(customerId) {
  if (USE_MOCK) { await delay(300); return mockStore.getInvestmentsByCustomer(customerId); }
  const { data } = await client.get(`/customers/${customerId}/investments`);
  return data;
}

export async function createInvestment(customerId, payload) {
  if (USE_MOCK) { await delay(500); return mockStore.createInvestment(customerId, payload); }
  const { data } = await client.post(`/customers/${customerId}/investments`, payload);
  return data;
}

export async function updateInvestment(id, payload) {
  if (USE_MOCK) { await delay(400); return mockStore.updateInvestment(id, payload); }
  const { data } = await client.put(`/investments/${id}`, payload);
  return data;
}

export async function deleteInvestment(id) {
  if (USE_MOCK) { await delay(400); mockStore.deleteInvestment(id); return; }
  await client.delete(`/investments/${id}`);
}
