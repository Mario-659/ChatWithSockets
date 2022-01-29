package ChatWithSockets.server;

import java.io.IOException;

public class RunServer {
    public static void main(String[] args) throws IOException {
        Server server;
        if(args.length==2)
            server = new Server(args[0], Integer.parseInt(args[1]));
        else
            server = new Server("localhost", 55);

        server.run();
    }
}