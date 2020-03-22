package cn.ncu.chat.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class IOUtil {

    /**
     * 从socket管道读取消息
     * @param socket
     * @return message
     */
    public static Object readMessage(Socket socket) {
        Object message = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            message = ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return message;
    }

    /**
     * 从socket管道写出消息
     * @param socket
     * @param message
     * @return
     */
    public static void writeMessage(Socket socket, Object message) {
        Object obj = null;
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
