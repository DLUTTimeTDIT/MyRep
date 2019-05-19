package netty.utils;

import com.esotericsoftware.kryo.Kryo;

/**
 * threadLocal kryo
 */
public class KryoUtils {

    public static final ThreadLocal<Kryo> kryos = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        kryo.setReferences(false);
        return kryo;
    });

    public static Kryo getKryo() {
        return kryos.get();
    }
}
