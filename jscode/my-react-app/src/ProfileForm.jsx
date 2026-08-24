import { useState } from 'react';

// ===== 物件 State =====
function ProfileForm() {
  const [user, setUser] = useState({ name: "", email: "" });

  const handleNameChange = (e) => {
    // ✅ 用展開運算子保留其他欄位，只更新需要的
    setUser({ ...user, name: e.target.value });
  };

  return (
    <div>
      <input value={user.name} onChange={handleNameChange} />
      <button onClick={() => console.log(user)}>送出</button>
    </div>
  );
}
export default ProfileForm;