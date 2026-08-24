import { Navigate, Route, Routes } from 'react-router-dom';
import Login from './pages/Login';
import ClienteLogin from './pages/ClienteLogin/ClienteLogin';
import HomeAgente from './pages/HomeAgente/HomeAgente';
import Clientes from './pages/Clientes/Clientes';
import Contas from './pages/Contas/Contas';
import Operacoes from './pages/Operacoes/Operacoes';
import Reversoes from './pages/Reversoes/Reversoes';
import CriarConta from './pages/CriarConta/CriarConta';
import AdicionarSaldo from './pages/AdicionarSaldo/AdicionarSaldo';
import ClienteArea from './pages/ClienteHome/ClienteHome';
import Loja from './pages/Loja/Loja';

function App() {
    return (
        <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/clientes/login" element={<ClienteLogin />} />
            <Route path="/cliente" element={<ClienteArea />} />
            <Route path="/loja" element={<Loja />} />
            <Route path="/agente" element={<HomeAgente />} />
            <Route path="/agente/clientes" element={<Clientes />} />
            <Route path="/agente/contas" element={<Contas />} />
            <Route path="/agente/contas/nova" element={<CriarConta />} />
            <Route path="/agente/contas/criar" element={<CriarConta />} />
            <Route path="/agente/operacoes" element={<Operacoes />} />
            <Route path="/agente/reversoes" element={<Reversoes />} />
            <Route path="/agente/contas/saldo" element={<AdicionarSaldo />} />
            <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
    );
}

export default App;