package cn.ncu.chat.server;

import cn.ncu.chat.enums.ChatStatus;
import cn.ncu.chat.dto.User;
import cn.ncu.chat.utils.IOUtil;

import javax.swing.JList;
import javax.swing.JTextPane;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class ServerHandler extends Thread {

    private Socket socket;
    public ServerFrame serverFrame;
    // 每次客户端连接之后，都会新建一个ServerHandler线程，所以用户集合和Socket集合必须是static的
    private static List<String> onlineUsers = new ArrayList<>();
    private static List<Socket> onlineSockets = new ArrayList<>();
    private static HashMap<String, Socket> socketMap = new HashMap<>();

    public ServerHandler(Socket socket, ServerFrame serverFrame) {
        this.socket = socket;
        this.serverFrame = serverFrame;
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println("服务器端的循环");
                Object obj = IOUtil.readMessage(socket);
                if (obj instanceof User) {
                    User user = (User) obj;
                    switch (user.getStatus()) {
                        case NOT_ONLINE:
                            sendLoginMsg(user); // 发送登录消息
                            break;
                        case LOGIN:
                            // 在ClientHandler里面登录之后LOGIN状态会立即变为NOTICE状态
                            // 所以这里不可能会出现LOGIN状态
                            System.out.println("无效状态");
                            break;
                        case NOTICE:
                            sendOnlineNoticeToAll(user);    // 群发用户上线通知
                            log(" >>> " + user.getUsername() + "上线啦！");
                            flushOnlineUserList();  // 刷新服务器中的用户列表
                            sendUserListToClient(user); // 发消息给客户端，在客户端显示用户列表
                            break;
                        case CHAT:
                            if("ALL".equals(user.getReceiver())) {
                                // 群发消息
                                sendMsgToAll(user);
                                log(user.getSender() + " 在群里说：" + user.getContent());
                            } else {
                                // 私聊
                                log(user.getSender() + " 对 " + user.getReceiver() + " 说：" + user.getContent());
                                sendPrivateMsg(user);
                            }
                            break;
                        case DD:
                            if("ALL".equals(user.getReceiver())) {
                                // 群发抖动
                                sendMsgToAll(user);
                                log(user.getSender() + " 在群里发送了一个窗口抖动");
                            } else {
                                // 私发抖动
                                log(user.getSender() + " 对 " + user.getReceiver() + " 发送了一个窗口抖动");
                                sendPrivateMsg(user);
                            }
                            break;
                        case QUIT:
                            // 退出处理
                            logout(user);
                            log(user.getUsername() + " 已下线");
                            // 休眠一秒后
                            Thread.sleep(1000);
                            // 关闭当前socket连接
                            socket.close();
                            // 关闭当前线程
                            this.interrupt();
                            // 跳出循环
                            break;
                    }
                }
                System.out.println("服务端：" + obj);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 用户退出处理，清理在线人数，刷新用户列表，告诉所有人你已经离开
     * @param user
     */
    private void logout(User user) {
        String username = user.getUsername();
        // 将该用户从onlineUsers中移除
        for(int i = 0; i < onlineUsers.size(); ++i) {
            if(onlineUsers.get(i).equals(username)) {
                onlineUsers.remove(i);
                break;
            }
        }

        // 将该用户从onlineSockets中移除
        for(int i = 0; i < onlineSockets.size(); ++i) {
            if(onlineSockets.get(i) == socket) {
                onlineSockets.remove(i);
                break;
            }
        }

        // 将该用户从socketMap中移除
        socketMap.remove(username);

        // 刷新服务器界面中的在线用户数量
        flushOnlineUserList();

        // 给所有在线的用户发送下线消息
        user.setStatus(ChatStatus.NOTICE);
        sendMsgToAll(user);

        //告诉其他人刷新用户列表
        sendUserListToClient(user);
    }

    /**
     * 发送私聊消息
     * @param user
     */
    private void sendPrivateMsg(User user) {
        String receiver = user.getReceiver();
        Socket recSocket = socketMap.get(receiver);
        user.setReceiver("你");
        IOUtil.writeMessage(recSocket, user);

        String sender = user.getSender();
        Socket senSocket = socketMap.get(sender);
        user.setSender("你");
        user.setReceiver(receiver);
        IOUtil.writeMessage(senSocket, user);
    }

    /**
     * 在服务器记录日志
     * @param log
     */
    private void log(String log) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateStr = sdf.format(date);
        JTextPane txtLog = serverFrame.serverInfoPanel.txtLog;
        txtLog.setText(txtLog.getText() + "\n" + dateStr + " " + log);
    }

    /**
     * 群发消息
     * @param user
     */
    private void sendMsgToAll(User user) {
        for (int i = 0; i < onlineSockets.size(); ++i) {
            IOUtil.writeMessage(onlineSockets.get(i), user);
        }
    }

    /**
     * 发消息给客户端，在客户端显示用户列表
     * @param user
     */
    private void sendUserListToClient(User user) {
        user.setStatus(ChatStatus.USERLIST);
        user.setOnlineUserList(onlineUsers);
        // 给所有客户端发，因为所有客户端页面都要更新用户列表
        for (int i = 0; i < onlineSockets.size(); ++i) {
            IOUtil.writeMessage(onlineSockets.get(i), user);
        }
    }

    /**
     * 刷新服务器页面中的用户列表
     */
    private void flushOnlineUserList() {
        JList lstUser = serverFrame.onlineUserPanel.lstUser;
        String[] userArr = onlineUsers.toArray(new String[onlineUsers.size()]);
        // 显示服务器页面的用户列表
        lstUser.setListData(userArr);
        // 显示服务器页面的在线用户数量
        serverFrame.serverInfoPanel.txtNumber.setText(String.valueOf(userArr.length));
    }


    /**
     * 发送登录消息
     *
     * @param user
     * @throws FileNotFoundException
     */
    private void sendLoginMsg(User user) throws FileNotFoundException {
        Scanner sc = new Scanner(new File("src/user.txt"));
        int flag = 0;
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            // 如果用户名和密码正确，则把标志置为true
            if (line.equals(user.getUsername() + "|" + user.getPassword())) {
                flag = 1;
                user.setStatus(ChatStatus.LOGIN);
                onlineUsers.add(user.getUsername());
                onlineSockets.add(socket);
                socketMap.put(user.getUsername(), socket);
                break;
            }
        }
        // 把登录信息写回给服务端
        IOUtil.writeMessage(socket, user);
        if(flag == 0) {
            log(user.getUsername() + "用户名或密码错误，登录失败...");
        }
    }

    /**
     * 群发上线通知
     *
     * @param user 上线用户
     */
    private void sendOnlineNoticeToAll(User user) {
        user.setNotice(" >>> " + user.getUsername() + "上线啦！");
        for (int i = 0; i < onlineSockets.size(); ++i) {
            IOUtil.writeMessage(onlineSockets.get(i), user);
        }
    }
}
