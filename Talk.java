
public class Talk {
    private String msgType;
    private String id;
    private String receiveid;
    private String msg;
    private String type;
    // Talk class
    // 메시지타입, 발신id, 수신id, 메시지, 타입을 데이터로 갖고있다
    public Talk() {
    }

    public Talk(String msgType, String id, String receiveid, String msg, String type) {
        this.msgType = msgType;
        this.id = id;
        this.receiveid = receiveid;
        this.msg = msg;
        this.type = type;
    }
    // 기본 & 파리미터 생성자

    // getter & setter
    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReceiveid() {
        return receiveid;
    }

    public void setReceiveid(String receiveid) {
        this.receiveid = receiveid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
