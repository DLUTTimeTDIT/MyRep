package route;

/**
 * 路由类型枚举
 */
public enum RouteTypeEnum {

    /**
     * 随机
     */
    RANDOM((byte)0),

    /**
     * 一致性hash
     */
    CONSISTENT_HASH((byte)1);

    private byte routeTyp;

    public byte getRouteTyp() {
        return routeTyp;
    }

    RouteTypeEnum(byte routeTyp) {
        this.routeTyp = routeTyp;
    }}
