package foo;

import redis.clients.jedis.Jedis;

public class SetClient {
    public static void main(String[] args) throws Exception {
//        Jedis jedis = new Jedis("172.17.171.43", 6379);
        Jedis jedis = new Jedis("127.0.0.1", 1234);
        String res = jedis.set("good", "haha");
        System.out.println("res: " + res);
    }
}
