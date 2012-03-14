package org.byars.struts2.actions;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.byars.struts2.models.Booking;
import org.byars.struts2.services.MyService;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.code.rees.scope.conversation.annotations.ConversationController;
import com.google.code.rees.scope.conversation.annotations.ConversationField;
import com.opensymphony.xwork2.Preparable;

@Namespace(value = "/struts2/booking")
@ConversationController
public class BookingController extends BaseController implements Preparable {

    private static final long serialVersionUID = 4170597761405470486L;

    @Autowired
    private Booking sibby;

    @ConversationField(name = "checkoutItem")
    private Booking booking;

    @Autowired
    MyService myService;

    @Action("pre-booking")
    public String preBooking() {
        return SUCCESS;
    }

    @Action("begin-booking")
    public String beginBooking() {
        return SUCCESS;
    }

    @Action("continue-to-room-selection")
    public String continueToRoomSelection() {
        System.out.println("Sibby:  " + sibby);
        sibby.setItemPrice(booking.getItemPrice());
        return SUCCESS;
    }

    @Action("continue-to-amenity-selection")
    public String continueToAmenitySelection() {
        myService.printPrice();
        return SUCCESS;
    }

    public Booking getBooking() {
        return booking;
    }

    @Override
    public void prepare() throws Exception {
        if (booking == null) {
            booking = new Booking();
        }
    }

}
