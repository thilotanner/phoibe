package models;

import play.data.validation.Required;

import javax.persistence.Entity;

@Entity
public class ReportType extends EnhancedModel {

    @Required
    public String name;

    public boolean rootReportType;
}
