package soadev.ext.adf.contextualevents;

import oracle.adf.share.logging.ADFLogger;

import soadev.view.utils.JSFUtils;

public class EventHandler {
    private static ADFLogger _logger =
        ADFLogger.createADFLogger(EventHandler.class);
    public void setExpressionValue(String expression, Object newValue){
        _logger.fine("setExpressionValue [expression: " + expression);
        JSFUtils.setExpressionValue(expression, newValue);
    }
}
