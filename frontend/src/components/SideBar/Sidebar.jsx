import { NavLink, useNavigate } from 'react-router-dom';
import './Sidebar.css';

const menuGroups = [
	{
		title: 'Menu',
		items: [
			{ label: 'Dashboard', path: '/agente', icon: '◉', end: true },
		],
	},
	{
		title: 'Operações',
		items: [
			{ label: 'Clientes', path: '/agente/clientes', icon: '▣' },
			{ label: 'Contas', path: '/agente/contas', icon: '□' },
			{ label: 'Operações', path: '/agente/operacoes', icon: '⇄' },
			{ label: 'Reversões', path: '/agente/reversoes', icon: '↶' },
		],
	},
	{
		title: 'Gerenciamento',
		items: [
			{ label: 'Criar conta', path: '/agente/contas/nova', icon: '+' },
			{ label: 'Adicionar saldo', path: '/agente/contas/saldo', icon: '$' },
		],
	},
];

function Sidebar({ onMenuClick }) {
	const navigate = useNavigate();

	function handleLogout() {
		localStorage.removeItem('bancofinancas.token');
		navigate('/login');
	}

	return (
		<aside className="sidebar" aria-label="Navegacao da agencia">
			<div className="sidebar-brand">
				{onMenuClick ? (
					<button
						className="sidebar-brand-mark"
						onClick={onMenuClick}
						type="button"
						aria-label="Fechar menu"
					>
						BF
					</button>
				) : (
					<div className="sidebar-brand-mark" aria-hidden="true">BF</div>
				)}
				<div>
					<strong>Banco</strong>
					<span>Pança</span>
				</div>
			</div>

			<nav className="sidebar-navigation">
				{menuGroups.map((group) => (
					<div className="sidebar-group" key={group.title}>
						<p className="sidebar-group-title">{group.title}</p>
						<div className="sidebar-links">
							{group.items.map((item) => (
								<NavLink
									className={({ isActive }) => (
										isActive ? 'sidebar-link active' : 'sidebar-link'
									)}
									end={item.end}
									key={item.path}
									to={item.path}
								>
									<span className="sidebar-link-icon" aria-hidden="true">
										{item.icon}
									</span>
									<span>{item.label}</span>
								</NavLink>
							))}
						</div>
					</div>
				))}
			</nav>

			<div className="sidebar-footer">
				<button className="sidebar-link sidebar-logout" onClick={handleLogout} type="button">
					<span className="sidebar-link-icon" aria-hidden="true">↪</span>
					<span>Sair</span>
				</button>
			</div>
		</aside>
	);
}

export default Sidebar;
