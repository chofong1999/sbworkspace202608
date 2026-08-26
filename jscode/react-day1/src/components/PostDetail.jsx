// src/components/PostDetail.jsx
import { useState, useEffect } from 'react';

function PostDetail({ postId }) {
    const [post, setPost] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    async function fetchArticleById(postId) {
        // setLoading(true);
        // setError(null);
        try {
            const response = await fetch(`https://jsonplaceholder.typicode.com/posts/${postId}`);

            if (!response.ok) {
                throw new Error(`HTTP 錯誤：${response.status}`);
            }
            const data = await response.json();
            setPost(data);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }
    useEffect(() => {
        async function fetchData() {
            await fetchArticleById(postId);
        }
        fetchData();
    }, [postId]);
    if(loading) return <p>載入中...</p>;
    if(error) return <p>錯誤：{error}</p>;
return (
    <div>
        <h2>文章詳情</h2>
        <p>文章ID：{postId}</p>
        <p>文章標題：{post?.title}</p>
        <p>文章內容：{post?.body}</p>
    </div>
)
}

export default PostDetail