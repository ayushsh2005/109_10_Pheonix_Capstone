import client from '../client';
import { USE_MOCK, delay } from '../config';
import { mockStore } from '../mock/store';

export async function getDashboard() {
  if (USE_MOCK) { await delay(); return mockStore.getDashboard(); }
  const { data } = await client.get('/dashboard/summary');
  return data;
}
