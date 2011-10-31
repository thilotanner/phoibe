package controllers;

import models.ActivityLogEntry;

import java.util.List;

public class ActivityLogEntries extends ApplicationController {
    public static void index() {
        List<ActivityLogEntry> activityLogEntries = ActivityLogEntry.findAll();
        render(activityLogEntries);
    }
}
