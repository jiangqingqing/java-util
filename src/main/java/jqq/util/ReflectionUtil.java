package jqq.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Java反射的便利工具
 * 
 * @author liangzb@ucweb.com
 * @since 1.9.0
 * @createDate 2013-6-5
 */
public class ReflectionUtil {
	/**
	 * 获取java.lang.Object的所有public method名称
	 * 
	 * @return
	 * @since 1.9.0
	 */
	public static Set<String> getObjectPublicMethodNames() {
		/**
		 * 把java.lang.Object的所有public method名记录起来
		 */
		Method[] methods = Object.class.getMethods();
		Set<String> objectPublicMethodNames = new HashSet<String>();
		for (Method method : methods) {
			objectPublicMethodNames.add(method.getName());
		}
		return objectPublicMethodNames;
	}

	/**
	 * 获取指定Class的所有父类
	 * 
	 * @param clazz
	 * @return
	 * @since 1.9.0
	 */
	public static <T> List<Class> getAllSuperClasses(Class clazz) {
		List<Class> superClasses = new ArrayList<Class>();

		Class superClass = clazz.getSuperclass();
		if (superClass != null) {
			superClasses.add(superClass);
			superClasses.addAll(getAllSuperClasses(superClass)); // 递归，穷举所有父类
		}

		return superClasses;
	}

}
