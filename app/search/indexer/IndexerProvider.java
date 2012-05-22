package search.indexer;

import models.Contact;
import models.Creditor;
import models.Debitor;
import models.EnhancedModel;
import models.MetricProduct;
import models.Order;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilder;
import search.ElasticSearch;
import search.IndexEvent;
import search.IndexEventType;

import java.util.HashMap;
import java.util.Map;

public class IndexerProvider {

    private Map<Class, Indexer> indexerMap;

    public IndexerProvider() {
        indexerMap = new HashMap<Class, Indexer>();
        indexerMap.put(Contact.class, new ContactIndexer());
        indexerMap.put(MetricProduct.class, new MetricProductIndexer());
        indexerMap.put(Order.class, new OrderIndexer());
        indexerMap.put(Debitor.class, new DebitorIndexer());
        indexerMap.put(Creditor.class, new CreditorIndexer());
    }

    public <M extends EnhancedModel> void index(M entity)
    {
        Indexer indexer = getIndexer(entity.getClass());

        if(entity.id == null) {
            throw new IllegalArgumentException(String.format("Unable to index entity without id: %s", entity.toString()));
        }

        // schedule an indexer job
        IndexEvent<M> event = new IndexEvent<M>(entity.id, (Class<M>) entity.getClass(), indexer, IndexEventType.INDEX);
        event.now();
    }

    public <M extends EnhancedModel> void remove(M entity)
    {
        Indexer indexer = getIndexer(entity.getClass());

        if(entity.id == null) {
            throw new IllegalArgumentException(String.format("Unable to index entity without id: %s", entity.toString()));
        }

        // schedule an indexer job
        IndexEvent<M> event = new IndexEvent<M>(entity.id, (Class<M>) entity.getClass(), indexer, IndexEventType.REMOVE);
        event.now();
    }

    public <M extends EnhancedModel> void deleteIndex(Class<M> clazz) {
        Indexer indexer = getIndexer(clazz);

        indexer.removeIndex();
    }

    public <M extends EnhancedModel> SearchRequestBuilder builder(QueryBuilder builder, Class<M> clazz) {
        Indexer indexer = getIndexer(clazz);
        return ElasticSearch.client().prepareSearch(indexer.getIndexName()).setSearchType(SearchType.QUERY_THEN_FETCH).setQuery(builder);
    }

    private Indexer getIndexer(Class clazz)
    {
        if(!indexerMap.containsKey(clazz)) {
            throw new IllegalArgumentException(String.format("No indexer for class found: %s", clazz.getName()));
        }

        return indexerMap.get(clazz);

    }
}
