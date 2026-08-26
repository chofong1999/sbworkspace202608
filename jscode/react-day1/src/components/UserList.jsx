import { useState, useEffect } from 'react';

function UserList() {
    const [users, setUsers] = useState([]);
    const [user, setUser] = useState({});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [userId, setUserId] = useState(1); // 新增 state 來追蹤使用者 ID

    useEffect(() => {
        // 定義非同步函式（useEffect 的 callback 本身不能是 async）
        async function fetchUsers() {
            try {
                const response = await fetch('https://jsonplaceholder.typicode.com/users');

                if (!response.ok) {
                    throw new Error(`HTTP 錯誤：${response.status}`);
                }

                const data = await response.json();
                setUsers(data);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        }

        fetchUsers();
    }, []); // 空依賴陣列 → 只在掛載時執行一次

    useEffect(() => {
        // 定義非同步函式（useEffect 的 callback 本身不能是 async）
        async function fetchUserById(userId) {
            try {
                const response = await fetch(`https://jsonplaceholder.typicode.com/users/${userId}`);

                if (!response.ok) {
                    throw new Error(`HTTP 錯誤：${response.status}`);
                }

                const data = await response.json();
                setUser(data);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        }

        fetchUserById(userId);
    }, [userId]); // 空依賴陣列 → 只在掛載時執行一次
    // 渲染不同狀態
    if (loading) return <p>載入中...</p>;
    if (error) return <p>錯誤：{error}</p>;

    return (
        <div>
            <label htmlFor="userId">使用者 ID：</label>
            <input
                id="userId"
                type="number"
                value={userId}
                onChange={(e) => setUserId(Number(e.target.value))}
            />
            <br />
            <span>使用者名稱：{user.name}</span>
            <h2>使用者列表</h2>
            {error && <p>錯誤：{error}</p>}
            <ul>
                {users.map(user => (
                    <li key={user.id}>{user.name} — {user.email}</li>
                ))}
            </ul>
        </div>
    );
}

export default UserList;