package search;

import play.db.jpa.Model;
import play.jobs.Job;
import search.adapter.ElasticSearchAdapter;
import search.mapping.ModelMapper;

public class ElasticSearchIndexEvent<M extends Model> extends Job {

    private M model;
    private ModelMapper<M> mapper;
    private ElasticSearchIndexEventType type;

    public ElasticSearchIndexEvent(M model,
                                   ModelMapper<M> mapper,
                                   ElasticSearchIndexEventType type) {
        this.model = model;
        this.mapper = mapper;
        this.type = type;
    }

    @Override
    public void doJob() throws Exception {
        if(type == ElasticSearchIndexEventType.INDEX) {
            ElasticSearchAdapter.indexModel(ElasticSearch.client, mapper, model);
        } else if(type == ElasticSearchIndexEventType.DELETE) {
            ElasticSearchAdapter.deleteModel(ElasticSearch.client, mapper, model);
        } else {
            throw new UnsupportedOperationException("Unsupported index event");
        }
    }
}
