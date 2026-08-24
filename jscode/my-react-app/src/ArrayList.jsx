const fruits = ["蘋果", "香蕉", "芒果"];

export function FruitList() {
  return (
    <table border="1" width="30%">
        <thead>
          <tr>
            <th>序號</th>
            <th>水果</th>
          </tr>
        </thead>
      <tbody>
        {fruits.map((fruit, index) => (
          // 純文字陣列沒有 id 可用時，暫時用 index（見下方「更好的做法」）
          <tr key={index}>
            <td>{index + 1}</td>
            <td>{fruit}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

// ✅ 更好的做法：資料有唯一 id 時，優先使用 id 當 key（資料增刪時才不會錯位）
const products = [
  { id: 1, name: "iPhone", price: 999 },
  { id: 2, name: "MacBook", price: 1999 },
];

export function ProductList() {
  return (
    <table border="1" width="30%">
        <thead>
          <tr>  
            <th>序號</th>
            <th>產品名稱</th>
            <th>價格</th>
            </tr>
        </thead>
      <tbody>
      {products.map((product, index) => (
        <tr key={product.id}>
          <td>{index + 1}</td>
          <td>{product.name}</td>
          <td>${product.price}</td>
        </tr>
      ))}
      </tbody>
    </table>
  );
}