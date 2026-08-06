import client from '../client';
import { USE_MOCK, delay } from '../config';
import { mockStore } from '../mock/store';

/**
 * Fetches Gemini-generated AI portfolio suggestions for a customer.
 * Falls back to rule-based suggestions server-side if Gemini is unavailable
 * (response.source will be "AI" or "RULE_BASED").
 */
export async function getAiSuggestions(customerId) {
  if (USE_MOCK) { await delay(400); return mockStore.getAiSuggestions(customerId); }
  /* istanbul ignore next */
  const { data } = await client.get(`/customers/${customerId}/ai-suggestions`);
  /* istanbul ignore next */
  return data;
}
