package cn.ncu.chat.dto;

import cn.ncu.chat.enums.ChatStatus;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private static final long serialVersionUID = 5467533544208632944L;

    private String username;
    private String password;
    private String notice;  // 用户上线通知
    private ChatStatus status;
    private List<String> onlineUserList;    // 所有在线用户的名字

    private String sender;  // 消息发送者
    private String receiver;    // 消息接收者
    private String content; // 发送的消息内容

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public List<String> getOnlineUserList() {
        return onlineUserList;
    }

    public void setOnlineUserList(List<String> onlineUserList) {
        this.onlineUserList = onlineUserList;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public ChatStatus getStatus() {
        return status;
    }

    public void setStatus(ChatStatus status) {
        this.status = status;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", status=" + status +
                '}';
    }
}
