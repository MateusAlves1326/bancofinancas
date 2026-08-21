import './Header.css';

function Header({ onMenuClick }) {
    return (
        <header className="header">
            <button
                className="brand-mark"
                onClick={onMenuClick}
                type="button"
                aria-label="Abrir menu"
            >
                <span className="brand-mark-text">BF</span>
            </button>

            <span className="brand-name">Banco Finanças</span>
        </header>
    );
}

export default Header;