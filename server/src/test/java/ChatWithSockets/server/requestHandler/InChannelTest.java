package ChatWithSockets.server.requestHandler;

import ChatWithSockets.server.ClientSession;
import ChatWithSockets.server.channels.Channel;
import ChatWithSockets.server.channels.ChannelManager;
import ChatWithSockets.shared.Request.Request;
import ChatWithSockets.shared.Request.RequestType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InChannelTest {
    private ClientSession session;
    private Channel channel;
    private InChannel state;
    private RequestHandler handler;
    private ArgumentCaptor<Request> captor;

    @Test
    public void shouldSendChannelMessage(){
        state.handleRequest(new Request(
                RequestType.SENDMESSAGE, "Test"));
        verify(channel).sendChatMessage("Test", session);
    }

    @Test
    public void shouldLeaveChannel(){
        state.handleRequest(new Request(
                RequestType.LEAVECHANNEL, ""));
        verify(handler).changeState(any(WithoutChannel.class));
        Request sent = getSentRequest();
        assertEquals(RequestType.REQUESTSUCCEEDED, sent.getType());
    }

    @ParameterizedTest
    @EnumSource(
            value = RequestType.class,
            names = {"SENDMESSAGE", "LEAVECHANNEL"},
            mode = EnumSource.Mode.EXCLUDE
    )
    public void shouldHandleInvalidRequest(RequestType type){
        state.handleRequest(new Request(
                type, ""
        ));
        Request sent = getSentRequest();
        assertEquals(RequestType.ERROR, sent.getType());
    }

    private Request getSentRequest(){
        verify(session).sendRequest(captor.capture());
        return captor.getValue();
    }

    @BeforeEach
    private void prepareData(){
        session = mock(ClientSession.class);
        handler = mock(RequestHandler.class);
        channel = mock(Channel.class);
        when(session.getChannel()).thenReturn(channel);
        when(session.getReqHandler()).thenReturn(handler);
        state = new InChannel(session, mock(ChannelManager.class));
        captor = ArgumentCaptor.forClass(Request.class);
    }
}
