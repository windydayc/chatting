package cn.ncu.chat.client;

import cn.ncu.chat.enums.ChatStatus;
import cn.ncu.chat.dto.User;
import cn.ncu.chat.utils.IOUtil;

import javax.swing.JTextPane;
import java.net.Socket;
import java.util.List;

public class ClientHandler extends Thread {

    private Socket socket;
    private LoginFrame loginFrame;
    private ChatFrame chatFrame;

    public ClientHandler(Socket socket, LoginFrame loginFrame) {
        this.socket = socket;
        this.loginFrame = loginFrame;
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println("客户端的循环");
                Object obj = IOUtil.readMessage(socket);
                System.out.println("客户端：" + obj);
                if (obj instanceof User) {
                    User user = (User) obj;
                    switch (user.getStatus()) {
                        case NOT_ONLINE:
                            System.out.println("账号或密码错误，登录失败！");
                            break;
                        case LOGIN:
                            handleLogin(user);  // 处理用户登录
                            break;
                        case NOTICE:
                            handleNotice(user);   // 处理上线通知
                            break;
                        case USERLIST:
                            handleUserList(user);   // 客户端显示用户列表
                            break;
                        case CHAT:
                            handleChat(user);   // 展示聊天消息
                            break;
                        case DD:
                            handleDD(user); // 处理抖动

                    }
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void handleDD(User user) {
        JTextPane acceptPane = chatFrame.acceptPane;
        if ("ALL".equals(user.getReceiver())) {
            acceptPane.setText(acceptPane.getText() + user.getSender() + " 在群里" + user.getContent() + "\n");
        } else {
            acceptPane.setText(acceptPane.getText() + user.getSender() + " 对 " + user.getReceiver() + " " + user.getContent() + "\n");
        }
        new DouDong(chatFrame).start();
    }

    private void handleChat(User user) {
        JTextPane acceptPane = chatFrame.acceptPane;
        if ("ALL".equals(user.getReceiver())) {
            acceptPane.setText(acceptPane.getText() + user.getSender() + " 说：" + user.getContent() + "\n");
        } else {
            acceptPane.setText(acceptPane.getText() + user.getSender() + " 对 " + user.getReceiver() + " 说：" + user.getContent() + "\n");
        }
    }

    private void handleUserList(User user) {
        List<String> userList = user.getOnlineUserList();
        chatFrame.lstUser.setListData(userList.toArray(new String[userList.size()]));
    }

    private void handleNotice(User user) {
        JTextPane acceptPane = chatFrame.acceptPane;
        // 在多人聊天面板(接收消息面板)追加信息
        acceptPane.setText(acceptPane.getText() + user.getNotice() + "\n");
    }


    /**
     * 处理用户登录用户登录，并且发送用户上线通知给服务器
     *
     * @param user
     */
    private void handleLogin(User user) {
        chatFrame = new ChatFrame(socket, user.getUsername());
        loginFrame.dispose();
        System.out.println(user.getUsername() + "登录成功");
        // 修改状态为上线通知，把上线通知发送给服务器，让服务器群发给所有客户端
        user.setStatus(ChatStatus.NOTICE);
        IOUtil.writeMessage(socket, user);
    }
}
