public class User {
    private String msgType;
    private String id;
    private String pw;
    private int winPoint;
    private String type;
    // User class
    // 메시지 타입, 아이디, 비밀번호, 승점, 타입을 데이터로 갖고있다
    public User() {
        msgType=null;
        id = null;
        pw = null;
        winPoint = 0;
        type = null;
    }

    public User(String msgType, String id, String pw, int winPoint, String type) {
        this.msgType = msgType;
        this.id = id;
        this.pw = pw;
        this.winPoint = winPoint;
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

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public int getWinPoint() {
        return winPoint;
    }

    public void setWinPoint(int winPoint) {
        this.winPoint = winPoint;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}