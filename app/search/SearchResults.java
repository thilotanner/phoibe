package search;

import org.elasticsearch.search.facet.Facets;
import play.db.Model;

import java.util.List;

public class SearchResults<M extends Model> {
    /** The total count. */
	public long totalCount;

	/** The objects. */
	public List<M> objects;

	/** The facets. */
	public Facets facets;

	/**
	 * Instantiates a new search results.
	 *
	 * @param totalCount the total count
	 * @param objects the objects
	 * @param facets the facets
	 */
	public SearchResults(long totalCount, List<M> objects, Facets facets) {
		this.totalCount = totalCount;
		this.objects = objects;
		this.facets = facets;
	}
}
