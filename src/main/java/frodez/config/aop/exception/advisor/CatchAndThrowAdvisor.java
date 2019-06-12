package frodez.config.aop.exception.advisor;

import frodez.config.aop.exception.annotation.CatchAndThrow;
import frodez.constant.errors.code.ErrorCode;
import frodez.constant.errors.code.ServiceException;
import frodez.util.common.StrUtil;
import frodez.util.reflect.ReflectUtil;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(Integer.MIN_VALUE)
public class CatchAndThrowAdvisor implements PointcutAdvisor {

	private Map<String, ErrorCode> errorCodeCache = new ConcurrentHashMap<>();

	@Override
	public Advice getAdvice() {
		return (MethodInterceptor) invocation -> {
			try {
				return invocation.proceed();
			} catch (Exception e) {
				String methodName = ReflectUtil.getFullMethodName(invocation.getMethod());
				log.error(StrUtil.concat("[", methodName, "]"), e);
				throw new ServiceException(errorCodeCache.get(methodName));
			}
		};
	}

	@Override
	public boolean isPerInstance() {
		return true;
	}

	@Override
	public Pointcut getPointcut() {
		return new Pointcut() {

			/**
			 * 根据方法判断
			 * @author Frodez
			 * @date 2019-05-10
			 */
			@Override
			public MethodMatcher getMethodMatcher() {
				return new MethodMatcher() {

					/**
					 * 对方法进行判断(运行时)
					 * @author Frodez
					 * @date 2019-05-10
					 */
					@Override
					public boolean matches(Method method, Class<?> targetClass, Object... args) {
						//isRuntime()方法返回值为false时,不会进行运行时判断
						return false;
					}

					/**
					 * 对方法进行判断
					 * @author Frodez
					 * @date 2019-05-10
					 */
					@Override
					public boolean matches(Method method, Class<?> targetClass) {
						//这里可以进行运行前检查
						CatchAndThrow annotation = method.getAnnotation(CatchAndThrow.class);
						if (annotation == null) {
							return false;
						}
						String methodName = ReflectUtil.getFullMethodName(method);
						errorCodeCache.put(methodName, annotation.errorCode());
						return true;
					}

					/**
					 * 默认true
					 * @author Frodez
					 * @date 2019-05-10
					 */
					@Override
					public boolean isRuntime() {
						return false;
					}
				};
			}

			/**
			 * 根据类型判断
			 * @author Frodez
			 * @date 2019-05-10
			 */
			@Override
			public ClassFilter getClassFilter() {
				return clazz -> true;
			}

		};
	}

}
