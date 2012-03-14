package org.byars.struts2.actions;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.byars.struts2.models.User;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.code.rees.scope.conversation.ConversationUtil;
import com.google.code.rees.scope.conversation.annotations.ConversationController;
import com.opensymphony.xwork2.ActionSupport;

@ConversationController
public abstract class BaseController extends ActionSupport {

    private static final long serialVersionUID = -7888401345677144192L;
    @Autowired
    User user;

    @Action(value = "base-begin", results = { @Result(
            name = SUCCESS,
            location = "/struts2/booking/pre-booking.action",
            type = "conversationRedirect") })
    public String begin() {
        user.setAccountName(ConversationUtil.getId("base"));
        return SUCCESS;
    }

    public String end() {
        return SUCCESS;
    }
}
