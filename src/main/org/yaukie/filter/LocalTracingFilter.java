package org.yaukie.filter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.yaukie.config.SpringContextUtil;

import brave.Tracing;
import brave.context.log4j12.MDCCurrentTraceContext;
import brave.http.HttpTracing;
import brave.http.HttpTracing.Builder;
import brave.httpclient.TracingHttpClientBuilder;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;
import brave.servlet.TracingFilter;
import brave.spring.webmvc.SpanCustomizingHandlerInterceptor;
import zipkin2.codec.SpanBytesEncoder;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.urlconnection.URLConnectionSender;

/**
 * 重写httpTracing过滤器
 * 使其配置更加灵活
 * @author yaukie
 *
 */
public class LocalTracingFilter implements Filter {

	private static Logger log = Logger.getLogger(LocalTracingFilter.class);
	
	private Filter tracingFilter ; 
	
	private CloseableHttpClient httpClient ; 
	 
	 private SpanCustomizingHandlerInterceptor spanCustomizingHandlerInterceptor ;
	
	public void init(FilterConfig filterConfig) throws ServletException {
		if(log.isDebugEnabled()){
			log.debug("LocalTracingFilter begins ");
		}
		Sender sender  = URLConnectionSender.create("http://localhost:9411/api/v2/spans");
		AsyncReporter  asyncReporter = AsyncReporter.builder(sender)
				.closeTimeout(500, TimeUnit.MILLISECONDS)
				.build(SpanBytesEncoder.JSON_V2);
		
		Tracing tracing = Tracing.newBuilder()
				.localServiceName("localZipKin")
				.spanReporter(asyncReporter)
				.propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "user-name"))
				.currentTraceContext(MDCCurrentTraceContext.create())
				.build();
		
		HttpTracing httpTracing = HttpTracing.create(tracing);
		
		tracingFilter = TracingFilter.create(httpTracing);
		tracingFilter.init(filterConfig);
		
		httpClient = TracingHttpClientBuilder.create(httpTracing)
				.build();
		
//		SpringContextUtil.addBean(httpClient.getClass(), "httpClient");
		
		if(log.isDebugEnabled()){
			log.debug("LocalTracingFilter tracingFilter init end  ");
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		tracingFilter.doFilter(request, response, chain);
	}

	public void destroy() {
		tracingFilter.destroy();

	}

}
