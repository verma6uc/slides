package com.viksipro.oldCMS.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import slides.SlideUtils;

public class lessonXMLServices {

	public String LessonHTML(int lessonID) {
		String LessonHTML = "";
		LessonHTML += (new SlideUtils()).getLessonHTML(lessonID);
		return LessonHTML;
	}
	
	public String lessonXML(int lessonID){
		int pptID=626;
		String sql = "select * from presentation where lesson_id = "+lessonID;
		for(HashMap<String, Object> presentation : (new com.viksitpro.core.utilities.DBUTILS()).executeQuery(sql)){
			pptID = Integer.parseInt(presentation.get("id").toString());
		}
		String lessonXML="";
		lessonXML+=(new SlideUtils()).getLessonXML(pptID);
		return lessonXML;
	}
	
	public boolean createlessonXMLFile(String lessonXML, int lessonID){
		boolean success = false;
		String path = "/home/ab/Documents/lessonXMLs/";
		path+=""+lessonID+".xml";
		System.err.println(path);
		File file = new File(path);
		
		try{
			if(file.createNewFile()){
				FileWriter fw = new FileWriter(file);
				fw.write(lessonXML);
				fw.close();
				success = true;
			}
		} catch (IOException e) {
			// TODO: handle exception
		}
			
		return success;
	}
	
	//to write xml files to local file system
	public void generateAllLessonXMLFiles(){
		String sql = "select lesson_id from presentation ORDER BY lesson_id";
		List<HashMap<String, Object>> lessons = (new com.viksitpro.core.utilities.DBUTILS()).executeQuery(sql);
		lessonXMLServices xmlServices = new lessonXMLServices();
		//System.out.println(slideUtils.getLessonXML(626));
		for(HashMap<String, Object> lesson : lessons){
			System.err.println(createlessonXMLFile(xmlServices.lessonXML(Integer.parseInt(lesson.get("lesson_id").toString())), 
					Integer.parseInt(lesson.get("lesson_id").toString())));
		}
		
	}
}
