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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.istarindia.cms.lessons.CMSLesson;
import com.istarindia.cms.lessons.CMSSlide;

public class LessonServices {

	public LessonServices() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String lessonHTMLfromLessonXML(int lessonID) {
		StringBuffer stringBuffer = new StringBuffer();
		
		String path = "";
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
		
		path += "" + lessonID + ".xml";
	
		File file = new File(path);

		try {
			JAXBContext jaxbcontext = JAXBContext.newInstance(CMSLesson.class);
			Unmarshaller unmarshaller = jaxbcontext.createUnmarshaller();
			CMSLesson cmsLesson = (CMSLesson) unmarshaller.unmarshal(file);
			for (CMSSlide cmsSlide : cmsLesson.getSlides()) {
				String ext = "_desktop.vm";
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
				String[] transitions = { "fade", "slide", "convex", "concave", "zoom", "cube", "slide-in fade-out" };
				int rand = (new Random()).nextInt(7);

				String bg_image = null;
				String bgImage = "";
				String type="cover";
				if(cmsSlide.getImage_BG()!=null){
					if(cmsSlide.getImage_BG().contains(".png")){
						bg_image = cmsSlide.getImage_BG().replaceAll(".png","_desktop.png");
						bgImage = "data-background-image='"+bg_image+"'";
						

					}
					if (cmsSlide.getImage_BG().contains(".gif")) {
						bg_image = cmsSlide.getImage_BG();
						 type="contain";
                        bgImage = "data-background-image='"+bg_image+"'";
					}
	
					 
				}

				
				
				String header = "id='"+cmsSlide.getId()+"' data-background-transition='"+transitions[rand]+"' data-background-color='"+cmsSlide.getBackground()+"' "+bgImage+" data-background-size='"+type+"'";
				if(cmsSlide.getBackground().equalsIgnoreCase("#000000")) {
							
					header = "id='"+cmsSlide.getId()+"' data-background-transition='"+transitions[rand]+"'   "+bgImage+"  data-background-color='#ffffff' data-background-size='"+type+"'";
				}

				if(cmsSlide.getBackground().equalsIgnoreCase("null")) {
					header = "id='"+cmsSlide.getId()+"' data-background-transition='"+transitions[rand]+"'   "+bgImage+"  data-background-color='#ffffff' data-background-size='"+type+"'";
				}

				if(cmsSlide.getBackground().equalsIgnoreCase("none")) {
					header = "id='"+cmsSlide.getId()+"' data-background-transition='"+transitions[rand]+"'   "+bgImage+" data-background-color='#ffffff' data-background-size='"+type+"'";
				}					
				
			
				context.put("header", header);
				context.put("slide", cmsSlide);
				Template t = ve.getTemplate(templateVMFileName);
				StringWriter writer = new StringWriter();
				t.merge(context, writer);
				String data1 = writer.toString();
				data1 = data1.replaceAll("<p></p>", "");
				data1 = data1.replaceAll("/content/media_upload\\?getfile=", "/video/");

				if (!data1.contains("<table")) {

					data1 = data1.replaceAll("<p>", "<p class='fragment fade-up'>");
					

				}

				data1 = data1.replaceAll("<b>", "");

				data1 = data1.replaceAll("width:500px", "");

				if (templateVMFileName.contains("ONLY_TITLE_PARAGRAPH_cells_fragemented")) {
				   // data1 = data1.replaceAll("<span style=\"font-size:22px\">", "<span class='fragment fade-up ' style=\"font-size:22px\">");
					
					  data1 = data1.replaceAll("<td", "<td class='fragment fade-up visible' style=' border: 1px solid;'");
					  data1 = data1.replaceAll("<th scope=\"row\"", "<th scope='row' style='border: 1px solid;background: lightgray;'");
					  data1 = data1.replaceAll("<th scope=\"col\"", "<th scope='row' style='border: 1px solid;background: lightgray;'");
				}

				if (templateVMFileName.contains("ONLY_PARAGRAPH_TITLE")) {

					data1 = data1.replaceAll("<li>", "<li class='fragment fade-up' >");
					data1 = data1.replaceAll("<h1>", "<h1 class='fragment fade-up' >");
				}

	            if(templateVMFileName.contains("ONLY_TITLE_PARAGRAPH")) {
					
	                	   data1 = data1.replaceAll("<li>", "<li class='fragment fade-up' >");
					
				}
	            if(templateVMFileName.contains("ONLY_PARAGRAPH_IMAGE")) {
	       				
	                	   data1 = data1.replaceAll("<li>", "<li class='fragment fade-up' >");
	            }
	                   
	            if(templateVMFileName.contains("ONLY_2BOX")) {
		       				
	                	data1 = data1.replaceAll("<p class='fragment fade-up'>", "<p>");
	            }
				
				stringBuffer.append(data1);
				Document doc = Jsoup.parse(stringBuffer.toString());
			    int length = doc.text().length();
			
				if(cmsSlide.getId() == 981868){
					System.err.println("doc.text().length()------->"+doc.text().length());
				}
				if(length<500) {
					length = 120;
				} else {
					length = 100;
				}
				stringBuffer.append("<div class='hidden_element' id='slide_"+cmsSlide.getId()+"' data-template='"+cmsSlide.getTemplateName().toLowerCase()+"' data-slide_id='"+cmsSlide.getId()+"' data-length='"+length+"' ></div>");

				
				
				
				
				
			}
		} catch (JAXBException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return stringBuffer.toString();
	}
}
