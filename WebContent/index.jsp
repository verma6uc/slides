<!DOCTYPE html>
<%@page import="java.util.List"%>
<%@page import="com.viksitpro.core.dao.entities.*"%>
<html lang="en">
<head>
	<meta charset="utf-8">
	<title>Table of Contents Style Navigation</title>
	<link rel="stylesheet" href="http://5thirtyone.com/sandbox/share/toc/main.css" type="text/css" media="screen" />
</head>
<body>
<div id="wrapper">
	<h1>List of courses</h1>
	<ul id="toc">
		<% 
		List<Course> courses = (new CourseDAO()).findAll();
		System.out.println("---index.jsp>>>>");
		for(Course course: courses ) { %>
		<li><span><%=course.getCourseName() %></span> <a target="_blank"  href="/index3.jsp?course_id=<%=course.getId() %>"><%=course.getId() %></a></li>
		<% } %>
	</ul>
</div>
</body>
</html>
