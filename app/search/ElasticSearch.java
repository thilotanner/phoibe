package search;

import models.EnhancedModel;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import search.indexer.IndexerProvider;

public class ElasticSearch {

    private static Node node;
    private static Client client;

    private static IndexerProvider indexerProvider;

    public static void start() {
        // Start Node Builder
		Builder settings = ImmutableSettings.settingsBuilder();
		settings.put("client.transport.sniff", true);
		settings.build();

        NodeBuilder nb = NodeBuilder.nodeBuilder().settings(settings).local(true).client(false).data(true);
        node = nb.node();
        client = node.client();

        indexerProvider = new IndexerProvider();
    }

    public static void stop() {
        client.close();
        node.stop();
        node.close();
    }

    public static Client client() {
        if(client == null) {
            throw new RuntimeException("ElasticSearch not started");
        }

        return client;
    }

    public static <M extends EnhancedModel> void index(M model) {
        indexerProvider.index(model);
	}

    public static <M extends EnhancedModel> void remove(M model) {
        indexerProvider.remove(model);
    }

    public static <M extends EnhancedModel> void deleteIndex(Class<M> clazz) {
        indexerProvider.deleteIndex(clazz);
    }

    public static <M extends EnhancedModel> Query<M> query(QueryBuilder queryBuilder,
                                                           Class<M> clazz) {
        return new Query<M>(queryBuilder, clazz);
    }

    public static <M extends EnhancedModel> SearchRequestBuilder builder(QueryBuilder queryBuilder,
                                                                         Class<M> clazz) {
        return indexerProvider.builder(queryBuilder, clazz);
    }
}
