<!DOCTYPE html>
<%@page import="java.io.InputStream"%>
<%@page import="java.util.Properties"%>
<%@page import="java.io.FileNotFoundException"%>
<%@page import="java.io.File"%>
<%@page import="java.util.List"%>
<%@page import="com.viksitpro.core.dao.entities.*"%>

<% Course course = (new CourseDAO()).findById(Integer.parseInt(request.getParameter("course_id"))) ;%>
<html lang="en">
<head>
	<meta charset="utf-8">
	<title>Table of Contents Style Navigation</title>
	<link rel="stylesheet" href="http://5thirtyone.com/sandbox/share/toc/main.css" type="text/css" media="screen" />
</head>
<body>
<div id="wrapper">
	<h1>List of Lessons for <%=course.getCourseName() %></h1>
	<ul id="toc">
		<% 
		System.out.println("--index3.jsp-->>>>");
		for(Module module: course.getModules() ) { 
			System.out.println("--module-->>>>"+module.getModuleName());
		for(Cmsession cmsession:  module.getCmsessions()) {
			System.out.println("--cmsession-->>>>"+cmsession.getTitle());
			for(Lesson lesson : cmsession.getLessons()) {
				System.out.println("--lesson-->>>>"+lesson.getTitle());
	
				String path ="";
				try {
					Properties properties = new Properties();
					String propertyFileName = "app.properties";
					InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertyFileName);
					if (inputStream != null) {
						properties.load(inputStream);
					} else {
						throw new FileNotFoundException("property file '" + propertyFileName + "' not found in the classpath");
					}
					path = properties.getProperty("mediaLessonPath");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println("---->>>>"+path);
		File f = new File(path+"/"+lesson.getId()+".xml");
		if(f.exists() && !f.isDirectory()) {
		
			%>
			<li><span><%=lesson.getTitle() %></span> <a target="_blank" href="/magic.jsp?lesson_id=<%=lesson.getId() %>"><%=lesson.getId() %></a></li>
		<% } }		}
		} %>
	</ul>
</div>
</body>
</html>
