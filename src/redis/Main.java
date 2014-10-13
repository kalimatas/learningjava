package redis;

import redis.clients.jedis.Jedis;

import java.util.BitSet;

public class Main {
    public static void main(String... args) {
        Jedis jedis = new Jedis("localhost");

        String key = "dau";

        jedis.setbit(key, 6, true);
        jedis.setbit(key, 2, true);
        jedis.setbit(key, 24, false);

        BitSet dau = BitSet.valueOf(jedis.get(key.getBytes()));
        System.out.println(Long.toString(dau.toLongArray()[0], 2));
        System.out.println(dau.cardinality());
        System.out.println(dau.length());
    }
}
