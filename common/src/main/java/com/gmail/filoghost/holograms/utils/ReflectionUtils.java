package com.gmail.filoghost.holograms.utils;

import java.lang.reflect.Field;
import java.util.Map;

import net.minecraft.server.v1_8_R1.EntityArmorStand;

public class ReflectionUtils {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void putInPrivateStaticMap(Class clazz, String fieldName, Object key, Object value) throws Exception {
		Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		Map map = (Map) field.get(null);
		map.put(key, value);
	}

	public static void setPrivateField(Class<EntityArmorStand> clazz, Object handle, String fieldName, Object value) throws Exception {
		Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(handle, value);
	}
}
