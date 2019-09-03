package com.pikachugo.RestManager.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;

@SpringBootApplication
public class ConstructDataDUKCAPIL {

	public static Map constructRequestDataDUKCAPIL(Map dataMap) {
		Map requestDataMap = new HashMap();
		String nik = (String) dataMap.get("NIK");
		String reffNo = (String) dataMap.get("REFF_NO");
		String userId = (String) dataMap.get("user_id"); 
		requestDataMap.put("NIK", nik);
		requestDataMap.put("REFF_NO", reffNo);
		requestDataMap.put("user_id", userId);
		return requestDataMap;
	}
	
	public static HttpHeaders constructRequestHeaderDUKCAPIL(String uri) {
		HttpHeaders requestHeaders = new HttpHeaders();
		String authorization = "";
		requestHeaders.add("Authorization", authorization);
		requestHeaders.add("Content-Type", "application/json");
		return requestHeaders;
	}
}
