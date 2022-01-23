package ChatWithSockets.server;

import ChatWithSockets.shared.Client;
import ChatWithSockets.shared.Request.Request;
import ChatWithSockets.shared.Request.RequestType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.rmi.RemoteException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ServerTest {
    private Server server;
    private Client client1;
    private Client client2;
    private ArgumentCaptor<Request> captor;

    @Test
    public void shouldReturnChannels() throws RemoteException {
        sendRequest(RequestType.GETCHANNELS, client1);
        Request response = getResponse(client1);
        assertEquals(RequestType.REQUESTSUCCEEDED, response.getType());
        assertFalse(response.getPayload().isEmpty());
    }

    @Test
    public void shouldSendMessagesToOthers() throws RemoteException {
        sendRequest(RequestType.CREATECHANNEL,"ChannelName,Username1", client1);
        Request response = getResponse(client1);
        assertEquals(RequestType.REQUESTSUCCEEDED, response.getType());

        sendRequest(RequestType.JOINCHANNEL,"ChannelName,Username2", client2);
        //request to client1 sent about username2 joining the channel
        verify(client1, times(2)).sendRequest(any(), any());
        response = getResponse(client2);
        assertEquals(RequestType.REQUESTSUCCEEDED, response.getType());

        sendRequest(RequestType.SENDMESSAGE, "test message", client1);

        verify(client1, times(2)).sendRequest(any(), any());
        verify(client2, times(2)).sendRequest(captor.capture(), any());

        Request secondRequest = captor.getValue();
        assertEquals(RequestType.SENDMESSAGE, secondRequest.getType());
        assertEquals("Username1", secondRequest.getSender());
        assertEquals("test message", secondRequest.getPayload());
    }

    private void sendRequest(RequestType type, String payload, Client client) throws RemoteException {
        server.sendRequest(new Request(type, payload), client);
    }

    private void sendRequest(RequestType type, Client client) throws RemoteException {
        sendRequest(type, "", client);
    }

    private Request getResponse(Client client) throws RemoteException {
        verify(client).sendRequest(captor.capture(), any());
        return captor.getValue();
    }

    @BeforeEach
    private void prepareData() throws RemoteException {
        server = new Server(0);
        client1 = mock(Client.class);
        client2 = mock(Client.class);
        captor = ArgumentCaptor.forClass(Request.class);
    }
}
