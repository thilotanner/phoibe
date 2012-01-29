package util.check;

import play.data.validation.Check;

public class NumericalCheck extends Check {

    @Override
    public boolean isSatisfied(Object validatedObject, Object value) {
        if(value == null || ((String) value).isEmpty()) {
            return true;
        }

        try {
            Double.parseDouble((String) value);
        } catch(NumberFormatException e) {
            return false;
        }
        return true;
    }
}
