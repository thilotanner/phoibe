package controllers;

import models.ActivityLogEntry;
import models.Metric;
import play.db.Model;

import java.util.ArrayList;
import java.util.List;

public class ActivityLogEntries extends ApplicationController {
     public static void index(int page, String orderBy, String order, String search) {
        if (page < 1) {
            page = 1;
        }

        List<Model> activityLogEntries = Model.Manager.factoryFor(ActivityLogEntry.class).fetch(
                (page - 1) * getPageSize(),
                getPageSize(),
                orderBy,
                order,
                new ArrayList<String>(),
                search,
                null
        );

        Long count = Model.Manager.factoryFor(ActivityLogEntry.class).count(new ArrayList<String>(), search, null);

        renderArgs.put("pageSize", getPageSize());
        render(activityLogEntries, count);
    }
}
