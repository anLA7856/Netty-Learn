package com.anla.netty.aio;

/**
 * @user anLA7856
 * @time 19-1-11 下午11:35
 * @description
 */
public class TimeClient {
    public static void main(String[] args){
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        new Thread(new AsyncTimeClientHandler("127.0.0.1", port), "AIO-AsyncTimeClientHandler-001").start();
    }
}
