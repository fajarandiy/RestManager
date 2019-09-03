package com.pikachugo.RestManager.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;

@SpringBootApplication
public class ConstructDataAsliRI {

	public static Map constructRequestDataAsliRI(Map dataMap) {
		Map requestDataMap = new HashMap();
		String nik = (String) dataMap.get("nik");
		String selfiePhoto = (String) dataMap.get("selfie_photo");
		requestDataMap.put("nik", nik);
		requestDataMap.put("selfiePhoto", selfiePhoto);
		return requestDataMap;
	}
	
	public static HttpHeaders constructRequestHeaderAsliRI(String uri) {
		HttpHeaders requestHeaders = new HttpHeaders();
		String authorization = "";
		requestHeaders.add("Authorization", authorization);
		requestHeaders.add("Content-Type", "application/json");
		return requestHeaders;
	}
}
