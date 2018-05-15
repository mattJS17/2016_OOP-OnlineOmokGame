import java.awt.*;

public class Omok {
    private String msgType;
    private boolean winflag;
    private boolean loseflag;
    private Point pt;
    private String color;
    private String type;
    // Omok class
    // 메시지 타입, 이겼는지, 졌는지, 착수한 위치 돌 색, 타입을 데이터로 갖고 있다
    public Omok() {
        msgType = null;
        winflag = false;
        loseflag = false;
        pt = new Point();
        color = null;
        type = null;
    }

    public Omok(String msgType, boolean winflag, boolean loseflag, Point pt, String color, String type) {

        this.msgType = msgType;
        this.winflag = winflag;
        this.loseflag = loseflag;
        this.pt = pt;
        this.color = color;
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

    public boolean isWinflag() {
        return winflag;
    }

    public void setWinflag(boolean winflag) {
        this.winflag = winflag;
    }

    public boolean isLoseflag() {
        return loseflag;
    }

    public void setLoseflag(boolean loseflag) {
        this.loseflag = loseflag;
    }

    public Point getPt() {
        return pt;
    }

    public void setPt(Point pt) {
        this.pt = pt;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
