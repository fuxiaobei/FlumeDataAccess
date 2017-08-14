package com.asiainfo.mme.socket;

import org.apache.log4j.Logger;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by yangjing5 on 2016/4/18.
 */
public class SocketServer implements  Runnable {
    public static LinkedBlockingQueue<Socket> socketQueue = new LinkedBlockingQueue<Socket>(Integer.MAX_VALUE);
    private final static Logger logger = Logger.getLogger(SocketServer.class);
    private String socketPort;
    public SocketServer(String socketPort) {
        this.socketPort = socketPort;
    }
    @Override
    public void run() {
        ServerSocket server=null;
        try {
            server=new ServerSocket(Integer.parseInt(socketPort));
        } catch (IOException e) {
            logger.error("could not listen ",e);
        }
        Socket socket=null;
        while(true) {
            try {
                server.setReceiveBufferSize(10 * 10240 * 1024);
                socket = server.accept();
                socket.setReceiveBufferSize(10 * 10240 * 1024);
                //socketQueue.put(socket);
                logger.info("ReceiveBufferSize:"+socket.getReceiveBufferSize());
                new Thread(new SocketAnalysisTask(socket)).start();
                //test
                /*logger.info("starting...");
                socket = server.accept();
                DataInputStream in = new DataInputStream(socket.getInputStream());
                byte[] len = new byte[4];
                in.readFully(len);
                int alllen = SocketUtil.toHexString2(len);
                logger.info("alllen:"+alllen);
                byte[] all_datas = new byte[alllen - 4];
                in.readFully(all_datas);
                //获得类型长度
                byte[] type_len = new byte[1];
                System.arraycopy(all_datas,0,type_len,0,1);
                int type_l = SocketUtil.byte1Int(type_len);
                //获得类型
                byte[] type = new byte[type_l];
                System.arraycopy(all_datas,1,type,0,type_l);
                String type_str = new String(type);
                //获得数据长度
                byte[] data_len = new byte[4];
                System.arraycopy(all_datas,1 + type_l,data_len,0,4);
                int data_l = SocketUtil.toHexString2(data_len);
                //获得数据
                byte[] data = new byte[data_l];
                System.arraycopy(all_datas,1 + type_l + 4,data,0,data_l);
                String data_str = new String(data);
                logger.info("type_len:" + type_l);
                logger.info("type:" + type_str);
                logger.info("data_len:" + data_l);
                logger.info("data:" + data_str);*/
            } catch (IOException e) {
                logger.error("could not accept data from client",e);
            }/*finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/
        }
    }
}
