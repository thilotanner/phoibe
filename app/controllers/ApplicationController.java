package controllers;

import models.User;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.Play;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@With(Secure.class)
public class ApplicationController extends Controller {
    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final int LOG_PRIORITY = -10000;
       private static final int USER_PRIORITY = -1000;

       @Before(priority = LOG_PRIORITY)
       public static void logAction()
       {
           if (Logger.log4j.isDebugEnabled()) {
               List<String> parameters = new ArrayList<String>();
               parameters.add(request.toString());
               for (Map.Entry entry : params.allSimple().entrySet()) {
                   parameters.add(String.format("%s->%s", entry.getKey(), entry.getValue()));
               }
               Logger.debug(StringUtils.join(parameters, " | "));
           }
       }

       @Before(priority = USER_PRIORITY)
       public static void user() throws Throwable
       {
           String username = ApplicationSecurity.connected();

           if (username != null) {
               User user = User.find("username = ?", username).first();
               if (user != null) {
                   renderArgs.put("user", user);
               }
           }
       }

       public static User getCurrentUser()
       {
           Object user = renderArgs.get("user");

           if (user != null)
               return (User) user;

           return null;
       }


    public static int getPageSize()
    {
        try {
            return Integer.parseInt((String) Play.configuration.get("paging.pageSize"));
        } catch (Exception e) {
            return DEFAULT_PAGE_SIZE;
        }
    }
}
