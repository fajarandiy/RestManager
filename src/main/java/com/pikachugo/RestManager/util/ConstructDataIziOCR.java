package com.pikachugo.RestManager.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;

@SpringBootApplication
public class ConstructDataIziOCR {
	private static Logger log = Logger.getLogger(ConstructDataIziOCR.class);
	
	public static LinkedMultiValueMap constructRequestDataIziOCR(Map dataMap) {
		Map requestDataMap = new HashMap();
		LinkedMultiValueMap requestBody = new LinkedMultiValueMap();
		String img = (String) dataMap.get("img");
		log.debug("masuk live check construct");
		log.debug("set request body");
		if (img != null && !"".equals(img)) {
			File temp = null;
			try {
				log.debug("decode base64");
				byte[] bytes = Base64.decodeBase64(img);
				temp = File.createTempFile(bytes.toString(), ".jpg");
				FileUtils.writeByteArrayToFile(temp, bytes);
				temp = ResizeImageUtil.resizeFile(temp, 0, 0, 0, null);
				requestBody.add("img", new FileSystemResource(temp.getAbsolutePath().toString()));
				log.debug("selesai construct");
			}
			catch (Exception ex) {}
		}
		return requestBody;
	}
	
	public static HttpHeaders constructRequestHeaderIziOCR(String uri) {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		return requestHeaders;
	}
}
