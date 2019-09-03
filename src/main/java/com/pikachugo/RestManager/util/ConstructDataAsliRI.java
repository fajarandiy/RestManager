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
		requestDataMap.put("selfie_photo", selfiePhoto);
		return requestDataMap;
	}
	
	public static HttpHeaders constructRequestHeaderAsliRI(String uri) {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add("x-ibm-client-id", ""); //masih ditanyakan ke umg ini maksudnya apa
		requestHeaders.add("Content-Type", "application/json");
		return requestHeaders;
	}
}
