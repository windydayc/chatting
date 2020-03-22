package cn.ncu.chat.client;

public class DouDong extends Thread {

    private ChatFrame chatFrame;

    public DouDong(ChatFrame chatFrame) {
        this.chatFrame = chatFrame;
    }
    @Override
    public void run() {
        int x = chatFrame.getX();
        int y = chatFrame.getY();
        int t = 10;
        try {
            for(int i = 0; i < 3; ++i) {
                chatFrame.setLocation(x-t, y);
                Thread.sleep(88);
                chatFrame.setLocation(x, y+t);
                Thread.sleep(88);
                chatFrame.setLocation(x+t, y);
                Thread.sleep(88);
                chatFrame.setLocation(x, y-t);
                Thread.sleep(88);
                chatFrame.setLocation(x, y);
                Thread.sleep(88);
            }
            chatFrame.setLocation(x, y);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }
}
