package com.pikachugo.RestManager.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;

@SpringBootApplication
public class ConstructDataNegativeList {
	public static Map constructRequestDataNegativeList(Map dataMap) {
		Map requestDataMap = new HashMap();
		String nik = (String) dataMap.get("NIK");
		requestDataMap.put("NIK", nik);
		return requestDataMap;
	}
	
	public static HttpHeaders constructRequestHeaderNegativeList(String uri, String authorization) {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add("Authorization", authorization);
		requestHeaders.add("Content-Type", "application/json");
		return requestHeaders;
	}
}
