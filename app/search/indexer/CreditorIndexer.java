package search.indexer;

import models.Creditor;
import search.source.CreditorSourceBuilder;import search.source.SourceBuilder;

public class CreditorIndexer extends Indexer<Creditor> {

    private static final String INDEX_NAME = "creditors";

    private static final String TYPE_NAME = "creditor";

    private SourceBuilder<Creditor> sourceBuilder;

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public SourceBuilder<Creditor> getSourceBuilder() {
        if(sourceBuilder == null) {
            sourceBuilder = new CreditorSourceBuilder();
        }
        return sourceBuilder;
    }
}
