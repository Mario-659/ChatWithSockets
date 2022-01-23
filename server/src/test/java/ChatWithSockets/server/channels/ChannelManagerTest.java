package ChatWithSockets.server.channels;

import ChatWithSockets.server.ClientSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ChannelManagerTest {
    private ChannelManager channelManager;
    private ClientSession[] sessions;
    private String[] usernames;
    private String[] channelNames;


    @Test
    public void shouldCreateChannel() throws IOException {
        channelManager.createAndJoinChannel(channelNames[0], sessions[0], usernames[0]);
        checkNumOfChannels(1);
        verify(sessions[0]).setChannel(notNull());
    }

    @Test
    public void shouldJoinExistingChannel() throws IOException{
        channelManager.createAndJoinChannel(channelNames[0], sessions[0], usernames[0]);
        channelManager.joinChannel(channelNames[0], sessions[1], usernames[1]);
        checkNumOfChannels(1);
        verify(sessions[0], times(1)).setChannel(notNull());
        verify(sessions[1]).setChannel(notNull());
    }

    @Test
    public void shouldThrowIfClientJoinsWhileInChannel(){
        when(sessions[0].isInChannel()).thenReturn(true);
        assertThrows(RuntimeException.class, () ->
                channelManager.createAndJoinChannel(channelNames[1], sessions[0], usernames[1]));
    }

    @Test
    public void shouldNotJoinIfUsernameTaken() throws IOException {
        channelManager.createAndJoinChannel(channelNames[0], sessions[0], usernames[0]);
        assertThrows(IOException.class, () ->
                channelManager.createAndJoinChannel(channelNames[0], sessions[1], usernames[0]));
    }

    @Test
    public void shouldNotCreateChannelIfNameTaken() throws IOException {
        channelManager.createAndJoinChannel(channelNames[0], sessions[0], usernames[0]);
        assertThrows(IOException.class, () ->
                channelManager.createAndJoinChannel(channelNames[0], sessions[1], usernames[1]));
    }

    @Test
    public void shouldLeaveChannel() throws IOException {
        channelManager.createAndJoinChannel(channelNames[0], sessions[0], usernames[0]);
        channelManager.joinChannel(channelNames[0], sessions[1], usernames[1]);
        when(sessions[0].isInChannel()).thenReturn(true);
        when(sessions[0].getChannel()).thenReturn(channelManager.getChannel(channelNames[0]));
        channelManager.leaveChannel(sessions[0]);
        checkNumOfChannels(1);
    }

    @Test
    public void shouldDeleteChannelWhenLastLeaves() throws IOException {
        channelManager.createAndJoinChannel(channelNames[0], sessions[0], usernames[0]);
        when(sessions[0].isInChannel()).thenReturn(true);
        when(sessions[0].getChannel()).thenReturn(channelManager.getChannel(channelNames[0]));
        channelManager.leaveChannel(sessions[0]);
        checkNumOfChannels(0);
    }

    @Test
    public void shouldThrowIfChannelNameDoesNotExist() throws IOException {
        channelManager.createAndJoinChannel(channelNames[0], sessions[0], usernames[0]);
        assertThrows(IOException.class, () ->
                channelManager.getChannel(channelNames[1]));
    }

    @Test
    public void shouldThrowIfClientLeavesWhileNotInChannel() throws IOException {
        channelManager.createAndJoinChannel(channelNames[0], sessions[0], usernames[0]);
        assertThrows(RuntimeException.class, () ->
                channelManager.leaveChannel(sessions[1]));
    }

    @BeforeEach
    private void prepareData(){
        channelManager = new ChannelManager();
        sessions = new ClientSession[5];
        usernames = new String[5];
        channelNames = new String[5];

        for(int i=0; i<5; i++){
            sessions[i] = mock(ClientSession.class);
            usernames[i] = "username" + (i+1);
            channelNames[i] = "test channel " + (i+1);
        }
    }

    private void checkNumOfChannels(int expected){
        String[] channels = channelManager.getAvailableChannels();
        assertEquals(expected, channels.length);
    }
}
