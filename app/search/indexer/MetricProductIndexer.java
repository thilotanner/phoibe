package search.indexer;

import models.MetricProduct;
import search.source.MetricProductSourceBuilder;
import search.source.SourceBuilder;

public class MetricProductIndexer extends Indexer<MetricProduct> {

    private static final String INDEX_NAME = "metricproducts";

    private static final String TYPE_NAME = "metricproduct";

    private SourceBuilder<MetricProduct> sourceBuilder;

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public SourceBuilder<MetricProduct> getSourceBuilder() {
        if(sourceBuilder == null) {
            sourceBuilder = new MetricProductSourceBuilder();
        }
        return sourceBuilder;
    }
}
