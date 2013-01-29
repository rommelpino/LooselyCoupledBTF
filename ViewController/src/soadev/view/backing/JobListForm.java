package soadev.view.backing;

import java.util.HashMap;
import java.util.Map;

import javax.faces.event.ActionEvent;

import oracle.adf.model.BindingContext;

import oracle.adf.model.binding.DCBindingContainer;

import oracle.adfinternal.view.faces.model.binding.FacesCtrlHierBinding;

import oracle.binding.BindingContainer;
import oracle.binding.ControlBinding;

import oracle.jbo.uicli.binding.JUCtrlActionBinding;

import soadev.model.Job;


public class JobListForm extends BaseForm {
    //workaroud as I am failure to raise contextual
    //events from table currency change
    public void jobSelected(ActionEvent event) {
        if (unbox((Boolean)getPageFlowScope().get("initiateLaunchActivityEvent"))){//from TF param
            Job job = (Job)getCurrentRowDataProvider("findAllJobsIterator");
            Map<String, Object> payload = new HashMap<String, Object>();
            payload.put("jobId", job.getJobId());
            payload.put("taskFlowId",
                        getPageFlowScope().get("detailTaskFlowId"));
            payload.put("title", "Job: " + job.getJobId());
            fireEvent("produceEvent", payload);
        }
    }
}

