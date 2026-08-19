import { Navigate, Route, Routes } from 'react-router-dom';
import Login from './pages/Login';
import HomeAgente from './pages/HomeAgente';
import Clientes from './pages/Clientes';
import Contas from './pages/Contas';
import Operacoes from './pages/Operacoes';

function App() {
    return (
        <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/agente" element={<HomeAgente />} />
            <Route path="/agente/clientes" element={<Clientes />} />
            <Route path="/agente/contas" element={<Contas />} />
            <Route path="/agente/operacoes" element={<Operacoes />} />
            <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
    );
}

export default App;