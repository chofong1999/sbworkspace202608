// components/Navbar.jsx
import { NavLink } from 'react-router-dom';
import './Navbar.css';

function Navbar() {
  // 共用 className 回呼函式（避免重複）
  const linkClass = ({ isActive, isPending }) =>
    'nav-link' + (isActive ? ' active' : '') + (isPending ? ' pending' : '');

  return (
    <nav className="navbar">
      <div className="navbar-brand">我的網站</div>
      <ul className="navbar-menu">
        <li>
          {/* end 屬性：防止 /dashboard 等路徑也匹配 / */}
          <NavLink to="/" end className={linkClass}>
            首頁
          </NavLink>
        </li>
        <li>
          <NavLink to="/about" className={linkClass}>關於</NavLink>
        </li>
        <li>
          <NavLink to="/products" className={linkClass}>商品</NavLink>
        </li>
         <li>
          <NavLink to="/login" className={linkClass}>登入表單</NavLink>
        </li>
        <li>
          <NavLink to="/dashboard" className={linkClass}>後台</NavLink>
        </li>
      </ul>
    </nav>
  );
}

export default Navbar;