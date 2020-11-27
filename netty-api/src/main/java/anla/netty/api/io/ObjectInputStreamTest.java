package anla.netty.api.io;

import java.io.*;

import static anla.netty.api.io.FileReaderTest.FILE_NAME;

/**
 * @author luoan
 * @version 1.0
 * @date 2020/11/12 23:26
 **/
public class ObjectInputStreamTest {

    private static void writeObject() throws FileNotFoundException, IOException {
        Dog dog = new Dog("tom",18);
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(FILE_NAME)));
        oos.writeObject(dog);
        oos.close();
    }


    private static void readObject() throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME));
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
