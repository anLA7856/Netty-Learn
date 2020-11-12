package anla.netty.api;

import javax.xml.crypto.Data;
import java.io.*;

/**
 * @author luoan
 * @version 1.0
 * @date 2020/11/12 23:26
 **/
public class ObjectInputStreamTest {

    private static void writeObject() throws FileNotFoundException, IOException {
        Dog dog = new Dog("tom",18);
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("/home/anla7856/workspace/io.examples/data.xml")));
        oos.writeObject(dog);
        oos.close();
    }


    private static void readObject() throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("/home/anla7856/workspace/io.examples/data.xml"));
        Dog dog = (Dog) ois.readObject();
        System.out.println(dog);
    }


}


class Dog{
    private String name;
    private int age;



    public Dog(String name, int age){
        this.age = age;
        this.name = name;
    }
}
