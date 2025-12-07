package com.server.global.config;

import com.server.global.config.converter.SearchPageTypeConverter;
import com.server.global.config.converter.SearchTargetTypeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final SearchPageTypeConverter searchPageTypeConverter;
	private final SearchTargetTypeConverter searchTargetTypeConverter;

	@Override
	@SuppressWarnings("null")
	public void addFormatters(@NonNull FormatterRegistry registry) {
		registry.addConverter(searchPageTypeConverter);
		registry.addConverter(searchTargetTypeConverter);
	}

	@Override
	public void addCorsMappings(@NonNull CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins(
				"http://localhost:3000",
				"https://withiy.zerohertz.xyz",
				"https://withiy-prod.zerohertz.xyz"
			)
			.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
			.allowedHeaders("*")
			.allowCredentials(true);
	}
}
