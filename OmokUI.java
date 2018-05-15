import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.Border;

public class OmokUI extends JFrame {

    // 로그인 화면
    private JPanel p1;
    private JLabel gameN;
    protected JTextField txtID;
    protected JLabel lblID;
    protected JPasswordField txtPW;
    protected JLabel lblPW;
    protected JButton btnLogin;
    protected JButton btnMemShip;
    protected JButton btnRegister;
    protected String id;
    protected String pw;
    protected String Rid;
    protected String Rpw;

    // 매치 화면
    private JPanel mp2;
    protected JButton btnMatch;
    private ImageIcon match;
    //protected JLabel lblmatching;
    protected LabelThread lblmatching;

    private JPanel matchLoad;
    protected JLabel lblMatch;

  //  ImageIcon icon1 = new ImageIcon("/Users/mattshim/Intellij_WS/Java_Winter/WinterProject/src/loading1.jpeg");
    ImageIcon icon1 = new ImageIcon("loading1.jpeg"); // 이미지

    // 게임 화면
    protected JPanel gp3;
    //맨위 패널
    private JPanel up;
    //오목 panel
    //private JPanel op;
    //rank panel
    private JPanel rp;
    //chat panel
    private JPanel mp;

    //맨 위에
    private JLabel lbl1;
    protected JLabel lblTime;
    protected JLabel lbluser;
    protected JTextArea txtrank;

    //rank 부분
    private JLabel lblR;

    //메시지 보내는 부분
    protected JTextField tfM;
    private JLabel lblM;
    private JPanel text;
    private JScrollPane mscroll;
    protected JTextArea marea;

    protected OmokPanelUI op;


    // 스위치
    protected CardLayout card;
    protected Container tab;
    Image img=new ImageIcon().getImage();
    //ImageIcon icon=new ImageIcon("/Users/mattshim/Intellij_WS/Java_Winter/WinterProject/src/back9.png");
    ImageIcon icon=new ImageIcon("back9.png"); // 이미지

    //오목알 아이콘
    private ImageIcon Image;


    public OmokUI(){

        //frame
        super("OMOK");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.LIGHT_GRAY);
        setPreferredSize(new Dimension(1105,835));
        setResizable(false);
        setLayout(null);
        // Frame에 대한 설정들


        //처음 화면: 로그인, 회원가입
        p1 = new JPanel() {
            public void paintComponent(Graphics g){
                g.drawImage(icon1.getImage(), 0, 0, 1105,845,null);
                setOpaque(false);
                super.paintComponent(g);
            } // 배경에 이미지를 그린
        };
        //  p1 = new JPanel();
        p1.setBounds(0, 0, 1105, 845);
        p1.setBackground(Color.pink);
        p1.setLayout(null);
        // 처음화면인 p1에 대한 설정

        //game name
        gameN=new JLabel("Who Is My Rival?");
        gameN.setBounds(100, 80, 850,200);
        gameN.setFont(new Font("Verdana",Font.ITALIC+Font.BOLD,85));
        gameN.setForeground(Color.white);
        gameN.setLayout(null);
        gameN.setVisible(true);
        p1.add(gameN);
        // 로그인화면에 뜨는 게임 제목 라벨에 대한 설정

        Font fnt=new Font(null, Font.PLAIN,45);

        //ID part
        txtID=new JTextField(10);
        txtID.setBounds(390,350,400,60);
        txtID.setFont(fnt);
        lblID=new JLabel("ID ");
        lblID.setForeground(Color.white);
        lblID.setBounds(310,350,400,70);
        lblID.setFont(fnt);
        p1.add(txtID);
        p1.add(lblID);

        //PW part
        txtPW=new JPasswordField(10);
        txtPW.setBounds(390,450,400,60);
        txtPW.setFont(fnt);
        lblPW=new JLabel("PW ");
        lblPW.setForeground(Color.white);
        lblPW.setBounds(310,450,400,70);
        lblPW.setFont(fnt);
        p1.add(txtPW);
        p1.add(lblPW);

        // 아이디와 패스워드를 입력하는 곳에 대한 설정
        // ID, PW라고 써주고 pw는 JPasswordField로 설정하였다

        Font f=new Font(null, Font.PLAIN,30);

        //login button
        btnLogin=new JButton("login");
        btnLogin.setBounds(390,550,160,80);
        btnLogin.setFont(f);
        btnMemShip=new JButton("member");
        btnMemShip.setBounds(630,550,160,80);
        btnMemShip.setFont(f);
        p1.add(btnLogin);
        p1.add(btnMemShip);
        // 로그인과 회원가입 버튼

        btnRegister = new JButton("Register");
        btnRegister.setBounds(510,550,160,80);
        btnRegister.setFont(f);
        btnRegister.setEnabled(false);
        btnRegister.setVisible(false);
        p1.add(btnRegister);
        // 가입 완료버튼은 보이지 않다가 회원가입버튼을 누르면 로그인, 회원가입 버튼이 가려지고 가입완료 버튼이
        // 띄워진다


        /*--------매치 화면--------*/
        //mp2=new JPanel();
        mp2=new JPanel() {
            public void paintComponent(Graphics g1){
                g1.drawImage(icon1.getImage(), 0, 0, 1105,845,null);
                setOpaque(false);
                super.paintComponent(g1);
            } // 배경을 그린다
        };

        mp2.setBounds(0, 0, 1105, 845);
        // mp2.setBackground(Color.lightGray);
        mp2.setLayout(null);
        // 매치 기다리는 화면

        //btnMatch button
//        match = new ImageIcon("/Users/mattshim/Intellij_WS/Java_Winter/WinterProject/src/btn.png");
        match = new ImageIcon("btn.png"); // 이미지

        //match = new ImageIcon("btn.png");
        Image settingImg = match.getImage();
        Image settingImg1 = settingImg.getScaledInstance(300, 300, java.awt.Image.SCALE_SMOOTH);
        match.setImage(settingImg1);

        btnMatch =new JButton(match);
        btnMatch.setBounds(390,350,300,300);
        btnMatch.setHorizontalTextPosition(SwingConstants.CENTER);
        btnMatch.setVerticalTextPosition(SwingConstants.CENTER);
        btnMatch.setBorderPainted(false);// 테두리 설정
        btnMatch.setContentAreaFilled(false);// 배경 색 설정
        btnMatch.setFocusPainted(false);
        mp2.add(btnMatch);
        // 매치 버튼

        //대국 신청 중
        lblmatching=new LabelThread("Searching Your Rival...");
        lblmatching.setBounds(200,100,800,200);
        lblmatching.setFont(new Font(null, Font.BOLD,55));
        lblmatching.setVisible(false);
        mp2.add(lblmatching);
        // 매치 버튼을 누르면 대기중이라는 문구가 뜬다. 스레드로 처리해서 .이 움직인다

       matchLoad = new JPanel() {
            public void paintComponent(Graphics g3){
                g3.drawImage(icon.getImage(), 0, 0, 1105,845,null);
                setOpaque(false);
                super.paintComponent(g3);
            } // 배경을 이미지로 그린다
        };
        matchLoad.setBounds(0, 0, 1105, 845);
        matchLoad.setBackground(Color.lightGray);
        matchLoad.setLayout(new BorderLayout());
        // 게임 들어가기 전 누구 vs 누구인지를 보여주는 패널

        lblMatch = new JLabel("");
        lblMatch.setFont(new Font("",Font.BOLD, 60));
        lblMatch.setForeground(Color.white);
        lblMatch.setHorizontalAlignment(SwingConstants.CENTER);
        matchLoad.add(lblMatch,BorderLayout.CENTER);
        // lblMatch에 누구 vs 누구가 쓰인다


        /*--------게임 화면--------*/

        gp3 = new JPanel();
        gp3.setBounds(0, 0, 1100, 800);
        gp3.setLayout(null);
        // 게임 화면에 대한 설정

        //맨위
        //panel
        up=new JPanel();
        up.setBounds(0, 0, 1100, 100);
        up.setBackground(new Color(190,130,70));
        up.setLayout(null);
        gp3.add(up);
        // 가장 위의 패널

        //맨위
        lbl1=new JLabel("OMOK");
        lbl1.setFont(new Font(null, Font.BOLD,50));
        lbl1.setBounds(460, 17, 180, 55);
        up.add(lbl1);
        // OMOK이 up패널에 add

        lblTime = new JLabel("");
        lblTime.setFont(new Font(null, Font.BOLD,50));
        lblTime.setBounds(1000,17, 70, 60);
        up.add(lblTime);
        // 시간을 나타내는 라벨이 up패널에 add

        //유저 이름
        lbluser=new JLabel("USER: ");
        lbluser.setFont(new Font(null,Font.PLAIN,20));
        lbluser.setBounds(10,10,400,30);
        lbluser.setHorizontalAlignment(SwingConstants.LEFT);
        up.add(lbluser);
        // 유저 이름이 up패널에 add

        //오목 패널
        op=new OmokPanelUI();
        op.setBounds(0, 100, 700, 700);
        op.setLayout(null);
        gp3.add(op);
        // 오목게임이 진행되는 패널

        //랭크 부분
        //랭크 패널
        rp=new JPanel();
        rp.setBounds(700, 100, 400, 200);
        rp.setBackground(Color.pink);
        rp.setLayout(new BorderLayout());
        gp3.add(rp);
        // 우측 상단에 나타나는 랭크 패널

        txtrank = new JTextArea();
        txtrank.setFont(new Font("",Font.BOLD,20));
        txtrank.setEditable(false);
        txtrank.setBackground(Color.pink);
        rp.add(txtrank,BorderLayout.CENTER);
        // 랭킹이 나타나는 JTextArea

        Font lblf=new Font(null,Font.BOLD,30);
        //Rank label
        lblR=new JLabel("Ranking");
        lblR.setFont(lblf);
        lblR.setHorizontalAlignment(SwingConstants.CENTER);
        rp.add(lblR, BorderLayout.NORTH);
        // Ranking이라고 쓰여진 라벨이 랭킹패널의 상단에 위치한다

        //메시지 부분
        //메시지 글꼴
        Font fm=new Font(null,Font.PLAIN,14);

        //메시지 패널
        mp=new JPanel();
        mp.setBounds(700, 300, 400, 500);
        mp.setBackground(new Color(200,165,233));
        mp.setLayout(new BorderLayout());
        gp3.add(mp);
        // 채팅이 나타나는 패널

        text = new JPanel();
        text.setLayout(new BorderLayout());
        tfM = new JTextField(13);
        tfM.setFont(fm);
        lblM = new JLabel(" 채팅");
        lblM.setFont(fm);
        text.add(lblM, BorderLayout.WEST);
        text.add(tfM,BorderLayout.CENTER);
        // text패널에 메시지 입력창을 더한다

        //메시지 textarea
        marea=new JTextArea("",10,10);
        marea.setFont(fm);
        marea.setBounds(0,55,385,415);
        marea.setEditable(false);
        // 채팅 내용이 뜨는 JTextArea


        //스크롤바 넣기
        mscroll=new JScrollPane(marea,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mp.add(mscroll, BorderLayout.CENTER);
        mp.add(text,BorderLayout.SOUTH);
        // marea에 스크롤 추


        /*--------화면 스위치--------*/
        tab = new JPanel();
        tab.setBounds(0, 0, 1105, 845);
        card = new CardLayout();
        tab.setLayout(card);
        tab.add(p1, "login");
        tab.add(mp2, "match");
        tab.add(gp3,"game");
        tab.add(matchLoad, "load");
        add(tab);
        // 카드레이아웃을 적용하기 위한 설정 총 4개의 화면을 스위치한다

        pack();
        setVisible(true);
    }

    public void addButtonActionListener(ActionListener listener) { // 이벤트처리를 Control Part에 맡기기 위해 모아둠
        btnLogin.addActionListener(listener);
        btnMemShip.addActionListener(listener);
        tfM.addActionListener(listener);
        btnRegister.addActionListener(listener);
        btnMatch.addActionListener(listener);
    }
    public class LabelThread extends JLabel implements Runnable { // 라벨 스레드
        private Thread th;
        public LabelThread(String text) {
            super(text);
        }

        public void setTh(Thread th) {
            this.th = th;
        }

        public void start() { // 스레드 시작
            if (th == null)
                th = new Thread(this);
            th.start();
        } // start();

        public void stop() { // 스레드 종료
            if (th != null)
                th.stop();
        } // stop()

        public void run() {
            try {
                while (true) { // 계속 . . . 의 이동을 반복하는 스레드
                    setText("Searching Your Rival");
                    th.sleep(500);
                    setText("Searching Your Rival.");
                    th.sleep(500);
                    setText("Searching Your Rival .");
                    th.sleep(500);
                    setText("Searching Your Rival  .");
                    th.sleep(500);
                    setText("Searching Your Rival   .");
                    th.sleep(500);
                }

            } catch (Exception e) {
            }
        }
    }

}