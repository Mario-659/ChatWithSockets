package ChatWithSockets.shared.Request;

import java.io.Serializable;

public class Request implements Serializable {
    private final RequestType type;
    private final String payload;
    private final String sender;

    public Request(RequestType type, String payload, String sender){
        this.payload = payload;
        this.type = type;
        this.sender = sender;
    }

    public Request(RequestType type, String payload){
        this(type, payload, null);
    }

    public String getPayload(){
        return payload;
    }

    public RequestType getType(){
        return type;
    }

    public String getSender(){
        return this.sender;
    }

    @Override
    public String toString() {
        return "Request{" +
                "type=" + type +
                ", payload='" + payload + '\'' +
                ", sender='" + sender + '\'' +
                '}';
    }
}
