package net.xjdsz.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

/**
 * Created by dingshuo on 2017/2/20.
 */
public class BootStrapingClient {
    public static void main(String[] args) throws Exception{
        EventLoopGroup group=new NioEventLoopGroup();
        Bootstrap b=new Bootstrap();

        b.group(group).channel(NioSocketChannel.class).handler(new SimpleChannelInboundHandler<ByteBuf>() {
            @Override
            protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
                System.out.println("Received data");
                byteBuf.clear();
            }
        });

        ChannelFuture f=b.connect("127.0.0.1",20000);

        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if(channelFuture.isSuccess()){
                    System.out.println("connection finished");
                }else {
                    System.out.println("connection failed");
                    channelFuture.cause().printStackTrace();
                }
            }
        });
    }
}
