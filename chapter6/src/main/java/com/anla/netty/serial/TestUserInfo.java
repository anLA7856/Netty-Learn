package com.anla.netty.serial;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @user anLA7856
 * @time 19-1-17 下午10:43
 * @description
 */
public class TestUserInfo {
    public static void main(String[] args) throws IOException{
        UserInfo info = new UserInfo();
        info.buildUserID(100).buildUserName("Welcome to Netty");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(info);
        oos.flush();
        oos.close();
        byte[] b = bos.toByteArray();
        System.out.println("The jdk serializable length is : " + b.length);
        bos.close();
        System.out.println("-----------------------------------------------");
        System.out.println("The byte array serializable length is : " + info.codeC().length);
    }
}
