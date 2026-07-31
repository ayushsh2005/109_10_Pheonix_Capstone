/**
 * Shared helpers for the API service layer.
 */

/** Returns true when the app is running with mock data. */
export const USE_MOCK = import.meta.env.VITE_USE_MOCK !== 'false';

/** Simulate a realistic network round-trip in mock mode. */
export const delay = (ms = 350) =>
  new Promise(resolve => setTimeout(resolve, ms));
