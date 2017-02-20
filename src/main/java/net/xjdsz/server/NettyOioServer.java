package net.xjdsz.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

/**
 * Created by dingshuo on 2017/2/17.
 */
public class NettyOioServer {
    public void server(int port) throws Exception{
        final ByteBuf buf= Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi!\r\n", CharsetUtil.UTF_8));

        //事件循环组
        //这里网上的资料写的是new NioEventLoopGroup()，但是Channel和EventLoopGroup的EventLoop必须相容
        EventLoopGroup group =new OioEventLoopGroup();
        try{
            //用来引导服务器配置
            ServerBootstrap b=new ServerBootstrap();
            //使用OIO模式
            b.group(group).channel(OioServerSocketChannel.class).localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            //添加一个“入站”handler到ChannelPipeline
                            channel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    //连接后，写消息到客户端，写完后关闭连接
                                    ctx.writeAndFlush(buf.duplicate()).addListener(ChannelFutureListener.CLOSE);
                                }
                            });
                        }
                    });

            //绑定服务器接收连接
            ChannelFuture f=b.bind().sync();
            System.out.println(NettyOioServer.class.getName()+"started and listen on "+f.channel().localAddress());
            f.channel().closeFuture().sync();
        }catch (Exception e){
            //释放所有资源
            e.printStackTrace();
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception{
        NettyOioServer nettyOioServer= new NettyOioServer();
        nettyOioServer.server(21000);
    }
}
