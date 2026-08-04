import client from '../client';
import { USE_MOCK, delay } from '../config';
import { mockStore } from '../mock/store';

export async function getTrades(customerId) {
  if (USE_MOCK) { await delay(300); return mockStore.getTrades(customerId); }
  try {
    const { data } = await client.get(`/customers/${customerId}/trades`);
    return data;
  } catch {
    // Backend currently may not expose trade history endpoints.
    return [];
  }
}

export async function sellInvestment(investmentId, payload) {
  if (USE_MOCK) {
    await delay(500);
    return mockStore.sellInvestment(investmentId, payload.quantity, payload.sellPrice, payload.tradeDate);
  }
  try {
    const { data } = await client.post(`/investments/${investmentId}/sell`, payload);
    return data;
  } catch {
    throw new Error('Sell transactions are not available in the current backend.');
  }
}
