package search;

import models.EnhancedModel;
import play.db.jpa.JPA;
import play.jobs.Job;
import search.indexer.Indexer;

public class IndexEvent<M extends EnhancedModel> extends Job {

    private Long modelId;
    private Class<M> modelClass;
    private Indexer<M> indexer;
    private IndexEventType type;

    public IndexEvent(Long modelId,
                      Class<M> modelClass,
                      Indexer<M> indexer,
                      IndexEventType type) {
        this.modelId = modelId;
        this.modelClass = modelClass;
        this.indexer = indexer;
        this.type = type;
    }

    @Override
    public void doJob() throws Exception {

        if(type == IndexEventType.INDEX) {
            M model = JPA.em().find(modelClass, modelId);

            if(model == null) {
                throw new IllegalArgumentException(String.format("Unable to index non-existing model. ID: %d Class: %s",
                        modelId, modelClass));
            }

            indexer.index(model);
        } else if(type == IndexEventType.REMOVE) {
            indexer.remove(modelId);
        } else {
            throw new UnsupportedOperationException("Unsupported index event");
        }
    }
}
