import { useState } from 'react';

function UserGreeting({ isLoggedIn, username }) {
  // 方法3：提前 return（登入狀態不同，整個畫面就不同）
  if (!isLoggedIn) {
    return <h1>請先登入</h1>;
  }
  return <h1>歡迎回來，{username}！</h1>;
}

function MyFirstApp() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  return (
    <div>
      {/* 方法3：提前 return 的結果 */}
      <UserGreeting isLoggedIn={isLoggedIn} username="小明" />

      {/* 方法2：&& 短路運算（登入後才顯示登出按鈕） */}
      {isLoggedIn && <button>登出</button>} <br/>

      {/* 方法1：三元運算子（切換按鈕的文字） */}
      <button onClick={() => setIsLoggedIn(prev => !prev)}>
        目前{isLoggedIn ? "已登入" : "未登入"}，點我切換
      </button>
    </div>
  );
}

export default MyFirstApp;