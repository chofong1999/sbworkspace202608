const name = "Alice";
const price = 99.9;
const isLoggedIn = true;
const username = "Alice123";

// 方法3：提前 return（適合「整個畫面」切換，最直覺）
export function UserGreeting({ isLoggedIn, username }) {
  if (!isLoggedIn) {
    return <h1>請先登入</h1>;
  }
  return <h1>歡迎回來，{username}！</h1>;
}
export function VarTest() {
  return (
    <div>
      {/* 插入變數 */}
      <h1>Hello, {name}!</h1>

      {/* 數學運算 */}
      <p>含稅價格：{price * 1.05} 元</p>

      {/* 呼叫函式 */}
      <p>{name.toUpperCase()}</p>

      {/* 三元運算子 */}
      <p>{isLoggedIn ? "已登入" : "請登入"}</p>

      // 方法1：三元運算子（有 else）
      {isLoggedIn ? <h1>歡迎回來，{username}！</h1> : <h1>請先登入</h1>}

    // 方法2：&& 短路運算（只有 if，沒有 else）
        {isLoggedIn && <button>登出</button>}
    </div>
  );
}
