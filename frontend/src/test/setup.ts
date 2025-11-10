import "@testing-library/jest-dom/vitest";

// Vitest/jsdom environment sometimes lacks a working Web Crypto implementation used by some libs
// Provide a small polyfill for crypto.getRandomValues using Node's crypto.randomFillSync
try {
	// eslint-disable-next-line @typescript-eslint/no-var-requires
	const { randomFillSync } = require('crypto');

	if (typeof globalThis.crypto === 'undefined') {
		// Minimal Crypto-like object
			// @ts-ignore
			globalThis.crypto = {
				// use any to satisfy TypeScript's generic signature for ArrayBufferView
				// @ts-ignore
				getRandomValues: (arr: any) => randomFillSync(arr),
			};
	} else if (typeof (globalThis.crypto as any).getRandomValues !== 'function') {
		// @ts-ignore
		(globalThis.crypto as any).getRandomValues = (arr: any) => randomFillSync(arr);
	}
} catch (e) {
	// If crypto is not available, tests that need it will fail; log for debugging
	// eslint-disable-next-line no-console
	console.warn('Could not polyfill crypto.getRandomValues in test setup:', e);
}
