package org.yaukie.zipkin;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Frontend {
	
	private static Logger log = Logger.getLogger(Frontend.class);

  @RequestMapping("/index")
  public String index(HttpServletRequest request, HttpServletResponse response ) throws IOException {
	 if(log.isDebugEnabled()){
		 log.debug("service Frontend starts ");
	 }
	 String context = request.getContextPath();
	 request.getSession().setAttribute("casClient", context);
	return "index";
  }
}