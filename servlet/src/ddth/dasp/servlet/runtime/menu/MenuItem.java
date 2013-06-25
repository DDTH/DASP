package ddth.dasp.servlet.runtime.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MenuItem {
    private Object id;
    private String name;
    private String url;
    private List<MenuItem> children;
    private int position;

    public static MenuItem createMenuItem(Object id, String name, String url, int position) {
        MenuItem menuItem = new MenuItem();
        menuItem.id = id;
        menuItem.name = name;
        menuItem.url = url;
        menuItem.position = position;
        return menuItem;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public MenuItem[] getChildren() {
        return children != null ? children.toArray(new MenuItem[0]) : null;
    }

    public void addChild(MenuItem child) {
        if (children == null) {
            children = new ArrayList<MenuItem>();
            // children = new TreeSet<MenuItem>(new Comparator<MenuItem>() {
            // @Override
            // public int compare(MenuItem menuItem1, MenuItem menuItem2) {
            // return menuItem1.position - menuItem2.position;
            // }
            // });
        }
        // if (!children.contains(child)) {
        children.add(child);
        Collections.sort(children, new Comparator<MenuItem>() {
            @Override
            public int compare(MenuItem menuItem1, MenuItem menuItem2) {
                return menuItem1.position - menuItem2.position;
            }
        });
        // }
    }

    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public boolean equals(Object obj) {
    // if (!(obj instanceof MenuItem)) {
    // return false;
    // }
    // MenuItem other = (MenuItem) obj;
    // return new EqualsBuilder().append(this.id, other.id).isEquals();
    // }
    //
    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public int hashCode() {
    // return new HashCodeBuilder(19,
    // 81).append(id).append(name).append(url).hashCode();
    // }
}
