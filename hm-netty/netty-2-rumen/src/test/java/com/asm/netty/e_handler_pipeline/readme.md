### 组件 

### pipeline
        流水线
        主要 介绍 各种方法 执行顺序
#### 3. 出站处理器 ChannelOutboundHandler 里面不能有 从底部开始往上找的方法，不然一直出站 无线执行
####    出站处理器 ChannelOutboundHandler 里面只能是 从当前往上执行
 *  ch.writeAndFlush  、 ctx.channel().writeAndFlush     【最后一个handler 往上找】      【一般都是入站处理器使用】
 *  ctx.writeAndFlush    ctx.write                       【当前节点 往上找 出站处理器】


#### 可以粗略理解：   ch.write        || ch.writeAndFlush
================     ||                        || 
####        等于： ctx.channel.write || ctx.channel.writeAndFlush  