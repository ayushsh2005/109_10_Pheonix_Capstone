import client from '../client';
import { USE_MOCK, delay } from '../config';
import { mockStore } from '../mock/store';

export async function getTrades(customerId) {
  if (USE_MOCK) { await delay(300); return mockStore.getTrades(customerId); }
  const { data } = await client.get(`/customers/${customerId}/trades`);
  return data;
}

export async function sellInvestment(investmentId, payload) {
  if (USE_MOCK) {
    await delay(500);
    return mockStore.sellInvestment(investmentId, payload.quantity, payload.sellPrice, payload.tradeDate);
  }
  const { data } = await client.post(`/investments/${investmentId}/sell`, payload);
  return data;
}
