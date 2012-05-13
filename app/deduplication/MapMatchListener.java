package deduplication;

import models.EnhancedModel;
import no.priv.garshol.duke.Record;
import no.priv.garshol.duke.matchers.AbstractMatchListener;
import play.db.jpa.JPA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapMatchListener<T extends EnhancedModel> extends AbstractMatchListener {

    private Class<T> entityClass;
    private Map<Long, Map<Long, T>> entityMap;
    private List<Map<Long, T>> duplicates;

    public MapMatchListener(Class<T> entityClass) {
        this.entityClass = entityClass;
        entityMap = new HashMap<Long, Map<Long, T>>();
        duplicates = new ArrayList<Map<Long, T>>();
    }

    @Override
    public void matches(Record record, Record duplicate, double value) {
        Long recordId = Long.valueOf(record.getValue("id"));
        Long duplicateId = Long.valueOf(duplicate.getValue("id"));

        if(recordId < duplicateId) {
            insertMatch(recordId, duplicateId);
        } else {
            insertMatch(duplicateId, recordId);
        }
    }

    private void insertMatch(Long recordId, Long duplicateId) {
        Map<Long, T> duplicate;
        if(entityMap.containsKey(recordId)) {
            duplicate = entityMap.get(recordId);
        } else if(entityMap.containsKey(duplicateId)) {
            duplicate = entityMap.get(duplicateId);
        } else {
            duplicate = new HashMap<Long, T>();
            duplicates.add(duplicate);
        }

        // add record
        if(!duplicate.containsKey(recordId)) {
            T entity = JPA.em().find(entityClass, recordId);
            duplicate.put(recordId, entity);
        }
        entityMap.put(recordId, duplicate);

        // add duplicate
        if(!duplicate.containsKey(duplicateId)) {
            T entity = JPA.em().find(entityClass, duplicateId);
            duplicate.put(duplicateId, entity);
        }
        entityMap.put(duplicateId, duplicate);
    }

    public List<Map<Long, T>> getDuplicates() {
        return duplicates;
    }
}
