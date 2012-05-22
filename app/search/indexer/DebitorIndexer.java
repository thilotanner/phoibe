package search.indexer;

import models.Debitor;
import search.source.DebitorSourceBuilder;
import search.source.SourceBuilder;

public class DebitorIndexer extends Indexer<Debitor> {

    private static final String INDEX_NAME = "debitors";

    private static final String TYPE_NAME = "debitor";

    private SourceBuilder<Debitor> sourceBuilder;

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public SourceBuilder<Debitor> getSourceBuilder() {
        if(sourceBuilder == null) {
            sourceBuilder = new DebitorSourceBuilder();
        }
        return sourceBuilder;
    }
}
