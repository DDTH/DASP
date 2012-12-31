package ddth.dasp.hetty.mvc.view;

import java.util.Map;

/**
 * Resolves a view name to an {@link IView} object.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public interface IViewResolver {

    public final static String REDIRECT_VIEW_PREFIX = "redirect:";
    public final static String FORWARD_VIEW_PREFIX = "forward:";

    public IView resolveView(String name, Map<String, String> replacements);
}
