package ddth.dasp.servlet.runtime.menu;

public interface IMenuManager {
    /**
     * Gets the main menu.
     * 
     * @return
     */
    public MenuItem[] getMainMenu();

    /**
     * Gets the side menu.
     * 
     * @return
     */
    public MenuItem[] getSideMenu();

    /**
     * Adds an item to the main menu.
     * 
     * @param menuItem
     */
    public void addMainMenuItem(MenuItem menuItem);

    /**
     * Adds an item to the side menu.
     * 
     * @param menuItem
     */
    public void addSideMenuItem(MenuItem menuItem);
}
