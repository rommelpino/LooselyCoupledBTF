package oracle.ui.pattern.dynamicShell;
import java.util.Map;
import soadev.ext.adf.taskflows.helper.Messenger;

public class DynamicShellHelper {
      public void markCurrentTabDirty(TabContext tabContext, Boolean isDirty) {
          tabContext.markCurrentTabDirty(isDirty);
      }

      public void handleMessage(TabContext tabContext, Messenger messenger) {
          messenger.accept();
          tabContext.setMessenger(messenger);
          messenger.setRegion("pt_region" + tabContext.getSelectedTabIndex());
          System.out.println(messenger.getRegion());
          tabContext.showTabDirtyPopup();
      }

      public void launchActivity(TabContext tabContext, String title, String taskFlowId,
                                 Map<String, Object> parameterMap,
                                 boolean newTab) {
          try {
              
              if (newTab) { //allows multiple instance of taskflow.

                  tabContext.addTab(title, taskFlowId, parameterMap);
              } else {
                  tabContext.addOrSelectTab(title, taskFlowId, parameterMap);
              }
          } catch (TabContext.TabOverflowException toe) {
              // causes a dialog to be displayed to the user saying that there are
              // too many tabs open - the new tab will not be opened...
              toe.handleDefault();
          }
      }

  }
