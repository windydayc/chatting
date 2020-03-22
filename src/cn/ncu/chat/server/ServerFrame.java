package cn.ncu.chat.server;

import cn.ncu.chat.server.ui.CustomTabbedPaneUI;
import cn.ncu.chat.server.ui.OnlineUserPanel;
import cn.ncu.chat.server.ui.ServerInfoPanel;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

/**
 * 服务器端主界面
 */
public class ServerFrame extends JFrame {

	private static final long serialVersionUID = -8152378291726535742L;

	/**
	 * 服务器窗体宽度
	 */
	public static final Integer FRAME_WIDTH = 550;
	
	/**
	 * 服务器窗体高度
	 */
	public static final Integer FRAME_HEIGHT = 500;

	public OnlineUserPanel onlineUserPanel;
	public ServerInfoPanel serverInfoPanel;

	public ServerFrame() {

		this.setTitle("网络聊天室服务器");
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		//窗体不可扩大
		setResizable(false);
		//获取屏幕
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = screenSize.width;
		int height = screenSize.height;
		//屏幕居中处理
		setLocation((width-FRAME_WIDTH)/2, (height-FRAME_HEIGHT)/2);
		
		//设置窗体关闭，程序退出
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//选项卡
		JTabbedPane tpServer = new JTabbedPane(JTabbedPane.TOP);
		tpServer.setBackground(Color.WHITE);
		tpServer.setFont(new Font("宋体", 0, 18));
		tpServer.setUI(new CustomTabbedPaneUI());
		
		serverInfoPanel = new ServerInfoPanel();
		tpServer.add("服务器信息" , serverInfoPanel.getServerInfoPanel());
		onlineUserPanel = new OnlineUserPanel();
		tpServer.add("用户在线列表" , onlineUserPanel.getUserPanel());
		add(tpServer);
		setVisible(true);
	}

}
