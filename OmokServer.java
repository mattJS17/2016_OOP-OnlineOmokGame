import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;

import javax.swing.JFrame;

import com.google.gson.*;

public class OmokServer {

   public static void main(String[] args) {
      OmokServer a = new OmokServer();//자기자신을 생성한다.
   }
   Gson gson = new Gson();//gson 객체 생성
   private ServerSocket ss = null;//serversocket 선언
   private Socket s = null;//socket선언

   private UserDB userDB;
   Set<String> ids= new HashSet<String>();//로그인중인 유저의 정보를 저장하기위한 집합
   ArrayList<ChatThread> chatThreads = new ArrayList<ChatThread>();//소켓에 연결된 사용자들을 넣기위한 arrayList
   int rm=0;
   int usernum=0;
   ChatThread waitingRow;//대기중인 사용자가 들어갈곳
   
   boolean status;

   public OmokServer() {
      JFrame frame = new JFrame();
      frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
      frame.pack();
      frame.setVisible(true);
      //종료를 쉽게만들기위한 프레임
      waitingRow=new ChatThread();
      //대기열을 위한 객체 생성
      waitingRow.roomnum=0;//대기열의 초기 방번호는 0
      userDB = new UserDB(this);
      start();//start 메소드 호출
      
   }

   private void start() {//서버가 시작하는 부분
      try {
         ss = new ServerSocket(8888);//8888포트로 서버소켓을 열어준다.
         while (true){
            s = ss.accept();//클라이언트에서 서버로 들어오는 소켓을 받아준다. 
            ChatThread chat = new ChatThread(s); //받은 소켓으로 사용자를 위한 객체를 생성후 소켓정보를 넘겨준다.
            chatThreads.add(chat); //arrayList에 해당 객체를 애드
            chat.start();//스레드 시작
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   public void msgSend(String msg,int num){//채팅메세지를 같은방에있는 사용자들에게 보내준다.
      for(ChatThread ct : chatThreads){
         if(ct.roomnum==num) ct.outMsg.println(msg);//메세지를 클라이언트에게 보내준다.
      }
   }
   public void omokSend(String msg,int num,ChatThread c){//오목정보를 상대에게 보내준다.
      for(ChatThread ct : chatThreads){
         if(ct.roomnum==num&&ct!=c&&!c.omok.isWinflag()){//윈플래그가 false일때 오목정보를 상대에게 보내주기만한다.
            ct.outMsg.println(msg);
            
         }
         else if(ct.roomnum==num&&ct!=c&&c.omok.isWinflag()) {//윈플래그가 true일때 게임을 끝내도록 초기화를 시켜준다.
            userDB.downRate(ct.user);//진 사용자의 점수를 내린다.
            ct.isGaming=false;//게임중인 플래그를 false로 바꾼다.
            ct.outMsg.println(msg);//오목정보를 보내준다.
            c.roomnum=0;
            ct.roomnum=0;//방번호를 0으로 만들어서 방에 속하지 않게한다.
         }
         else{
         }
      }
   }
  public void out(int num,ChatThread c){//상대방이 게임중에 나갔을경우
      String msg;
      for(ChatThread ct : chatThreads){
         if(ct.roomnum==num&&ct!=c){
            msg = gson.toJson(new Omok(c.omok.getMsgType(),true,false,c.omok.getPt(),c.omok.getColor(),c.omok.getType()));//나가지 않은 사용자에게 winflag를 true로 바꿔 보내준다.
            ct.outMsg.println(msg);
            ct.roomnum=0;
            userDB.upRate(ct.user);//나가지 않은 사용자의 점수를 올려준다.
         }
      }
   }
   private class ChatThread extends Thread {
      User user = new User();
      Talk talk = new Talk();
      Omok omok = new Omok();
      Rank rn = new Rank();
      String msg;
      boolean isGaming=false;//현재 게임중인지 보기위한 플래그
      int roomnum;
      private BufferedReader inMsg;
      private PrintWriter outMsg;
      private Socket s1;
      public ChatThread(){
         
      }
      public ChatThread(Socket s) {
         this.s1=s;//소켓인자를 전달받아서 넣어준다.
      }

      public void run() 
      {
         status = true;
         try {
            inMsg = new BufferedReader(new InputStreamReader(s1.getInputStream()));
            outMsg = new PrintWriter(s1.getOutputStream(), true);//넘겨받은 소켓정보를 토대로 메세지를 주고받기위한 스트림을 생성해준다.
         } catch (IOException e) {
            e.printStackTrace();
         }
         while (status) {
            try {
               msg = inMsg.readLine();//메세지를 읽는다.
            } catch (IOException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
               if(isGaming){
                     out(roomnum,this);//사용자가 게임중에 나간경우 이메소드를 호출한다.
                     userDB.dodgeUser(user);   //그 유저의 점수를 0으로 만든다.
               }
               else{
                  if(roomnum!=0) waitingRow.roomnum=0;//게임중이 아니였을경우 대기열을 초기화한다.
               }
               try {
                  ids.remove(user.getId());//사용자의 로그인 정보를 없애준다.
                  inMsg.close();//메세지 스트림을 닫는다
                  outMsg.close();
                  s1.close();//소켓을 닫는다.
                  chatThreads.remove(this);//arrayList에서 해당 사용자의 객체를 삭제한다.
         } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
         }
               break;    //소켓이 종료될경우 해당 while을 멈춘다.
            }
            JsonParser parser = new JsonParser();//jsonparser를 생성
            JsonElement rootObject = parser.parse(msg).getAsJsonObject().get("msgType");//받은 json 메세지에서 msgType에 해당하는 부분을 가져온다.
            if (rootObject.getAsString().equals("User")) {//그 부분과 User라는 문자열과 비교한다.
               user = gson.fromJson(msg, User.class);//user클래스를 기반으로 메세지를 객체화 시켜준다.
               if (user.getType().equals("login")) {//user의 type이 login일경우
                  if (!userDB.checkLogin(user)) {//디비에서 해당 객체의 id와 pw를 검색해서 비교한다 
                     try {
                        outMsg.println(gson.toJson(new User("User","","", 0,"loginFail")));//다르다면 loginFail이라는 메세지를 클라이언트에게 보낸다.
                        inMsg.close();
                        outMsg.close();
                        s.close();//연결을 닫아준다.
                        chatThreads.remove(this);//객체를 삭제해준다.
                        break;
                     } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                     }
                  } else {
                     ids.add(user.getId());//로그인이 성공하면 로그인중인 정보를 hashset에 넣어준다.
                     outMsg.println(gson.toJson(new User("User",user.getId(),"", 0,"loginSuccess")));//로그인 성공 메세지를 보낸다.
                  }
               } else if (user.getType().equals("register")) {//type이 가입일경우
                  
                  if (!userDB.newUser(user)) {//db에 유저 정보를 보내서 db에 등록시켜준다. 이미 등록된 동일한 아이디가 있을경우 false가 뜬다.
                     outMsg.println(gson.toJson(new User("User",user.getId(),"", 0,"registerFail")));
                  } else {
                     outMsg.println(gson.toJson(new User("User",user.getId(),"", 0,"registerSuccess")));
                  }
                  try {
                     inMsg.close();
                     outMsg.close();
                     s.close();
                     chatThreads.remove(this);
                  } catch (IOException e3) {
                     // TODO Auto-generated catch block
                     e3.printStackTrace();
                  }
               } else if (user.getType().equals("match")) {
                  synchronized(waitingRow){//대기열을 동기화 시켜준다. 순차적인 접근을위해서

                  if(waitingRow.roomnum==0){ //대기중인 사용자가 없을경우
                    try {
                  Thread.sleep(333);
               } catch (InterruptedException e1) {
                  // TODO Auto-generated catch block
                  e1.printStackTrace();
               }
                     waitingRow=this;//대기에 현재 사용자를 넣어준다.
                     rm++;//룸넘버를 올려준다
                     waitingRow.roomnum=rm;//방번호를 넣어준다.
                  }
                  
                  else 
                  {
                     
                     //waitingRow.roomnum=rm;
                     waitingRow.isGaming=true;
                     isGaming=true;//같은 방에 들어갈 사용자 둘에게 현재 게임중이라는 플래그를 true 로 바꿔준다.
                     this.roomnum=rm;//현재 사용자에게도 같은 방번호를 넣어준다.
                     outMsg.println(gson.toJson(new User("User",user.getId(),waitingRow.user.getId(), 0,"matchSuccess")));
                     waitingRow.outMsg.println(gson.toJson(new User("User",waitingRow.user.getId(),user.getId(), 0,"matchSuccess")));//그 후 두 사용자에게 매치가 성공하였다는 메세지를 보내준다.
                     waitingRow.outMsg.println(gson.toJson(new Omok("Omok",false,false,omok.getPt(),"Black","Start")));
                     outMsg.println(gson.toJson(new Omok("Omok",false,false,omok.getPt(),"White","Start")));//둘에게 각각 black과 white의 정보를 보내준다.
                     rn=userDB.getUserRank();//DB에서 랭킹정보를 받아온다
                     msg=gson.toJson(new Rank(rn.getMsgType(),rn.getRank1(),rn.getRank2(),rn.getRank3(),rn.getRank4(),rn.getRank5()));//사용자둘에게 보내준다.
                     waitingRow=null;
                     waitingRow = new ChatThread();
                     waitingRow.roomnum=0;//다시 새로운 대기열을 생성해준다.
                     msgSend(msg,roomnum);

                  }
               }
               }
               
            }
            else if(rootObject.getAsString().equals("Talk")){//msgtype이 Talk일경우
               msgSend(msg,roomnum);//단지 메세지를 같은 방번호를 가진 사용자에게 보내준다.
            }
            else if(rootObject.getAsString().equals("Omok")){//msgType이 Omok일경우
               omok=gson.fromJson(msg, Omok.class);//메세지를 omok클래스 형식으로 바꿔준다.
               if(omok.isWinflag()){//승리 플래그가 true일경우
                  userDB.upRate(user);//사용자의 승점을 올려주는 메소드를 호출한다
                  isGaming=false;//게임중이 아니라고 바꿔준다.
      
                  msg = gson.toJson(new Omok(omok.getMsgType(),false,true,omok.getPt(),"",omok.getType()));//상대방에게 보낼메세지를 수정해준다.
               }
               else{
                  msg = gson.toJson(new Omok(omok.getMsgType(),false,false,omok.getPt(),"",omok.getType()));//그대로 보내준다.
               }
               omokSend(msg,roomnum,this);//같은 방번호를 가진 상대방에게 전송하는 메소드를 호출
            }
            this.interrupt();
         }
      }
   }
}