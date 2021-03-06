<%@page import="com.viksitpro.oldCMS.XML.Generators.LessonServices"%>
<%@page import="slides.SlideUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><!doctype html>
<html lang="en">
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/slide_assets/";
	int lesson_id =6020;
	SlideUtils data = new SlideUtils();
	LessonServices lessonServices = new LessonServices();
	if(request.getParameter("lesson_id")!=null){
		 lesson_id = Integer.parseInt(request.getParameter("lesson_id"));
	}
	
%>
<head>
<meta charset="utf-8">

<title>reveal.js - The HTML Presentation Framework</title>

<meta name="description" content="A framework for easily creating beautiful presentations using HTML">
<meta name="author" content="Hakim El Hattab">

<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black-translucent">

<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">

<link rel="stylesheet" href="<%=basePath%>css/reveal.css">
<link rel="stylesheet" href="<%=basePath%>css/theme/black.css" id="theme">

<!-- Theme used for syntax highlighting of code -->
<link rel="stylesheet" href="<%=basePath%>lib/css/zenburn.css">

<!-- Printing and PDF exports -->
<script>
	var link = document.createElement('link');
	link.rel = 'stylesheet';
	link.type = 'text/css';
	link.href = window.location.search.match(/print-pdf/gi) ? '<%=basePath%>css/print/pdf.css'
			: '<%=basePath%>css/print/paper.css';
	document.getElementsByTagName('head')[0].appendChild(link);
</script>
<% //fetch All slides froma given ppt %>
<!--[if lt IE 9]>
		<script src="lib/js/html5shiv.js"></script>
		<![endif]-->
</head>

<body>

	<div class="reveal">

		<div class="slides">
			
			<%=lessonServices.lessonHTMLfromLessonXML(lesson_id) %>
          
		</div>

	</div>

	<script src="<%=basePath%>lib/js/head.min.js"></script>
	<script src="<%=basePath%>js/reveal.js"></script><script src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js" type="text/javascript" charset="utf-8"></script>
	
<script src="<%=basePath%>js/jquery.jeditable.js" type="text/javascript" charset="utf-8"></script>

	<script>
    <%-- $('.edit').editable('<%=basePath%>../edit_ppt?lesson_id=<%=lesson_id%>&',{
    	
    	 submit : 'OK',
    	 indicator : 'Saving...',
         tooltip   : 'Click to edit...'
    }); --%>
 
     $('.edit').editable(function(value, settings) {
       
        console.log('slide_id--'+$(this).attr("data-slide_id"));
        console.log('element_type--'+$(this).attr("data-element_type"));
        console.log('lesson_id--'+<%=lesson_id%>);
        console.log(value);
        
        $.post('<%=basePath%>../edit_ppt',
                {
        	slide_id:  $(this).attr("data-slide_id"),
        	element_type: $(this).attr("data-element_type"),
        	lesson_id:<%=lesson_id%>
                },
                function(data,status){
                 value = data;
                });
           
      
        return(value);
     }, {
    	 indicator : 'Saving...',
         submit  : 'OK',
    }); 
    
    $('.edit').editable('<%=basePath%>../edit_ppt', {
    	indicator : 'Saving...',
        submit   : 'OK',
        tooltip   : 'Click to edit...',
        callback : function(value, settings) {
        	 console.log(value);
            return(value);
        }
    }); 
    
    // More info https://github.com/hakimel/reveal.js#configuration
		Reveal.initialize({
			controls : true,
			progress : true,
			history : false,
			center : true,
			autoPlayMedia : false,
			margin: 0,
			width: "90%",
			height: "100%",
			transition : 'slide', // none/fade/slide/convex/concave/zoom
			transitionSpeed: 'slow',
			showSlideNumber: 'all',
			slideNumber: 'c/t',
			// More info https://github.com/hakimel/reveal.js#dependencies
			dependencies : [ {
				src : '<%=basePath%>lib/js/classList.js',
				condition : function() {
					return !document.body.classList;
				}
			}, {
				src : '<%=basePath%>plugin/markdown/marked.js',
				condition : function() {
					return !!document.querySelector('[data-markdown]');
				}
			}, {
				src : '<%=basePath%>plugin/markdown/markdown.js',
				condition : function() {
					return !!document.querySelector('[data-markdown]');
				}
			}, {
				src : '<%=basePath%>plugin/highlight/highlight.js',
				async : true,
				callback : function() {
					hljs.initHighlightingOnLoad();
				}
			}, {
				src : '<%=basePath%>plugin/zoom-js/zoom.js',
				async : true
			}, {
				src : '<%=basePath%>plugin/notes/notes.js',
				async : true
			} ]
		});
	
	
		Reveal.addEventListener( 'ready', function( event ) {
		//	console.log('ready slide chnagd');
			var height = document.getElementsByClassName("slides")[0].style.height;
			var width =	document.getElementsByClassName("slides")[0].style.width;
			
			document.getElementsByClassName("present")[0].style.height = height;
			document.getElementsByClassName("present")[0].style.width = width;
			document.getElementsByClassName("slides")[0].style.display = 'table';
			var x = document.getElementsByClassName("section");
			var i;
			for (i = 0; i < x.length; i++) {
				    x[i].style.height = height;
					x[i].style.width = width;
					var slide_id=x[i].id;
					var HtmlElementSlideHolder =  document.getElementById('slide_'+slide_id);
					var size =HtmlElementSlideHolder.dataset.length;
					var templateName =HtmlElementSlideHolder.dataset.template;
					//console.log(templateName);
					//console.log(size);
					
					
					if(templateName ==='only_title'){
						x[i].style.fontSize = size+'%';
						 x[i].style.top = window.innerHeight/3+'px';;
						 x[i].style.verticalAlign='middle';
						 x[i].style.display='table-cell';
					}else if(templateName ==='only_video'){
						x[i].style.top = null;
					}
					else{
						//x[i].style.top = null;
						x[i].style.top = '5%';
					}
					

			}
			
		} );
		
	
		Reveal.addEventListener( 'slidechanged', function( event ) {
		//	console.log('slide chnagd');
		
			var height = document.getElementsByClassName("slides")[0].style.height;
			var width =	document.getElementsByClassName("slides")[0].style.width;
			

			document.getElementsByClassName("present")[0].style.height = height;
			document.getElementsByClassName("present")[0].style.width = width;
			
		

			var x = document.getElementsByTagName("section");
			var i;
			for (i = 0; i < x.length; i++) {			    
				x[i].style.height = height;
				x[i].style.width = width;
				
				
				var slide_id=x[i].id;
				var HtmlElementSlideHolder =  document.getElementById('slide_'+slide_id);
				var size =HtmlElementSlideHolder.dataset.length;
				var templateName =HtmlElementSlideHolder.dataset.template;
				//console.log(templateName);
				//console.log(size);
				
				
				if(templateName ==='only_title'){
					 x[i].style.fontSize = size+'%';
					 x[i].style.top = window.innerHeight/3+'px';;
					 x[i].style.verticalAlign='middle';
					 x[i].style.display='table-cell';
				}else if(templateName ==='only_video'){
					x[i].style.top = null;
				}
				else{
				//	x[i].style.top = null;
					x[i].style.top = '5%';
				}
				
				
			}

		});
	</script>

</body>
</html>
