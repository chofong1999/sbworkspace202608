// App.jsx — 定義路由對應關係
import { Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar.jsx';
import Home from './components/Home.jsx';
import About from './components/About.jsx';
import Products from './components/Products.jsx';
import NotFound from './components/NotFound.jsx';
import FakeProductDetail from './components/FakeProductDetail.jsx';
import LoginForm from './components/LoginForm.jsx'; 

    
function Approute() {
  return (
    <>
      <Navbar />  {/* Navbar 永遠顯示 */}

      {/* Routes 內只渲染「第一個符合 URL 的 Route」 */}
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/about" element={<About />} />        
        <Route path="/products" element={<Products />} />
        <Route path="/fakeproductdetail/:id" element={<FakeProductDetail />} />
        <Route path="/login" element={<LoginForm />} /> 
        <Route path="*" element={<NotFound />} />  {/* 萬用：404 */}
      </Routes>
    </>
  );
}

export default Approute;