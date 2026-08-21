var x=[1,2,3,4,5];
for(var i=0;i<x.length;i++){
    console.log(x[i]);
}
x.forEach(function(value,index){
    console.log(value);
});
var students=[
    {name:"John",score:90},
    {name:"Mary",score:85},
    {name:"Janny",score:78}];

students.forEach(function(student,index){
    console.log("index:"+index+" "+student.name+" "+student.score);
});