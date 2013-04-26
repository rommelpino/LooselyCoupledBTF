package soadev.view.backing;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import oracle.adf.controller.ControllerContext;
import oracle.adf.controller.TaskFlowId;

import oracle.ui.pattern.dynamicShell.TabContext;

import org.apache.myfaces.trinidad.render.ExtendedRenderKitService;
import org.apache.myfaces.trinidad.util.Service;

import soadev.view.utils.JSFUtils;

public class Launcher {
    private static final String FRAGMENT_WRAPPER_TF =
        "/WEB-INF/taskflows/soadev/FragmentWrapper.xml#FragmentWrapper";
    private static final String JOB_LIST_TF =
        "/WEB-INF/flows/job-list-task-flow.xml#job-list-task-flow";
    private static String JOB_DETAIL_TF =
            "/WEB-INF/flows/job-details-task-flow.xml#job-details-task-flow";
    private static final String remoteAppURL =
        "http://127.0.0.1:7101/LooselyCoupledBTF/faces/adf.task-flow?";
    private static final String TASK_FLOW_ADAPTER = "/WEB-INF/taskflows/soadev/task-flow-adapter.xml#task-flow-adapter";

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
        String title = "Jobs";
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("detailTaskFlowId", JOB_DETAIL_TF);
        launchActivity(title, JOB_LIST_TF, parameterMap, false);
    }

//    public void launchMenu(ActionEvent event) {
//        UIComponent component = event.getComponent();
//        String title = (String)component.getAttributes().get("title");
//        String taskFlowId =
//            (String)component.getAttributes().get("taskFlowId");
//        String detailTaskFlowId =
//            (String)component.getAttributes().get("detailTaskFlowId");
//        Map<String, Object> parameterMap =
//            (Map<String, Object>)component.getAttributes().get("parameterMap");
//        if (parameterMap == null) {
//            parameterMap = new HashMap<String, Object>();
//        }
//        parameterMap.put("detailTaskFlowId", detailTaskFlowId);
//        Boolean newTab =
//            Boolean.parseBoolean(component.getAttributes().get("newTab").toString());
//        launchActivity(title, taskFlowId, parameterMap, newTab);
//    }

    public void launchMenuOnNewWindow(ActionEvent event) {
        String title = "Title";
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("detailTaskFlowId", JOB_DETAIL_TF);
        String url = getTaskFlowURL(remoteAppURL, JOB_LIST_TF, parameterMap);
        openLink(url);
    }

    public void launchRemoteOnTab(ActionEvent event) {
        String title = "Job List Remote";
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("taskFlowId", JOB_LIST_TF);
        parameterMap.put("remoteAppURL", remoteAppURL);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("detailTaskFlowId", JOB_DETAIL_TF);
        parameterMap.put("parameterMap", params);
        JSFUtils.setRequestAttribute("parameterMap", parameterMap);
        launchActivity(title, TASK_FLOW_ADAPTER, parameterMap, true);
    }

    public String getTaskFlowURL(String remoteAppURL, String taskFlowId,
                                 Map<String, Object> parameterMap) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("taskFlowId", taskFlowId);
        if (parameterMap != null && !parameterMap.isEmpty()) {
            params.put("parameters", buildParameterString(parameterMap));
        }
        String url =
            ControllerContext.getInstance().getTaskFlowURL(false, TaskFlowId.parse(FRAGMENT_WRAPPER_TF),
                                                           params);
        if (remoteAppURL == null || "".equals(remoteAppURL)) {
            url = "/" + url;
        } else {
            String[] array = url.split("\\/faces\\/adf.task-flow\\?");
            url = remoteAppURL + array[1];
        }
        System.out.println("url: " + url);
        return url;
    }

    private String buildParameterString(Map<String, Object> parameterMap) {
        if (parameterMap == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
            sb.append(entry.getKey());
            sb.append(":");
            sb.append(entry.getValue());
            sb.append(";");
        }
        return sb.toString();
    }

    public void openLink(String destination) {
        ExtendedRenderKitService erks =
            Service.getRenderKitService(FacesContext.getCurrentInstance(),
                                        ExtendedRenderKitService.class);
        StringBuilder script = new StringBuilder();
        script.append("window.open('").append(destination).append("');");

        erks.addScript(FacesContext.getCurrentInstance(), script.toString());
    }
}
