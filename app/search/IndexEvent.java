package search;

import models.EnhancedModel;
import play.jobs.Job;
import search.indexer.Indexer;

public class IndexEvent<M extends EnhancedModel> extends Job {

    private M model;
    private IndexEventType type;
    private Indexer<M> indexer;

    public IndexEvent(M model,
                      IndexEventType type,
                      Indexer<M> indexer) {
        this.model = model;
        this.type = type;
        this.indexer = indexer;
    }

    @Override
    public void doJob() throws Exception {
        if(type == IndexEventType.INDEX) {
            indexer.index(model);
        } else if(type == IndexEventType.REMOVE) {
            indexer.remove(model);
        } else {
            throw new UnsupportedOperationException("Unsupported index event");
        }
    }
}
