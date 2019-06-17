package netty.client;

import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * refer to Dubbo URL
 */
public final class RemotingURL {
    private static final String CONNECTIONINDEX = "&b=";
    /**
     * 把解析过了的URL缓存起来，避免生成太多ConnectionUrl对象. 使用软引用，防止内存泄露.
     * <p>
     * 不保证值集合中的对象是唯一的。如果客户把URL中的参数调换位置前后， 两次调用get()方法，那么会向Map中插入两个相同的ConnectionUrl.
     */
    private final static ConcurrentHashMap<String, SoftReference<RemotingURL>> parsedUrls = new ConcurrentHashMap<String, SoftReference<RemotingURL>>();
    // private static final Cache<String, RemotingURL> parsedUrls = CacheBuilder.newBuilder()//
    // .maximumSize(2 << 18)// 最大是65535
    // .expireAfterAccess(1, TimeUnit.HOURS)// 一小时后未访问，清除掉
    // .build();

    public static final String SERIALIZE_TYPE = "st";

    public static final String TIMEOUT = "to";


    private final String protocol;
    private final String host;
    private final int port;
    private final String path;
    private final Map<String, String> parameters;
    private final String url;
    private final int timeout;
    private final byte serializeType;

    public RemotingURL(String host, int port, Map<String, String> params){
        this(null, null, host, port, null, params);
    }

    public RemotingURL(String url, String protocol, String host, int port, String path, Map<String, String> parameters) {
        this.url = url;
        this.protocol = protocol;
        this.host = host;
        this.port = (port < 0 ? 0 : port);
        this.path = path;
        // trim the beginning "/"
        while (path != null && path.startsWith("/")) {
            path = path.substring(1);
        }
        if (parameters == null) {
            parameters = new HashMap<String, String>();
        } else {
            parameters = new HashMap<String, String>(parameters);
        }
        this.parameters = Collections.unmodifiableMap(parameters);

        this.serializeType = (byte) this.getParameter(SERIALIZE_TYPE, 1);
        this.timeout = this.getParameter(TIMEOUT, 3000);
    }

    public static RemotingURL valueOf(String targetURL, int connectionIndex) {
        return valueOf(targetURL + CONNECTIONINDEX + connectionIndex);
    }

    /**
     * Parse url string
     *
     * @param urlParameter
     *            URL string
     * @return URL instance
     * @see RemotingURL
     */
    public static RemotingURL valueOf(final String urlParameter) {
        if (urlParameter == null) {
            throw new IllegalArgumentException("url == null");
        }

        SoftReference<RemotingURL> remotingURL = parsedUrls.get(urlParameter);
        RemotingURL url = null;
        if (remotingURL != null) {
            if ((url = remotingURL.get()) != null) {
                return url;
            } else {
                parsedUrls.remove(urlParameter);
            }
        }
        url = parseUrl(urlParameter);
        parsedUrls.put(urlParameter, new SoftReference<RemotingURL>(url));

        // if (parsedUrls.size() > 33333) {
        // cleanItNow();
        // }

        return url;
    }

    // private static void cleanItNow() {
    // for (String remotingUrl : parsedUrls.keySet()) {
    // if(parsedUrls.get(remotingUrl).get() == null){
    // parsedUrls.remove(remotingUrl);
    // }
    // }
    // }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getPort(int defaultPort) {
        return port <= 0 ? defaultPort : port;
    }

    public String getAddress() {
        return port <= 0 ? host : host + ":" + port;
    }

    public String getPath() {
        return path;
    }

    public String getAbsolutePath() {
        if (path != null && !path.startsWith("/")) {
            return "/" + path;
        }
        return path;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getParameterAndDecoded(String key) {
        return getParameterAndDecoded(key, null);
    }

    public String getParameterAndDecoded(String key, String defaultValue) {
        return decode(getParameter(key, defaultValue));
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public String getParameter(String key, String defaultValue) {
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        return value;
    }

    public String[] getParameter(String key, String[] defaultValue) {
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        return value.split(":");
    }

    public boolean hasParameter(String key) {
        String value = getParameter(key);
        return value != null && value.length() > 0;
    }

    public String getMethodParameterAndDecoded(String method, String key) {
        return RemotingURL.decode(getMethodParameter(method, key));
    }

    public String getMethodParameterAndDecoded(String method, String key, String defaultValue) {
        return RemotingURL.decode(getMethodParameter(method, key, defaultValue));
    }

    public String getMethodParameter(String method, String key) {
        String value = parameters.get(method + "." + key);
        if (value == null || value.length() == 0) {
            return getParameter(key);
        }
        return value;
    }

    public String getMethodParameter(String method, String key, String defaultValue) {
        String value = getMethodParameter(method, key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        return value;
    }

    public char getMethodParameter(String method, String key, char defaultValue) {
        String value = getMethodParameter(method, key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        return value.charAt(0);
    }

    public boolean getMethodParameter(String method, String key, boolean defaultValue) {
        String value = getMethodParameter(method, key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    public boolean hasMethodParameter(String method, String key) {
        if (method == null) {
            String suffix = "." + key;
            for (String fullKey : parameters.keySet()) {
                if (fullKey.endsWith(suffix)) {
                    return true;
                }
            }
            return false;
        }
        if (key == null) {
            String prefix = method + ".";
            for (String fullKey : parameters.keySet()) {
                if (fullKey.startsWith(prefix)) {
                    return true;
                }
            }
            return false;
        }
        String value = getMethodParameter(method, key);
        return value != null && value.length() > 0;
    }

    public RemotingURL addParameterAndEncoded(String key, String value) {
        if (value == null || value.length() == 0) {
            return this;
        }
        return addParameter(key, encode(value));
    }

    public RemotingURL addParameter(String key, boolean value) {
        return addParameter(key, String.valueOf(value));
    }

    public RemotingURL addParameter(String key, char value) {
        return addParameter(key, String.valueOf(value));
    }

    public RemotingURL addParameter(String key, byte value) {
        return addParameter(key, String.valueOf(value));
    }

    public RemotingURL addParameter(String key, int value) {
        return addParameter(key, String.valueOf(value));
    }

    public RemotingURL addParameter(String key, long value) {
        return addParameter(key, String.valueOf(value));
    }

    public RemotingURL addParameter(String key, float value) {
        return addParameter(key, String.valueOf(value));
    }

    public RemotingURL addParameter(String key, double value) {
        return addParameter(key, String.valueOf(value));
    }

    public RemotingURL addParameter(String key, Enum<?> value) {
        if (value == null)
            return this;
        return addParameter(key, String.valueOf(value));
    }

    public RemotingURL addParameter(String key, Number value) {
        if (value == null)
            return this;
        return addParameter(key, String.valueOf(value));
    }

    public RemotingURL addParameter(String key, CharSequence value) {
        if (value == null || value.length() == 0)
            return this;
        return addParameter(key, String.valueOf(value));
    }

    public String getRawParameter(String key) {
        if ("protocol".equals(key))
            return protocol;
        if ("host".equals(key))
            return host;
        if ("port".equals(key))
            return String.valueOf(port);
        if ("path".equals(key))
            return path;
        return getParameter(key);
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>(parameters);
        if (protocol != null)
            map.put("protocol", protocol);
        if (host != null)
            map.put("host", host);
        if (port > 0)
            map.put("port", String.valueOf(port));
        if (path != null)
            map.put("path", path);
        return map;
    }

    public String toString(String... parameters) {
        return buildString(false, true, parameters);
    }

    public String toIdentityString(String... parameters) {
        return buildString(true, false, parameters);
    }

    public String toFullString(String... parameters) {
        return buildString(true, true, parameters);
    }

    public String toParameterString(String... parameters) {
        StringBuilder buf = new StringBuilder();
        buildParameters(buf, false, parameters);
        return buf.toString();
    }

    private void buildParameters(StringBuilder buf, boolean concat, String[] parameters) {
        if (getParameters() != null && getParameters().size() > 0) {
            List<String> includes = (parameters == null || parameters.length == 0 ? null : Arrays.asList(parameters));
            boolean first = true;
            for (Map.Entry<String, String> entry : new TreeMap<String, String>(getParameters()).entrySet()) {
                if (entry.getKey() != null && entry.getKey().length() > 0
                        && (includes == null || includes.contains(entry.getKey()))) {
                    if (first) {
                        if (concat) {
                            buf.append("?");
                        }
                        first = false;
                    } else {
                        buf.append("&");
                    }
                    buf.append(entry.getKey());
                    buf.append("=");
                    buf.append(entry.getValue() == null ? "" : entry.getValue().trim());
                }
            }
        }
    }

    private String buildString(boolean appendUser, boolean appendParameter, String... parameters) {
        return buildString(appendUser, appendParameter, false, false, parameters);
    }

    private String buildString(boolean appendUser, boolean appendParameter, boolean useIP, boolean useService,
                               String... parameters) {
        StringBuilder buf = new StringBuilder();
        if (protocol != null && protocol.length() > 0) {
            buf.append(protocol);
            buf.append("://");
        }
        String host = getHost();
        if (host != null && host.length() > 0) {
            buf.append(host);
            if (port > 0) {
                buf.append(":");
                buf.append(port);
            }
        }
        String path;
        if (useService) {
            path = getServiceKey();
        } else {
            path = getPath();
        }
        if (path != null && path.length() > 0) {
            buf.append("/");
            buf.append(path);
        }
        if (appendParameter) {
            buildParameters(buf, true, parameters);
        }
        return buf.toString();
    }

    public String getServiceKey() {
        String inf = getParameter("interface");
        if (inf == null)
            return null;
        StringBuilder buf = new StringBuilder();
        String group = getParameter("group");
        if (group != null && group.length() > 0) {
            buf.append(group).append("/");
        }
        buf.append(inf);
        String version = getParameter("version");
        if (version != null && version.length() > 0) {
            buf.append(":").append(version);
        }
        return buf.toString();
    }

    public java.net.URL toJavaURL() {
        try {
            return new java.net.URL(toString());
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public InetSocketAddress toInetSocketAddress() {
        return new InetSocketAddress(host, port);
    }

    public String toServiceString() {
        return buildString(true, false, true, true);
    }

    public static String encode(String value) {
        if (value == null || value.length() == 0) {
            return "";
        }
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String decode(String value) {
        if (value == null || value.length() == 0) {
            return "";
        }
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static RemotingURL parseUrl(final String urlParameter) {
        String fullUrl = urlParameter;
        String url = urlParameter;
        String protocol = null;
        String host = null;
        int port = 0;
        String path = null;
        Map<String, String> parameters = new HashMap<String, String>();
        int i = url.indexOf("?"); // seperator between body and parameters
        if (i >= 0) {
            String[] parts = url.substring(i + 1).split("\\&");
            for (String part : parts) {
                part = part.trim();
                if (part.length() > 0) {
                    int j = part.indexOf('=');
                    if (j >= 0) {
                        parameters.put(part.substring(0, j), part.substring(j + 1));
                    } else {
                        parameters.put(part, part);
                    }
                }
            }
            url = url.substring(0, i);
        }
        i = url.indexOf("://");
        if (i >= 0) {
            if (i == 0) {
                throw new IllegalStateException("url missing protocol: \"" + url + "\"");
            }
            protocol = url.substring(0, i);
            url = url.substring(i + 3);
        } else {
            // case: file:/path/to/file.txt
            i = url.indexOf(":/");
            if (i >= 0) {
                if (i == 0)
                    throw new IllegalStateException("url missing protocol: \"" + url + "\"");
                protocol = url.substring(0, i);
                url = url.substring(i + 1);
            }
        }

        i = url.indexOf("/");
        if (i >= 0) {
            path = url.substring(i + 1);
            url = url.substring(0, i);
        }
        i = url.indexOf(":");
        if (i >= 0 && i < url.length() - 1) {
            port = Integer.parseInt(url.substring(i + 1));
            url = url.substring(0, i);
        }
        if (url.length() > 0)
            host = url;

        return new RemotingURL(fullUrl, protocol, host, port, path, parameters);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RemotingURL other = (RemotingURL) obj;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }

    public int getParameter(String parameterName, int defaultValue) {
        String stringValue = this.getParameter(parameterName);
        if (StringUtils.isBlank(stringValue)) {
            return defaultValue;
        } else {
            return Integer.valueOf(stringValue);
        }
    }

    public String toString() {
        return url;
    }

    public int getTimeout() {
        return timeout;
    }

    public byte getSerializeType() {
        return serializeType;
    }

}
