package models;

import org.apache.commons.lang3.builder.ToStringBuilder;
import play.db.jpa.JPA;
import play.db.jpa.JPABase;
import play.db.jpa.Model;
import util.collection.CollectionUtils;
import util.string.ModelToStringStyle;

import javax.persistence.EntityManager;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

@MappedSuperclass
public class EnhancedModel extends Model {

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    @PrePersist
    protected void onCreate() {
        created = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = new Date();
    }

    public Date getCreated() {
        return created;
    }

    public Date getUpdated() {
        return updated;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ModelToStringStyle.getModelToStringStyle(), false);
    }

    public <T extends JPABase> T loggedSave(User user) {
        T object = save();

        logActivity(object.toString(), ActivityLogAction.SAVE, user);

        return object;
    }

    public <T extends JPABase> T loggedDelete(User user) {
        T object = delete();

        logActivity(object.toString(), ActivityLogAction.DELETE, user);

        return object;
    }

    public boolean isReferenced(Class<? extends Model>... classes) {
        long numberOfReferences = 0l;
        for (Class clazz : classes) {
            numberOfReferences += getNumberOfDBReferences(clazz);
        }
        return numberOfReferences > 0l;
    }

    protected long getNumberOfDBReferences(Class clazz) {
        return getNumberOfDBReferences(clazz, getSingularObjectName());
    }

    protected long getNumberOfDBReferences(Class clazz, String objectName) {
        try {
            EntityManager em = JPA.em();
            CriteriaBuilder qb = em.getCriteriaBuilder();

            CriteriaQuery<Long> cq = qb.createQuery(Long.class);
            Root r = cq.from(clazz);
            cq.select(qb.count(r));
            cq.where(qb.equal(r.get(objectName), this));
            return em.createQuery(cq).getSingleResult();
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Unreferenced property found: " + clazz.getName());
        }
    }

    private String getSingularObjectName() {
        String objectName = this.getClass().getSimpleName();
        return objectName.substring(0, 1).toLowerCase() + objectName.substring(1, objectName.length());
    }

    private void logActivity(String message,
                             ActivityLogAction action,
                             User user) {
        ActivityLogEntry activityLogEntry = new ActivityLogEntry();
        activityLogEntry.date = new Date();
        activityLogEntry.message = message;
        activityLogEntry.activityLogAction = action;
        activityLogEntry.user = user;
        activityLogEntry.save();
    }

    public static <M extends EnhancedModel> List<M> findByIds(List<Long> ids, Class<M> clazz) {
        CriteriaBuilder builder = JPA.em().getCriteriaBuilder();
        CriteriaQuery<M> criteria = builder.createQuery(clazz);
        Root<M> root = criteria.from(clazz);
        criteria.where(root.get("id").in(ids));
        TypedQuery<M> query = JPA.em().createQuery(criteria);

        List<M> results = query.getResultList();

        // order by id list
        CollectionUtils.sortByIdList(results, ids);

        return results;
    }
}
