package ddth.dasp.status.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import ddth.dasp.status.DaspBundleConstants;

public class HomeController implements Controller {

	private final static String VIEW_NAME = DaspBundleConstants.MODULE_NAME
			+ ":home";

	@Override
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(VIEW_NAME);
		return mav;
	}
}
