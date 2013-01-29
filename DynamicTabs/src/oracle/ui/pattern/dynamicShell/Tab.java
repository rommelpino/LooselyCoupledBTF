package oracle.ui.pattern.dynamicShell;

import java.io.Serializable;

import java.util.Collections;

import java.util.List;

import java.util.Map;

import oracle.adf.controller.TaskFlowId;
import oracle.adf.controller.internal.binding.DCTaskFlowBinding;
import oracle.adf.model.BindingContext;


/**
 * This class is not meant for public API.
 * 
 * Copyright 2010, Oracle USA, Inc.
 */
public class Tab implements Serializable
{
  public void setTitle(String title)
  {
    _localizedName = title;
  }
  
  public String getTitle()
  {
    return _localizedName;
  }
  
  public int getIndex()
  {
    return _index;
  }
  
  public DCTaskFlowBinding getBinding()
  {
    return (DCTaskFlowBinding) BindingContext.getCurrent(
      ).getCurrentBindingsEntry().get("r" + _index); // NOTRANS
  }
  
  public void setActive(boolean rendered)
  {
    _isActive = rendered;
    
    if (!_isActive)
      setDirty(false);
  }
  
  public boolean isActive()
  {
    return _isActive;
  }
  
  public void setDirty(boolean isDirty)
  {
    _isDirty = isDirty;
  }
  
  public boolean isDirty()
  {
    return _isDirty;
  }

  public void setTaskflowId(TaskFlowId id)
  {
    _taskflowId = id;
  }
  
  public TaskFlowId getTaskflowId()
  {
    return _taskflowId;
  }
  
  public void setParameters(Map<String,Object> parameters) 
  {
    _parameters = parameters;    
  }
  
  public Map<String,Object> getParameters() 
  {
    return _parameters;
  }
  
  public List<Tab> getChildren()
  {
    return Collections.emptyList();
  }

  Tab(int index, TaskFlowId id)
  {
    _index = index;
    _taskflowId = id;
  }
  
  private final int _index;
  
  private boolean _isActive = false;
  private boolean _isDirty = false;
  private String _localizedName;
  private TaskFlowId  _taskflowId;
  private Map<String,Object> _parameters;
}
