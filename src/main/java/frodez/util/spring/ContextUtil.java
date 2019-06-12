package frodez.util.spring;

import frodez.constant.settings.DefStr;
import frodez.util.common.StrUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

/**
 * spring工具类
 * @author Frodez
 * @date 2018-12-21
 */
@Component("contextUtil")
public class ContextUtil implements ApplicationContextAware {

	private static ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
		Assert.notNull(context, "context must not be null");
	}

	/**
	 * 关闭spring应用,默认返回码1
	 * @author Frodez
	 * @date 2019-06-04
	 */
	public static void exit() {
		exit(1);
	}

	/**
	 * 关闭spring应用
	 * @param exitCode 返回码
	 * @author Frodez
	 * @date 2019-06-04
	 */
	public static void exit(int exitCode) {
		SpringApplication.exit(context, () -> exitCode);
	}

	/**
	 * 获取项目根路径
	 * @author Frodez
	 * @date 2019-01-13
	 */
	public static String rootPath() {
		return ClassUtils.getDefaultClassLoader().getResource("").getPath();
	}

	/**
	 * 获取spring上下文环境
	 * @author Frodez
	 * @date 2018-12-21
	 */
	public static ApplicationContext context() {
		return context;
	}

	/**
	 * 使用spring上下文环境获取bean
	 * @param klass bean的类型
	 * @author Frodez
	 * @date 2018-12-21
	 */
	public static <T> T get(Class<T> klass) {
		return context.getBean(klass);
	}

	/**
	 * 使用spring上下文环境获取bean
	 * @param klass bean的类型
	 * @author Frodez
	 * @param <T>
	 * @date 2018-12-21
	 */
	public static <T> Map<String, T> gets(Class<T> klass) {
		return context.getBeansOfType(klass);
	}

	/**
	 * 使用spring上下文环境获取bean
	 * @param beanName bean的名字
	 * @param klass bean的类型
	 * @author Frodez
	 * @date 2018-12-21
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(String beanName, Class<T> klass) {
		return (T) context.getBean(beanName);
	}

	/**
	 * 根据ant风格模式字符串匹配路径,并获取路径下所有类
	 * @author Frodez
	 * @throws LinkageError
	 * @throws ClassNotFoundException
	 * @date 2019-05-23
	 */
	/**
	 * 获取Reader
	 * @author Frodez
	 * @date 2019-05-24
	 */
	@SneakyThrows
	public static List<Class<?>> getClasses(String pattern) {
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
		String packageSearchPath = StrUtil.concat(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX, ClassUtils
			.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(pattern)), DefStr.CLASS_SUFFIX);
		List<Resource> resources = Arrays.asList(resourcePatternResolver.getResources(packageSearchPath)).stream()
			.filter(Resource::isReadable).collect(Collectors.toList());
		List<Class<?>> classes = new ArrayList<>();
		for (Resource resource : resources) {
			classes.add(ClassUtils.forName(metadataReaderFactory.getMetadataReader(resource).getClassMetadata()
				.getClassName(), null));
		}
		return classes;
	}

}
