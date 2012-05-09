package search.indexer;

import models.EnhancedModel;
import search.ElasticSearch;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

public class IndexerListener<T extends EnhancedModel> {
    @PostPersist
    @PostUpdate
    public void index(T entity)
    {
        ElasticSearch.index(entity);
    }

    @PostRemove
    public void remove(T entity)
    {
        ElasticSearch.remove(entity);
    }
}
