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

import soadev.view.utils.JSFUtils;


public class JobListForm extends BaseForm {

    public void jobSelected(ActionEvent event) {
        Job job = (Job)getCurrentRowDataProvider("findAllJobsIterator");
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("jobId", job.getJobId());
        payload.put("taskFlowId", getPageFlowScope().get("detailTaskFlowId"));
        payload.put("title", "Job: " + job.getJobId());
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("jobId", job.getJobId());
        payload.put("parameterMap", parameterMap);
        JSFUtils.setRequestAttribute("parameterMap", parameterMap);
        fireEvent("produceEvent", payload);
    }

}

