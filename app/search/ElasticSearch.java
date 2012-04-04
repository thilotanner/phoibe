package search;

import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.search.facet.AbstractFacetBuilder;
import play.db.jpa.Model;
import search.adapter.ElasticSearchAdapter;
import search.mapping.MapperFactory;
import search.mapping.MappingUtil;
import search.mapping.ModelMapper;

import java.util.HashMap;
import java.util.Map;

public class ElasticSearch {

    private static Node node;
    static Client client;
	private static Map<Class<? extends Model>, ModelMapper<? extends Model>> mappers;

    public static void start() {
        // Start Node Builder
		Builder settings = ImmutableSettings.settingsBuilder();
		settings.put("client.transport.sniff", true);
		settings.build();

        NodeBuilder nb = NodeBuilder.nodeBuilder().settings(settings).local(true).client(false).data(true);
        node = nb.node();
        client = node.client();

        mappers = new HashMap<Class<? extends Model>, ModelMapper<? extends Model>>();
    }

    public static void stop() {
        client.close();
        node.stop();
        node.close();
    }

    public static <M extends Model> void index(M model) {
		Class<Model> clazz = (Class<Model>) model.getClass();

		// Check if object is searchable
		if (!MappingUtil.isSearchable(clazz)) {
			throw new IllegalArgumentException("model is not searchable");
		}

        ModelMapper<Model> mapper = getMapper(clazz);
		ElasticSearchIndexEvent event = new ElasticSearchIndexEvent(model, mapper, ElasticSearchIndexEventType.INDEX);
		event.now();
	}

    public static <M extends Model> void delete(M model) {
        Class<Model> clazz = (Class<Model>) model.getClass();

		// Check if object is searchable
		if (!MappingUtil.isSearchable(clazz)) {
			throw new IllegalArgumentException("model is not searchable");
		}

        ModelMapper<Model> mapper = getMapper(clazz);
		ElasticSearchIndexEvent event = new ElasticSearchIndexEvent(model, mapper, ElasticSearchIndexEventType.DELETE);
		event.now();
    }

    public static <M extends Model> void deleteIndex(Class<M> clazz) {
        // Check if object is searchable
        if (!MappingUtil.isSearchable(clazz)) {
            throw new IllegalArgumentException("model is not searchable");
        }

        ModelMapper<M> mapper = getMapper(clazz);
        ListenableActionFuture<DeleteIndexResponse> action =
                client.admin().indices().prepareDelete(mapper.getIndexName()).execute();
        try {
            action.get(); // wait until mapping is deleted
        } catch (Exception e) {
            throw new RuntimeException("Unable to delete mapping", e);
        }
    }
    
    private static <M extends Model> ModelMapper<M> getMapper(Class<M> clazz) {
		if (mappers.containsKey(clazz)) {
			return (ModelMapper<M>) mappers.get(clazz);
		}

		ModelMapper<M> mapper = MapperFactory.getMapper(clazz);
        ElasticSearchAdapter.startIndex(client, mapper);
		mappers.put(clazz, mapper);

		return mapper;
	}

    /**
	 * Build a SearchRequestBuilder
	 *
	 * @param <T>
	 *            the generic type
	 * @param query
	 *            the query builder
	 * @param clazz
	 *            the clazz
	 *
	 * @return the search request builder
	 */
	static <T extends Model> SearchRequestBuilder builder(QueryBuilder query, Class<T> clazz) {
		ModelMapper<T> mapper = getMapper(clazz);
		String index = mapper.getIndexName();
		return client.prepareSearch(index).setSearchType(SearchType.QUERY_THEN_FETCH).setQuery(query);
	}

	/**
	 * Build a Query
	 *
	 * @param <T>
	 *            the generic type
	 * @param query
	 *            the query builder
	 * @param clazz
	 *            the clazz
	 *
	 * @return the query
	 */
	public static <T extends Model> Query<T> query(QueryBuilder query, Class<T> clazz) {
		return new Query<T>(clazz, query);
	}

	/**
	 * Search with optional facets.
	 *
	 * @param <T>
	 *            the generic type
	 * @param query
	 *            the query builder
	 * @param clazz
	 *            the clazz
	 * @param facets
	 *            the facets
	 *
	 * @return the search results
	 */
	public static <T extends Model> SearchResults<T> search(QueryBuilder query, Class<T> clazz, AbstractFacetBuilder... facets) {
		return search(query, clazz, false, facets);
	}

	/**
	 * Search with optional facets. Hydrates entities
	 *
	 * @param <T>
	 *            the generic type
	 * @param queryBuilder
	 *            the query builder
	 * @param clazz
	 *            the clazz
	 * @param facets
	 *            the facets
	 *
	 * @return the search results
	 */
	public static <T extends Model> SearchResults<T> searchAndHydrate(QueryBuilder queryBuilder, Class<T> clazz, AbstractFacetBuilder... facets) {
		return search(queryBuilder, clazz, true, facets);
	}

	/**
	 * Faceted search, hydrates entities if asked to do so.
	 *
	 * @param <T>
	 *            the generic type
	 * @param query
	 *            the query builder
	 * @param clazz
	 *            the clazz
	 * @param hydrate
	 * 			  hydrate JPA entities
	 * @param facets
	 *            the facets
	 *
	 * @return the search results
	 */
	private static <T extends Model> SearchResults<T> search(QueryBuilder query, Class<T> clazz, boolean hydrate, AbstractFacetBuilder... facets) {
		// Build a query for this search request
		Query<T> search = query(query, clazz);

		// Control hydration
		search.hydrate(hydrate);

		// Add facets
		for( AbstractFacetBuilder facet : facets ) {
			search.addFacet(facet);
		}

		return search.fetch();
	}
}
