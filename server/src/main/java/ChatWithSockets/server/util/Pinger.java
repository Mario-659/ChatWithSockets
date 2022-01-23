package ChatWithSockets.server.util;

import ChatWithSockets.server.ClientSession;
import ChatWithSockets.shared.Request.Request;
import ChatWithSockets.shared.Request.RequestType;

import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Responsible for sending Ping request to clients. Used for making sure that client is still connected
 */
public class Pinger {
    private ScheduledFuture<?> future;
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);
    private  Runnable pingToAll;
    private final HashSet<ClientSession> sessions = new HashSet<>();
    private final HashSet<ClientSession> queToAdd = new HashSet<>();
    private final HashSet<ClientSession> queToDelete = new HashSet<>();

    /**
     * Sends ping request to clients with given rate
     *
     * @param pingRate Ping rate in seconds
     */
    public Pinger(int pingRate){
        setPingToAll();
        setScheduler(pingRate);
    }

    /**
     * Adds session to ping
     *
     * @param session session to ping
     */
    public void addSession(ClientSession session){
        queToDelete.remove(session);
        queToAdd.add(session);
    }

    /**
     * Removes sessions from ping list
     *
     * @param session session to delete
     */
    public void deleteSession(ClientSession session){
        queToAdd.remove(session);
        queToDelete.add(session);
    }

    private void setScheduler(int pingRate) {
        future = scheduler.scheduleAtFixedRate(pingToAll, pingRate, pingRate, SECONDS);
        scheduler.schedule(() -> { future.cancel(true); }, 60 * 60, SECONDS);
    }

    private void setPingToAll(){
        pingToAll = () -> {
            updateSessions();
            for (ClientSession session : sessions) {
                session.sendRequest(new Request(RequestType.PING, ""));
            }
        };
    }

    private void updateSessions(){
        sessions.removeAll(queToDelete);
        sessions.addAll(queToAdd);
        queToDelete.clear();
        queToAdd.clear();
    }
}
