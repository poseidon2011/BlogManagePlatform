package frodez.config.validator;

import frodez.config.aop.validation.annotation.Check;
import frodez.util.common.StrUtil;
import frodez.util.reflect.ReflectUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import javax.validation.Valid;
import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanUtils;

/**
 * 代码检查相关实现
 * @author Frodez
 * @date 2019-05-22
 */
@UtilityClass
public class CodeChecker {

	/**
	 * 检查字段
	 * @author Frodez
	 * @date 2019-05-22
	 */
	public static void checkField(Field field) {
		Class<?> type = field.getType();
		if (Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type)) {
			checkFieldCollectionOrMap(field, (ParameterizedType) field.getGenericType());
			return;
		}
		assertComplexFieldValid(field, field.getType());
	}

	/**
	 * 检查方法参数
	 * @author Frodez
	 * @date 2019-05-22
	 */
	public static void checkParameter(Method method, Parameter parameter) {
		Class<?> type = parameter.getType();
		if (Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type)) {
			checkParameterCollectionOrMap(method, parameter, (ParameterizedType) parameter.getParameterizedType());
			return;
		}
		assertComplexParameterValid(method, parameter, parameter.getType());
	}

	/**
	 * 判断是否为复杂对象
	 * @author Frodez
	 * @date 2019-05-22
	 */
	public static boolean isComplex(Class<?> type) {
		return !BeanUtils.isSimpleProperty(type);
	}

	private static void assertComplexFieldValid(Field field, Class<?> type) {
		if (isComplex(type) && field.getAnnotation(Valid.class) == null) {
			throw new RuntimeException(StrUtil.concat(field.getDeclaringClass().getName(), ".", field.getName(),
				"是复杂类型,需要加上@", Valid.class.getName(), "注解!"));
		}
	}

	private static void checkFieldCollectionOrMap(Field field, ParameterizedType genericType) {
		if (Map.class.isAssignableFrom((Class<?>) genericType.getRawType())) {
			return;
		}
		Type[] actualTypes = genericType.getActualTypeArguments();
		for (Type actualType : actualTypes) {
			//如果是直接类型,直接判断
			if (actualType instanceof Class) {
				assertComplexFieldValid(field, (Class<?>) actualType);
				continue;
			}
			//如果是泛型类型
			if (actualType instanceof ParameterizedType) {
				checkFieldCollectionOrMap(field, (ParameterizedType) actualType);
				continue;
			}
		}
	}

	private static void assertComplexParameterValid(Method method, Parameter parameter, Class<?> type) {
		if (isComplex(type) && parameter.getAnnotation(Valid.class) == null) {
			throw new IllegalArgumentException(StrUtil.concat("含有", "@", Check.class.getName(), "注解方法", ReflectUtil
				.getFullMethodName(method), "的参数", parameter.getName(), "必须使用@Valid注解!"));
		}
	}

	private static void checkParameterCollectionOrMap(Method method, Parameter parameter,
		ParameterizedType genericType) {
		if (Map.class.isAssignableFrom((Class<?>) genericType.getRawType())) {
			return;
		}
		Type[] actualTypes = genericType.getActualTypeArguments();
		for (Type actualType : actualTypes) {
			//如果是直接类型,直接判断
			if (actualType instanceof Class) {
				assertComplexParameterValid(method, parameter, (Class<?>) actualType);
				continue;
			}
			//如果是泛型类型
			if (actualType instanceof ParameterizedType) {
				checkParameterCollectionOrMap(method, parameter, (ParameterizedType) actualType);
				continue;
			}
		}
	}

}