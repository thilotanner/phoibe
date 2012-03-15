package controllers;

import play.CorePlugin;

public class Status extends ApplicationController {
    public static void index() {
        String status = CorePlugin.computeApplicationStatus(false);
        render(status);
    }
}
