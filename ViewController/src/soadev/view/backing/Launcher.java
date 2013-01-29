package soadev.view.backing;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;

import oracle.ui.pattern.dynamicShell.TabContext;

public class Launcher {
    protected void launchActivity(String title, String taskflowId,
                                  Map<String, Object> parameterMap,
                                  boolean newTab) {
        try {
            TabContext tabContext = TabContext.getCurrentInstance();
            if (newTab) { //allows multiple instance of taskflow.

                tabContext.addTab(title, taskflowId, parameterMap);
            } else {
                tabContext.addOrSelectTab(title, taskflowId, parameterMap);
            }
        } catch (TabContext.TabOverflowException toe) {
            // causes a dialog to be displayed to the user saying that there are
            // too many tabs open - the new tab will not be opened...
            toe.handleDefault();
        }
    }

    public void launchJobList(ActionEvent event) {
        String taskflowId =
            "/WEB-INF/flows/job-list-task-flow.xml#job-list-task-flow";
        String title = "Jobs";
        launchActivity(title, taskflowId, null, true);
    }

    public void launchJobDetails(ActionEvent event) {
        String taskflowId =
            "/WEB-INF/flows/job-details-task-flow.xml#job-details-task-flow";
        String jobId = "EMPTY";
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("jobId", jobId);
        String title = "Job: ";
        launchActivity(title, taskflowId, parameters, true);
    }

    public void launchMenu(ActionEvent event) {
        UIComponent component = event.getComponent();
        String title = (String)component.getAttributes().get("title");
        String taskFlowId =
            (String)component.getAttributes().get("taskFlowId");
      String detailTaskFlowId =
          (String)component.getAttributes().get("detailTaskFlowId");
        Map<String, Object> parameterMap =
            (Map<String, Object>)component.getAttributes().get("parameterMap");
        if (parameterMap == null) {
            parameterMap = new HashMap<String, Object>();
        }
        parameterMap.put("detailTaskFlowId", detailTaskFlowId);
        parameterMap.put("initiateLaunchActivityEvent", true);
        Boolean newTab =
            Boolean.parseBoolean(component.getAttributes().get("newTab").toString());

        launchActivity(title, taskFlowId, parameterMap, newTab);
    }
}
