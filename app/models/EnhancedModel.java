package models;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import play.db.jpa.JPA;
import play.db.jpa.JPABase;
import play.db.jpa.Model;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Date;

public class EnhancedModel extends Model {

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE, false);
    }

    public <T extends JPABase> T loggedSave(User user) {
        T object = super.save();

        // Log
        ActivityLogEntry activityLogEntry = new ActivityLogEntry();
        activityLogEntry.date = new Date();
        activityLogEntry.message = object.toString();
        activityLogEntry.activityLogAction = ActivityLogAction.SAVE;
        activityLogEntry.user = user;
        activityLogEntry.save();

        return object;
    }

    @Override
    public <T extends JPABase> T delete() {
        return super.delete();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public boolean isReferenced(Class<? extends Model>[] classes)
    {
        long numberOfReferences = 0l;
        for (Class clazz : classes) {
            numberOfReferences += getNumberOfDBReferences(clazz);
        }
        return numberOfReferences > 0l;
    }

    protected long getNumberOfDBReferences(Class clazz)
    {
        try {
            EntityManager em = JPA.em();
            CriteriaBuilder qb = em.getCriteriaBuilder();

            CriteriaQuery<Long> cq = qb.createQuery(Long.class);
            Root r = cq.from(clazz);
            cq.select(qb.count(r));
            cq.where(qb.equal(r.get(getSingularObjectName()), this));
            return em.createQuery(cq).getSingleResult();
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Unreferenced property found: " + clazz.getName());
        }
    }

    private String getSingularObjectName()
    {
        String objectName = this.getClass().getSimpleName();
        return objectName.substring(0, 1).toLowerCase() + objectName.substring(1, objectName.length());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final EnhancedModel other = (EnhancedModel) obj;

        return !(!this.id.equals(other.id) && (this.id == null || !this.id.equals(other.id)));
    }
}
