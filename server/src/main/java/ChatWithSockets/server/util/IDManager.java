package ChatWithSockets.server.util;

public class IDManager {
    private static int idCount = 0;

    public static int getFreeId(){
        return idCount++;
    }
}
