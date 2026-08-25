# 第32章延伸閱讀：React useEffect與Fetch穩定性

## 本頁快速索引

- [1. 不要共用不相干的Loading State](#1-不要共用不相干的loading-state)
- [2. 新Request開始前清除舊錯誤](#2-新request開始前清除舊錯誤)
- [3. 數字輸入先保留String](#3-數字輸入先保留string)
- [4. 取消已經過期的Fetch](#4-取消已經過期的fetch)
- [5. StrictMode中的重複Effect](#5-strictmode中的重複effect)
- [6. 何時不需要Effect](#6-何時不需要effect)

## 1. 不要共用不相干的Loading State

完整清單與單一使用者是兩個獨立Request。若共用一個`loading`，任一Request先完成就可能提早關閉載入畫面。

```jsx
const [listLoading, setListLoading] = useState(true)
const [detailLoading, setDetailLoading] = useState(false)
```

每個畫面區塊應由自己的Request狀態控制；若產品設計要求整頁共同載入，再刻意合併。

## 2. 新Request開始前清除舊錯誤

```jsx
async function loadUser() {
  setError(null)
  setDetailLoading(true)
  try {
    // fetch...
  } catch (error) {
    setError(error.message)
  } finally {
    setDetailLoading(false)
  }
}
```

若錯誤畫面使用`if (error) return ...`提前結束render，輸入欄位也會消失，使用者可能無法修改ID重試。可把錯誤顯示在表單旁邊，而不是替換整個畫面。

## 3. 數字輸入先保留String

`Number('')`會得到`0`。若欄位允許暫時清空，直接在`onChange`轉Number會把空白立即變成0。

```jsx
const [userIdInput, setUserIdInput] = useState('1')

function submit(event) {
  event.preventDefault()
  const id = Number(userIdInput)
  if (!Number.isInteger(id) || id < 1) {
    setError('ID必須是大於0的整數')
    return
  }
  setUserId(id)
}
```

這讓「正在輸入的文字」與「已通過驗證的ID」各自有明確責任。

## 4. 取消已經過期的Fetch

使用者快速連續改變ID時，較早送出的Request可能較晚完成，最後反而覆蓋新資料。Effect cleanup可取消舊Request：

```jsx
useEffect(() => {
  const controller = new AbortController()

  async function load() {
    try {
      const response = await fetch(
        `https://jsonplaceholder.typicode.com/users/${userId}`,
        { signal: controller.signal }
      )
      if (!response.ok) throw new Error(`HTTP ${response.status}`)
      setUser(await response.json())
    } catch (error) {
      if (error.name !== 'AbortError') setError(error.message)
    }
  }

  load()
  return () => controller.abort()
}, [userId])
```

cleanup會在dependency改變而準備執行新Effect前，以及元件卸載時執行。

## 5. StrictMode中的重複Effect

開發模式的`StrictMode`可能執行一次「設定 → 清理 → 再設定」，用來暴露缺少cleanup的副作用。不要以移除`StrictMode`掩蓋問題；應讓Effect可安全地重做並正確清理。

## 6. 何時不需要Effect

- 按下按鈕後才送出資料：直接放在click／submit handler。
- 由現有Props或State即可算出的值：在render中計算，或必要時使用memoization。
- 只為了依State再設定另一個可推導State：通常可移除第二份State。

Effect的核心用途是「同步React外部系統」，不是所有程式流程的通用入口。
