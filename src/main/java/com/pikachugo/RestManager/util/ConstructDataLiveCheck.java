package com.pikachugo.RestManager.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;

import com.pikachugo.RestManager.connector.RestConnector;

@SpringBootApplication
public class ConstructDataLiveCheck {
	private static Logger log = Logger.getLogger(RestConnector.class);
	public static LinkedMultiValueMap<String, Object> constructRequestDataLiveCheck(Map dataMap) {
		Map requestDataMap = new HashMap();
		LinkedMultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
		String img = (String) dataMap.get("img");
		log.debug("masuk live check construct");
			log.debug("set request body");
			if(img != null && !"".equals(img)) {  
				File temp = null;
		    	try {
		    		log.debug("decode base64");
		    		byte[] bytes = Base64.decodeBase64(img);
		    		temp = File.createTempFile(bytes.toString(), ".jpg");
			    	FileUtils.writeByteArrayToFile( temp, bytes );
			    	FileSystemResource newImage = new FileSystemResource(temp.getAbsolutePath().toString());
			    	requestBody.add("img", new FileSystemResource(temp.getAbsolutePath().toString()));
			    	log.debug("selesai construct");
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		
//		requestDataMap.put("nik", nik);
//		requestDataMap.put("selfie_photo", selfiePhoto);
		return requestBody;
	}
	
	public static HttpHeaders constructRequestHeaderLiveCheck(String uri) {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		return requestHeaders;
	}
}
