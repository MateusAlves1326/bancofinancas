import './Header.css';

function Header() {
    return (
        <header className="header">
            <div className="brand-mark">
                <span className="brand-mark-text">BF</span>
            </div>
            <span className="brand-name">Banco Financas</span>
            <button className="logout-button">Sair</button>
        </header>
    );
}

export default Header;