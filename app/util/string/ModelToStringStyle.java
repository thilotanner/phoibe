package util.string;

import models.EnhancedModel;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ModelToStringStyle extends ToStringStyle {

    private static ToStringStyle modelToStringStyle;

    public static ToStringStyle getModelToStringStyle() {
        if(modelToStringStyle == null) {
            modelToStringStyle = new ModelToStringStyle();
        }
        return modelToStringStyle;
    }

    private ModelToStringStyle() {
        super();
        setUseShortClassName(true);
        setUseIdentityHashCode(false);
    }

    @Override
    public void append(StringBuffer buffer, String fieldName, Object value, Boolean fullDetail) {

        if(value instanceof EnhancedModel) {
            EnhancedModel model = (EnhancedModel) value;
            super.append(buffer, fieldName, model.id);
        } else {
            super.append(buffer, fieldName, value, fullDetail);
        }
    }

    @Override
    protected void appendDetail(StringBuffer buffer, String fieldName, Collection collection) {
        
        if(collection == null) {
            super.appendDetail(buffer, fieldName, collection);
            return;
        }
        
        List list = new ArrayList(collection);
        if(list.size() > 0 &&
           list.get(0) instanceof EnhancedModel) {
            // possibly a list of enhanced models
            
            buffer.append(getContentStart());
            
            boolean written = false;
            for(Object object : list) {
                try {
                    EnhancedModel model = (EnhancedModel) object;
                    
                    if(written) {
                        buffer.append(getArraySeparator());
                    }

                    buffer.append(model.id);
                    written = true;
                } catch(Exception e) {
                    // skip
                }
            }

            buffer.append(getContentEnd());

            return;
        }

        super.appendDetail(buffer, fieldName, collection);
    }
}
