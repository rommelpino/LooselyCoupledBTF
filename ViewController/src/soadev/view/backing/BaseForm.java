package soadev.view.backing;

import java.io.Serializable;

import java.util.Map;

import oracle.adf.model.BindingContext;
import oracle.adf.model.bean.DCDataRow;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.model.events.EventProducer;

import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.binding.BindingContainer;

import oracle.binding.OperationBinding;

import oracle.jbo.RowSetIterator;
import oracle.jbo.uicli.binding.JUCtrlActionBinding;


/**@author pino http://soadev.blogspot.com
 */
public class BaseForm implements Serializable {
    public BindingContainer getBindings() {
        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }
    
  protected OperationBinding getOperationBinding(String methodAction) {
      OperationBinding oper =
          getBindings().getOperationBinding(methodAction);
      if (oper == null) {
          throw new IllegalArgumentException(methodAction +
                                             " operation not found");
      }
      return oper;
  }
  
    //eager style
    public void fireEvent(EventProducer eventProducer, Object payload) {
        BindingContainer bindings = getBindings();
        ((DCBindingContainer)bindings).getEventDispatcher().fireEvent(eventProducer,
                                                                      payload);
    }
    //more convenient
    public void fireEvent(String eventProducer, Object payload) {
        fireEvent(getEventProducer(eventProducer),payload);
    }
    
    
    

    //lazy style: queue first then process

    public void queueEvent(EventProducer eventProducer, Object payload) {
        BindingContainer bindings = getBindings();
        ((DCBindingContainer)bindings).getEventDispatcher().queueEvent(eventProducer,
                                                                       payload);
    }

    public void processContextualEvents() {
        BindingContainer bindings = getBindings();
        ((DCBindingContainer)bindings).getEventDispatcher().processContextualEvents();
    }

    public Object getCurrentRowDataProvider(String iteratorName) {
        BindingContainer bindings = getBindings();
        DCIteratorBinding dcib = (DCIteratorBinding)bindings.get(iteratorName);
        RowSetIterator iter = dcib.getRowSetIterator();
        DCDataRow row = (DCDataRow)iter.getCurrentRow();
        return row.getDataProvider();
    }

    public Map<String, Object> getPageFlowScope() {
        return AdfFacesContext.getCurrentInstance().getPageFlowScope();
    }

    public boolean unbox(Boolean value) {
        return value == null ? false : value;
    }
    
  //accepts valid actionBinding
  public EventProducer getEventProducer(String producer){
    BindingContainer bindings = getBindings();
    JUCtrlActionBinding actionBinding =
        (JUCtrlActionBinding)bindings.getControlBinding(producer);
    return actionBinding.getEventProducer();
  }

    
    


}
