package search;

import org.apache.commons.lang.Validate;
import play.Logger;
import play.PlayPlugin;
import play.db.jpa.Model;
import search.mapping.MappingUtil;

public class ElasticSearchPlugin extends PlayPlugin {

    @Override
    public void afterApplicationStart() {
        ElasticSearch.start();
    }

    @Override
    public void onApplicationStop() {
        ElasticSearch.stop();
    }

    @Override
    public void onEvent(String message, Object object) {
        Logger.debug("Received %s Event, Object: %s", message, object);

		if (!isInterestingEvent(message)) {
			return;
		}

		Logger.debug("Processing %s Event", message);

		// Check if object is searchable
		if (!MappingUtil.isSearchable(object.getClass())) {
			return;
		}

		// Sanity check, we only index models
		Validate.isTrue(object instanceof Model, "Only play.db.jpa.Model subclasses can be indexed");

        if(message.endsWith(".objectDeleted")) {
            ElasticSearch.delete((Model) object);
        } else {
            ElasticSearch.index((Model) object);
        }
    }

    private static boolean isInterestingEvent(String message) {
		return message.endsWith(".objectPersisted") ||
               message.endsWith(".objectUpdated") ||
               message.endsWith(".objectDeleted");
	}
}
