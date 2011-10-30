package models;

import javax.persistence.ManyToOne;

public class ReportItem {

    @ManyToOne
    public Report report;

}
