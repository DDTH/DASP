package ddth.dasp.framework.springmvc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import ddth.dasp.common.rp.IRequestParser;
import ddth.dasp.framework.osgi.BundleResourceLoader;

/**
 * This controller serves bundle's static content over the web.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version since v0.1.0
 */
public class BundleStaticResourceController extends BaseAnnotationController {

	private String resourcePrefix = "";
	private BundleResourceLoader bundleResourceLoader;

	protected String getResourcePrefix() {
		return resourcePrefix;
	}

	public void setResourcePrefix(String resourcePrefix) {
		this.resourcePrefix = resourcePrefix;
	}

	protected BundleResourceLoader getBundleResourceLoader() {
		return bundleResourceLoader;
	}

	public void setBundleResourceLoader(
			BundleResourceLoader bundleResourceLoader) {
		this.bundleResourceLoader = bundleResourceLoader;
	}

	@RequestMapping
	public void handle(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		IRequestParser rp = getRequestParser(request);
		String requestUri = rp.getRequestUri();

		String resourceUri = resourcePrefix != null ? resourcePrefix
				+ requestUri : requestUri;
		if (serveStaticResource(resourceUri, request, response)) {
			return;
		}

		String requestAction = getRequestAction(request);
		if (!StringUtils.isBlank(requestAction)) {
			if (requestAction.length() + 1 < requestUri.length()) {
				requestUri = requestUri.substring(requestAction.length() + 1);
				if (serveStaticResource(resourceUri, request, response)) {
					return;
				}
			}
		}

		response.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	protected String deltectMimetype(String resourceUri) {
		String mimetype = URLConnection.getFileNameMap().getContentTypeFor(
				resourceUri);
		return !StringUtils.isBlank(mimetype) ? mimetype
				: "application/octet-stream";
	}

	protected boolean serveStaticResource(String resourceUri,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		if (!bundleResourceLoader.resourceExists(resourceUri)) {
			return false;
		}
		String mimetype = deltectMimetype(resourceUri);
		response.setContentType(mimetype);
		InputStream is = bundleResourceLoader.loadResource(resourceUri);
		try {
			OutputStream os = response.getOutputStream();
			byte[] buffer = new byte[1024];
			int bytesRead = is.read(buffer);
			while (bytesRead != -1) {
				os.write(buffer, 0, bytesRead);
				bytesRead = is.read(buffer);
			}
			os.flush();
		} finally {
			is.close();
		}
		return true;
	}
}
