package frodez.util.spring.properties;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.Getter;

/**
 * 访问控制参数配置
 * @author Frodez
 * @date 2018-11-14
 */
@Getter
@Component
public class PropertiesUtil {
	
	private static final String DEV = "dev";
	
	private static final String TEST = "test";
	
	private static final String RELEASE = "release";
	
	private static final String PROD = "prod";

	/**
	 * spring环境参数配置
	 */
	@Autowired
	private Environment env;

	/**
	 * 根据key获取配置
	 * @author Frodez
	 * @date 2019-01-06
	 */
	public String get(String key) {
		return env.getProperty(key);
	}

	/**
	 * 根据key获取配置,获取失败返回默认值
	 * @author Frodez
	 * @date 2019-01-06
	 */
	public String get(String key, String defaultValue) {
		return env.getProperty(key, defaultValue);
	}

	/**
	 * 获取当前激活的配置版本
	 * @author Frodez
	 * @date 2019-01-06
	 */
	public List<String> getActiveProfiles() {
		return Arrays.asList(env.getActiveProfiles());
	}
	
	public boolean isDev() {
		return env.getActiveProfiles()[0].equals(DEV);
	}
	
	public boolean isTest() {
		return env.getActiveProfiles()[0].equals(TEST);
	}
	
	public boolean isRelease() {
		return env.getActiveProfiles()[0].equals(RELEASE);
	}
	
	public boolean isProd() {
		return env.getActiveProfiles()[0].equals(PROD);
	}

}