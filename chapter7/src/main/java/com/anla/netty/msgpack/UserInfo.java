package com.anla.netty.msgpack;

import org.msgpack.annotation.Message;

/**
 * @user anLA7856
 * @time 19-1-17 下午11:36
 * @description
 */
@Message
public class UserInfo {
    private int age;
    private String name;

    @Override
    public String toString() {
        return "UserInfo{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}';
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
