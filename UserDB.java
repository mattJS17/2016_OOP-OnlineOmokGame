import java.sql.DriverManager;
import java.sql.ResultSet;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;


public class UserDB {
   String jdbcDriver = "com.mysql.jdbc.Driver";//jdbc����̹� �ε峻��
   String jdbcUrl = "jdbc:mysql://localhost/omokgame";//jdbc�� url
   Connection conn;//��� ���������� ����
   PreparedStatement pstmt;//������ ���� ������ �����ϴ� ���
   ResultSet rs;//�������� �޾ƿ� ���� �����ϴ� ����
   String sql;//�������� ������ ����
   private OmokServer omok;
   public UserDB(OmokServer o){
      omok=o;
   }
   public boolean checkLogin(User user){
     String id=user.getId();
     String pw=user.getPw();
     
     rs=null;
     
      String sql="select * from user where id=?";//������ ����
      connectDB();//��� ����
      try{
         pstmt=(PreparedStatement) conn.prepareStatement(sql);//�غ���� ������ ����
         pstmt.setString(1, id);//�޾ƿ� id�� �������� �־��ش�.
         rs=pstmt.executeQuery();//������ ������.
         
         rs.next();
        for(String uid : omok.ids){
           if(uid.equals(rs.getString("id"))) return false;//���ռӿ� �̹� �α����� ������� id�� �ִٸ� false�� ��ȯ�Ѵ�.
       }
        if(rs.getString("pw").equals(user.getPw())) return true;//��񿡼� �˻��� ������� pw�� �Է¹��� pw�� ���Ѵ�.
                                                  //���� �����ϴٸ� true�� ��ȯ�Ѵ�.
      }
      catch(Exception e){
        e.printStackTrace();
      }
     
      return false;//false�� ��ȯ
   }
   
   public Boolean newUser(User user){
      connectDB();//��� ����
      sql="insert into user values(?,?,?)";//���� ����
      try{
        pstmt=(PreparedStatement) conn.prepareStatement("select * from user where id=?");//�غ���� ������ ����
        pstmt.setString(1, user.getId());
        rs=pstmt.executeQuery();
        if(rs.next()) return false;//�ش� ���̵� �̹� ������ false�� ��ȯ
         
         pstmt=(PreparedStatement) conn.prepareStatement(sql);//�غ���� ������ ����
         pstmt.setString(1, user.getId());
         pstmt.setString(2,user.getPw());
         pstmt.setInt(3, 0);
         pstmt.executeUpdate();//�Էµ� ������ �̿��ؼ� ������ �����ش�.
         conn.close();//��� ���� ����
         return true;
      }
      catch(Exception e){
         e.printStackTrace();
         return false;
      }
   }
   
   public Rank getUserRank(){
      connectDB();//��� ����
      Rank rn = new Rank();
      sql="select * from user ORDER BY rate DESC limit 5";//rate�� 1~5�������� ������ �޾ƿ´�.
      try{
         pstmt=(PreparedStatement) conn.prepareStatement(sql);//�غ���� ������ ����
         rs=pstmt.executeQuery();//������ ������ ��� ���տ� �־��ش�.
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
         //�ش� ���������� ��ü�� �־��ش�.
         conn.close();//��� ���� ����
      }
      catch(Exception e){
         
      }
      return rn;
   }
   public void upRate(User user){//����ڰ� �̰������
      int win=0;
      sql="select rate from user where id=?";//������ ����
      connectDB();//��� ����
         try{
            pstmt=(PreparedStatement) conn.prepareStatement(sql);//�غ���� ������ ����
            pstmt.setString(1, user.getId());//�޾ƿ� user id�� �������� �־��ش�.
            rs=pstmt.executeQuery();//������ ������.
           rs.next();
           win =rs.getInt("rate");
         }
         catch(Exception e2){
            e2.printStackTrace();
         }

      sql="update user set rate=? where id=?";//������ ����
      try{
         pstmt=(PreparedStatement) conn.prepareStatement(sql);
         pstmt.setInt(1,win+9);//������� rate���� 9�� �����ش�
         pstmt.setString(2,user.getId());
         pstmt.executeUpdate();
         conn.close();
      }
      catch(Exception e){
       e.printStackTrace();
         
      }
      
   }
   public void downRate(User user){//�� �����
      int win=0;
      sql="select rate from user where id=?";//������ ����
      connectDB();//��� ����
         try{
            pstmt=(PreparedStatement) conn.prepareStatement(sql);//�غ���� ������ ����
            pstmt.setString(1, user.getId());//�޾ƿ� id�� �������� �־��ش�.
            rs=pstmt.executeQuery();//������ ������.
           rs.next();
           win =rs.getInt("rate");
         }
         catch(Exception e2){
            e2.printStackTrace();
         }
      sql="update user set rate=? where id=?";//������ ����
      try{
         pstmt=(PreparedStatement) conn.prepareStatement(sql);
         pstmt.setInt(1,win-3);//������ �������� -3�� ���ش�.
         pstmt.setString(2,user.getId());
         pstmt.executeUpdate();
      }
      catch(Exception e){
       e.printStackTrace();
         
      }
   }
   public void dodgeUser(User user){//��⵵�� ���� �����
      connectDB();
      sql="update user set rate=? where id=?";//������ ����
      try{
         pstmt=(PreparedStatement) conn.prepareStatement(sql);
         pstmt.setInt(1,0);
         user.setWinPoint(0);
         pstmt.setString(2,user.getId());
         pstmt.executeUpdate();//������ �����ش�.
         conn.close();
      }
      catch(Exception e){
       e.printStackTrace();
         
      }
   }
   public void connectDB() {
      try {

         Class.forName(jdbcDriver);//JDBC����̹� �ε�
         conn = (Connection) DriverManager.getConnection(jdbcUrl,"root","akuk156");
         //�����ͺ��̽� ����
      } catch (Exception e) {
         e.printStackTrace();
      }

   }
}