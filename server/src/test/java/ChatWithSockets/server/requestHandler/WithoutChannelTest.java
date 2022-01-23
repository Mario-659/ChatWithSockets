package ChatWithSockets.server.requestHandler;

import ChatWithSockets.server.ClientSession;
import ChatWithSockets.server.channels.ChannelManager;
import ChatWithSockets.shared.Request.Request;
import ChatWithSockets.shared.Request.RequestType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WithoutChannelTest {
    private ClientSession session;
    private ChannelManager manager;
    private WithoutChannel state;
    private RequestHandler handler;
    private ArgumentCaptor<Request> captor;

    @Test
    public void shouldSendMsgWhenNoChannelsAvailable(){
        when(manager.getAvailableChannels()).thenReturn(new String[]{});
        state.handleRequest(new Request(
                RequestType.GETCHANNELS, ""));

        verify(manager).getAvailableChannels();
        Request sent = getSentRequest();
        assertEquals(RequestType.REQUESTSUCCEEDED, sent.getType());
        assertNotEquals("", captor.getValue().getPayload());
    }

    @Test
    public void shouldSendChannels(){
        when(manager.getAvailableChannels()).thenReturn(
                new String[]{"Channel 1", "Channel 2"});
        state.handleRequest(new Request(
                RequestType.GETCHANNELS, ""));

        verify(manager).getAvailableChannels();
        Request sent = getSentRequest();
        assertEquals(RequestType.REQUESTSUCCEEDED, sent.getType());
        assertTrue(captor.getValue().getPayload().contains("Channel 1"));
        assertTrue(captor.getValue().getPayload().contains("Channel 2"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "username", "one,two,three"})
    public void shouldHandleInvalidFormat(String payload){
        state.handleRequest(new Request(
                RequestType.JOINCHANNEL, payload
        ));
        Request sent = getSentRequest();
        assertEquals(RequestType.ERROR, sent.getType());
    }

    @Test
    public void shouldJoinExistingChannel() throws IOException {
        state.handleRequest(new Request(
                RequestType.JOINCHANNEL, "Channel 1,John"));
        verify(manager).joinChannel("Channel 1", session, "John");
        verify(handler).changeState(any());
        Request sent = getSentRequest();
        assertEquals(RequestType.REQUESTSUCCEEDED, sent.getType());
    }

    @Test
    public void shouldHandleExceptionWhenJoining() throws IOException {
        doThrow(new IOException("Test exception"))
                .when(manager)
                .joinChannel("Channel 1", session, "John");
        state.handleRequest(new Request(
                RequestType.JOINCHANNEL, "Channel 1,John"));
        Request sent = getSentRequest();
        assertEquals(RequestType.ERROR, sent.getType());
        assertEquals("Test exception", sent.getPayload());
    }

    @Test
    public void shouldCreateChannel() throws IOException {
        state.handleRequest(new Request(
                RequestType.CREATECHANNEL, "Channel 1,John"
        ));
        verify(manager).createAndJoinChannel("Channel 1", session, "John");
        verify(handler).changeState(any());
        Request sent = getSentRequest();
        assertEquals(RequestType.REQUESTSUCCEEDED, sent.getType());
    }

    @ParameterizedTest
    @EnumSource(
            value = RequestType.class,
            names = {"CREATECHANNEL", "JOINCHANNEL", "GETCHANNELS"},
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
        when(session.getReqHandler()).thenReturn(handler);
        manager = mock(ChannelManager.class);
        state = new WithoutChannel(session, manager);
        captor = ArgumentCaptor.forClass(Request.class);
    }
}
