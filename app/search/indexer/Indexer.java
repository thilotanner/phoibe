package search.indexer;

import models.EnhancedModel;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import search.ElasticSearch;
import search.source.SourceBuilder;

import java.io.IOException;

public abstract class Indexer<T extends EnhancedModel> {

    public Indexer() {
        createIndexIfNotExists();
        putMapping();
    }

    public void index(T model) {
        try {
            XContentBuilder source = getSourceBuilder().buildSource(model);
            ElasticSearch.client()
                    .prepareIndex(getIndexName(), getTypeName(), model.getId().toString())
                    .setSource(source)
                    .execute()
                    .actionGet();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove(Long modelId) {
        ElasticSearch.client()
                .prepareDelete(getIndexName(), getTypeName(), modelId.toString())
                .execute()
                .actionGet();
    }

    public void createIndexIfNotExists() {
        if(!ElasticSearch.client().admin().indices().prepareExists(getIndexName()).execute().actionGet().exists()) {
            ElasticSearch.client().admin().indices().create(new CreateIndexRequest(getIndexName())).actionGet();
        }
    }

    private void putMapping() {
        try {
            XContentBuilder mapping = getSourceBuilder().buildMapping();
            ElasticSearch.client().admin().indices()
                    .preparePutMapping(getIndexName())
                    .setType(getTypeName()).setSource(mapping)
                    .execute().actionGet();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeIndex() {
        ListenableActionFuture<DeleteIndexResponse> action =
                ElasticSearch.client().admin().indices().prepareDelete(getIndexName()).execute();
        try {
            action.get(); // wait until mapping is deleted
        } catch (Exception e) {
            throw new RuntimeException("Unable to delete mapping", e);
        }
    }

    public abstract String getIndexName();

    public abstract String getTypeName();

    public abstract SourceBuilder<T> getSourceBuilder();
}
