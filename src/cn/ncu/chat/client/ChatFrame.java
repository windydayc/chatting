package cn.ncu.chat.client;

import cn.ncu.chat.dto.User;
import cn.ncu.chat.enums.ChatStatus;
import cn.ncu.chat.utils.IOUtil;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.Socket;

/**
 * 聊天主界面
 */
public class ChatFrame extends JFrame {
    private static final long serialVersionUID = -8945833334986986964L;

    /**
     * 服务器窗体宽度
     */
    public static final Integer FRAME_WIDTH = 750;

    /**
     * 服务器窗体高度
     */
    public static final Integer FRAME_HEIGHT = 600;

    public JTextPane acceptPane;
    public JList lstUser;
    public JComboBox receiverBox;

    private String sender;    // 消息发送者(当前用户)
    private Socket socket;

    public ChatFrame(Socket socket, String username) {
        this.sender = username;
        this.socket = socket;

        this.setTitle("聊天室主界面 当前用户：" + username);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        //窗体不可扩大
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //获取屏幕
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;
        //屏幕居中处理
        setLocation((width - FRAME_WIDTH) / 2, (height - FRAME_HEIGHT) / 2);

        //加载窗体的背景图片
        ImageIcon imageIcon = new ImageIcon("src/image/beijing.jpg");
        //创建一个标签并将图片添加进去
        JLabel frameBg = new JLabel(imageIcon);
        //设置图片的位置和大小
        frameBg.setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
        this.add(frameBg);

        // 接收框
        acceptPane = new JTextPane();
        acceptPane.setOpaque(false);//设置透明
        acceptPane.setFont(new Font("宋体", 0, 16));

        // 设置接收框滚动条
        JScrollPane scoPaneOne = new JScrollPane(acceptPane);
        scoPaneOne.setBounds(15, 20, 500, 332);
        //设置背景透明
        scoPaneOne.setOpaque(false);
        scoPaneOne.getViewport().setOpaque(false);
        frameBg.add(scoPaneOne);


        //当前在线用户列表
        lstUser = new JList();
        lstUser.setFont(new Font("宋体", 0, 14));
        lstUser.setVisibleRowCount(17);
        lstUser.setFixedCellWidth(180);
        lstUser.setFixedCellHeight(18);

        //声明菜单
        JPopupMenu popupMenu = new JPopupMenu();

        //私聊按钮（菜单项）
        JMenuItem privateChat = new JMenuItem("私聊");
        privateChat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object receiverObj = lstUser.getSelectedValue();
                if (receiverObj != null) {
                    String receiver = new String((String) receiverObj);
                    receiverBox.removeAllItems();
                    receiverBox.addItem("ALL");
                    receiverBox.addItem(receiver);
                    receiverBox.setSelectedItem(receiver);
                }
            }
        });
        popupMenu.add(privateChat);

        //黑名单按钮（菜单项）
        JMenuItem blackList = new JMenuItem("黑名单");
        blackList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        popupMenu.add(blackList);

        // 添加点击时间，需要确认是右键点击
        lstUser.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                // 监听是鼠标左键还是右键
                if (e.isMetaDown()) {
                    if (lstUser.getSelectedIndex() >= 0) {
                        //弹出菜单,JavaScript JS
                        popupMenu.show(lstUser, e.getX(), e.getY());
                    }
                }
            }
        });

        JScrollPane spUser = new JScrollPane(lstUser);
        spUser.setFont(new Font("宋体", 0, 14));
        spUser.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        spUser.setBounds(530, 17, 200, 507);
        frameBg.add(spUser);


        // 输入框
        JTextPane sendPane = new JTextPane();
        sendPane.setOpaque(false);
        sendPane.setFont(new Font("宋体", 0, 16));

        JScrollPane scoPane = new JScrollPane(sendPane);// 设置滚动条
        scoPane.setBounds(15, 400, 500, 122);
        scoPane.setOpaque(false);
        scoPane.getViewport().setOpaque(false);
        frameBg.add(scoPane);

        // 添加表情选择
        JLabel lblface = new JLabel(new ImageIcon("src/image/face.png"));
        lblface.setBounds(14, 363, 25, 25);
        frameBg.add(lblface);

        // 添加抖动效果
        JLabel lbldoudong = new JLabel(new ImageIcon("src/image/doudong.png"));
        lbldoudong.setBounds(43, 363, 25, 25);
        lbldoudong.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                User user = new User();
                user.setSender(sender);
                String receiver = "ALL";
                user.setStatus(ChatStatus.DD);

                Object receiverObj = receiverBox.getSelectedItem();
                if (receiverObj != null) {
                    receiver = new String((String) receiverObj);
                }
                user.setReceiver(receiver);
                user.setContent("发送了一个窗口抖动！");
                IOUtil.writeMessage(socket, user);
            }
        });
        frameBg.add(lbldoudong);

        // 设置字体选择
        JLabel lblfontChoose = new JLabel(new ImageIcon("src/image/ziti.png"));
        lblfontChoose.setBounds(44, 363, 80, 25);
        frameBg.add(lblfontChoose);

        //字体下拉选项
        JComboBox fontFamilyCmb = new JComboBox();
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        String[] str = graphicsEnvironment.getAvailableFontFamilyNames();
        for (String string : str) {
            fontFamilyCmb.addItem(string);
        }
        fontFamilyCmb.setSelectedItem("楷体");
        fontFamilyCmb.setBounds(104, 363, 150, 25);
        frameBg.add(fontFamilyCmb);

        // 聊天对象
        JLabel receiverLabel = new JLabel("聊天对象");
        receiverLabel.setBounds(304, 363, 80, 25);
        frameBg.add(receiverLabel);

        //下拉选择框
        receiverBox = new JComboBox();
        receiverBox.setSelectedItem("ALL");
        receiverBox.addItem("ALL");
        receiverBox.setBounds(374, 363, 150, 25);
        frameBg.add(receiverBox);

        /*
         * 发送按钮
         */
        JButton send = new JButton("发 送");
        send.setBounds(15, 533, 125, 25);
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                User user = new User();
                user.setSender(sender);
                String receiver = "ALL";
                Object receiverObj = receiverBox.getSelectedItem();
                if (receiverObj != null) {
                    receiver = new String((String) receiverObj);

                }
                user.setReceiver(receiver);
                user.setContent(sendPane.getText());
                user.setStatus(ChatStatus.CHAT);
                IOUtil.writeMessage(socket, user);
                sendPane.setText("");    // 发送完之后把消息发送框的内容置为空
            }
        });
        frameBg.add(send);

        // 客户端关闭窗口退出
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println(username + " 窗口关闭");
                User user = new User();
                user.setUsername(username);
                user.setNotice(username + " 已离开聊天室");
                user.setStatus(ChatStatus.QUIT);
                IOUtil.writeMessage(socket, user);
            }
        });

        setVisible(true);
    }

}
