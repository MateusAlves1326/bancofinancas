import { Navigate, Route, Routes } from 'react-router-dom';
import Login from './pages/Login';
import HomeAgente from './pages/HomeAgente/HomeAgente';
import Clientes from './pages/Clientes/Clientes';
import Contas from './pages/Contas/Contas';
import Operacoes from './pages/Operacoes/Operacoes';
import Reversoes from './pages/Reversoes/Reversoes';
import CriarConta from './pages/CriarConta/CriarConta';

function App() {
    return (
        <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/agente" element={<HomeAgente />} />
            <Route path="/agente/clientes" element={<Clientes />} />
            <Route path="/agente/contas" element={<Contas />} />
            <Route path="/agente/contas/nova" element={<CriarConta />} />
            <Route path="/agente/contas/criar" element={<CriarConta />} />
            <Route path="/agente/operacoes" element={<Operacoes />} />
            <Route path="/agente/reversoes" element={<Reversoes />} />
            <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
    );
}

export default App;