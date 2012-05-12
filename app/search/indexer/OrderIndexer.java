package search.indexer;

import models.Order;
import search.source.OrderSourceBuilder;
import search.source.SourceBuilder;

public class OrderIndexer extends Indexer<Order> {

    private static final String INDEX_NAME = "orders";

    private static final String TYPE_NAME = "order";

    private SourceBuilder<Order> sourceBuilder;

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public SourceBuilder<Order> getSourceBuilder() {
        if(sourceBuilder == null) {
            sourceBuilder = new OrderSourceBuilder();
        }
        return sourceBuilder;
    }
}
