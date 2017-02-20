package net.xjdsz.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Created by dingshuo on 2017/2/17.
 */
public class PlainOioServer{
    public void server(int port) throws Exception{
        final ServerSocket socket=new ServerSocket(20000);
        try{
            while (true){
                final Socket clientSocket=socket.accept();
                System.out.println("Accepted connection from"+clientSocket);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OutputStream out;
                        try{
                            out=clientSocket.getOutputStream();
                            out.write("Hi!\r\n".getBytes(Charset.forName("UTF-8")));
                            out.flush();
                            clientSocket.close();
                        }catch (Exception ex){
                            try{
                                clientSocket.close();
                            }catch (IOException el){
                                el.printStackTrace();
                            }
                        }
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
            socket.close();
        }
    }

    public static void main(String[] args) throws Exception{
        PlainOioServer plainOioServer=  new PlainOioServer();
        plainOioServer.server(20000);
    }

}
