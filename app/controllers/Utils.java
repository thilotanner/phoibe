package controllers;

import models.Entry;

public class Utils extends ApplicationController {
    public static void index() {
        render();
    }

    public static void recalculateEntryChecksums() {
        for(Entry entry : Entry.<Entry>findAll()) {
            entry.checksum = entry.calculateChecksum();
            entry.superuserSave();
        }
    }
}
