package models;

import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class MetricProduct extends Model {

    @Required
    public String name;

    @Lob
    public String description;

    @ManyToOne
    public Metric metric;
}
