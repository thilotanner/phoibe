package controllers;

import play.Play;
import play.mvc.Controller;

public class ApplicationController extends Controller {
    private static final int DEFAULT_PAGE_SIZE = 10;

    public static int getPageSize()
    {
        try {
            return Integer.parseInt((String) Play.configuration.get("paging.pageSize"));
        } catch (Exception e) {
            return DEFAULT_PAGE_SIZE;
        }
    }
}
