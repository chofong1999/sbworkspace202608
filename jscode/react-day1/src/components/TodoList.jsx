import { useState } from 'react';

function TodoList() {
  const [todos, setTodos] = useState([
    { id: 1, text: "買咖啡" },
    { id: 2, text: "學 React" },
  ]);

  // 新增（展開運算子產生新陣列）
  const addTodo = (text) => {
    const maxId = Math.max(...todos.map(item => item.id));  
    setTodos([...todos, { id: maxId + 1, text }]); // ✅
  };

  // 刪除（用 filter 產生新陣列）
  const removeTodo = (id) => {
    setTodos(todos.filter((todo) => todo.id !== id)); // ✅
  };

  // 更新（用 map 產生新陣列）
  const updateTodo = (id, newText) => {
    setTodos(
      todos.map((todo) => (todo.id === id ? { ...todo, text: newText } : todo)) // ✅
    );
  };
  const handleUpdate = (id, oldText) => {
    const newText = prompt("請輸入新的任務內容", oldText ?? "");
    if (newText===0||newText===""||newText) {
      updateTodo(id, newText);
    }
  };
//   const handleUpdate = (id) => {
//   const todo = todos.find(todo => todo.id === id)

//   console.log(todo.text, typeof todo.text)
//   console.log(todo.text || "")

//   const newText = Number(
//     prompt("請輸入新的任務內容", todo.text || "")
//   )

//   if (newText === 0 || newText) {
//     updateTodo(id, newText)
//   }
// }
  return (
    <div>
      <button onClick={() => addTodo("新任務")}>新增任務</button>
      <button onClick={() => updateTodo(1, "買咖啡（已更新）")}>更新任務</button>
      <ul>
        {todos.map((todo) => (
          <li key={todo.id}>
            {todo.text}
            <button onClick={() => removeTodo(todo.id)}>刪除</button>
            <button onClick={() => handleUpdate(todo.id, todo.text)}>更新</button> 
          </li>
        ))}
      </ul>
    </div>
  );
}

export default TodoList;