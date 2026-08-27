import { useState } from 'react'
import { login, validateToken, fetchProtectedData } from '../api/authApi.js'

const TOKEN_KEY = 'token'
const USER_KEY = 'username'

export default function LoginForm() {
  // ✅ 用 useState 取代全域變數 isLoggedIn 與手動 DOM 操作
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [message, setMessage] = useState('Message')     // 原 #loginMessage
  const [status, setStatus] = useState('Status')         // 原 #loginStatus

  // 登入：取代 $('#loginBtn').click(...)
  async function handleLogin(e) {
    e.preventDefault()
    try {
      const res = await login(username, password)
      localStorage.setItem(TOKEN_KEY, res.token)
      sessionStorage.setItem(USER_KEY, username)
      setMessage('')
      setStatus(`歡迎，${username}`)
      alert('登入成功！')
    } catch {
      setMessage('帳號或密碼錯誤')
    }
  }

  // 檢驗 Token：取代 checkLoginStatus()
  async function handleCheckStatus() {
    const token = localStorage.getItem(TOKEN_KEY)
    if (!token) return handleLogout()

    try {
      const res = await validateToken(token)
      if (res.valid) {
        setStatus(`歡迎回來，${sessionStorage.getItem(USER_KEY)}`)
      } else {
        handleLogout()
      }
    } catch {
      handleLogout()
    }
  }

  // 登出：取代 handleLogout()
  function handleLogout() {
    localStorage.removeItem(TOKEN_KEY)
    sessionStorage.removeItem(USER_KEY)
    setStatus('請登入')
  }

  // Header Token：取代 fetchProtectedData()
  async function handleProtected() {
    const token = localStorage.getItem(TOKEN_KEY)
    try {
      const data = await fetchProtectedData(token)
      setMessage(`${data.user}\n${data.timestamp}`)
    } catch (err) {
      alert('登入已過期，請重新登入')
      handleLogout()
    }
  }

  return (
    <>
      <label>User Name</label>
      {/* ✅ 受控元件：value 綁定 state */}
      <input
        type="text"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        placeholder="admin"
      />
      <br />

      <label>Password</label>
      <input
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        placeholder="1234"
      />
      <br />

      <button onClick={handleLogin}>登入</button>
      <button onClick={handleCheckStatus}>檢驗Token</button>
      <br />
      <button onClick={handleProtected}>Header Toaken</button>

      <div>{message}</div>
      <div>{status}</div>
    </>
  )
}