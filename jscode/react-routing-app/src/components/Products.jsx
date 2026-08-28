// pages/Products.jsx — 連結到各商品的詳細頁
import { Link } from 'react-router-dom';

function Products() {
  const products = [
    { id: 1, name: 'Fjallraven - Foldsack No. 1 Backpack, Fits 15 Laptops' },
    { id: 2, name: 'Mens Casual Premium Slim Fit T-Shirts ' },
    { id: 3, name: 'Mens Cotton Jacket' },
  ];

  return (
     <ul className="row">
      {products.map(p => (
        <li className="col" key={p.id}>
          {/* 動態產生連結 */}
          <Link to={`/fakeproductdetail/${p.id}`}>{p.name}</Link>
        </li>
      ))}
    </ul>
  );
}
export default Products;