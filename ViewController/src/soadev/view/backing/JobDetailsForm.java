package soadev.view.backing;

import javax.faces.event.ActionEvent;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;

import oracle.adf.model.binding.DCControlBinding;
import oracle.adf.model.binding.DCControlBindingDef;
import oracle.adf.model.events.EventProducer;

import oracle.binding.BindingContainer;

import oracle.binding.ControlBinding;

import oracle.jbo.uicli.binding.JUCtrlActionBinding;

import soadev.ext.adf.taskflows.helper.Messenger;


public class JobDetailsForm extends BaseForm {

    public String cancel() throws Exception {
        Messenger messenger = new Messenger();
        messenger.setAffirmativeOutcome("rollback");
        fireEvent("produceEvent", messenger);
        System.out.println("messenger accepted? " +messenger.isAccepted());
        if (messenger.isAccepted()) {
            //stay on current page and wait for
            //the knight in shining armor
            return null;
        }
        //no one cares...
        return "rollback";
    }


    public void edit(ActionEvent actionEvent) {
        getPageFlowScope().put("editMode", true);
        raiseTransDirtyEvent();
    }
    
    public void raiseTransDirtyEvent(){
      fireEvent("transDirtyProducer", true);
    }
}
