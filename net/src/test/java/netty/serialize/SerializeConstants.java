package netty.serialize;

import java.util.ArrayList;
import java.util.List;

public class SerializeConstants {

    public static final List<Integer> SERIALIZE_OBJECT;

    public static final Integer PRE_RUNTIME_EACH_THREAD = 10000;

    public static final Integer TEST_TIME = 10000;

    static {
        SERIALIZE_OBJECT = new ArrayList<>();
        for(int i = 0; i < 1000; i++){
            SERIALIZE_OBJECT.add(i);
        }
    }
}
