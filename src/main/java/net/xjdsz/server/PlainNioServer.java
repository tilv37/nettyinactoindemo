package net.xjdsz.server;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.ParseException;
import java.util.Iterator;

/**
 * Created by dingshuo on 2017/2/17.
 */
public class PlainNioServer {
    public void server(int port) throws Exception{
        System.out.println("Listening for connections on port "+port);

        Selector selector=Selector.open();
        ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
        ServerSocket serverSocket=serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(20000));

        //设置非阻塞模式
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);
        final ByteBuffer msg=ByteBuffer.wrap("Hi!\r\n".getBytes());
        while (true) {
            int n=selector.select();
            if(n>0){
                Iterator<SelectionKey> iter=selector.selectedKeys().iterator();
                while(iter.hasNext()){
                    SelectionKey selectionKey=iter.next();
                    iter.remove();
                    try{
                        if(selectionKey.isAcceptable()){
                            ServerSocketChannel serverSocketChannel1=(ServerSocketChannel)selectionKey.channel();
                            SocketChannel client=serverSocketChannel1.accept();
                            System.out.println("Accepted connection from "+client);
                            client.configureBlocking(false);
                            client.register(selector,SelectionKey.OP_WRITE,msg.duplicate());
                        }

                        if(selectionKey.isWritable()){
                            SocketChannel client=(SocketChannel)selectionKey.channel();
                            ByteBuffer buff=(ByteBuffer)selectionKey.attachment();
                            while (buff.hasRemaining()){
                                if(client.write(buff)==0){
                                    break;
                                }
                            }
                            client.close();
                        }
                    }catch (Exception e){
                        selectionKey.cancel();
                        selectionKey.channel().close();
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception{
        PlainNioServer plainNioServer=new PlainNioServer();
        plainNioServer.server(20000);
    }
}
