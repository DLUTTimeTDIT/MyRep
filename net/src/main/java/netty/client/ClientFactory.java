package netty.client;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface ClientFactory {

    Client get(RemotingURL remotingURL) throws ExecutionException;

    List<Client> retrieveAllClient() throws Exception;

    void remove(Client client);
}
