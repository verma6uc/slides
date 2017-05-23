
/**
 * 
 */
package slides;


import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.istarindia.cms.lessons.CMSLesson;
import com.istarindia.cms.lessons.CMSSlide;
import com.viksitpro.core.utilities.DBUTILS;

/**
 * @author vaibhav verma
 *
 */
public class SlideUtils {

	
	public String getSlideXML(int pptID){
		
		DBUTILS util = new DBUTILS();
		StringBuffer stringBuffer = new StringBuffer();
		String sql = "select * from slide where  presentation_id="+pptID+" order by order_id";
		
		List<HashMap<String, Object>> data = util.executeQuery(sql);
		
		
		for (HashMap<String, Object> hashMap : data) {
			CMSSlide cMSlide = new CMSSlide();
			String xml = (String) hashMap.get("slide_text");
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(CMSSlide.class);
				String slide_text = xml.replaceAll("<br />", " ").replaceAll("<br>", " ").replaceAll("&nbsp;", " ").replaceAll("&lt;br&gt;"," ").replaceAll("&lt;br /&gt;", " ");
				InputStream in = IOUtils.toInputStream(slide_text, "UTF-8");
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				cMSlide = (CMSSlide) jaxbUnmarshaller.unmarshal(in);
			} catch (Exception e) {
				//e.printStackTrace();
			}
			String ext = "_desktop.vm";
			System.out.println((String) hashMap.get("template")+""+hashMap.get("id"));
			String templateVMFileName = cMSlide.getTemplateName() + ext;
			if (cMSlide.getTemplateName().equalsIgnoreCase("ONLY_TITLE_LIST")) {
				templateVMFileName = cMSlide.getList().getList_type() + "___" + cMSlide.getTemplateName() + ext;
			}
			VelocityEngine ve = new VelocityEngine();
			ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			ve.init();
			VelocityContext context = new VelocityContext();
			int cnt = 1;
			///fade/slide/convex/concave/zoom
			String[] transitions = {"fade","slide","convex","concave","zoom","cube"};
			int rand = 5;;//(new Random()).nextInt(6);
			/*String header = "id='"+hashMap.get("id")+"' data-background-transition='"+transitions[rand]+"' data-background-color='"+cMSlide.getBackground()+"' data-background-image='"+cMSlide.getImage_BG()+"'";
			if(cMSlide.getBackground().equalsIgnoreCase("#000000")) {
				header = "id='"+hashMap.get("id")+"' data-background-transition='"+transitions[rand]+"'   data-background-image='"+cMSlide.getImage_BG()+"' data-background-color='#ffffff'";
			}*/
			String bg_image = null;
			String type="cover";
			if(cMSlide.getImage_BG()!=null){
				if(cMSlide.getImage_BG().contains(".png")){
					bg_image = cMSlide.getImage_BG().replaceAll(".png","_desktop.png");
				}
				if(cMSlide.getImage_BG().contains(".gif")){
					bg_image = cMSlide.getImage_BG();
					type = "contain";
					
				}
				 
			}
			
			String header = "id='"+hashMap.get("id")+"' data-background-transition='"+transitions[rand]+"' data-background-color='"+cMSlide.getBackground()+"' data-background-image='"+bg_image+"' data-background-size='"+type+"'";
			if(cMSlide.getBackground().equalsIgnoreCase("#000000")) {
							
				header = "id='"+hashMap.get("id")+"' data-background-transition='"+transitions[rand]+"'   data-background-image='"+bg_image+"' data-background-color='#ffffff' data-background-size='"+type+"'";
			}
			if(cMSlide.getBackground().equalsIgnoreCase("null")) {
				header = "id='"+hashMap.get("id")+"' data-background-transition='"+transitions[rand]+"'   data-background-image='"+bg_image+"' data-background-color='#ffffff' data-background-size='"+type+"'";
			}
			if(cMSlide.getBackground().equalsIgnoreCase("none")) {
				header = "id='"+hashMap.get("id")+"' data-background-transition='"+transitions[rand]+"'   data-background-image='"+bg_image+"' data-background-color='#ffffff' data-background-size='"+type+"'";
			}					
			
			
		
			context.put("header", header);
			context.put("slide", cMSlide);
			Template t = ve.getTemplate(templateVMFileName);
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			String data1 = writer.toString();
			data1 = data1.replaceAll("<p></p>", "");
			data1 = data1.replaceAll("/content/media_upload\\?getfile=", "/video/");
			
			data1 = data1.replaceAll("/video/", "http://api.talentify.in/video/");

			if(!data1.contains("<table")) {
				
				data1 = data1.replaceAll("<p>", "<p class='fragment fade-up visible' >");
			}
				if(((int)hashMap.get("id")) == 626) {
					System.err.println("data1"+data1);
				}
				data1 = data1.replaceAll("<b>", "");
				//data1 = data1.replaceAll("<ol>", "<ol class='fragment fade-up' >");
		
				// else {
				data1 = data1.replaceAll("width:500px", "");
			//}

			//Document doc = Jsoup.parse(data1);
			//data1 = doc.text();
			
			
			//Fix image urls http://192.168.0.125:8080/video/salesSession1_4.png
			if(templateVMFileName.contains("ONLY_TITLE_PARAGRAPH_cells_fragemented")) {
				data1 = data1.replaceAll("<span style=\"font-size:22px\">", "<span class='fragment fade-up ' style=\"font-size:22px\">");
                
				data1 = data1.replaceAll("<td", "<td class='fragment fade-up visible' ");
				//data1 = data1.replaceAll("<p>", "<p class='fragment fade-up' >");
			}
			
if(templateVMFileName.contains("ONLY_PARAGRAPH_TITLE")) {
				
				/*data1 = data1.replaceAll("<ol>", "<ol class='fragment fade-up' >");
				data1 = data1.replaceAll("<ul>", "<ul class='fragment fade-up' >");*/
	           data1 = data1.replaceAll("<li>", "<li class='fragment fade-up' >");
				data1 = data1.replaceAll("<h1>", "<h1 class='fragment fade-up' >");
			}
                   if(templateVMFileName.contains("ONLY_TITLE_PARAGRAPH")) {
				
				/*data1 = data1.replaceAll("<ol>", "<ol class='fragment fade-up' >");
				data1 = data1.replaceAll("<ul>", "<ul class='fragment fade-up' >");*/
				data1 = data1.replaceAll("<li>", "<li class='fragment fade-up' >");
				
			}
                   if(templateVMFileName.contains("ONLY_PARAGRAPH_IMAGE")) {
       				
       				/*data1 = data1.replaceAll("<ol>", "<ol class='fragment fade-up' >");
       				data1 = data1.replaceAll("<ul>", "<ul class='fragment fade-up' >");*/
       				data1 = data1.replaceAll("<li>", "<li class='fragment fade-up' >");
              }
			stringBuffer.append(data1);

		}
		return stringBuffer.toString();
		
	}
	
	
	/*public static void main(String[] args) {
		DBUTILS util = new DBUTILS();
		String sql = "select * from slide where  presentation_id=100 order by order_id";
		
		List<HashMap<String, Object>> data = util.executeQuery(sql);
		
		System.err.println(data.size());
		
		for (HashMap<String, Object> hashMap : data) {
			CMSSlide cMSlide = new CMSSlide();
			String xml = (String) hashMap.get("slide_text");
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(CMSSlide.class);
				String slide_text = xml.replaceAll("<br />", " ").replaceAll("<br>", " ").replaceAll("&nbsp;", " ").replaceAll("&lt;br&gt;"," ").replaceAll("&lt;br /&gt;", " ");
				InputStream in = IOUtils.toInputStream(slide_text, "UTF-8");
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				cMSlide = (CMSSlide) jaxbUnmarshaller.unmarshal(in);
			} catch (Exception e) {
				//e.printStackTrace();
			}
			String ext = "_desktop.vm";
			System.out.println((String) hashMap.get("template"));
			String templateVMFileName = cMSlide.getTemplateName() + ext;
			if (cMSlide.getTemplateName().equalsIgnoreCase("ONLY_TITLE_LIST")) {
				templateVMFileName = cMSlide.getList().getList_type() + "___" + cMSlide.getTemplateName() + ext;
			}
			VelocityEngine ve = new VelocityEngine();
			ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			ve.init();
			VelocityContext context = new VelocityContext();
			int cnt = 1;
			
			context.put("slide", cMSlide);
			Template t = ve.getTemplate(templateVMFileName);
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			String data1 = writer.toString();
			//System.out.println(data1);
		}
	}*/
	
	public String getLessonXML(int pptID){
		java.io.StringWriter buffer = new StringWriter();
		DBUTILS dbutils = new DBUTILS();
		CMSLesson cmsLesson = new CMSLesson();
		ArrayList<CMSSlide> cmSslides = new ArrayList<>();
		String sql = "select * from slide where presentation_id="+pptID+" order by order_id";
		List<HashMap<String, Object>> slides = dbutils.executeQuery(sql);
		for(HashMap<String, Object> slide : slides){
			CMSSlide cmsSlide = new CMSSlide();
			String slide_xml = (String) slide.get("slide_text");
			try{
				JAXBContext context = JAXBContext.newInstance(CMSSlide.class);
				String slide_text = slide_xml.replaceAll("<br />", " ").replaceAll("<br>", " ").replaceAll("&nbsp;", " ").replaceAll("&lt;br&gt;"," ").replaceAll("&lt;br /&gt;", " ");
				InputStream in = IOUtils.toInputStream(slide_text, "UTF-8");
				Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
				cmsSlide = (CMSSlide) jaxbUnmarshaller.unmarshal(in);
				cmsSlide.setId(Integer.parseInt(slide.get("id").toString()));
				cmsSlide.setOrder_id(Integer.parseInt(slide.get("order_id").toString()));
			} catch (JAXBException e) {
				// TODO: handle exception
			} catch (IOException e) {
				// TODO: handle exception
			}
			cmSslides.add(cmsSlide);
		}
		cmsLesson.setSlides(cmSslides);
		sql = "select * from lesson where id = (select p.lesson_id from presentaion  as p where p.id = "+pptID+")";
		List<HashMap<String, Object>> lesson = dbutils.executeQuery(sql);
		cmsLesson.setLessonTitle(lesson.get(0).get("title").toString());
		cmsLesson.setLessonDescription("NA");
		cmsLesson.setStudentNotes("NA");
		cmsLesson.setTeacherNotes("NA");
		cmsLesson.setType("PRESENTATION");
		try{
			JAXBContext context = JAXBContext.newInstance(CMSLesson.class);
			javax.xml.bind.Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(cmsLesson, buffer);
		} catch (JAXBException e) {
			// TODO: handle exception
		}
		return buffer.toString();
	}
	
	public String getLessonHTML (int lessonID){
		String LessonHTML = "";
		String sql = "select id from presentation  where lesson_id = "+lessonID;
		List<HashMap<String, Object>> presentations = (new DBUTILS()).executeQuery(sql);
		for(HashMap<String, Object> presentation : presentations){
			LessonHTML+=getSlideXML(Integer.parseInt(presentation.get("id").toString()));
		}
		return LessonHTML;
	}
}
