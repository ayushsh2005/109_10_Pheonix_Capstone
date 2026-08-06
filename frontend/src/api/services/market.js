import client from '../client';
import { USE_MOCK, delay } from '../config';
import { mockStore } from '../mock/store';

/** Fetches the latest market price for a ticker (Yahoo Finance backed). */
export async function getMarketPrice(ticker) {
  if (USE_MOCK) { await delay(200); return mockStore.getMarketPrice(ticker); }
  /* istanbul ignore next */
  const { data } = await client.get(`/market/${ticker}`);
  /* istanbul ignore next */
  return data;
}
