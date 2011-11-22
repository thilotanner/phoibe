package util.check;

import models.Contact;
import play.data.validation.Check;
import play.i18n.Messages;

public class ContactNameCheck extends Check {

    @Override
    public boolean isSatisfied(Object validatedObject, Object value) {
        if(value == null || ((String) value).isEmpty()) {
            // check if there's a company
            Contact contact = (Contact) validatedObject;

            setMessage("validation.either", Messages.get("contact.company"), Messages.get("contact.lastName"));
            return contact.company != null && !contact.company.isEmpty();
        }
        return true;
    }
}
