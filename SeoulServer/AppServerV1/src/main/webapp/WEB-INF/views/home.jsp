<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="org.json.simple.*"%>

<%

	String recvMessage = request.getParameter("msg");

	JSONObject jsonMain = new JSONObject();
	JSONArray jArray = new JSONArray();
	
	JSONObject jObject = new JSONObject();

	jObject.put("msg1", recvMessage);
	jObject.put("msg2", "메시지2!");
	jObject.put("msg3", "3번째 메시지!");
	
	jArray.add(0, jObject);

	jsonMain.put("List", jArray);
	
	out.println(jsonMain.toJSONString());
	
%>