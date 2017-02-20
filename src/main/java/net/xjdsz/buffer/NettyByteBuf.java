package net.xjdsz.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Iterator;

/**
 * Created by dingshuo on 2017/2/20.
 */
public class NettyByteBuf {
    public void bufferTest(){
        CompositeByteBuf compositeByteBuf= Unpooled.compositeBuffer();
        ByteBuf heapBuf=Unpooled.buffer(8);
        ByteBuf directBuf=Unpooled.buffer(16);

        //添加ByteBuf到CompositeByteBuf;
        compositeByteBuf.addComponents(heapBuf,directBuf);
        //删除第一个ByteBuf
        compositeByteBuf.removeComponent(0);

        Iterator<ByteBuf> iterator=compositeByteBuf.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next().toString());
        }

        if(!compositeByteBuf.hasArray()){
            int len=compositeByteBuf.readableBytes();
            byte[] arr=new byte[len];
            compositeByteBuf.getBytes(0,arr);
        }
    }

    public void operateBuf(){
        //遍历buf中的每一个字节
        ByteBuf buf=Unpooled.buffer(16);

        //写
        for (int i=0;i<16;i++){
            buf.writeByte(i);
        }

        //读
        for (int i=0;i<buf.capacity();i++){
            System.out.println();buf.getByte(i);
        }

        //逐个读取buf中的字节，同事读取的index将会随之移位
        ByteBuf buf1=Unpooled.buffer(16);
        while (buf.isReadable()){
            System.out.println(buf1.readByte());
        }
    }
}
