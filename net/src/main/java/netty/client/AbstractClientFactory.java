package netty.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public abstract class AbstractClientFactory implements ClientFactory {

    private final Cache<RemotingURL, Client> cache = CacheBuilder.newBuilder()
            .maximumSize(2<<17)
            .expireAfterAccess(27, TimeUnit.MINUTES)
            .removalListener(removalNotification -> {
                if(((Client)(removalNotification.getValue())).isConnected()){
                    ((Client)(removalNotification.getValue())).close("removed from cache");
                }
            }).build();

    @Override
    public Client get(RemotingURL remotingURL) throws ExecutionException {
        Client client = cache.get(remotingURL, () -> {
            Client client1 = createClient(remotingURL);
            if(client1 != null){
                client1.startHeartBeat();
            }
            return client1;
        });
        if(client == null || !client.isConnected()){
            cache.invalidate(remotingURL);
        }
        return client;
    }

    @Override
    public List<Client> retrieveAllClient() throws Exception {
        List<Client> result = new ArrayList<>((int) cache.size());
        result.addAll(cache.asMap().values());
        return result;
    }

    @Override
    public void remove(Client client) {
        if(client != null){
            cache.invalidate(client.getUrl());
        }
    }

    protected abstract Client createClient(RemotingURL remotingURL) throws Exception;

}
