import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
//import App from './App.jsx'
//import { VarTest ,UserGreeting } from './VarTest.jsx'
//import MyFirstApp from './MyFirstApp.jsx'
//import { FruitList, ProductList } from './ArrayList.jsx'
//import ProductCard from './ProductCard.jsx'
import Counter from './Counter.jsx'
createRoot(document.getElementById('root')).render(
  <StrictMode>    
  
    <Counter />

  </StrictMode>,
)
