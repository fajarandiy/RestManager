package com.pikachugo.RestManager.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.log4j.Logger;

import com.pikachugo.RestManager.connector.RestConnector;

public class ResizeImageUtil {
	private static Logger log = Logger.getLogger(RestConnector.class);
	public static File resizeFile(File temp ,int maxSize ,  int width , int height , String toogle) throws IOException {
		// Get length of file in bytes
		File fileReturn = temp;
		
		String stringParam ="maxSize:1023,width:1400,height:1795,toogle:ON";
		log.debug("authString"+stringParam);	
		String[] paramString = stringParam.split(",");
		log.debug("paramString"+paramString);
		String[] paramStringMaxSize = paramString[0].split(":"); 
		String[] paramStringWidth = paramString[1].split(":"); 
		String[] paramStringHeight = paramString[2].split(":"); 
		String[] paramStringToogle = paramString[3].split(":"); 
		
		log.debug("paramStringTemp 0"+paramStringMaxSize[1]);
		log.debug("paramStringTemp 1"+paramStringWidth[1]);
		log.debug("paramStringTemp 2"+paramStringHeight[1]);
		log.debug("paramStringTemp 3"+paramStringToogle[1]);
		
		if(0 == maxSize ||"".equals(maxSize)) {
			maxSize = Integer.parseInt(paramStringMaxSize[1]);
			log.debug("maxSize " + maxSize);
		}
		if(0 == width ||"".equals(width)) {
			width = Integer.parseInt(paramStringWidth[1]);		
			log.debug("width "+width);
		}
		if(0 == height ||"".equals(height)) {
			height = Integer.parseInt(paramStringHeight[1]);
			log.debug("height" +height);
		}
		if(null == toogle ||"".equals(toogle) || "null".equals(toogle)) {
			toogle = paramStringToogle[1];
			log.debug("toogle" +toogle );
		}
		
    	long fileSizeInBytes = temp.length();
    	// Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
    	long fileSizeInKB = fileSizeInBytes / 1024;
    	// Convert the KB to MegaBytes (1 MB = 1024 KBytes)
    	long fileSizeInMB = fileSizeInKB / 1024;
    	
    	log.debug("fileSizeInKB aco"+fileSizeInKB);
    	log.debug("fileSizeInMB aco"+fileSizeInMB);
    	log.debug("temp name"+temp.getAbsolutePath().toString());
    	if(fileSizeInKB > maxSize && "ON".equalsIgnoreCase(toogle)) {
    	//	isResize = true;
    		log.debug("masuk resize");
    		BufferedImage imgResize = ImageIO.read(temp); // load image
	    	int type = imgResize.getType() == 0? BufferedImage.TYPE_INT_ARGB : imgResize.getType();
	    	log.debug("type"+type);
	    	log.debug("INT_ARGB"+BufferedImage.TYPE_INT_ARGB);
	    	BufferedImage resizeImageJpg = resizeImage(imgResize, type ,width , height);
			ImageIO.write(resizeImageJpg, "jpg", new File(temp.getAbsolutePath().toString())); 
			return fileReturn;
    	}
    	else {
    		return fileReturn;
    	}
    	
	}
	
	private static BufferedImage resizeImage(BufferedImage originalImage, int type , int width , int height){
		BufferedImage resizedImage = new BufferedImage(width, height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, width, height, null);
		g.dispose();
			
		return resizedImage;
	}
}
