package net.xjdsz.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

/**
 * Created by dingshuo on 2017/2/20.
 */
public class BootstrapingFromChannel {
    public static void main(String[] args){
        EventLoopGroup bossGroup=new NioEventLoopGroup(1);
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        ServerBootstrap b=new ServerBootstrap();
        b.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
                .childHandler(new SimpleChannelInboundHandler<ByteBuf>() {
                    ChannelFuture connectFuture;
                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        Bootstrap b=new Bootstrap();
                        b.channel(NioSocketChannel.class).handler(
                                new SimpleChannelInboundHandler<ByteBuf>() {

                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        ctx.write(Unpooled.copiedBuffer("client rocks!", CharsetUtil.UTF_8));
                                        ctx.flush();
                                    }

                                    @Override
                                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
                                        System.out.println("Received data");
                                        byteBuf.clear();
                                    }
                                }
                        );
                        b.group(ctx.channel().eventLoop());
                        connectFuture=b.connect(new InetSocketAddress("127.0.0.1",20001));
                    }

                    @Override
                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
                        if(connectFuture.isDone()){
                            if(!byteBuf.hasArray()){
                                int len = byteBuf.readableBytes();
                                byte[] arr = new byte[len];
                                byteBuf.getBytes(0, arr);
                                System.out.println(ByteBufUtil.hexDump(arr));
                            }else {
                                System.out.println(ByteBufUtil.hexDump(byteBuf.array()));
                            }
                        }
                    }
                });
        ChannelFuture f=b.bind(20000);
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if(channelFuture.isSuccess()){
                    System.out.println("Server bound");
                }else{
                    System.out.println("bound fail");
                    channelFuture.cause().printStackTrace();
                }
            }
        });
    }
}
