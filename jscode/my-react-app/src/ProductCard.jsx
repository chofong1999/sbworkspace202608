function ProductCard({ name, price, inStock }) {
  return (
    <div className="card">
      <h2>{name}</h2>
      <p>價格：${price}</p>
      {inStock ? <span>有庫存</span> : <span>缺貨中</span>}
    </div>
  );
}
export default ProductCard;