package ChatWithSockets.server.channels;

import ChatWithSockets.server.ClientSession;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.HashMap;

@Log4j2
public class ChannelManager {
    HashMap<String, Channel> channels = new HashMap<>();

    public void joinChannel(String channelName, ClientSession clientSession, String username) throws IOException {
        validIfNotInChannel(clientSession);
        Channel validChannel = getChannel(channelName);
        validChannel.addUser(clientSession, username);
    }

    public void createAndJoinChannel(String channelName, ClientSession session, String username) throws IOException{
        validIfNotInChannel(session);

        if(channels.containsKey(channelName))
            throw new IOException("Channel with name " + channelName + " already exists");
        Channel channel = new Channel(channelName);
        channel.addUser(session, username);
        channels.put(channelName, channel);
    }

    public void leaveChannel(ClientSession session){
        validIfInChannel(session);
        Channel channel = session.getChannel();
        channel.deleteUser(session);
        if (channel.getUsers().isEmpty()){
            boolean isDeleted = channels.remove(channel.getChannelName(), channel);
            if(!isDeleted) {
                RuntimeException e =
                        new RuntimeException("Could not delete channel when last user left." +
                                " Channel left=" + channel +
                                ", channels in Manager=" + channels);
                log.debug(e.getMessage());
                throw e;
            }
        }
    }

    public String[] getAvailableChannels(){
        return channels.keySet().toArray(new String[0]);
    }

    public Channel getChannel(String channelName) throws IOException {
        Channel channel = channels.get(channelName);
        if(channel == null)
            throw new IOException("Channel with name " + channelName + " does not exist");
        else return channel;
    }

    private void validIfNotInChannel(ClientSession session){
        if(session.isInChannel()) {
            RuntimeException e = new RuntimeException(
                    "Client tried to join to channel while already in channel." +
                            " ClientSession=" + session);
            log.debug(e.getMessage());
            throw e;
        }
    }

    private void validIfInChannel(ClientSession session){
        if(!session.isInChannel()) {
            RuntimeException e =
                    new RuntimeException("Client tried to leave channel while already not in channel. " +
                            "ClientSession=" + session);
            log.debug(e.getMessage());
            throw e;
        }
    }
}
