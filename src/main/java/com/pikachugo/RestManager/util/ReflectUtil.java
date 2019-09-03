package com.pikachugo.RestManager.util;

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.commons.lang3.reflect.FieldUtils;

public class ReflectUtil {
	
	public static void reflectToObject (Object objClass, Map dataMap) {
		Field[] fields = objClass.getClass().getDeclaredFields();
		System.out.println("length"+fields.length);
		for (Field field : fields) {
			try {
				//Field harus diset public supaya ga kena exception
				FieldUtils.writeField(objClass, field.getName(), dataMap.get(field.getName()), true);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				System.out.println("ex : "+e.getMessage());
			}
		}
		System.out.println("result object : "+objClass);
	}
	
	public static void reflectToMap (Object objClass, Map dataMap) {
		Field[] fields = objClass.getClass().getDeclaredFields();
		System.out.println("length"+fields.length);
		for (Field field : fields) {
			try {
				dataMap.put(field.getName(), FieldUtils.readField(objClass,field.getName(), true));
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				System.out.println("ex : "+e.getMessage());
			}
		}
		System.out.println("result map : "+dataMap);
	}
}
