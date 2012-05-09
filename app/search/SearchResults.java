package search;

import models.EnhancedModel;

import java.util.List;

public class SearchResults<M extends EnhancedModel> {
    /** The total count. */
    public long totalCount;

    /** The objects. */
    public List<M> objects;

    public SearchResults(long totalCount, List<M> objects) {
        this.totalCount = totalCount;
        this.objects = objects;
    }
}
