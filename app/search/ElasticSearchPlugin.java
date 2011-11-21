package search;

import play.PlayPlugin;

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
    public void onEvent(String message, Object context) {

    }
}
