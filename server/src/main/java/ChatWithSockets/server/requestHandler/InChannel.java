package ChatWithSockets.server.requestHandler;

import ChatWithSockets.server.channels.ChannelManager;
import ChatWithSockets.server.ClientSession;
import ChatWithSockets.shared.Request.Request;
import ChatWithSockets.shared.Request.RequestType;


public class InChannel extends State {

    protected InChannel(ClientSession session, ChannelManager manager){
        super(session, manager);
    }

    @Override
    void handleRequest(Request request) {
        switch (request.getType()) {
            case SENDMESSAGE:
                handleSendMessage(request);
                break;
            case LEAVECHANNEL:
                handleLeaveChannel(request);
                break;
            default:
                handleInvalReq(request);
                break;
        }
    }

    private void handleLeaveChannel(Request request) {
        channelManager.leaveChannel(session);
        changeToWithoutChannel();
        sendRequest(RequestType.REQUESTSUCCEEDED,
                "Left channel");
    }

    private void handleSendMessage(Request request) {
        session.getChannel().sendChatMessage(
                request.getPayload(), session);
    }

    private void changeToWithoutChannel(){
        session.getReqHandler().changeState(
                new WithoutChannel(session, channelManager));
    }
}
