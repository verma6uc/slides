package com.viksitpro.oldCMS.XML.Generators;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;
import java.util.Random;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.istarindia.cms.lessons.CMSLesson;
import com.istarindia.cms.lessons.CMSSlide;

public class LessonServices {
	
	public LessonServices() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String lessonHTMLfromLessonXML(int lessonID) {
		StringBuffer stringBuffer = new StringBuffer();
		////////
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
		///////
	//	String path = "C:/root/lessonXMLs/";
		path += "" + lessonID + ".xml";
		System.out.println("-------------->>>>>>>>>>>>>>>>>>>>>>"+path);
		File file = new File(path);  
        
		try{
			JAXBContext jaxbcontext = JAXBContext.newInstance(CMSLesson.class);
			Unmarshaller unmarshaller = jaxbcontext.createUnmarshaller();
			CMSLesson cmsLesson = (CMSLesson) unmarshaller.unmarshal(file);
			for(CMSSlide cmsSlide : cmsLesson.getSlides()){
				System.err.println(cmsSlide.getId());
				//////////////////////////////////////////////////////////
				String ext = "_desktop.vm";
				System.out.println((String) cmsSlide.getTemplateName()+""+cmsSlide.getId());
				String templateVMFileName = cmsSlide.getTemplateName() + ext;
				if (cmsSlide.getTemplateName().equalsIgnoreCase("ONLY_TITLE_LIST")) {
					templateVMFileName = cmsSlide.getList().getList_type() + "___" + cmsSlide.getTemplateName() + ext;
				}
				VelocityEngine ve = new VelocityEngine();
				ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
				ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
				ve.init();
				VelocityContext context = new VelocityContext();
				int cnt = 1;
				///fade/slide/convex/concave/zoom
				String[] transitions = {"fade","slide","convex","concave","zoom"};
				int rand = (new Random()).nextInt(5);
				
				String bg_image = null;
				String type="cover";
				if(cmsSlide.getImage_BG()!=null){
					if(cmsSlide.getImage_BG().contains(".png")){
						bg_image = cmsSlide.getImage_BG().replaceAll(".png","_desktop.png");
					}
					if(cmsSlide.getImage_BG().contains(".gif")){
						bg_image = cmsSlide.getImage_BG();
						type = "contain";
						
					}
					 
				}
				
				String header = "id='"+cmsSlide.getId()+"' data-background-transition='"+transitions[rand]+"' data-background-color='"+cmsSlide.getBackground()+"' data-background-image='"+bg_image+"' data-background-size='"+type+"'";
				if(cmsSlide.getBackground().equalsIgnoreCase("#000000")) {
							
					header = "id='"+cmsSlide.getId()+"' data-background-transition='"+transitions[rand]+"'   data-background-image='"+bg_image+"' data-background-color='#ffffff' data-background-size='"+type+"'";
				}
				if(cmsSlide.getBackground().equalsIgnoreCase("null")) {
					header = "id='"+cmsSlide.getId()+"' data-background-transition='"+transitions[rand]+"'   data-background-image='"+bg_image+"' data-background-color='#ffffff' data-background-size='"+type+"'";
				}
				if(cmsSlide.getBackground().equalsIgnoreCase("none")) {
					header = "id='"+cmsSlide.getId()+"' data-background-transition='"+transitions[rand]+"'   data-background-image='"+bg_image+"' data-background-color='#ffffff' data-background-size='"+type+"'";
				}					
				
			
				context.put("header", header);
				context.put("slide", cmsSlide);
				Template t = ve.getTemplate(templateVMFileName);
				StringWriter writer = new StringWriter();
				t.merge(context, writer);
				String data1 = writer.toString();
				data1 = data1.replaceAll("<p></p>", "");
				data1 = data1.replaceAll("/content/media_upload\\?getfile=", "/video/");
				

				if(!data1.contains("<table")) {
					
					data1 = data1.replaceAll("<p>", "<p class='fragment fade-up visible' >");
				}
					
					data1 = data1.replaceAll("<b>", "");
					//data1 = data1.replaceAll("<ol>", "<ol class='fragment fade-up' >");
			
					
					data1 = data1.replaceAll("width:500px", "");
			

			
				
				
				//Fix image urls http://192.168.0.125:8080/video/salesSession1_4.png
				if(templateVMFileName.contains("ONLY_TITLE_PARAGRAPH_cells_fragemented")) {
					data1 = data1.replaceAll("<span style=\"font-size:22px\">", "<span class='fragment fade-up ' style=\"font-size:22px\">");
	                
					data1 = data1.replaceAll("<td", "<td class='fragment fade-up visible' ");
					//data1 = data1.replaceAll("<p>", "<p class='fragment fade-up' >");
				}
				
	             if(templateVMFileName.contains("ONLY_PARAGRAPH_TITLE")) {
					
	            	data1 = data1.replaceAll("<li>", "<li class='fragment fade-up' >");
					data1 = data1.replaceAll("<h1>", "<h1 class='fragment fade-up' >");
				}
	                   if(templateVMFileName.contains("ONLY_TITLE_PARAGRAPH")) {
					
	                	   data1 = data1.replaceAll("<li>", "<li class='fragment fade-up' >");
					
				}
	                   if(templateVMFileName.contains("ONLY_PARAGRAPH_IMAGE")) {
	       				
	                	   data1 = data1.replaceAll("<li>", "<li class='fragment fade-up' >");
	              }
				stringBuffer.append(data1);

				//////////////////////////////////////////////////////////
			}
		} catch (JAXBException e) {
			// TODO: handle exception
		}
		return stringBuffer.toString();
	}
}
