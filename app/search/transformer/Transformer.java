package search.transformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import search.SearchResults;
import search.util.ReflectionUtil;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import play.Logger;
import play.db.jpa.Model;

public class Transformer<T extends Model> {
    /**
	 * To search results.
	 *
	 * @param <T> the generic type
	 * @param searchResponse the search response
	 * @param clazz the clazz
	 * @return the search results
	 */
	public static <T extends Model> SearchResults<T> toSearchResults(SearchResponse searchResponse, Class<T> clazz) {
		// Get Total Records Found
		long count = searchResponse.hits().totalHits();

		// Init List
		List<T> objects = new ArrayList<T>();

		// Loop on each one
		for (SearchHit h : searchResponse.hits()) {
			// Init Model Class
			Logger.debug("Starting Record!");
			T o = ReflectionUtil.newInstance(clazz);

			// Get Data Map
			Map<String, Object> map = h.sourceAsMap();
			Logger.debug("Record Map: %s", map);

			// Bind Data
			for (Map.Entry<String, Object> e : map.entrySet()) {
				ReflectionUtil.setFieldValue(o, e.getKey(), e.getValue());
			}

			// Log Debug
			Logger.debug("Model Instance: %s", o);
			objects.add(o);
		}

		// Return Results
		return new SearchResults<T>(count, objects, searchResponse.facets());
	}
}
