package oracle.ui.pattern.dynamicShell;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import javax.servlet.http.HttpSession;

import oracle.adf.controller.ControllerContext;
import oracle.adf.controller.TaskFlowId;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.layout.RichPanelGroupLayout;
import oracle.adf.view.rich.component.rich.layout.RichPanelStretchLayout;
import oracle.adf.view.rich.component.rich.nav.RichCommandNavigationItem;
import oracle.adf.view.rich.component.rich.nav.RichNavigationPane;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.DialogEvent;

import oracle.adf.view.rich.event.RegionNavigationEvent;

import org.apache.myfaces.trinidad.model.ChildPropertyMenuModel;
import org.apache.myfaces.trinidad.model.MenuModel;
import org.apache.myfaces.trinidad.render.ExtendedRenderKitService;
import org.apache.myfaces.trinidad.util.Service;

import soadev.ext.adf.taskflows.helper.Messenger;


/**
 * Responsible for handling tab state for the UI Dynamic Tab Shell.
 *
 * This class supports two modes for the tab shell:
 *
 * 1. Dynamic set of tabs, each represented by a taskflow content page.
 *
 * 2. Single content area, that is also backed by a taskflow content page, and
 *    can be switched on demand.
 *
 * These two modes are mutually exclusive, and cannot be used with one another.
 *
 * Changes:
 *
 * 1/14/2009, cstraub: Added parameter mapping to taskflows
 * 1/06/2009, cstraub: Added persistent tab state for sessions
 *
 * Copyright 2010, Oracle USA, Inc.
 */
public final class TabContext implements Serializable
{
  /**
   * Returns the current instance of the TabShell, gauaranteed to return a non null
   * value. For access through taskflow fragment, this call will locate the TabContext
   * for the owner page.
   * 
   * The scope of the TabContext is the view scope.
   * 
   * @return Current instance of the TabContext
   */
  public static TabContext getCurrentInstance()
  {
    AdfFacesContext adfFacesContext = AdfFacesContext.getCurrentInstance();
    
    TabContext tabContext = (TabContext) adfFacesContext.getViewScope().get(_KEY);
    
    // in case we are sending as a taskflow param, search here too...
    if (tabContext == null)
      tabContext = (TabContext) adfFacesContext.getPageFlowScope().get(_KEY);
    
    return tabContext;
  }
  
  /**
   * Launches the taskflow with the associated id in the main content area of the
   * page. 
   * 
   * This call is incompatible with adding additional tabs (or if tabs were already added).
   * If this is attempted, a TabContentAreaNotReadyException will be thrown.
   *
   * @param taskflowId The taskflow to launch.
   * @throws TabContentAreaDirtyException If the tab state is currently dirty
   */
  public void setMainContent(String taskflowId)
    throws TabContentAreaDirtyException
  {
    this.setMainContent(taskflowId, null);
  }
  
  /**
   * Launches the taskflow with the associated id in the main content area of the
   * page. 
   * 
   * This call is incompatible with adding additional tabs (or if tabs were already added).
   * If this is attempted, a TabContentAreaNotReadyException will be thrown.
   *
   * @param taskflowId The taskflow to launch.
   * @param parameters Parameters that will be passed into the taskflow
   * @throws TabContentAreaDirtyException If the tab state is currently dirty
   */
  public void setMainContent(String taskflowId, Map<String,Object> parameters)
    throws TabContentAreaDirtyException
  {
    // we cannot combine new and old tab modules...
    if (_tabTracker.getNumRendered() > 1)
      throw new TabContentAreaNotReadyException();
    
    int index = getSelectedTabIndex();
    if (index == -1)
    {
      try
      {
        addTab("", taskflowId, parameters); // NOTRANS
      } catch (TabOverflowException toe) { /* impossible */ }
    }
    else
    {
      Tab tab = getTabs().get(getSelectedTabIndex());
      
      if (tab.isDirty())
        throw new TabContentAreaDirtyException();
      
      tab.setTitle(""); // NOTRANS
      tab.setTaskflowId(TaskFlowId.parse(taskflowId));
    }

    setTabsRendered(false);
    _refreshTabContent();
  }
  
  /**
   * Adds a new tab to the page with the given taskflow. If this taskflow is already
   * running on the page in an existing tab, that tab will instead be selected, and no
   * new tab will be added.
   *
   * @param localizedName The name of the tab
   * @param taskflowId The taskflow to launch or select
   * @throws TabOverflowException If the maximum number of tabs is already shown 
   */
  public void addOrSelectTab(String localizedName, String taskflowId)
    throws TabOverflowException
  {
    this.addOrSelectTab(localizedName, taskflowId, null);
  }
  
  /**
   * Adds a new tab to the page with the given taskflow. If this taskflow is already
   * running on the page in an existing tab, that tab will instead be selected, and no
   * new tab will be added.
   *
   * @param localizedName The name of the tab
   * @param taskflowId The taskflow to launch or select
   * @param parameters Parameters that will be passed into the taskflow
   * @throws TabOverflowException If the maximum number of tabs is already shown 
   */
  public void addOrSelectTab(String localizedName, String taskflowId, Map<String,Object> parameters)
    throws TabOverflowException
  {
    int index = getFirstTabIndex(taskflowId);
    if (index != -1)
    {
      setSelectedTabIndex(index);
    }
    else
    {
      addTab(localizedName, taskflowId, parameters);  
    }
  }
  
  /**
   * Adds a new tab to the page specified by the taskflow. This call will still add
   * a new tab even if an existing tab is already showing that taskflow content.
   * 
   * @param localizedName The name of the tab
   * @param taskflowId The taskflow to launch or select
   * @throws TabOverflowException If the maximum number of tabs is already shown
   */
  public void addTab(String localizedName, String taskflowId)
    throws TabOverflowException
  {
    this.addTab(localizedName, taskflowId, null);
  }
  
  /**
   * Adds a new tab to the page specified by the taskflow. This call will still add
   * a new tab even if an existing tab is already showing that taskflow content.
   * 
   * @param localizedName The name of the tab
   * @param taskflowId The taskflow to launch or select
   * @param parameters Parameters that will be passed into the taskflow
   * @throws TabOverflowException If the maximum number of tabs is already shown
   */
  public void addTab(String localizedName, String taskflowId, Map<String,Object> parameters)
    throws TabOverflowException
  {
    if (_tabTracker.getNumRendered() == __MAX_TASKFLOWS)
      throw new TabOverflowException();
    
    int index = _findNextAvailable();
    
    Tab tab = getTabs().get(index);
    tab.setTitle(localizedName);
    tab.setActive(true);
    tab.setTaskflowId(TaskFlowId.parse(taskflowId));
    tab.setParameters(parameters);
       
    _tabTracker.setNumRendered(_tabTracker.getNumRendered()+1);
    _tabTracker.setNextRenderedLoc(_tabTracker.getNextRenderedLoc()+1);
    setSelectedTabIndex(index);
  }
  
  /**
   * Marks the current tab dirty (or not). If dirty, this will produce a visual 
   * affect on the tab (usually place the title of the tab in italics). 
   * 
   * @param isDirty Whether or not the tab is dirty
   */
  public void markCurrentTabDirty(boolean isDirty)
  {
    markTabDirty(getSelectedTabIndex(), isDirty);
  }
  
  /**
   * Marks a particular tab dirty.
   * 
   * @see markCurrentTabDirty
   * 
   * @param index Index of the tab to dirty.
   * @param isDirty Whether or not the tab is dirty.
   */
  public void markTabDirty(int index, boolean isDirty)
  {
    Tab tab = getTabs().get(index);
    tab.setDirty(isDirty);
    _refreshTabContent();
  }
  
  /**
   * Removes the current tab from the page. If the page is dirty, this will result
   * in a warning shown to the user, at which point the user can cancel the action.
   */
  public void removeCurrentTab()
  {
    removeTab(getSelectedTabIndex());
  }
  
  /**
   * Removes the tab at the given index.
   * 
   * @see removeCurrentTab
   * 
   * @param index The tab index to remove
   */
  public void removeTab(int index)
  {
    _removeTab(index, false);
  }
  
  /**
   * Returns true if any of the visible tabs on the page are marked dirty.
   * 
   * @return True if any tab is dirty
   */
  public boolean isTagSetDirty()
  {
    for (Tab t : getTabs())
    {
      if (t.isActive() && t.isDirty())
        return true;
    }
    
    return false;
  }
  
  /**
   * Returns true if the current tab is marked dirty.
   * 
   * @return True if current tab is dirty
   */
  public boolean isCurrentTabDirty()
  {
    int index = getSelectedTabIndex();
    if (index == -1)
      return false;
    
    return getTabs().get(index).isDirty();
  }
  
  /**
   * Returns the index of the currently selected tab.
   * 
   * @return The current tab index
   */
  public int getSelectedTabIndex()
  {
    return _tabTracker.getSelectedTabIndex();
  }



  
  /*
   * The methods below are meant for private implementation use are not meant to be called
   * by external clients:
   * -------------------------------------------------------------------------------------------
   */
  
  public void setTabsRendered(boolean render)
  {
    _tabTracker.setTabsRendered(render);
  }
  
  public boolean isTabsRendered()
  {
    return _tabTracker.isTabsRendered();
  }
  
  public int getFirstTabIndex(String taskflowId)
  {
    List<Tab> tabs = _tabTracker.getTabs();
    
    for (int i=0; i<tabs.size(); i++)
    {
      Tab tab = tabs.get(i);
        
      if (tab == null || !tab.isActive())
        continue;
      
      if (tab.getTaskflowId().getFullyQualifiedName().equals(taskflowId))
        return i;
    }
    return -1;
  }
  
  public void setSelectedTabIndex(int index)
  {
    _tabTracker.setSelectedTabIndex(index);
    _refreshTabContent();
  }
  
  public List<Tab> getTabs()
  {
    return _tabTracker.getTabs();
  }

  public MenuModel getTabMenuModel()
  {
    ChildPropertyMenuModel menuModel = 
      new ChildPropertyMenuModel(getTabs(), "children", Collections.singletonList(getSelectedTabIndex()));
    return menuModel;
  }

  public void setTabsNavigationPane(RichNavigationPane tabsNavigationPane)
  {
    _tabsNavigationPane = tabsNavigationPane;
  }

  public RichNavigationPane getTabsNavigationPane()
  {
    return _tabsNavigationPane;
  }
  
  public void setToolbarArea(RichPanelStretchLayout toolbarArea)
  {
    _toolbarArea = toolbarArea;
  }

  public RichPanelStretchLayout getToolbarArea()
  {
    return _toolbarArea;
  }
  
  public void setTooManyTabsPopup(RichPopup tooManyTabsPopup)
  {
    _tooManyTabsPopup = tooManyTabsPopup;
  }
  
  public RichPopup getTooManyTabsPopup()
  {
    return _tooManyTabsPopup;
  }
  
  public void setTabDirtyPopup(RichPopup tabDirtyPopup)
  {
    _tabDirtyPopup = tabDirtyPopup;
  }

  public RichPopup getTabDirtyPopup()
  {
    return _tabDirtyPopup;
  }

  public void setInnerToolbarArea(RichPanelGroupLayout innerToolbar)
  {
    _innerToolbar = innerToolbar;
  }

  public RichPanelGroupLayout getInnerToolbarArea()
  {
    return _innerToolbar;
  }
  
  public void setContentArea(RichPanelStretchLayout contentArea)
  {
    _contentArea = contentArea;
  }

  public RichPanelStretchLayout getContentArea()
  {
    return _contentArea;  
  }
  
  public void tabActivatedEvent(ActionEvent action)
  {
    RichCommandNavigationItem tab = (RichCommandNavigationItem) action.getComponent();
    
    // get tab index from id
    Object tabIndex = tab.getAttributes().get("tabIndex"); // NOTRANS
    setSelectedTabIndex((Integer) tabIndex);
  }
  
  public void tabRemovedEvent(ActionEvent action)
  {
    removeCurrentTab();
  }
  
  public void handleDirtyTabDialog(DialogEvent ev)
  {
    if (ev.getOutcome().equals(DialogEvent.Outcome.yes))
    {
        if(messenger != null){
            messenger.affirmativeOutcomeCallback();
            messenger = null;
        }else{//not initiated from inside the BTF
            //
           _removeTab(getSelectedTabIndex(), true);
        }
    }else{
        if(messenger != null){
            messenger.negativeOutcomeCallback();
            messenger = null;
        }
    }
    
  }
  
  public TabContext() 
  {
    if (_USE_SESSION_TRACKED_TABS)
    {
      HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
      
      String viewId = ControllerContext.getInstance().getCurrentRootViewPort().getViewId();
      String currentViewKey = String.format("__tc_%s", viewId);
      
      Tabs tabTracker = (Tabs) session.getAttribute(currentViewKey);
      if (tabTracker == null)
      {
        tabTracker = new Tabs();
        session.setAttribute(currentViewKey, tabTracker);
      }
      _tabTracker = tabTracker;
    }
    else
    {
      _tabTracker = new Tabs();
    }
  }
  
  public void showTabDirtyPopup(){
    _showDialog(getTabDirtyPopup());
  }
  
  private void _removeTab(int index, boolean force)
  {
    List<Tab> tabs = getTabs();
    
    Tab tab = tabs.get(index);
    
    if (tab.isDirty() && !force)
    {
      _showDialog(getTabDirtyPopup());
      return;
    }
    
    tab.setTaskflowId(__BLANK);
    tab.setParameters(null);
    tab.setTitle("");
    tab.setActive(false);
    _tabTracker.setNumRendered(_tabTracker.getNumRendered()-1);
    
    if (_tabTracker.getSelectedTabIndex() == index)
    {
      _tabTracker.setSelectedTabIndex(-1);
      if (_tabTracker.getNumRendered() > 0)
      {
        // attempt to find the next tab
        int start = index == __MAX_TASKFLOWS - 1 ? 0 : index + 1;
        do 
        {
          if (start == __MAX_TASKFLOWS)
            start = 0;
          Tab itorTab = tabs.get(start);
          if (itorTab.isActive())
          {
            _tabTracker.setSelectedTabIndex(start);
            break;
          }
          start++;
        } while (start != index);
      }
      else
        _tabTracker.setSelectedTabIndex(-1);
    }

    _refreshTabContent();
  }
  
  private void _refreshTabContent()
  {
    AdfFacesContext.getCurrentInstance().addPartialTarget(getTabsNavigationPane());
    AdfFacesContext.getCurrentInstance().addPartialTarget(getContentArea());
    AdfFacesContext.getCurrentInstance().addPartialTarget(getToolbarArea());
    AdfFacesContext.getCurrentInstance().addPartialTarget(getInnerToolbarArea());
  }
  
  // callers should ensure that the there is some tabs that have not been used yet
  // before calling this
  private int _findNextAvailable()
  {
    List<Tab> tabs = getTabs();
    
    if (_tabTracker.getNextRenderedLoc() == __MAX_TASKFLOWS)
    {
      // if we exceed the tabs, and we have space left, collapse the arrays --
      // this will ensure our tabs always open to the right of other tabs
      for (int i=0; i<__MAX_TASKFLOWS; i++)
      {
        Tab tab = tabs.get(i);
        
        // if its empty, fill it with something
        if (!tab.isActive())
        {
          int j = i + 1;
          Tab toSwap = null;
          while (j<__MAX_TASKFLOWS)
          {
            Tab testTab = tabs.get(j++);
            if (testTab.isActive())
            {
              toSwap = testTab;
              break;
            }
          }
          
          // nothing else to do
          if (toSwap == null)
            break;
          
          tab.setActive(true);
          toSwap.setActive(false);
          tab.setTitle(toSwap.getTitle());
          toSwap.setTitle(""); // NOTRANS
          tab.setTaskflowId(toSwap.getTaskflowId());
          tab.setParameters(toSwap.getParameters());
          toSwap.setTaskflowId(__BLANK);
          toSwap.setParameters(null);
        }
      }
      
      _tabTracker.setNextRenderedLoc(_tabTracker.getNumRendered());
    }
    
    return _tabTracker.getNextRenderedLoc();
  }
  
  private void _showDialog(RichPopup popup)
  {
    FacesContext context = FacesContext.getCurrentInstance();
    StringBuilder toSend = new StringBuilder();
    toSend
      .append("var popup = AdfPage.PAGE.findComponent('") // NOTRANS
      .append(popup.getClientId(context))
      .append("'); ") // NOTRANS
      .append("if (!popup.isPopupVisible()) { ") // NOTRANS
      .append("var hints = {}; ") // NOTRANS
      .append("popup.show(hints);}"); // NOTRANS
    ExtendedRenderKitService erks = 
        Service.getService(context.getRenderKit(), ExtendedRenderKitService.class);
      erks.addScript(context, toSend.toString());
  }

    public void setMessenger(Messenger messenger) {
        this.messenger = messenger;
    }
    
    

    public Messenger getMessenger() {
        return messenger;
    }

    public final class TabOverflowException extends Exception 
  {
    public void handleDefault()
    {
      _showDialog(getTooManyTabsPopup());
    }
  }
  
  public static final class TabContentAreaNotReadyException extends RuntimeException {}
  
  public static final class TabContentAreaDirtyException extends Exception {}
  
  //chris muir's self-closing BTF
  public void myRegionNavigationListener(RegionNavigationEvent regionNavigationEvent) {
       String newViewId = regionNavigationEvent.getNewViewId();
       if (newViewId == null) {
           //there is no turning back
           //trans committed or rolledback already
            _removeTab(getSelectedTabIndex(), true);
       }
  }

  
  private final Tabs _tabTracker;
  
  private static final boolean _USE_SESSION_TRACKED_TABS = true;
  private Messenger messenger;
  private transient RichPanelStretchLayout _contentArea;
  private transient RichPanelStretchLayout _toolbarArea;
  private transient RichPanelGroupLayout _innerToolbar;
  private transient RichNavigationPane _tabsNavigationPane;
  private transient RichPopup _tooManyTabsPopup;
  private transient RichPopup _tabDirtyPopup;
  
  private static final String _KEY = "tabContext"; // NOTRANS
  
  static final TaskFlowId __BLANK = TaskFlowId.parse("/WEB-INF/oracle/ui/pattern/dynamicShell/infra/blank.xml#blank"); // NOTRANS
  static final int __MAX_TASKFLOWS = 15;
  
  private static final long serialVersionUID = 11112L;
}
