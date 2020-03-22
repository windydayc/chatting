package cn.ncu.chat.enums;

/**
 * 消息类型枚举
 */
public enum ChatStatus {
    NOT_ONLINE(0, "未登录"),
    LOGIN(1, "登录消息"),
    NOTICE(2, "系统消息"),   // 用户上线/下线提示
    USERLIST(3, "客户端显示在线用户列表"),
    CHAT(4, "聊天消息"),
    DD(5, "抖动消息"),
    QUIT(6, "离开聊天室");

    private Integer stat;
    private String desc;

    private ChatStatus(Integer stat, String desc) {
        this.stat = stat;
        this.desc = desc;
    }
}
