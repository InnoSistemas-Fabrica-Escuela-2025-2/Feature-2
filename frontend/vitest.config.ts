import { defineConfig } from "vitest/config";
import react from "@vitejs/plugin-react-swc";
import path from "path";
import { fileURLToPath } from "url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));

export default defineConfig({
  plugins: [react()] as any,
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
