import { useState } from 'react';

function SessionUI() {
    const [data, setData] = useState("");
       // 定義非同步函式（useEffect 的 callback 本身不能是 async）
      async function createSession() {
            try {
                const response = await fetch("http://localhost:8011/api/create");

                if (!response.ok) {
                    throw new Error(`HTTP 錯誤：${response.status}`);
                }

                const data = await response.text();
                setData(data);
            } catch (err) {
                console.error(err);
            }
        }
        async function getSession() {
            try {
                const response = await fetch("http://localhost:8011/api/get");

                if (!response.ok) {
                    throw new Error(`HTTP 錯誤：${response.status}`);
                }

                const data = await response.text();
                setData(data);
            } catch (err) {
                console.error(err);
            }
        }
       
   

    return (
        <div>
            <button onClick={() => createSession()}>Create Session</button>
            <button onClick={() => getSession()}>Get Session</button>
            <h2>{data}</h2>
        </div>
    );
}

export default SessionUI;