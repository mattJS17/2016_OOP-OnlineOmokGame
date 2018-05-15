
public class Rank {
    private String msgType;
    private String rank1;
    private String rank2;
    private String rank3;
    private String rank4;
    private String rank5;
    // Rank class
    // 메시지타입, 랭킹 1~5를 데이터로 갖고 있다
    public Rank() {
    }

    public Rank(String msgType, String rank1, String rank2, String rank3, String rank4, String rank5) {
        this.msgType = msgType;
        this.rank1 = rank1;
        this.rank2 = rank2;
        this.rank3 = rank3;
        this.rank4 = rank4;
        this.rank5 = rank5;
    }
    // 기본 & 파리미터 생성자

    // getter & setter

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getRank1() {
        return rank1;
    }

    public void setRank1(String rank1) {
        this.rank1 = rank1;
    }

    public String getRank2() {
        return rank2;
    }

    public void setRank2(String rank2) {
        this.rank2 = rank2;
    }

    public String getRank3() {
        return rank3;
    }

    public void setRank3(String rank3) {
        this.rank3 = rank3;
    }

    public String getRank4() {
        return rank4;
    }

    public void setRank4(String rank4) {
        this.rank4 = rank4;
    }

    public String getRank5() {
        return rank5;
    }

    public void setRank5(String rank5) {
        this.rank5 = rank5;
    }
}
