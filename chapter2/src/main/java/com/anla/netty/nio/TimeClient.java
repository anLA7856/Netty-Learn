package com.anla.netty.nio;

/**
 * @user anLA7856
 * @time 19-1-9 下午11:27
 * @description
 */
public class TimeClient {
    public static void main(String[] args){
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            }catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        new Thread(new TimeClientHandler("127.0.0.1", port), "TimeClient-anla").start();
    }
}
