const nums = [1, 2, 3, 4, 5];
console.log("org:"+nums);
// map：每個元素乘 2，回傳新陣列
const doubled = nums.map(n => n * 2);
console.log("double array: "+doubled);   // [2, 4, 6, 8, 10]

// filter：只保留偶數
const evens = nums.filter(n => n % 2 === 0);
console.log("even array: "+evens);     // [2, 4]

// reduce：加總所有元素（acc 是累計值，0 是初始值）
const sum = nums.reduce((acc, n) => acc + " "+n, "");
console.log("sum: "+sum);       // 15

// 串接使用（先篩再轉換）
const result = nums
  .filter(n => n > 2)       // [3, 4, 5]
  .map(n => n * 10);        // [30, 40, 50]
console.log("result array: "+result);        // [30, 40, 50]