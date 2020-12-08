package anla.netty.api.nio;

import io.netty.util.Recycler;
import lombok.Data;

/**
 *
 * https://www.jianshu.com/p/854b855bd198
 * @author luoan
 * @version 1.0
 * @date 2020/12/7 19:17
 **/
public class RecyclerTest {

    private static final Recycler<User> userRecycler = new Recycler<User>() {
        @Override
        protected User newObject(Handle<User> handle) {
            return new User(handle);
        }
    };

    @Data
    static final class User {
        private String name;
        private Recycler.Handle<User> handle;

        public User(Recycler.Handle<User> handle) {
            this.handle = handle;
        }

        public void recycle() {
            handle.recycle(this);
        }
    }


    public static void main(String[] args) throws InterruptedException {
        testGetAndRecycleAtSameThread();
        // testGetAndRecycleAtDifferentThread();
    }

    public static void testGetAndRecycleAtSameThread() {
        // 1、从回收池获取对象
        User user1 = userRecycler.get();
        // 2、设置对象并使用
        user1.setName("hello,java");
        System.out.println(user1);
        // 3、对象恢复出厂设置
        user1.setName(null);
        // 4、回收对象到对象池
        user1.recycle();
        // 5、从回收池获取对象
        User user2 = userRecycler.get();
        System.out.println("user1"+user1);
        System.out.println("user2"+user2);
    }


    public static void testGetAndRecycleAtDifferentThread() throws InterruptedException {
        // 1、从回收池获取对象
        User user1 = userRecycler.get();
        // 2、设置对象并使用
        user1.setName("hello,java");

        Thread thread = new Thread(()->{
            System.out.println(user1);
            // 3、对象恢复出厂设置
            user1.setName(null);
            // 4、回收对象到对象池
            user1.recycle();
        });

        thread.start();
        thread.join();

        // 5、从回收池获取对象
        User user2 = userRecycler.get();
        System.out.println("user1"+user1);
        System.out.println("user2"+user2);
    }
}
