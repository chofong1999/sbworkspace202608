package com.example.demo.controller;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.ApiResponse;
import com.example.demo.model.User;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

//@RestController = @Controller + @ResponseBody（回傳 JSON）
@RestController
@RequestMapping("/api/users")     // 所有端點前綴 /api/users
public class UserController implements CommandLineRunner {
	 // ConcurrentHashMap：執行緒安全的 HashMap，不需要資料庫
    private final Map<Long, User> store = new ConcurrentHashMap<>();
    // AtomicLong：執行緒安全的 Long，用來產生自動遞增 ID
    private final AtomicLong idGen = new AtomicLong(4);
    
    // ========== GET /api/users — 查詢全部 ==========
    @GetMapping
    public List<User> getAll() {
        // values() 取得所有 User，包裝為 List 回傳
        return List.copyOf(store.values());
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getById(@PathVariable Long id) {
        User user = store.get(id);
        if (user == null) {
            return ResponseEntity.status(404).body(ApiResponse.error("用戶不存在"));
        }
        return ResponseEntity.ok(ApiResponse.ok(user));
    }
    // ========== GET /api/users/{id} — 查詢單筆 ==========
//    @GetMapping("/{id}")
//    public ResponseEntity<User> getById(@PathVariable Long id) {
//        // @PathVariable：從 URL 路徑取值，如 /api/users/5 → id=5
//        return store.containsKey(id)
//            ? ResponseEntity.ok(store.get(id))
//            : ResponseEntity.notFound().build();
//        // ResponseEntity：可控制 HTTP 狀態碼與標頭
//        // ok() → 200 OK，notFound() → 404 Not Found
//    }
    @GetMapping("/params")
    public List<User> list(
        @RequestParam(defaultValue = "1") int begin,
        @RequestParam(defaultValue = "3") int end) {
    	    List<User> users=store.values().stream().toList();
    	    return users.stream().skip(begin-1).limit(end-begin+1).toList();	
    }
 // ========== POST /api/users — 新增 ==========
    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        // @RequestBody：將 HTTP Request Body（JSON）自動轉為 User 物件
        // 需要 Jackson 依賴（spring-boot-starter-web 已包含）

        user.setId(idGen.getAndIncrement());             // 自動產生 ID
        user.setCreatedAt(java.time.LocalDateTime.now()); // 設定建立時間
        store.put(user.getId(), user);                   // 存入記憶體

        // URI location：告知客戶端新資源的位置
        URI location = URI.create("/api/users/" + user.getId());

        // 201 Created：資源建立成功，並回傳 Location 標頭
        return ResponseEntity.created(location).body(user);
    }
    
    // ========== PUT /api/users/{id} — 完整更新 ==========
    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User user) {
        if (!store.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        user.setId(id);              // 保留原 ID
        store.put(id, user);         // 覆蓋原資料
        return ResponseEntity.ok(user);
    }

 // ========== DELETE /api/users/{id} — 刪除 ==========
    @DeleteMapping("/{id}")
    public ResponseEntity<User> delete(@PathVariable Long id) {
        if (!store.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        User del=store.remove(id);
        //return ResponseEntity.noContent().build(); // 204 No Content
        return ResponseEntity.ok(del); 
    }
   
	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		
		store.put(1L,new User(1L,"Alice","alice@example.com"));
		store.put(2L,new User(2L,"Jimmy","jimmy@example.com"));
		store.put(3L,new User(3L,"Kate","kate@example.com"));
	}
}
