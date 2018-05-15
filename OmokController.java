import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class OmokController implements Runnable {
    private OmokUI v;
    private TalkData t;
    private Socket socket;
    private BufferedReader inMsg;
    private PrintWriter outMsg;
    private Gson gson = new Gson();
    private boolean status;
    private Thread thread;
    private User user;
    private Omok omok;
    private Talk talk;
    private Rank rank;
    private String id;
    private mouseListener mouseL;
    private mousemListener mmouseL;
    private boolean turn;
    private timeCheck tc;
    private loadTime lt;


    // ----오목 변수-----------------

    public static final int interval = 35;   //줄 간격
    public static final int num = 19;      //돌 갯수
    public static final int sizeStone = 25;   //돌 사이즈
    public static final int BLACK = 1;      //놓을 흑돌 색
    public static final int WHITE = -1;      //놓을 백돌 색
    public static final int BLACKWIN = 1;   //흑돌 이김
    public static final int WHITEWIN = 2;   //백돌 이김

    private int count = 0;   //총 놓은 돌
    // ---------------------------

    public static void main(String[] args) {
        new OmokController(new OmokUI(), new TalkData()).appMain();   //OmokController 생성
    }

    public OmokController(OmokUI v, TalkData t) {
        this.v = v;   //UI연결
        this.t = t;   //TalkData 연결

        mouseL = new mouseListener();   //마우스 리스너(UI)
        mmouseL = new mousemListener();   //마우스 모션 리스너(UI)
        tc = new timeCheck();
        lt = new loadTime();
    }

    public void appMain() {   //주요 동작 함수
        t.addObj(v.marea);

        v.addButtonActionListener(new ActionListener() {   //UI의 버튼 리스너
            @Override
            public void actionPerformed(ActionEvent e) {
                Object obj = e.getSource();

                if (obj == v.btnLogin) {   //로그인 버튼일 때
                    v.id = v.txtID.getText();   //id 설정
                    v.pw = v.txtPW.getText();   //pw 설정
                    connectServer(true);   //서버에 연결
                } else if (obj == v.btnMemShip) {   //회원가입 버튼일 때 회원가입 창으로 넘겨줌
                    v.txtID.setText("");
                    v.txtPW.setText("");
                    v.btnLogin.setEnabled(false);
                    v.btnLogin.setVisible(false);
                    v.btnMemShip.setEnabled(false);
                    v.btnMemShip.setVisible(false);
                    v.btnRegister.setEnabled(true);
                    v.btnRegister.setVisible(true);
                } else if (obj == v.btnRegister) {   //회원가입 버튼일 때
                    v.Rid = v.txtID.getText();
                    v.Rpw = v.txtPW.getText();
                    v.txtID.setText("");
                    v.txtPW.setText("");
                    v.btnLogin.setEnabled(true);
                    v.btnLogin.setVisible(true);
                    v.btnMemShip.setEnabled(true);
                    v.btnMemShip.setVisible(true);
                    v.btnRegister.setEnabled(false);
                    v.btnRegister.setVisible(false);
                    connectServer(false);
                } else if (obj == v.btnMatch) {   //매치 버튼일 때
                    v.lblmatching.setVisible(true);
                    outMsg.println(gson.toJson(new User("User", id, "", 0, "match")));
                    v.btnMatch.setEnabled(false);
                    v.lblmatching.start();
                } else if (obj == v.tfM) {      //텍스트 필드 메시지 버튼을 눌렀을 때
                    String ms = v.tfM.getText();
                    if (ms.equals("내가이겼다")) {   //"내가이겼다" 라고 채팅창에 넣었을 때 내가 이기게 해줌(백도어)
                        Point clickPoint = new Point(0, 0);
                        Boolean win = true;
                        v.tfM.setText("");
                        outMsg.println(gson.toJson(new Omok("Omok", win, false, clickPoint, null, "omok")));   //Omok형으로 winFlag를 true로ㅡ clickPoint는 0,0으로 제이슨 메시지를 서버로 보냄
                        turn = false;
                        JOptionPane.showMessageDialog(null, "YOU WIN ^^;", "VICTORY", JOptionPane.PLAIN_MESSAGE);   //내가 이겼다고 메시지 출력
                        System.out.println("black win!");
                        gameOver();   //게임이 종료된 후 처리 함수 부름
                    } else {   //아니라면 메시지 전송
                        v.tfM.setText("");
                        outMsg.println(gson.toJson(new Talk("Talk", id, "", ms, "talk")));   //Talk 형으로 내 아이디와 메시지 내용을 보냄
                    }
                }
            }
        });
    }

    public void connectServer(boolean flag) {   //서버와 연결
        try {
            socket = new Socket("210.107.233.72", 8888);   //소켓 연결할 IP주소와 포트
            inMsg = new BufferedReader(new InputStreamReader(socket.getInputStream()));   //inputStream를 받는다
            outMsg = new PrintWriter(socket.getOutputStream(), true);               //outStream을 내보낸다
            if (flag) {   //로그인 일 때와 회원가입일 때 나눠줌
                user = new User("User", v.id, v.pw, 0, "login");   //User형으로 로그인 할 id, pw를 적음
            } else {
                user = new User("User", v.Rid, v.Rpw, 0, "register");   //User형으로 회원가입 할 id,pw를 적음
            }
            outMsg.println(gson.toJson(user));   //적은 메시지를 서버로 전송

            thread = new Thread(this);   //쓰레드 생성
            thread.start();            //쓰레드 시작
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {   //쓰레드 실행
        String msg;
        status = true;

        while (status) {
            try {
                msg = inMsg.readLine();   //서버에서 메시지 올 때까지 기다림
                JsonParser parser = new JsonParser();   //파서 생성
                JsonElement rootObject = parser.parse(msg).getAsJsonObject().get("msgType");   //받은 메시지 중 msgType를 가져올 rootObject 생성

                if (rootObject.getAsString().equals("User")) {   //가져온 msgType이 User일 때
                    user = gson.fromJson(msg, User.class);

                    if (user.getType().equals("loginSuccess")) {   //type이 loginSuccess일 때
                        id = user.getId();            //id 가져옴
                        v.card.show(v.tab, "match");   //match 화면에 출력

                    } else if (user.getType().equals("loginFail")) {   //type이 loginFail일 때
                        JOptionPane.showMessageDialog(null, user.getId() + "   Login Failed", "Login Fail",
                                JOptionPane.PLAIN_MESSAGE);   //로그인 실패했다고 메시지 띄워줌

                    } else if (user.getType().equals("registerSuccess")) {   //registerSuccess일 때
                        JOptionPane.showMessageDialog(null, user.getId() + "   Register Success", "Register Success",
                                JOptionPane.PLAIN_MESSAGE);   //회원가입 성공했다고 메시지 띄워줌

                    } else if (user.getType().equals("registerFail")) {   //registerFail일 때
                        JOptionPane.showMessageDialog(null, user.getId() + "   Register Failed", "Register Fail",
                                JOptionPane.PLAIN_MESSAGE);   //회원가입 실패했다고 메시지 띄워줌

                    } else if (user.getType().equals("matchSuccess")) {   //상대방과 매치가 잡혔을 때
                        v.card.show(v.tab,"load");   //로딩 창으로 넘김
                        v.lblMatch.setText("YOU : " + user.getId()+" VS RIVAL : "+user.getPw());   //서버가 보낸 메시지를 바탕으로 ooo vs ooo 라고 화면에 표시
                        lt.start();   //lt 쓰레드 시작
                        // v.card.show(v.tab, "game"); // 게임 화면으로 스위치
                        // v.op.addMouseListener(mouseL, mmouseL);
                        turn = false;   //오목 화면 못 누르게 함
                        v.lbluser.setText("YOU : " + user.getId()+" VS RIVAL : "+user.getPw());   //서버가 보낸 메시지를 바탕으로 ooo vs ooo 라고 화면에 표시
                    }
                } else if (rootObject.getAsString().equals("Omok")) { //가져온 msgType이 Omok일 때
                    omok = gson.fromJson(msg, Omok.class);   //가져온 메시지 omok에 넣어줌
                    int x, y;
                    if (omok.getType().equals("Start")) { //type이 Start일 때
                        v.op.addMouseListener(mouseL, mmouseL); //마우스 리스너 붙임
                        if (omok.getColor().equals("Black")) {   //시작이 Black일 때
                            count++;   //놓은 총 돌 개수 올림
                            turn = true;//오목 화면 누를 수 있게 함
                            v.lblTime.setText("");   //타임 비워줌
                            v.lblTime.setForeground(Color.BLACK);
                            tc.start();   //시간 재는 쓰레드 시작
                        } else {
                            turn = false;   //시작이 White라면 화면 못 누르게 함
                        }
                    } else if (omok.isLoseflag()) {   //받은 메시지 중 Loseflag가 true일 때
                        x = omok.getPt().x;   //가져온 메시지 중 x,y좌표를 화면에 표시함
                        y = omok.getPt().y;
                        if (count % 2 == 0)   //상대가 놓은 돌 표시
                            v.op.stone[x][y] = BLACK;
                        else
                            v.op.stone[x][y] = WHITE;
                        v.op.repaint();   //다시 칠함
                        count = count + 2;   //총 놓은 돌 +2
                        JOptionPane.showMessageDialog(null, "YOU ARE LOOOOOOOSER", "루저 외톨이", JOptionPane.PLAIN_MESSAGE);   //졌다고 메시지 출력
                        gameOver();
                    } else if (omok.isWinflag()) {   //Winflag가 true일 때
                        count = count + 2;   //총 돌 +2
                        JOptionPane.showMessageDialog(null, "이겼다 당신 나갔다 상대방", "축하축하", JOptionPane.PLAIN_MESSAGE);   //상대방이 나가서 이겼다고 출력
                        gameOver();
                    } else {
                        x = omok.getPt().x;   //가져온 메시지 중 x,y좌표를 화면에 표시
                        y = omok.getPt().y;
                        if (count % 2 == 0)   //상대방 돌 화면에 표시
                            v.op.stone[x][y] = BLACK;
                        else
                            v.op.stone[x][y] = WHITE;
                        turn = true;   //오목 패널 누를 수 있게 함
                        v.op.repaint();   //다시 칠해줌
                        count = count + 2;   //놓은 총 돌 +2
                        v.lblTime.setText("");   //시간 출력 비워줌
                        v.lblTime.setForeground(Color.BLACK);
                        tc.start();   //시간 쓰레드 시작!
                    }
                } else if (rootObject.getAsString().equals("Talk")) {    //가져온 msgType이 User일 때
                    talk = gson.fromJson(msg, Talk.class);
                    t.refreshTalk(talk.getId() + "> " + talk.getMsg() + "\n");   //대화 내용 더해줌
                    v.marea.setCaretPosition(v.marea.getDocument().getLength());   //스크롤바 내림
                } else if (rootObject.getAsString().equals("Rank")) {    //가져온 msgType이 Rank일 때
                    rank = gson.fromJson(msg, Rank.class);            //rank에 받은 메시지 넣어줌
                    v.txtrank.setText("\n\t1. " + rank.getRank1() + "\n" + "\t2. " + rank.getRank2() + "\n" + "\t3. "
                            + rank.getRank3() + "\n" + "\t4. " + rank.getRank4() + "\n" + "\t5. " + rank.getRank5()
                            + "\n");   //순위 출력
                }
            } catch (Exception ex) {
                ex.getStackTrace();
            }
        }

    }

    // ---------------------오목 함수---------------------------------------------

    private boolean duplicate(int xPos, int yPos) { //이미 놓여 있는 돌 위치라면 true, 아니라면 false 반환
        if (v.op.stone[xPos][yPos] == BLACK || v.op.stone[xPos][yPos] == WHITE) {
            return true;
        }
        return false;
    }

    private boolean outRange(int xPos, int yPos) { //범위를 벗어나는지 검사
        if (xPos > 18 || yPos > 18 || xPos < 0 || yPos < 0) {
            return true;
        }
        return false;
    }

    private int isWin(int xPos, int yPos) { //이겼는지 체크
        int winCheck = 0;   //돌 5개가 한 열로 놓여있는지 검사할 플래그
        int j = 0;
        try {
            if (count % 2 == 0) {   //백돌일 때
                for (int i = xPos + 1; i <= xPos + 4; i++) {   //돌을 기준으로 오른쪽 검사
                    if (!outRange(i, yPos)) {               //범위 밖이라면 검사 안함
                        if (v.op.stone[i][yPos] != WHITE)      //돌이 white라면 winCheck 1올려줌
                            break;
                        winCheck++;
                    }
                }
                for (int i = xPos - 1; i >= xPos - 4; i--) {   //돌을 기준으로 왼쪽 검사
                    if (!outRange(i, yPos)) {
                        if (v.op.stone[i][yPos] != WHITE)
                            break;
                        winCheck++;
                    }
                }
                if (winCheck == 4)   //돌이 총 5개면 WHITEWIN반환
                    return WHITEWIN;
                winCheck = 0;      //아니라면 winCheck 0으로 만듬

                for (int i = yPos + 1; i <= yPos + 4; i++) {   //돌을 기준으로 아래쪽 검사
                    if (!outRange(xPos, i)) {
                        if (v.op.stone[xPos][i] != WHITE)
                            break;
                        winCheck++;
                    }
                }
                for (int i = yPos - 1; i >= yPos - 4; i--) {   //돌을 기준으로 위쪽 검사
                    if (!outRange(xPos, i)) {
                        if (v.op.stone[xPos][i] != WHITE)
                            break;
                        winCheck++;
                    }
                }
                if (winCheck == 4)
                    return WHITEWIN;
                winCheck = 0;

                j = yPos + 1;
                for (int i = xPos + 1; i <= xPos + 4; i++) {   //오른쪽 아래 대각선 검사
                    if (!outRange(i, j)) {
                        if (v.op.stone[i][j] != WHITE)
                            break;
                        winCheck++;
                    }
                    j++;
                }
                j = yPos - 1;
                for (int i = xPos - 1; i >= xPos - 4; i--) {   //왼쪽 위 대각선 검사
                    if (!outRange(i, j)) {
                        if (v.op.stone[i][j] != WHITE)
                            break;
                        winCheck++;
                    }
                    j--;
                }
                if (winCheck == 4)
                    return WHITEWIN;
                winCheck = 0;

                j = yPos - 1;
                for (int i = xPos + 1; i <= xPos + 4; i++) {   //오른쪽 위 대각선 검사

                    if (!outRange(i, j)) {
                        if (v.op.stone[i][j] != WHITE)
                            break;
                        winCheck++;
                    }
                    j--;
                }
                j = yPos + 1;
                for (int i = xPos - 1; i >= xPos - 4; i--) {   //왼쪽 아래 대각선 검사
                    if (!outRange(i, j)) {
                        if (v.op.stone[i][j] != WHITE)
                            break;
                        winCheck++;
                    }
                    j++;
                }
                if (winCheck == 4)
                    return WHITEWIN;
                winCheck = 0;
            } else   //흑돌일 때

            {
                for (int i = xPos + 1; i <= xPos + 4; i++) {
                    if (!outRange(i, yPos)) {
                        if (v.op.stone[i][yPos] != BLACK)
                            break;
                        winCheck++;
                    }
                }
                for (int i = xPos - 1; i >= xPos - 4; i--) {
                    if (!outRange(i, yPos)) {
                        if (v.op.stone[i][yPos] != BLACK)
                            break;
                        winCheck++;
                    }
                }
                if (winCheck == 4)
                    return BLACKWIN;
                winCheck = 0;

                for (int i = yPos + 1; i <= yPos + 4; i++) {
                    if (!outRange(xPos, i)) {
                        if (v.op.stone[xPos][i] != BLACK)
                            break;
                        winCheck++;
                    }
                }
                for (int i = yPos - 1; i >= yPos - 4; i--) {
                    if (!outRange(xPos, i)) {
                        if (v.op.stone[xPos][i] != BLACK)
                            break;
                        winCheck++;
                    }
                }
                if (winCheck == 4)
                    return BLACKWIN;
                winCheck = 0;

                j = yPos + 1;
                for (int i = xPos + 1; i <= xPos + 4; i++) {
                    if (!outRange(i, j)) {
                        if (v.op.stone[i][j] != BLACK)
                            break;
                        winCheck++;
                    }
                    j++;
                }
                j = yPos - 1;
                for (int i = xPos - 1; i >= xPos - 4; i--) {
                    if (!outRange(i, j)) {
                        if (v.op.stone[i][j] != BLACK)
                            break;
                        winCheck++;
                    }
                    j--;
                }
                if (winCheck == 4)
                    return BLACKWIN;
                winCheck = 0;

                j = yPos - 1;
                for (int i = xPos + 1; i <= xPos + 4; i++) {
                    if (!outRange(i, j)) {
                        if (v.op.stone[i][j] != BLACK)
                            break;
                        winCheck++;
                    }
                    j--;
                }
                j = yPos + 1;
                for (int i = xPos - 1; i >= xPos - 4; i--) {
                    if (!outRange(i, j)) {
                        if (v.op.stone[i][j] != BLACK)
                            break;
                        winCheck++;
                    }
                    j++;
                }
                if (winCheck == 4)
                    return BLACKWIN;
                winCheck = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error in isWin");
        }
        return 0;
    }

    private void gameOver() {   //게임이 끝났을 때 값 초기화 해 줌
        turn = false;
        v.btnMatch.setEnabled(true);
        v.lblmatching.setVisible(false);
        v.card.show(v.tab, "match");
        v.lblmatching.stop();
        v.lblmatching.setTh(null);
        lt.stop();
        lt.setT(null);
        tc.stop();
        tc.setTh(null);
        v.lblTime.setText("");
        count = 0;
        v.marea.setText("");
        for (int i = 0; i < 19; i++) {   //돌 초기화
            for (int j = 0; j < 19; j++) {
                v.op.stone[i][j] = 0;
                v.op.scheduleStone[i][j] = 0;
            }
        }
    }

    private class mouseListener implements MouseListener {   //마우스 리스너
        public void mouseClicked(MouseEvent event) {
            if (turn) {   //내 차례일 때만 실행
                try {
                    int xPos, yPos;
                    boolean win = false;   //이겼는지 체크하는 플래그
                    Point clickPoint;      //누른 좌표 저장하는 변수
                    xPos = (event.getX() - interval / 2) / interval;   //누른 곳을 바탕으로 좌표를 계산하는 식
                    yPos = (event.getY() - interval / 2) / interval;
                    clickPoint = new Point(xPos, yPos);   //clickPoint에 저장
                    if (!outRange(xPos, yPos)) {      //범위를 벗어나지 않고
                        if (!duplicate(xPos, yPos)) {   //누른 곳에 다시 누르지 않았을 때
                            if (count % 2 == 0) {      //내 돌에 따라 칠해줌
                                v.op.stone[xPos][yPos] = WHITE;
                            } else {
                                v.op.stone[xPos][yPos] = BLACK;
                            }

                            v.op.repaint();   //화면 다시 칠함

                            if (isWin(xPos, yPos) == WHITEWIN) {   //돌을 놨는데 내가 이겼을 때
                                System.out.println("white win!");
                                JOptionPane.showMessageDialog(null, "YOU WIN ^^;", "VICTORY",
                                        JOptionPane.PLAIN_MESSAGE);   //내가 이겼다고 서버에 메시지 띄움
                                win = true;   //win 플래그 true로 바꿔줌
                                gameOver();   //게임 오버
                            } else if (isWin(xPos, yPos) == BLACKWIN) {
                                JOptionPane.showMessageDialog(null, "YOU WIN ^^;", "VICTORY",
                                        JOptionPane.PLAIN_MESSAGE);
                                System.out.println("black win!");
                                win = true;
                                gameOver();
                            }
                            outMsg.println(gson.toJson(new Omok("Omok", win, false, clickPoint, null, "omok")));   //서버에 msgType을 Omok으로 winflag정보와 누른 좌표를 담아 메시지를 보내줌
                            turn = false;   //내 턴 끝!
                            tc.stop();      //시간초 멈춤
                            tc.setTh(null);
                            v.lblTime.setText("");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("here");
                }
            }
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }
    }

    private class mousemListener implements MouseMotionListener {   //마우스 모션 리스너(놓을 위치를 보여주는 마우스를 움직일 때 따라다니는 돌 표시)
        public void mouseMoved(MouseEvent event) {
            int xPos, yPos;
            if (turn) {   //내 차례일 때
                xPos = (event.getX() - interval / 2) / interval;   //마우스 위치에 따라 좌표 계산하는 식
                yPos = (event.getY() - interval / 2) / interval;

                if (!outRange(xPos, yPos)) {   //범위 밖이 아닐 때
                    if (count % 2 == 0) {      //놓을 돌 표시
                        v.op.scheduleStone[xPos][yPos] = BLACK;
                    } else {
                        v.op.scheduleStone[xPos][yPos] = WHITE;
                    }
                    v.op.repaint();   //repaint
                }
            }
        }

        public void mouseDragged(MouseEvent e) {
        }
    }
    public class loadTime implements Runnable {   //로딩 화면
        private Thread t;
        int a=10;
        public loadTime() {
        }

        public void setT(Thread t) {
            this.t = t;
        }

        public void start() {
            if (t == null)
                t = new Thread(this);
            t.start();
        } // start();

        public void stop() {
            if (t != null)
                t.stop();
        } // stop()

        public void run() {
            try {
                for (int i = 0; i < 3; i++) {
                    t.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            v.card.show(v.tab,"game");
        }
    }

    public class timeCheck implements Runnable {   //시간초 쓰레드, 사용자가 10초동안 놓지 않으면 랜덤으로 놓아서 서버로 전송
        private Thread th;
        int a=10;
        public timeCheck() {
        }

        public void setTh(Thread th) {
            this.th = th;
        }

        public void start() {
            if (th == null)
                th = new Thread(this);
            a=10;   //10초
            th.start();   //쓰레드 시작
        } // start();

        public void stop() {
            if (th != null)
                th.stop();
        } // stop()

        public void run() {
            try {
                for (int i = a; i > 0; i--) {   //10초 기다림
                    v.lblTime.setText("" + i);   //초 표시
                    th.sleep(1000);
                    if(i<=3)
                    {
                        v.lblTime.setForeground(Color.RED);   //3초 이하일 때 빨갛게 표시
                    }
                }
                //JOptionPane.showMessageDialog(null, "시간 초과",
                //      "time over", JOptionPane.PLAIN_MESSAGE);
                while(true)   //시간이 지났고 duplicate에 걸리지 않고 돌을 놓을 때 까지
                {
                    boolean win=false;   //winflag
                    int xPos=(int)(Math.random()*19);   //놓을 돌 랜덤
                    int yPos=(int)(Math.random()*19);
                    Point clickPoint;   //놓은 위치 담을 변수
                    if (!duplicate(xPos, yPos)) {      //놓을 돌이 놓인 돌과 겹치지 않으면
                        clickPoint=new Point(xPos,yPos);
                        if (count % 2 == 0) {   //돌 놓음
                            v.op.stone[xPos][yPos] = WHITE;
                        } else {
                            v.op.stone[xPos][yPos] = BLACK;
                        }

                        v.op.repaint();   //다시 칠함

                        if (isWin(xPos, yPos) == WHITEWIN) {   //랜덤으로 놓았는데 내가 이겼을 때 처리
                            System.out.println("white win!");
                            JOptionPane.showMessageDialog(null, "YOU WIN ^^;", "VICTORY",   //내가 이겼다고 표시
                                    JOptionPane.PLAIN_MESSAGE);
                            win = true;
                            gameOver();
                        } else if (isWin(xPos, yPos) == BLACKWIN) {
                            JOptionPane.showMessageDialog(null, "YOU WIN ^^;", "VICTORY",
                                    JOptionPane.PLAIN_MESSAGE);
                            System.out.println("black win!");
                            win = true;
                            gameOver();
                        }
                        outMsg.println(gson.toJson(new Omok("Omok", win, false, clickPoint, null, "omok")));   //놓은 돌 위치와 이겼는지 위치를 서버로 전송
                        turn = false;
                        break;
                    }
                }
                v.lblTime.setForeground(Color.BLACK);
                tc.setTh(null);
                v.lblTime.setText("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}