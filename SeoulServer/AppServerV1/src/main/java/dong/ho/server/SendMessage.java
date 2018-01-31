package dong.ho.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
 
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
 
@WebServlet("/message.do")
public class SendMessage extends HttpServlet {
    private static final long serialVersionUID = 1L;
    Connection conn = null;
	PreparedStatement pstm = null; 
	ResultSet rs = null; 
	
    String MESSAGE_ID = String.valueOf(Math.random() % 100 + 1);      //메시지 고유 ID
    boolean SHOW_ON_IDLE = true;                                      //옙 활성화 상태일때 보여줄것인지
    int LIVE_TIME = 1800;                                                //옙 비활성화 상태일때 FCM가 메시지를 유효화하는 시간
    int RETRY = 3;                                                    //메시지 전송실패시 재시도 횟수
    
    String simpleApiKey = "AAAADvaZ0qA:APA91bECK58NgBW7gvto-4vYfBA-_VfXzG2d_VvU4rBNGK_qks9jhKu_rg6YLWBoRqBivQxZeu-fl2bRiFBkbAVnstYbRwYiJfakKCuE318NB2moYJ7LLZ1IQJfgS6ogm91KxTWEUcE_";
    String gcmURL = "https://android.googleapis.com/fcm/send";
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} 
        doPost(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} 
    	
    	System.out.println("메세지");    	
    	
    	ArrayList<String> push = new ArrayList<String>();                //token값을 ArrayList에 저장
    	String sen = request.getParameter("SENDER_ID");
    	String rec = request.getParameter("RECEIVER_ID");
    	String date = request.getParameter("DATE");
    	String type = request.getParameter("TYPE");
    	String msg = request.getParameter("MESSAGE");    				//msg URLEncoder 처리 필요(한글 처리)
    	
    	System.out.println(msg+"/"+sen+"/"+rec+"/"+date);
        
        if(msg==null || msg.equals("")) msg="";			// msg null값일경우 에러 처리 msg값 초기화
        
        msg = URLEncoder.encode(msg,"UTF-8");		// 메세지 한글 깨짐 디코더
        
         
        try {
            //---------DB----------------------------------------------------
        	try {
        		
	            String quary = "SELECT MEM_PUSHKEY FROM MEMBERINFO where MEM_ID='"+rec+"' ";

	    		System.out.println(quary);
	    		conn = DBConnection.getConnection();
	    		pstm = conn.prepareStatement(quary);
	    		rs = pstm.executeQuery();
		           
	            while(rs.next()){	            	
	                String MEM_PUSHKEY = rs.getString(1);
	                push.add(MEM_PUSHKEY);		//pushkey	
	                System.out.println("MEM_PUSHKEY : "+MEM_PUSHKEY);
	            }
	   		 	
	   		 	
	        } catch (SQLException sqle) {
	            //sqle.printStackTrace();
	        	System.out.println("fail");
	            
	        }finally{
	            // DB 연결을 종료한다.
	            try{
	                if ( rs != null ){rs.close();}   
	                if ( pstm != null ){pstm.close();}   
	                if ( conn != null ){conn.close(); }
	            }catch(Exception e){
	                throw new RuntimeException(e.getMessage());
	            }
	            
	        }	
        	//---------DB 끝---------------------------------------------------
        	
        	
            String totalmsg = sen+"|"+rec+"|"+msg+"|"+type+"|"+date;            
            System.out.println(totalmsg);
            
        	//--------------------send-------------------------------
            Sender sender = new Sender(simpleApiKey);
            Message message = new Message.Builder()
            //.collapseKey(MESSAGE_ID)
            .delayWhileIdle(SHOW_ON_IDLE)
            .timeToLive(LIVE_TIME)
            .addData("message",totalmsg)
            .build();
            MulticastResult result1 = sender.send(message,push,RETRY);
            if (result1 != null) {
                List<Result> resultList = result1.getResults();
                for (Result result : resultList) {
                    //System.out.println(result.getErrorCodeName());                     
                }
            }
            push.clear();
            
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("실패");
            e.printStackTrace();
        }
    }
 
}