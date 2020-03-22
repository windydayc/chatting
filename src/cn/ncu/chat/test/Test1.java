package cn.ncu.chat.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Test1 {
    public static void main(String[] args) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream("src/user.txt"));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("src/user1.txt"));
        FileInputStream fis = new FileInputStream("src/user.txt");
        int b = bis.read();
        bos.write(b);
        System.out.println(b);
        bis.close();
        bos.close();    // 不关流就不会写出去
    }
}
