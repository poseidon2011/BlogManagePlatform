package frodez.config.swagger.plugin;

import static com.google.common.base.Strings.emptyToNull;
import static springfox.documentation.swagger.common.SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER;

import com.google.common.collect.ArrayListMultimap;
import frodez.config.swagger.SwaggerProperties;
import frodez.util.common.StrUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.Example;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.spring.web.DescriptionResolver;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

/**
 * 自动将@ApiModel中的数据转换为默认参数描述
 * @author Frodez
 * @date 2019-06-06
 */
@Slf4j
@Component
@Profile({ "dev", "test" })
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 100)
public class DefaultParamPlugin implements ParameterBuilderPlugin {

	private final DescriptionResolver descriptions;

	private boolean useCustomerizedPluggins = false;

	@Autowired
	public DefaultParamPlugin(DescriptionResolver descriptions, EnumTypeDeterminer enumTypeDeterminer,
		SwaggerProperties properties) {
		this.descriptions = descriptions;
		this.useCustomerizedPluggins = properties.getUseCustomerizedPluggins();
	}

	@Override
	public boolean supports(DocumentationType delimiter) {
		return useCustomerizedPluggins;
	}

	@Override
	public void apply(ParameterContext context) {
		ResolvedMethodParameter methodParameter = context.resolvedMethodParameter();
		if (methodParameter.hasParameterAnnotation(ApiParam.class)) {
			return;
		}
		Class<?> parameterClass = methodParameter.getParameterType().getErasedType();
		ApiModel apiModel = parameterClass.getAnnotation(ApiModel.class);
		if (apiModel == null) {
			if (!BeanUtils.isSimpleProperty(parameterClass)) {
				log.warn(StrUtil.concat(context.getOperationContext().requestMappingPattern(), "的参数", parameterClass
					.getName(), "既未配置@", ApiParam.class.getName(), "注解,也未配置@", ApiModel.class.getName(), "注解"));
			}
			return;
		}
		setDefaultApiParam(context, apiModel);
	}

	private void setDefaultApiParam(ParameterContext context, ApiModel annotation) {
		context.parameterBuilder().name(emptyToNull(annotation.description())).description(emptyToNull(descriptions
			.resolve(annotation.description()))).parameterAccess(emptyToNull(null)).defaultValue(emptyToNull(null))
			.allowMultiple(false).allowEmptyValue(false).scalarExample(new Example("")).complexExamples(
				ArrayListMultimap.create()).hidden(false).collectionFormat("").order(SWAGGER_PLUGIN_ORDER);
	}

}