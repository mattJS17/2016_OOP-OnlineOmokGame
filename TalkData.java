import javax.swing.*;

/**
 * Created by mattshim on 2016. 12. 28..
 */
public class TalkData {

    JTextArea msgOut; // Model Part에서 다룰 데이터 객체

    // Model Part에서 다룰 컴포넌트 객체를 전달받아 등록
    public void addObj(JComponent component) {
        msgOut = (JTextArea) component;
    }

    // 데이터를 갱신
    public void refreshTalk(String str) {
        msgOut.append(str);
    }

}

