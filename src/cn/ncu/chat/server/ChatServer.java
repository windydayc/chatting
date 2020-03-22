package cn.ncu.chat.server;

import cn.ncu.chat.constants.Constants;
import cn.ncu.chat.entity.ServerInfoBean;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 服务器启动入口
 */
public class ChatServer {

    public ServerFrame serverFrame;

    public ChatServer() {
        try {
            // 建立服务器的Socket监听
            ServerSocket sso = new ServerSocket(Constants.SERVER_PORT);
            serverFrame = new ServerFrame();

            // 初始化服务器信息
            ServerInfoBean serverInfo = getServerIP();
            loadServerInfo(serverInfo);

            // 等待连接，阻塞方式
            // 这个while循环是为了解决多个连接
            while (true) {
                Socket socket = sso.accept();
                ServerHandler serverHandler = new ServerHandler(socket, serverFrame);
                serverHandler.start();
                System.out.println("服务端接受到客户端的连接");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadServerInfo(ServerInfoBean serverInfo) {
        serverFrame.serverInfoPanel.txtIP.setText(serverInfo.getIp());
        serverFrame.serverInfoPanel.txtServerName.setText(serverInfo.getHostName());
        serverFrame.serverInfoPanel.txtPort.setText(String.valueOf(serverInfo.getPort()));
        serverFrame.serverInfoPanel.txtLog.setText("服务器已启动...");
    }

    /**
     * 获取服务器的主机名和IP地址
     * @return 返回服务器IP等信息
     */
    public ServerInfoBean getServerIP() {
        ServerInfoBean sib = null;
        try {
            InetAddress serverAddress = InetAddress.getLocalHost();
            byte[] ipAddress = serverAddress.getAddress();
            sib = new ServerInfoBean();
            sib.setIp(serverAddress.getHostAddress());
            sib.setHostName(serverAddress.getHostName());
            sib.setPort(Constants.SERVER_PORT);

            System.out.println("Server IP is:" + (ipAddress[0] & 0xff) + "."
                    + (ipAddress[1] & 0xff) + "." + (ipAddress[2] & 0xff) + "."
                    + (ipAddress[3] & 0xff));
        } catch (Exception e) {
            System.out.println("###Cound not get Server IP." + e);
        }
        return sib;
    }


    public static void main(String[] args) {
        new ChatServer();
    }

}
