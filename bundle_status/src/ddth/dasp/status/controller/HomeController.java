package ddth.dasp.status.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import ddth.dasp.framework.springmvc.BaseAnnotationController;

public class HomeController extends BaseAnnotationController {

	@RequestMapping
	public String handleRequest() {
		return "redirect:server";
	}
}
