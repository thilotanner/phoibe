package search.mapping.impl;

import org.elasticsearch.common.xcontent.XContentBuilder;
import util.reflection.ReflectionUtil;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Field mapper for simple, single-valued types
 * 
 * @param <M>
 *            the generic model type which owns this field
 */
public class SimpleFieldMapper<M> extends AbstractFieldMapper<M> {

	public SimpleFieldMapper(Field field) {
		super(field);
	}

	@Override
	public void addToMapping(XContentBuilder builder, String prefix) throws IOException {
		String name = field.getName();
		String type = getFieldType();
		if (prefix != null) {
			addField(prefix + name, type, meta, sortable, builder);
		} else {
			addField(name, type, meta, sortable, builder);
		}
	}

	@Override
	public void addToDocument(M model, XContentBuilder builder, String prefix) throws IOException {
		String name = field.getName();
		Object value = ReflectionUtil.getFieldValue(model, field);

		if (value != null) {
			if (prefix != null) {
				builder.field(prefix + name, value);
			} else {
				builder.field(name, value);
			}
		}

        if(sortable) {
            builder.field(SORTABLE_INDEX_PREFIX + name, value);
        }
	}

	protected String getFieldType() {
		if (meta != null && meta.type().length() > 0) {
			// Type was explicitly set, use it
			return meta.type();

		} else {
			// Detect type automatically
			return detectFieldType(field.getType());
		}
	}

}