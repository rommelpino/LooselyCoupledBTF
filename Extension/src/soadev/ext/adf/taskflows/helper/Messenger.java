package soadev.ext.adf.taskflows.helper;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import oracle.adf.view.rich.component.rich.fragment.RichRegion;

import soadev.view.utils.JSFUtils;


public class Messenger {

    private boolean accepted = false;
    private String affirmativeOutcome;
    private String negativeOutcome;
    private String outcome;
    private String region;


    public void accept() {
        accepted = true;
    }

    public void affirmativeOutcomeCallback() {
        outcome = getAffirmativeOutcome();
        handleOuterPageAction();
    }

    public void negativeOutcomeCallback() {
        outcome=getNegativeOutcome();
        handleOuterPageAction();
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void handleOuterPageAction() {
        UIComponent regionComponent = JSFUtils.findComponentInRoot(region);
        if (regionComponent instanceof RichRegion) {
            FacesContext fc = FacesContext.getCurrentInstance();
            ExpressionFactory ef = fc.getApplication().getExpressionFactory();
            ELContext elc = fc.getELContext();
            JSFUtils.setRequestAttribute("messenger", this);
            MethodExpression me =
                ef.createMethodExpression(elc, "#{messenger.getOutcome}",
                                          String.class, new Class[] { });
            ((RichRegion)regionComponent).queueActionEventInRegion(me, null,
                                                                   null, false,
                                                                   -1, -1,
                                                                   PhaseId.ANY_PHASE);
        }
    }

  
    public void setAffirmativeOutcome(String affirmativeOutcome) {
        this.affirmativeOutcome = affirmativeOutcome;
    }

    public String getAffirmativeOutcome() {
        return affirmativeOutcome;
    }

    public void setNegativeOutcome(String negativeOutcome) {
        this.negativeOutcome = negativeOutcome;
    }

    public String getNegativeOutcome() {
        return negativeOutcome;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegion() {
        return region;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getOutcome() {
        return outcome;
    }
}


