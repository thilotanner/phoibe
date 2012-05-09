package search;

import models.EnhancedModel;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import play.Logger;
import search.source.SourceBuilder;

import java.util.ArrayList;
import java.util.List;

public class Query<M extends EnhancedModel> {

    private final QueryBuilder queryBuilder;
    private final Class<M> clazz;
    private final List<SortBuilder> sorts;

    private Integer from = null;
    private Integer size = null;

    Query(QueryBuilder queryBuilder, Class<M> clazz) {
        this.queryBuilder = queryBuilder;
        this.clazz = clazz;
        this.sorts = new ArrayList<SortBuilder>();
    }

    public Query<M> from(int from) {
        this.from = from;
        return this;
    }

    public Query<M> size(int size) {
        this.size = size;
        return this;
    }

    public Query<M> addSort(String field, SortOrder order) {
        sorts.add(SortBuilders.fieldSort(SourceBuilder.SORTABLE_INDEX_PREFIX + field).order(order));

        return this;
    }

    public Query<M> addSort(SortBuilder sort) {
        sorts.add(sort);
        return this;
    }

    public SearchResults<M> fetch() {
        // Build request
        SearchRequestBuilder request = ElasticSearch.builder(queryBuilder, clazz);

        // Sorting
        for (SortBuilder sort : sorts) {
            request.addSort(sort);
        }

        // Paging
        if (from != null) {
            request.setFrom(from);
        }
        if (size != null) {
            request.setSize(size);
        }

        if (Logger.isDebugEnabled()) {
            Logger.debug("ES Query: %s", queryBuilder.toString());
        }

        SearchResponse searchResponse = request.execute().actionGet();

        // total hits
        long totalHits = searchResponse.getHits().totalHits();

        // fetch objects
        final List<Long> ids = new ArrayList<Long>();
        for (SearchHit hit : searchResponse.getHits()) {
            ids.add(Long.parseLong(hit.id()));
        }

        List<M> results = new ArrayList<M>();
        if (ids.size() > 0) {
            results = EnhancedModel.findByIds(ids, clazz);
        }
        return new SearchResults<M>(totalHits, results);
    }
}
