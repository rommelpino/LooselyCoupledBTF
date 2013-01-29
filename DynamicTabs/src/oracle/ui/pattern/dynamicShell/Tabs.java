package oracle.ui.pattern.dynamicShell;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;


/**
 * This class is not meant for public API.
 * 
 * Copyright 2010, Oracle USA, Inc.
 */
public class Tabs implements Serializable
{
  List<Tab> getTabs()
  {
    return _tabs;
  }
  
  int getSelectedTabIndex()
  {
    return _selectedTabIndex;
  }
  
  void setSelectedTabIndex(int index)
  {
    _selectedTabIndex = index;
  }

  void setNextRenderedLoc(int nextRenderedLoc)
  {
    _nextRenderedLoc = nextRenderedLoc;
  }
  
  int getNextRenderedLoc()
  {
    return _nextRenderedLoc;
  }
  
  void setNumRendered(int numRendered)
  {
    _numRendered = numRendered;
  }
  
  int getNumRendered()
  {
    return _numRendered;
  }
  
  void setTabsRendered(boolean rendered)
  {
    _renderTabs = rendered;
  }
  
  boolean isTabsRendered()
  {
    return _renderTabs;
  }
  
  Tabs()
  {
    for (int i=0; i<TabContext.__MAX_TASKFLOWS; i++)
    {
      _tabs.add(new Tab(i, TabContext.__BLANK));
    }
  }
  
  private final List<Tab> _tabs = new ArrayList<Tab>();
  private int _selectedTabIndex = -1;
  private int _nextRenderedLoc = 0;
  private int _numRendered = 0;  
  private boolean _renderTabs = true;

}
