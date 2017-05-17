
/**
 * 
 */
package slides;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
			///fade/slide/convex/concave/zoom
			String[] transitions = {"fade","slide","convex","concave","zoom"};
			int rand = (new Random()).nextInt(5);
			String header = "id='"+hashMap.get("id")+"' data-background-transition='"+transitions[rand]+"' data-background-color='"+cMSlide.getBackground()+"' data-background-image='"+cMSlide.getImage_BG()+"'";
			if(cMSlide.getBackground().equalsIgnoreCase("#000000")) {
				header = "id='"+hashMap.get("id")+"' data-background-transition='"+transitions[rand]+"'   data-background-image='"+cMSlide.getImage_BG()+"' data-background-color='#ffffff'";
			}
			
			context.put("header", header);
			context.put("slide", cMSlide);
			Template t = ve.getTemplate(templateVMFileName);
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			String data1 = writer.toString();
			data1 = data1.replaceAll("<p></p>", "");
			data1 = data1.replaceAll("/content/media_upload\\?getfile=", "/video/");
			

			//if(!data1.contains("<table")) {
				data1 = data1.replaceAll("<b>", "");
				data1 = data1.replaceAll("<p>", "<p class='fragment fade-up visible' >");
				if(((int)hashMap.get("id")) == 626) {
					System.err.println("data1"+data1);
				}
				
			//} else {
				data1 = data1.replaceAll("width:500px", "");
			//}

			//Document doc = Jsoup.parse(data1);
			//data1 = doc.text();
			
			
			//Fix image urls http://192.168.0.125:8080/video/salesSession1_4.png
			if(templateVMFileName.contains("ONLY_TITLE_PARAGRAPH_cells_fragemented")) {
				data1 = data1.replaceAll("<span style=\"font-size:22px\">", "<span class='fragment fade-up visible' style=\"font-size:22px\">");
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
}
