import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

function parseJwtPayload(token) {
  try {
    const payloadBase64 = token.split('.')[1]
      .replace(/-/g, '+')
      .replace(/_/g, '/');
    return JSON.parse(atob(payloadBase64));
  } catch {
    return null;
  }
}

function LoginForm() {
  const navigate = useNavigate();
  const [credentials, setCredentials] = useState({ username: '', password: '' });
  const [status, setStatus] = useState({ type: '', message: '' });
  const [isSubmitting, setIsSubmitting] = useState(false);

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
      const response = await fetch(`${API_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(credentials),
      });
      const body = await response.json().catch(() => ({}));

      if (!response.ok) {
        throw new Error(body.message || 'Nao foi possivel entrar. Confira seus dados.');
      }

      localStorage.setItem('bancofinancas.token', body.token);
      const subject = parseJwtPayload(body.token)?.sub;

      if (subject && /^cliente-\d+$/i.test(subject)) {
        navigate('/cliente');
      } else if (subject && subject.toUpperCase() === 'LOJA') {
        navigate('/loja');
      } else {
        navigate('/agente');
      }
    } catch (error) {
      setStatus({ type: 'error', message: error.message || 'Erro de conexao com o servidor.' });
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <section className="form-panel">
      <div className="form-container">
        <p className="eyebrow form-eyebrow">Acesso seguro</p>
        <h2>Bem-vindo de volta</h2>
        <p className="form-intro">Entre com suas credenciais para acessar sua conta.</p>

        <form onSubmit={handleSubmit}>
          <label htmlFor="username">Usuario</label>
          <input
            id="username"
            name="username"
            type="text"
            autoComplete="username"
            placeholder="Digite seu usuario"
            value={credentials.username}
            onChange={handleChange}
            required
          />

          <div className="password-heading">
            <label htmlFor="password">Senha</label>
            <button type="button" className="forgot-link">Esqueci minha senha</button>
          </div>
          <input
            id="password"
            name="password"
            type="password"
            autoComplete="current-password"
            placeholder="Digite sua senha"
            value={credentials.password}
            onChange={handleChange}
            required
          />

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

        <p className="security-note">
          <span aria-hidden="true">&#128274;</span>
          Seus dados sao protegidos por autenticacao segura.
        </p>
      </div>
    </section>
  );
}

export default LoginForm;