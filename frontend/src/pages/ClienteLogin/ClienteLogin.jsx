import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Eye, EyeOff } from 'lucide-react';
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

function ClienteLogin() {
  const navigate = useNavigate();
  const [credentials, setCredentials] = useState({ agenciaId: '', numeroConta: '', password: '' });
  const [status, setStatus] = useState({ type: '', message: '' });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [mostrarSenha, setMostrarSenha] = useState(false);

  function impedirAlteracaoPorScroll(event) {
    event.currentTarget.blur();
  }

  function handleChange(event) {
    const { name, value } = event.target;
    setCredentials((current) => ({ ...current, [name]: value }));
    setStatus({ type: '', message: '' });
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setIsSubmitting(true);
    setStatus({ type: '', message: '' });

    try {
      const payload = {
        agenciaId: Number(credentials.agenciaId),
        numeroConta: Number(credentials.numeroConta),
        password: credentials.password,
      };

      const response = await fetch(`${API_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });

      const body = await response.json().catch(() => ({}));

      if (!response.ok) {
        throw new Error(body.message || 'Não foi possível entrar. Confira os dados da conta.');
      }

      localStorage.setItem('bancofinancas.token', body.token);
      navigate('/cliente');
    } catch (error) {
      setStatus({ type: 'error', message: error.message || 'Erro de conexão com o servidor.' });
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main className="login-page">
      <section className="form-panel">
        <div className="form-container">
          <p className="eyebrow form-eyebrow">Acesso do cliente</p>
          <h2>Login cliente</h2>
          <p className="form-intro">Informe a agência, o número da conta e sua senha de 4 dígitos.</p>

          <form onSubmit={handleSubmit}>
            <label htmlFor="agenciaId">Agência</label>
            <input
              id="agenciaId"
              name="agenciaId"
              type="number"
              onWheel={impedirAlteracaoPorScroll}
              min="1"
              placeholder="Digite a agência"
              value={credentials.agenciaId}
              onChange={handleChange}
              required
            />

            <label htmlFor="numeroConta">Número da conta</label>
            <input
              id="numeroConta"
              name="numeroConta"
              type="number"
              onWheel={impedirAlteracaoPorScroll}
              min="1"
              placeholder="Digite o número da conta"
              value={credentials.numeroConta}
              onChange={handleChange}
              required
            />

            <div className="password-heading">
              <label htmlFor="password">Senha de 4 dígitos</label>
            </div>
            <div className="password-field">

              <input
                id="password"
                name="password"
                type={mostrarSenha ? 'text' : 'password'}
                inputMode="numeric"
                maxLength={4}
                pattern="[0-9]*"
                placeholder="Digite a senha"
                value={credentials.password}
                onChange={handleChange}
                required
              />

              <button
                aria-label={mostrarSenha ? 'Ocultar senha' : 'Mostrar senha'}
                aria-pressed={mostrarSenha}
                className="password-visibility-button"
                onClick={() => setMostrarSenha((visivel) => !visivel)}
                type="button"
              >
                {mostrarSenha ? <EyeOff aria-hidden="true" size={20} /> : <Eye aria-hidden="true" size={20} />}
              </button>
            </div>
            {status.message && (
              <p className={`status-message ${status.type}`} role="alert">
                {status.message}
              </p>
            )}

            <button className="submit-button" type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Entrando...' : 'Entrar'}
              {!isSubmitting && <span aria-hidden="true">&rarr;</span>}
            </button>
          </form>
        </div>
      </section>
    </main>
  );
}

export default ClienteLogin;
