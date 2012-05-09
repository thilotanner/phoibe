package search.indexer;

import models.Contact;
import search.source.ContactSourceBuilder;
import search.source.SourceBuilder;

public class ContactIndexer extends Indexer<Contact> {

    private static final String INDEX_NAME = "contacts";

    private static final String TYPE_NAME = "contact";

    private SourceBuilder<Contact> sourceBuilder;

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public SourceBuilder<Contact> getSourceBuilder() {
        if(sourceBuilder == null) {
            sourceBuilder = new ContactSourceBuilder();
        }
        return sourceBuilder;
    }
}
