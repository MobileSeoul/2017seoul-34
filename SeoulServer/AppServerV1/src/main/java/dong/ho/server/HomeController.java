package dong.ho.server;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	

	Connection conn = null; // DB연결된 상태(세션)을 담은 객체
    PreparedStatement pstm = null;  // SQL 문을 나타내는 객체
    ResultSet rs = null;  // 쿼리문을 날린것에 대한 반환값을 담을 객체
	
    @RequestMapping(value="test.do",produces="application/json;charset=utf-8")
    public @ResponseBody String test(HttpServletRequest request){
    	try {
            String user = "kok99274"; 
            String pw = "rabbit94!@";
            String url = "jdbc:mysql://localhost:3306/kok99274";
            
            Class.forName("com.mysql.jdbc.Driver");        
            conn = DriverManager.getConnection(url, user, pw);
            
            //System.out.println("Database에 연결되었습니다.\n");
            
        } catch (ClassNotFoundException cnfe) {
            return "DB 드라이버 로딩 실패 :"+cnfe.toString();
        } catch (SQLException sqle) {
        	return  "DB 접속실패 : "+sqle.toString();
        } catch (Exception e) {
        	return "Unkonwn error";
        }
    	return "test....";
    }
	
	@RequestMapping(value="version.do",produces="application/json;charset=utf-8")
    public @ResponseBody String version(HttpServletRequest request){
        
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} 
		int num=0;
		
		//DB처리 구문
		 try {
	            // SQL 문장을 만들고 만약 문장이 질의어(SELECT문)라면
	            // 그 결과를 담을 ResulSet 객체를 준비한 후 실행시킨다.
	            String quary = "SELECT COUNT(*) FROM TRAVELINFO";		
	            
	            conn = DBConnection.getConnection();
	            pstm = conn.prepareStatement(quary);
	            rs = pstm.executeQuery();
	           
	            while(rs.next()){
	                int num2 = rs.getInt(1);
	                if(num <num2) {
	                	num=num2;
	                }
	                
	            }
	            System.out.println("version = "+num);
	        } catch (SQLException sqle) {
	            System.out.println("SELECT문에서 예외 발생");
	            sqle.printStackTrace();
	            
	        } catch(Exception e) {
	        	return "error : " + conn.toString() + "\n" + e.getMessage();
	        }
		 	finally{
	            // DB 연결을 종료한다.
	            try{
	                if ( rs != null ){rs.close();}   
	                if ( pstm != null ){pstm.close();}   
	                if ( conn != null ){conn.close(); }
	            }catch(Exception e){
	                throw new RuntimeException(e.getMessage());
	            }
	            
	        }		
		 JSONObject jsonObj = new JSONObject();
		 jsonObj.put("VERSION", num);

		String sendData = jsonObj.toJSONString();
		
        System.out.println(sendData);
        return sendData;
    }
		

	@RequestMapping(value="login.do",produces="application/json;charset=utf-8")
    public @ResponseBody String login(HttpServletRequest request){
        
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String sendData = "false";
		//받을 데이터
		//facebook token, push key
		System.out.println("login");	//페이스북 토큰
		System.out.println(request.getParameter("FB_TOKEN"));	//페이스북 토큰
		System.out.println(request.getParameter("FCM_TOKEN"));	//fcm 푸쉬키
		System.out.println(request.getParameter("ID"));	//email
		
		//DB처리 구문		
		 try {
	            // SQL 문장을 만들고 만약 문장이 질의어(SELECT문)라면
	            // 그 결과를 담을 ResulSet 객체를 준비한 후 실행시킨다.
	            String quary = "SELECT * FROM MEMBERINFO WHERE MEM_ID='"+request.getParameter("ID")+"'";		//회원 유무 확인
	            
	            System.out.println("quary"+quary);
	            
	            conn = DBConnection.getConnection();
	            pstm = conn.prepareStatement(quary);
	            rs = pstm.executeQuery();
	            
	            while(rs.next()){
	                //int empno = rs.getInt("empno"); 숫자 대신 컬럼 이름을 적어도 된다.
	                String str1 = rs.getString(1);
	                String str2 = rs.getString(12);
	                String result = str1+str2;   
		            sendData = "true"+str2;   
	                          
	            }
	            
	            //update
	            quary = "UPDATE MEMBERINFO SET MEM_PUSHKEY='"+request.getParameter("FCM_TOKEN")+"' WHERE MEM_ID='"+request.getParameter("ID")+"'";
	            pstm = conn.prepareStatement(quary);
	            pstm.executeUpdate();
	            quary = "UPDATE MEMBERINFO SET ACCESS_TOKEN='"+request.getParameter("FB_TOKEN")+"' WHERE MEM_ID='"+request.getParameter("ID")+"'";
	            pstm = conn.prepareStatement(quary);
	            pstm.executeUpdate();
	            
	            
	        } catch (SQLException sqle) {
	            sqle.printStackTrace();
	            //sendData = "false";
	            
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
		System.out.println("sendData = "+sendData);	
        return sendData;
    }
	
	@RequestMapping(value="join.do",produces="application/json;charset=utf-8")
    public @ResponseBody String join(HttpServletRequest request){
        
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		String sendData ="false";		
		
		System.out.println("join");
		
		System.out.println(request.getParameter("NAME"));
		System.out.println(request.getParameter("BIRTHDAY"));		
		System.out.println(request.getParameter("GENDER"));
		System.out.println(request.getParameter("ID"));
		System.out.println(request.getParameter("NATION"));
		System.out.println(request.getParameter("LANGUAGE"));
		System.out.println(request.getParameter("SELECT"));	//가이드, 관광객
		System.out.println(request.getParameter("INTRODUCE"));	//소개글
		
		int select = 0;
		if(request.getParameter("SELECT").equals("관광객")) {
			select=1;
		}else if(request.getParameter("SELECT").equals("가이드")) {
			select=2;
		}else if(request.getParameter("SELECT").equals("TOURIST")) {
			select=1;
		}else if(request.getParameter("SELECT").equals("GUIDE")) {
			select=2;
		}else {
			select=3;
		}
		
		
		//DB처리 구문
		
		 try {
	            // SQL 문장을 만들고 만약 문장이 질의어(SELECT문)라면
	            // 그 결과를 담을 ResulSet 객체를 준비한 후 실행시킨다.
			 
			 if(!request.getParameter("NAME").equals("") &&
				!request.getParameter("BIRTHDAY").equals("")&&
				!request.getParameter("GENDER").equals("")&&
				!request.getParameter("NATION").equals("")&&
				!request.getParameter("ID").equals("")&&
				!request.getParameter("LANGUAGE").equals("")&&
				!request.getParameter("SELECT").equals("")&&
				!request.getParameter("INTRODUCE").equals("") ) {
				 
				String birthday = request.getParameter("BIRTHDAY");
				String month = birthday.substring(0,2);
				String day = birthday.substring(3,5);
				String year = birthday.substring(6,10);
				birthday = year+"-"+month+"-"+day;
				
				System.out.println("birthday : "+birthday);
			 
	            String quary = "INSERT INTO MEMBERINFO VALUES('"+request.getParameter("ID")+"', "
	            		+ "'"+request.getParameter("NAME")+"', "
	            		+ " STR_TO_DATE('"+birthday+"', '%Y-%m-%d %H:%i:%s'), "
	            		+ "'"+request.getParameter("GENDER")+"', "
	            		+ "'"+request.getParameter("ID")+"', "
	            		+ "'01012345678', "
	            		+ "'"+request.getParameter("NATION")+"', "
	            		+ "'"+request.getParameter("LANGUAGE")+"', "
	            		+ "'-', "
	            		+ "'-' , "
	            		+ "'disabled', "
	            		+ ""+select+","
	            		+ " '"+request.getParameter("INTRODUCE")+"' )";

	    		System.out.println(quary);
	            conn = DBConnection.getConnection();
	            pstm = conn.prepareStatement(quary);
	            pstm.executeUpdate();
	            sendData ="true";
			}else {
				System.out.println("null값");
				sendData ="false";
			}
	            
	    } catch (SQLException sqle) {
	            System.out.println("insert문에서 예외 발생");
	            sqle.printStackTrace();
	            sendData ="false";
	            
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
		
		System.out.println("sendData = "+sendData);
        return sendData;
    }
	
	
	@RequestMapping(value="join_update.do",produces="application/json;charset=utf-8")
    public @ResponseBody String join_update(HttpServletRequest request){
        
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		String sendData ="false";		
		
		System.out.println("join_update");		

		System.out.println(request.getParameter("NAME"));
		System.out.println(request.getParameter("BIRTHDAY"));		
		System.out.println(request.getParameter("GENDER"));
		System.out.println(request.getParameter("ID"));
		System.out.println(request.getParameter("NATION"));
		System.out.println(request.getParameter("LANGUAGE"));
		System.out.println(request.getParameter("SELECT"));	//가이드, 관광객
		System.out.println(request.getParameter("INTRODUCE"));	//소개글
		
		int select = 0;
		if(request.getParameter("SELECT").equals("관광객")) {
			select=1;
		}else if(request.getParameter("SELECT").equals("가이드")) {
			select=2;
		}else if(request.getParameter("SELECT").equals("TOURIST")) {
			select=1;
		}else if(request.getParameter("SELECT").equals("GUIDE")) {
			select=2;
		}else {
			select=3;
		}
				
		//DB처리 구문
		
		 try {
			 
			 if(!request.getParameter("NAME").equals("") &&
				!request.getParameter("BIRTHDAY").equals("")&&
				!request.getParameter("GENDER").equals("")&&
				!request.getParameter("NATION").equals("")&&
				!request.getParameter("ID").equals("")&&
				!request.getParameter("LANGUAGE").equals("")&&
				!request.getParameter("SELECT").equals("")&&
				!request.getParameter("INTRODUCE").equals("") ) {
	 
	            // SQL 문장을 만들고 만약 문장이 질의어(SELECT문)라면
	            // 그 결과를 담을 ResulSet 객체를 준비한 후 실행시킨다.
				 
				String birthday = request.getParameter("BIRTHDAY");
				String month = birthday.substring(0,2);
				String day = birthday.substring(3,5);
				String year = birthday.substring(6,10);
				birthday = year+"-"+month+"-"+day;
				 
				 
	            String quary = "UPDATE MEMBERINFO SET MEM_ID='"+request.getParameter("ID")+"' , "
	            		+ " MEM_NAME = '"+request.getParameter("NAME")+"' , "	   
	            		+ " STR_TO_DATE('"+birthday+"', '%Y-%m-%d %H:%i:%s'), "	   
	    	    	    + " MEM_GENDER='"+request.getParameter("GENDER")+"' , "	   
	    	    	    + " MEM_EMAIL='"+request.getParameter("ID")+"' , "	   
						+ " MEM_NATIONALITY='"+request.getParameter("NATION")+"' , "	   
						+ " MEM_LANGUAGE='"+request.getParameter("LANGUAGE")+"' , "	  
						+ " USER_CNUM="+select+" ,"
						+ " MEM_PR='"+request.getParameter("INTRODUCE")+"'"
	    	    	    
	            		+ " WHERE MEM_ID='"+request.getParameter("ID")+"' ";
	            

	    		System.out.println(quary);
	            conn = DBConnection.getConnection();
	            pstm = conn.prepareStatement(quary);
	            pstm.executeUpdate();
	            sendData ="true";
			 }
	            
	        } catch (SQLException sqle) {
	            System.out.println("update문에서 예외 발생");
	            sqle.printStackTrace();
	            sendData ="false";
	            
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

        System.out.println("join update sendData = "+sendData);
		
        return sendData;
    }
	
	
		
	
	@RequestMapping(value="all_guides.do",produces="application/json;charset=utf-8")
    public @ResponseBody String guide(HttpServletRequest request){	//신청
        
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}	
		
		System.out.println("guide"); // 
		//DB처리 구문
		
		String sendData="";
		 try {
	            // SQL 문장을 만들고 만약 문장이 질의어(SELECT문)라면
	            // 그 결과를 담을 ResulSet 객체를 준비한 후 실행시킨다.
	            String quary = "SELECT MEM_NAME, MEM_ID, MEM_GENDER, MEM_LANGUAGE, MEM_LINK, MEM_DOB FROM MEMBERINFO WHERE USER_CNUM=2";

	    		System.out.println(quary);
	            conn = DBConnection.getConnection();
	            pstm = conn.prepareStatement(quary);
	            rs = pstm.executeQuery();
	            JSONArray jArr = new JSONArray();
		           
	            while(rs.next()){
		            JSONObject jsonObj = new JSONObject();
	                String MEM_NAME = rs.getString(1);
	                String MEM_EMAIL = rs.getString(2);
	                String MEM_GENDER = rs.getString(3);
	                String MEM_LANGUAGE = rs.getString(4);
	                String MEM_LINK = rs.getString(5);
	            	String MEM_BIRTHDAY = rs.getString(6);
	                jsonObj.put("MEM_NAME", MEM_NAME);
	                jsonObj.put("MEM_ID", MEM_EMAIL);
	                jsonObj.put("MEM_GENDER", MEM_GENDER);
	                jsonObj.put("MEM_BIRTHDAY", MEM_BIRTHDAY);
	                jsonObj.put("MEM_LANGUAGE", MEM_LANGUAGE);
	                jsonObj.put("MEM_LINK", MEM_LINK);
	                jArr.add(jsonObj);
		   		 	//System.out.println(jArr);
	            }
	    		//보낼 데이터 처리
	            
	   		 	sendData = jArr.toJSONString();
	   		 	System.out.println(sendData);
	        } catch (SQLException sqle) {
	            System.out.println("insert문에서 예외 발생");
	            sqle.printStackTrace();
	            sendData ="false";
	            
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
		
		
		 
        return sendData;
    }
	
	@RequestMapping(value="request.do",produces="application/json;charset=utf-8")
    public @ResponseBody String request(HttpServletRequest request){	//신청
        
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}	
		
		System.out.println("신청하기"); // 
		System.out.println(request.getParameter("tourist"));	
		System.out.println(request.getParameter("guide"));
		System.out.println(request.getParameter("date"));
		System.out.println(request.getParameter("area"));
		System.out.println(request.getParameter("msg"));
		
		
		String birthday = request.getParameter("date");
		String month = birthday.substring(0,2);
		String day = birthday.substring(3,5);
		String year = birthday.substring(6,10);
		birthday = year+"-"+month+"-"+day;
		
		
		
		//DB처리 구문
		
		String sendData="";
		boolean flag=false;
		 try {
	            // SQL 문장을 만들고 만약 문장이 질의어(SELECT문)라면
	            // 그 결과를 담을 ResulSet 객체를 준비한 후 실행시킨다.
			 	// 이미 신청했는지 여부 확인.
	            String quary = "SELECT TOURIST_ID, GUIDE_ID FROM CHECKCONNECT WHERE TOURIST_ID='"+request.getParameter("tourist")+"' and GUIDE_ID='"+request.getParameter("guide")+"'";

	    		System.out.println(quary);
	            conn = DBConnection.getConnection();
	            pstm = conn.prepareStatement(quary);
	            rs = pstm.executeQuery();
		           
	            while(rs.next()){
	                String TOURIST_ID = rs.getString(1);
	                String GUIDE_ID = rs.getString(2);
	                flag=true;							//신청여부가 이미 존재
	            }
	    		//보낼 데이터 처리	            	   	
	   		 	
	   		 	if(flag==true) {
	   		 		//신청여부 이미 존재
	   		 		sendData += flag;
	   		 		System.out.println("신청여부 "+sendData);
	   		 	}else {
	   		 		//가이드 신청
	   		 		if(!request.getParameter("guide").equals(null) && !request.getParameter("tourist").equals(null) && !request.getParameter("guide").equals("null") && !request.getParameter("tourist").equals("null")) {
	   		 			
		   		 		quary = "INSERT INTO CHECKCONNECT VALUES('"+request.getParameter("tourist")+"', '"+request.getParameter("guide")+"', 0, "
		   		 			+ " STR_TO_DATE('"+birthday+"', '%Y-%m-%d %H:%i:%s'), "+request.getParameter("area")+"', '"+request.getParameter("msg")+"')";
		   		 		System.out.println(quary);
		   		 		pstm = conn.prepareStatement(quary);
		   		 		pstm.executeUpdate();
		   		 		
		   		 		flag=true;
		   		 		sendData += flag;
		   		 		System.out.println("신청여부 "+sendData);
	   		 		}else {
		   		 		System.out.println("아이디 null");
		   		 		flag =false;
		   		 		sendData = ""+flag;
	   		 		}

	   		 	}
	   		 	
	   		 	
	        } catch (SQLException sqle) {
	            sqle.printStackTrace();
	            flag =false;
	            sendData += flag;
   		 		System.out.println("신청여부 "+flag);
	            
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
		 
         return sendData;
    }
	
	
	@RequestMapping(value="selected_guides.do",produces="application/json;charset=utf-8")
    public @ResponseBody String selected_guides(HttpServletRequest request){	//신청
        
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}	
		
		System.out.println("관광객이 신청한 가이드 목록"); // 
		System.out.println(request.getParameter("TOURIST_ID"));		
		
		JSONArray jArr = new JSONArray();
		//DB처리 구문
		
		String sendData="";
		 try {
			    
	            String quary = "SELECT GUIDE_ID, CONNECTED FROM CHECKCONNECT WHERE TOURIST_ID='"+request.getParameter("TOURIST_ID")+"' ";

	    		System.out.println(quary);
	            conn = DBConnection.getConnection();
	            pstm = conn.prepareStatement(quary);
	            rs = pstm.executeQuery();
		           
	            while(rs.next()){
	            	
	                String GUIDE_ID = rs.getString(1);
		            int CONNECTED = rs.getInt(2);
	                
	                quary = "SELECT * FROM MEMBERINFO WHERE MEM_ID='"+GUIDE_ID+"' ";	
	                System.out.println(quary);
	                
		            pstm = conn.prepareStatement(quary);
		            ResultSet rs1 = pstm.executeQuery();
		            while(rs1.next()){
		            	JSONObject jsonObj = new JSONObject();
		            	String MEM_ID = rs1.getString(1);
		            	String MEM_NAME = rs1.getString(2);
		            	String MEM_GENDER = rs1.getString(4); 
		            	String MEM_BIRTHDAY = rs1.getString(3);
		            	String MEM_LANGUAGE = rs1.getString(8);	
		            	String MEM_LINK = rs1.getString(11);		

		                jsonObj.put("MEM_NAME", MEM_NAME);
		                jsonObj.put("MEM_GENDER", MEM_GENDER);
		                jsonObj.put("MEM_BIRTHDAY", MEM_BIRTHDAY);
		                jsonObj.put("MEM_ID", MEM_ID);
		                jsonObj.put("MEM_LANGUAGE", MEM_LANGUAGE);
		                jsonObj.put("CONNECTED", CONNECTED);	
		                jArr.add(jsonObj);
		            	
		            }	                
	            }

	   		 	sendData = jArr.toJSONString();
	   		 	
	   		 	
	        } catch (SQLException sqle) {
	            //sqle.printStackTrace();
	            sendData = "false";
   		 		System.out.println("신청여부 "+sendData);
	            
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
 		System.out.println(sendData);
		 
         return sendData;
    }
	
	
	
	@RequestMapping(value="tourist.do",produces="application/json;charset=utf-8")
    public @ResponseBody String tourist(HttpServletRequest request){	//신청
        
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}	
		
		System.out.println("가이드한테 신청한 관광객 목록"); // 
		System.out.println(request.getParameter("GUIDE_ID"));	
		
		JSONArray jArr = new JSONArray();
		//DB처리 구문
		
		String sendData="";
		 try {
		        String quary = "SELECT TOURIST_ID, CONNECTED FROM CHECKCONNECT WHERE GUIDE_ID='"+request.getParameter("GUIDE_ID")+"'";
		
				System.out.println(quary);
		        conn = DBConnection.getConnection();
		        pstm = conn.prepareStatement(quary);
		        rs = pstm.executeQuery();
		           
		        while(rs.next()){

		            String TOURIST_ID = rs.getString(1);
		            int CONNECTED = rs.getInt(2);
		            
		            
		            quary = "SELECT * FROM MEMBERINFO WHERE MEM_ID='"+TOURIST_ID+"' ";	
		            System.out.println(quary);
		            
		            pstm = conn.prepareStatement(quary);
		            ResultSet rs1 = pstm.executeQuery();
		            while(rs1.next()){
		            	JSONObject jsonObj = new JSONObject();
		            	String MEM_ID = rs1.getString(1);
		            	String MEM_NAME = rs1.getString(2);
		            	String MEM_GENDER = rs1.getString(4); 
		            	String MEM_BIRTHDAY = rs1.getString(3); 
		            	String MEM_LANGUAGE = rs1.getString(8);	
		            	String MEM_LINK = rs1.getString(11);		
		
		                jsonObj.put("MEM_NAME", MEM_NAME);
		                jsonObj.put("MEM_GENDER", MEM_GENDER);
		                jsonObj.put("MEM_BIRTHDAY", MEM_BIRTHDAY);
		                jsonObj.put("MEM_ID", MEM_ID);
		                jsonObj.put("MEM_LANGUAGE", MEM_LANGUAGE);
		                jsonObj.put("CONNECTED", CONNECTED);	//0: wait	1:yes	2:no
		                jArr.add(jsonObj);
		            	
		            }
		            
		        }
		
			 	sendData = jArr.toJSONString();
		 		System.out.println(sendData);
			 	
			 	
		    } catch (SQLException sqle) {
		        //sqle.printStackTrace();
		        sendData = "false";
		 		System.out.println("신청여부 "+sendData);
		        
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
	 	
		 System.out.println(sendData);
		 
         return sendData;
    }
	
	
	@RequestMapping(value="request_detail.do",produces="application/json;charset=utf-8")
    public @ResponseBody String tourist_request(HttpServletRequest request){	//신청
        
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}	
		
		System.out.println("신청한 관광객 상세정보"); // 
		System.out.println(request.getParameter("TOURIST_ID"));	//관광객 이메일
		System.out.println(request.getParameter("GUIDE_ID"));	//가이드 이메일
		
		//DB처리 구문
		
		String sendData="";
		 try {
	            String quary = "SELECT * FROM CHECKCONNECT WHERE TOURIST_ID='"+request.getParameter("TOURIST_ID")+"' AND GUIDE_ID='"+request.getParameter("GUIDE_ID")+"' ";

	    		System.out.println(quary);
	            conn = DBConnection.getConnection();
	            pstm = conn.prepareStatement(quary);
	            rs = pstm.executeQuery();
            	JSONObject jsonObj = new JSONObject();
		           
	            while(rs.next()){	            	

	                Date CONNECTDATE = rs.getDate(4);	
	                String AREA = rs.getString(5);
	                String MSG = rs.getString(6);	                

	                jsonObj.put("CONNECTDATE", CONNECTDATE);
	                jsonObj.put("AREA", AREA);
	                jsonObj.put("MESSAGE", MSG);
	                  
	                
	            }

	   		 	sendData = jsonObj.toJSONString();
   		 		System.out.println(sendData);	   		 	
	   		 	
	        } catch (SQLException sqle) {
	            //sqle.printStackTrace();
	            sendData = "false";
   		 		System.out.println("신청여부 "+sendData);
	            
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
		 
         return sendData;
    }
	
	
	@RequestMapping(value="request_accept.do",produces="application/json;charset=utf-8")
    public @ResponseBody String request_accept(HttpServletRequest request){	//신청
        
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}	
		
		System.out.println("신청한 관광객 상세정보"); // 
		System.out.println(request.getParameter("guide"));		//가이드 이메일
		System.out.println(request.getParameter("tourist"));	//관광객 이메일
		System.out.println(request.getParameter("accept"));		//수락 거절
		
		//DB처리 구문
		
		String sendData="false";
		 try {
			 	String quary = "";
			 	//CONNECTED 0=wait 1=ok 2=no
			 	if(request.getParameter("accept").equals("yes")) {
			 		quary = "UPDATE CHECKCONNECT SET CONNECTED = 1 WHERE TOURIST_ID='"+request.getParameter("tourist")+"' AND GUIDE_ID='"+request.getParameter("guide")+"'";
			 	}else if(request.getParameter("accept").equals("no")) {
			 		quary = "UPDATE CHECKCONNECT SET CONNECTED = 2 WHERE TOURIST_ID='"+request.getParameter("tourist")+"' AND GUIDE_ID='"+request.getParameter("guide")+"'";
			 	}else {
			 		System.out.println("잘못된 accept");
			 	}

	    		System.out.println(quary);
	            conn = DBConnection.getConnection();
	            pstm = conn.prepareStatement(quary);
	            pstm.executeUpdate();
		           
	            //memberinfo 데이터 변경
	            if(request.getParameter("accept").equals("yes")) {
	            	//가이드와 관광객 연결상태
	            	quary = "UPDATE MEMBERINFO SET MEM_LINK = 'abled' WHERE MEM_ID='"+request.getParameter("tourist")+"' ";
		            pstm = conn.prepareStatement(quary);
		            pstm.executeUpdate();
		            
		            quary = "UPDATE MEMBERINFO SET MEM_LINK = 'abled' WHERE MEM_ID='"+request.getParameter("guide")+"' ";
		            pstm = conn.prepareStatement(quary);
		            pstm.executeUpdate();
		            
			 	}else if(request.getParameter("accept").equals("no")) {
			 		//변동 없음
			 	}else {
			 		System.out.println("잘못된 accept");
			 	}

	   		 	sendData = "true";
   		 		System.out.println(sendData);
	   		 	
	   		 	
	        } catch (SQLException sqle) {
	            //sqle.printStackTrace();
	            sendData = "false";
   		 		System.out.println("신청여부 "+sendData);
	            
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
		 
         return sendData;
    }	
	
	@RequestMapping(value="member_detail.do",produces="application/json;charset=utf-8")
    public @ResponseBody String member_detail(HttpServletRequest request){	//신청
        
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}	
		
		System.out.println("가이드 상세정보"); // 
		System.out.println(request.getParameter("MEMBER_ID"));	//가이드 이메일
		
		//DB처리 구문
		
		String sendData="";
		 try {
	            String quary = "SELECT * FROM MEMBERINFO WHERE MEM_ID='"+request.getParameter("MEMBER_ID")+"'";

	    		System.out.println(quary);
	            conn = DBConnection.getConnection();
	            pstm = conn.prepareStatement(quary);
	            rs = pstm.executeQuery();
            	JSONObject jsonObj = new JSONObject();
		           
	            while(rs.next()){	            	

	            	String MEM_NAME  = rs.getString(2);	
	                String MEM_GENDER  = rs.getString(4);
	                String MEM_BIRTHDAY  = rs.getString(3);	  
	                String MEM_NATION  = rs.getString(7);	     
	                String MEM_LANGUAGE  = rs.getString(8);	       
	                String MEM_PR   = rs.getString(13);	          
	                String USER_CNUM   = rs.getString(12);	            

	                jsonObj.put("NAME", MEM_NAME);
	                jsonObj.put("GENDER", MEM_GENDER);
	                jsonObj.put("BIRTHDAY", MEM_BIRTHDAY);
	                jsonObj.put("NATION", MEM_NATION);
	                jsonObj.put("LANGUAGE", MEM_LANGUAGE);	  
	                jsonObj.put("INTRODUCE", MEM_PR);	      
	                jsonObj.put("SELECT", USER_CNUM);	                  
	                
	            }

	   		 	sendData = jsonObj.toJSONString();
   		 		System.out.println(sendData);	   		 	
	   		 	
	        } catch (SQLException sqle) {
	            //sqle.printStackTrace();
	            sendData = "false";
   		 		System.out.println("신청여부 "+sendData);
	            
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
		 
         return sendData;
    }
	
	@RequestMapping(value="leave.do",produces="application/json;charset=utf-8")
    public @ResponseBody String leave(HttpServletRequest request){
        
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String sendData = "false";
		//받을 데이터
		//facebook token, push key
		System.out.println("leave");	//페이스북 토큰
		System.out.println(request.getParameter("ID"));	//id
		
		//DB처리 구문		
		 try {
	            // SQL 문장을 만들고 만약 문장이 질의어(SELECT문)라면
	            // 그 결과를 담을 ResulSet 객체를 준비한 후 실행시킨다.
	            String quary = "DELETE MEMBERINFO WHERE MEM_ID='"+request.getParameter("ID")+"'";		//회원 유무 확인
	            
	            System.out.println("quary"+quary);
	            
	            conn = DBConnection.getConnection();
	            pstm = conn.prepareStatement(quary);
	            int result = pstm.executeUpdate();
	            
	            if(result==0) {
	            	 System.out.println("삭제 결과 없음");  	           
	            	
	            }else {
	            	sendData="true";
	            }
	            
	            quary = "DELETE CHECKCONNECT WHERE TOURIST_ID='"+request.getParameter("ID")+"' OR GUIDE_ID = '"+request.getParameter("ID")+"' ";
	            pstm = conn.prepareStatement(quary);
	            pstm.executeUpdate();
	            
	        } catch (SQLException sqle) {
	            sqle.printStackTrace();
	            //sendData = "false";
	            
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
		System.out.println("sendData = "+sendData);	
        return sendData;
    }

	
}
