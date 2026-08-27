// 取代原本散落各處的 $.ajax 呼叫
const url="http://localhost:8080/api/user";
export async function login(username, password) {
  const res = await fetch(`${url}/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password }),
  })
  if (!res.ok) throw new Error('帳號或密碼錯誤')
  return res.json() // { token: "..." }
}

export async function validateToken(token) {
  const res = await fetch(`${url}/validate`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ token }),
  })
  if (!res.ok) return { valid: false }
  return res.json() // { valid: true/false }
}

export async function fetchProtectedData(token) {
  const res = await fetch(`${url}/protected`, {
    headers: { Authorization: 'Bearer ' + token }, // JWT 放入 Header
  })
  if (res.status === 401) throw new Error('登入已過期')
  if (!res.ok) throw new Error('請求失敗')
  return res.json() // { user: "...", timestamp: "..." }
}