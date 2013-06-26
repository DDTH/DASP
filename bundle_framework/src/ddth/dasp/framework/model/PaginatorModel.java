package ddth.dasp.framework.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for paginating.
 * 
 * Explanation of {@link #urlTemplate}": {@link #urlTemplate} holds the template
 * to generate the URL. It is a string with a placeholder <code>${page}</code>
 * which will be replaced with the page's number. Other acceptable variances:
 * <code>${pageNum}, ${pageNumber}, ${PAGE_NUM}, ${PAGE_NUMBER}</code>.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public class PaginatorModel {

    public final static int DEFAULT_NUM_VISIBLE_PAGES = 11;
    public final static int DEFAULT_PAGE_SIZE = 10;

    private int numVisiblePages = DEFAULT_NUM_VISIBLE_PAGES;
    private int numEntries, pageSize = DEFAULT_PAGE_SIZE, currentPage = 1;
    private String urlTemplate;

    public PaginatorModel(String urlTemplate, int numEntries) {
        setUrlTemplate(urlTemplate);
        setNumEntries(numEntries);
    }

    public PaginatorModel(String urlTemplate, int numEntries, int pageSize, int currentPage,
            int numVisiblePages) {
        setUrlTemplate(urlTemplate);
        setNumEntries(numEntries);
        setPageSize(pageSize);
        setCurrentPage(currentPage);
        setNumVisiblePages(numVisiblePages);
    }

    public int getNumPages() {
        int numPages = numEntries / pageSize;
        return numPages * pageSize == numEntries ? numPages : numPages + 1;
    }

    public String getUrlForPage(int pageNum) {
        String url = urlTemplate;
        String sPageNum = String.valueOf(pageNum);
        return url.replace("${page}", sPageNum).replace("${pageNum}", sPageNum)
                .replace("${pageNumber}", sPageNum).replace("${PAGE_NUM}", sPageNum)
                .replace("${PAGE_MUMBER}", sPageNum);
    }

    public Integer[] getVisiblePages() {
        List<Integer> result = new ArrayList<Integer>();
        int numPages = getNumPages();
        if (numPages > numVisiblePages) {
            result.add(currentPage);

            // tail
            for (int temp = currentPage + 1; temp <= currentPage + 2; temp++) {
                if (temp <= numPages) {
                    result.add(temp);
                }
            }
            if (currentPage + 2 < numPages) {
                if (currentPage + 4 < numPages) {
                    result.add(0);
                }
                if (currentPage + 3 < numPages) {
                    result.add(numPages - 1);
                }
                result.add(numPages);
            }

            // head
            for (int temp = currentPage - 1; temp >= currentPage - 2; temp--) {
                if (temp >= 1) {
                    result.add(0, temp);
                }
            }
            if (currentPage - 2 > 1) {
                if (currentPage - 4 > 1) {
                    result.add(0, 0);
                }
                if (currentPage - 3 > 1) {
                    result.add(0, 2);
                }
                result.add(0, 1);
            }
        } else {
            for (int i = 1; i <= numPages; i++) {
                result.add(i);
            }
        }
        return result.toArray(new Integer[0]);
    }

    public int getNumVisiblePages() {
        return numVisiblePages;
    }

    public void setNumVisiblePages(int numVisiblePages) {
        this.numVisiblePages = numVisiblePages;
    }

    public int getNumEntries() {
        return numEntries;
    }

    public void setNumEntries(int numEntries) {
        this.numEntries = numEntries;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public String getUrlTemplate() {
        return urlTemplate;
    }

    public void setUrlTemplate(String urlTemplate) {
        this.urlTemplate = urlTemplate;
    }
}
