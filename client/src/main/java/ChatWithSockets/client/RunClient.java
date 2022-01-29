package ChatWithSockets.client;

import java.io.IOException;

public class RunClient {
    public static void main(String[] args) throws IOException {
        Client client;
        if(args.length == 2)
            client = new Client(args[0], Integer.parseInt(args[1]));
        else
            client = new Client("localhost", 55);

        client.startClient();
    }
}
