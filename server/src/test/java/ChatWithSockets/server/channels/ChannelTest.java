package ChatWithSockets.server.channels;

import ChatWithSockets.server.ClientSession;
import ChatWithSockets.shared.Request.Request;

import java.io.IOException;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ChannelTest {
    private Channel channel;
    private ClientSession[] sessions;

    @Test
    public void shouldAddUserToEmptyChannel() throws IOException {
        addUsersToChannel(1);
        verify(sessions[0]).setChannel(channel);
        verify(sessions[0], never()).sendRequest(any());
        assertEquals(1, channel.getUsers().size());
    }

    @Test
    public void shouldAddUserToNotEmptyChannel() throws IOException{
        addUsersToChannel(3);
        assertEquals(3, channel.getUsers().size());
    }

    @Test
    public void shouldNotAddIfClientInChannel() throws IOException {
        addUsersToChannel(3);
        assertThrows(RuntimeException.class, () ->
                channel.addUser(sessions[2], "username4"));
        assertEquals(3, channel.getUsers().size());
    }

    @Test
    public void shouldThrowIfUsernameTaken()throws IOException{
        addUsersToChannel(2);
        assertThrows(IOException.class, () ->
                channel.addUser(sessions[2], "username1"));
        assertEquals(2, channel.getUsers().size());
    }

    @Test
    public void shouldSendRequestToOthersWhenAdded() throws IOException{
        addUsersToChannel(3);
        verify(sessions[0], times(2)).sendRequest(any());
        verify(sessions[1], times(1)).sendRequest(any());
    }

    @Test
    public void shouldNotSendRequestToAdded() throws IOException{
        channel.addUser(sessions[0], "username1");
        verify(sessions[0], never()).sendRequest(any());

        channel.addUser(sessions[1], "username2");
        verify(sessions[1], never()).sendRequest(any());
    }

    @Test
    public void shouldSendToAll() throws IOException {
        addUsersToChannel(3);
        Request mockRequest = mock(Request.class);
        channel.sendToAll(mockRequest);
        verify(sessions[0], times(1)).sendRequest(mockRequest);
        verify(sessions[1], times(1)).sendRequest(mockRequest);
        verify(sessions[2], times(1)).sendRequest(mockRequest);
    }

    @Test
    public void shouldSendToAllExceptOne() throws IOException {
        addUsersToChannel(3);
        Request mockRequest = mock(Request.class);
        channel.sendToAll(mockRequest, sessions[1]);
        verify(sessions[0], times(1)).sendRequest(mockRequest);
        verify(sessions[1], never()).sendRequest(mockRequest);
        verify(sessions[2], times(1)).sendRequest(mockRequest);
    }

    @Test
    public void shouldDeleteUser() throws IOException{
        addUsersToChannel(3);
        channel.deleteUser(sessions[1]);
        verify(sessions[1]).setChannel(null);
        HashMap<ClientSession, String> users = channel.getUsers();
        assertEquals(2, users.size());
        assertFalse(users.containsKey(sessions[1]));
    }

    @BeforeEach
    private void prepareData(){
        channel = new Channel("TestChannel");
        sessions = new ClientSession[3];
        for(int i=0; i<sessions.length; i++){
            sessions[i] = mock(ClientSession.class);
        }
    }

    private void addUsersToChannel(int numOfUsers) throws IOException{
        for(int i=0; i<numOfUsers; i++){
            channel.addUser(sessions[i], "username" + (i+1));
        }
    }
}
