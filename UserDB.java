import java.sql.DriverManager;
import java.sql.ResultSet;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;


public class UserDB {
   String jdbcDriver = "com.mysql.jdbc.Driver";//jdbc드라이버 로드내용
   String jdbcUrl = "jdbc:mysql://localhost/omokgame";//jdbc의 url
   Connection conn;//디비 연결을위한 변수
   PreparedStatement pstmt;//쿼리를 날릴 변수를 저장하는 장소
   ResultSet rs;//쿼리에서 받아온 값을 저장하는 집함
   String sql;//쿼리문을 저장할 변수
   private OmokServer omok;
   public UserDB(OmokServer o){
      omok=o;
   }
   public boolean checkLogin(User user){
     String id=user.getId();
     String pw=user.getPw();
     
     rs=null;
     
      String sql="select * from user where id=?";//쿼리문 생성
      connectDB();//디비에 연결
      try{
         pstmt=(PreparedStatement) conn.prepareStatement(sql);//준비상태 쿼리를 연결
         pstmt.setString(1, id);//받아온 id를 쿼리문에 넣어준다.
         rs=pstmt.executeQuery();//쿼리를 날린다.
         
         rs.next();
        for(String uid : omok.ids){
           if(uid.equals(rs.getString("id"))) return false;//집합속에 이미 로그인한 사용자의 id가 있다면 false를 반환한다.
       }
        if(rs.getString("pw").equals(user.getPw())) return true;//디비에서 검색한 사용자의 pw와 입력받은 pw를 비교한다.
                                                  //비교후 동일하다면 true를 반환한다.
      }
      catch(Exception e){
        e.printStackTrace();
      }
     
      return false;//false를 반환
   }
   
   public Boolean newUser(User user){
      connectDB();//디비 연결
      sql="insert into user values(?,?,?)";//쿼리 선언
      try{
        pstmt=(PreparedStatement) conn.prepareStatement("select * from user where id=?");//준비상태 쿼리를 연결
        pstmt.setString(1, user.getId());
        rs=pstmt.executeQuery();
        if(rs.next()) return false;//해당 아이디가 이미 있으면 false를 반환
         
         pstmt=(PreparedStatement) conn.prepareStatement(sql);//준비상태 쿼리를 연결
         pstmt.setString(1, user.getId());
         pstmt.setString(2,user.getPw());
         pstmt.setInt(3, 0);
         pstmt.executeUpdate();//입력된 값들을 이용해서 쿼리를 날려준다.
         conn.close();//디비 연결 해제
         return true;
      }
      catch(Exception e){
         e.printStackTrace();
         return false;
      }
   }
   
   public Rank getUserRank(){
      connectDB();//디비에 연결
      Rank rn = new Rank();
      sql="select * from user ORDER BY rate DESC limit 5";//rate의 1~5위까지의 정보를 받아온다.
      try{
         pstmt=(PreparedStatement) conn.prepareStatement(sql);//준비상태 쿼리를 연결
         rs=pstmt.executeQuery();//쿼리를 날려서 결과 집합에 넣어준다.
         rs.next();
         rn.setRank1(rs.getString("id"));
         rs.next();
         rn.setRank2(rs.getString("id"));
         rs.next();
         rn.setRank3(rs.getString("id"));
         rs.next();
         rn.setRank4(rs.getString("id"));
         rs.next();
         rn.setRank5(rs.getString("id"));
         //해당 순위에따라 객체에 넣어준다.
         conn.close();//디비 연결 해제
      }
      catch(Exception e){
         
      }
      return rn;
   }
   public void upRate(User user){//사용자가 이겼을경우
      int win=0;
      sql="select rate from user where id=?";//쿼리문 생성
      connectDB();//디비에 연결
         try{
            pstmt=(PreparedStatement) conn.prepareStatement(sql);//준비상태 쿼리를 연결
            pstmt.setString(1, user.getId());//받아온 user id를 쿼리문에 넣어준다.
            rs=pstmt.executeQuery();//쿼리를 날린다.
           rs.next();
           win =rs.getInt("rate");
         }
         catch(Exception e2){
            e2.printStackTrace();
         }

      sql="update user set rate=? where id=?";//쿼리문 생성
      try{
         pstmt=(PreparedStatement) conn.prepareStatement(sql);
         pstmt.setInt(1,win+9);//사용자의 rate에서 9를 더해준다
         pstmt.setString(2,user.getId());
         pstmt.executeUpdate();
         conn.close();
      }
      catch(Exception e){
       e.printStackTrace();
         
      }
      
   }
   public void downRate(User user){//진 사용자
      int win=0;
      sql="select rate from user where id=?";//쿼리문 생성
      connectDB();//디비에 연결
         try{
            pstmt=(PreparedStatement) conn.prepareStatement(sql);//준비상태 쿼리를 연결
            pstmt.setString(1, user.getId());//받아온 id를 쿼리문에 넣어준다.
            rs=pstmt.executeQuery();//쿼리를 날린다.
           rs.next();
           win =rs.getInt("rate");
         }
         catch(Exception e2){
            e2.printStackTrace();
         }
      sql="update user set rate=? where id=?";//쿼리문 생성
      try{
         pstmt=(PreparedStatement) conn.prepareStatement(sql);
         pstmt.setInt(1,win-3);//유저의 점수에서 -3을 해준다.
         pstmt.setString(2,user.getId());
         pstmt.executeUpdate();
      }
      catch(Exception e){
       e.printStackTrace();
         
      }
   }
   public void dodgeUser(User user){//경기도중 나간 사용자
      connectDB();
      sql="update user set rate=? where id=?";//쿼리문 생성
      try{
         pstmt=(PreparedStatement) conn.prepareStatement(sql);
         pstmt.setInt(1,0);
         user.setWinPoint(0);
         pstmt.setString(2,user.getId());
         pstmt.executeUpdate();//쿼리를 날려준다.
         conn.close();
      }
      catch(Exception e){
       e.printStackTrace();
         
      }
   }
   public void connectDB() {
      try {

         Class.forName(jdbcDriver);//JDBC드라이버 로드
         conn = (Connection) DriverManager.getConnection(jdbcUrl,"root","akuk156");
         //데이터베이스 연결
      } catch (Exception e) {
         e.printStackTrace();
      }

   }
}