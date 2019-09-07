package com.pikachugo.RestManager.util;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BaseUtil {
	
	public static boolean nvl(String s) {
		if(s==null || s.trim().length() == 0 ){
			return true;
		}
		return false;
	}
	
	public static boolean nvl(Object o) {
		if(o==null) return true;
		if(o instanceof String) {
			if(((String)o).trim().length() == 0 ) return true;
		}
		return false;
	}
	
	public static ResponseEntity<Map> reply(Map response) {
		if(!nvl(response.get("errorMessage"))) {
			if (response.get("errorMessage").toString().contains("error.forbidden")) return new ResponseEntity<Map>(response,HttpStatus.FORBIDDEN);
			if (response.get("errorMessage").toString().contains("error.session.invalid")) return new ResponseEntity<Map>(response,HttpStatus.UNAUTHORIZED);
			return new ResponseEntity<Map>(response,HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<Map>(response,HttpStatus.OK);
	}
}
