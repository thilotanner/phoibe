package models;

import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;

@Entity
public class Metric extends Model {

    @Required
    public String name;

    @Required
    public String symbol;
}
