package ChatWithSockets.server.requestHandler;

import ChatWithSockets.server.channels.ChannelManager;
import ChatWithSockets.server.ClientSession;
import ChatWithSockets.shared.Request.Request;
import ChatWithSockets.shared.Request.RequestType;

import java.io.IOException;

public abstract class State {
    protected final ClientSession session;
    protected final ChannelManager channelManager;

    abstract void handleRequest(Request request);

    protected State(ClientSession session, ChannelManager channelManager){
        this.session = session;
        this.channelManager = channelManager;
    }

    protected void sendRequest(RequestType type, String payload){
        session.sendRequest(new Request(type, payload));
    }

    protected void handleInvalReq(Request request){
        sendRequest(RequestType.ERROR,
                "Invalid request. Request=" + request);
    }

    protected void handleInvalFormat(Request request){
        sendRequest(RequestType.ERROR,
                "Invalid data format. Request=" + request);
    }

    protected void sendIOException(IOException e){
        sendRequest(RequestType.ERROR,
                e.getMessage());
    }

}