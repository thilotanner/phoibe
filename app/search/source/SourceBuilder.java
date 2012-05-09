package search.source;

import models.EnhancedModel;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;

public interface SourceBuilder<T extends EnhancedModel> {

    public static final String SORTABLE_INDEX_PREFIX = "#";

    XContentBuilder buildSource(T object) throws IOException;

    XContentBuilder buildMapping() throws IOException;
}
