package search;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

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

    private static <M extends Model> ModelMapper<M> getMapper(Class<M> clazz) {
		if (mappers.containsKey(clazz)) {
			return (ModelMapper<M>) mappers.get(clazz);
		}

		ModelMapper<M> mapper = MapperFactory.getMapper(clazz);
        ElasticSearchAdapter.startIndex(client, mapper);
		mappers.put(clazz, mapper);

		return mapper;
	}
}
