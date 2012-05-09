package util.collection;

import models.EnhancedModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CollectionUtils {
    public static <M extends EnhancedModel> void sortByIdList(List<M> entities,
                                                              final List<Long> ids)
    {
        Collections.sort(entities, new Comparator<M>() {
            @Override
            public int compare(M entity1, M entity2) {
                Integer index1 = ids.indexOf(entity1.getId());
                Integer index2 = ids.indexOf(entity2.getId());

                return index1.compareTo(index2);
            }
        });
    }
}
