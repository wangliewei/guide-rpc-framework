### 以redis协议 为例
命令：set key value
     set name zhangsan
首先 把整个看成一个数组
协议内容：
```
*3   数组个数
$3   key命令长度
set
$4   key值 内容长度
name
$8   value值 内容长度
zhangsan
```




