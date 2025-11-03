import { createRoot } from "react-dom/client";
import App from "./App.tsx";
import "./index.css";

// Importar funciones de test y ejemplos para que estén disponibles en window
import './lib/testConnection';
import './lib/apiExamples';

createRoot(document.getElementById("root")!).render(<App />);
