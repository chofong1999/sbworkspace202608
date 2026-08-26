import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import PostDetail from './components/PostDetail.jsx'
const postId = window.location.pathname.split('/')[1] || 1
//import App from './App.jsx'
// import TodoList from './components/TodoList.jsx'
// import UserList from './components/UserList.jsx'
// import Timer from './components/Timer.jsx'
createRoot(document.getElementById('root')).render(
  <StrictMode>
    <PostDetail postId={postId} />
  </StrictMode>,
)
