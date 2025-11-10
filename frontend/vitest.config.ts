// Polyfill for Web Crypto getRandomValues used by Vite/Vitest in some environments.
// This runs early because this config file is loaded before the test setup.
import { randomFillSync } from 'crypto';

// @ts-ignore
if (typeof globalThis.crypto === 'undefined' || typeof (globalThis.crypto as any).getRandomValues !== 'function') {
  // @ts-ignore
  globalThis.crypto = globalThis.crypto || {};
  // @ts-ignore
  globalThis.crypto.getRandomValues = (arr: any) => randomFillSync(arr);
}

import { defineConfig } from "vitest/config";
import react from "@vitejs/plugin-react-swc";
import path from "path";
import { fileURLToPath } from "url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
  test: {
    environment: "jsdom",
    globals: true,
    setupFiles: path.resolve(__dirname, "./src/test/setup.ts"),
    coverage: {
      provider: "v8",
      reporter: ["text", "lcov"],
      reportsDirectory: path.resolve(__dirname, "../qa/reports/frontend"),
      cleanOnRerun: true,
    },
  },
});
