package controllers;

import models.Debitor;
import models.DebitorStatus;
import models.Entry;
import models.OrderStatus;
import play.data.validation.Valid;
import play.db.Model;
import play.i18n.Messages;

import java.util.ArrayList;
import java.util.List;

public class Debitors extends ApplicationController {
    public static void index(String filter, int page, String orderBy, String order, String search) {
        if (page < 1) {
            page = 1;
        }

        String where = null;
        for(DebitorStatus debitorStatus : DebitorStatus.values()) {
            if(debitorStatus.toString().equals(filter)) {
                where = String.format("debitorStatus = '%s'", filter);
            }
        }

        List<Model> debitors = Model.Manager.factoryFor(Debitor.class).fetch(
                (page - 1) * getPageSize(),
                getPageSize(),
                orderBy,
                order,
                new ArrayList<String>(),
                search,
                where
        );

        Long count = Model.Manager.factoryFor(Debitor.class).count(new ArrayList<String>(), search, where);

        renderArgs.put("pageSize", getPageSize());
        render(debitors, count, filter);
    }
    
    public static void show(Long id) {
        notFoundIfNull(id);
        Debitor debitor = Debitor.findById(id);
        notFoundIfNull(debitor);
        render(debitor);
    }

    public static void form(Long id) {
        notFoundIfNull(id);
        Debitor debitor = Debitor.findById(id);
        notFoundIfNull(debitor);
        render(debitor);
    }

    public static void save(@Valid Debitor debitor) {
        if(validation.hasErrors()) {
            render("@form", debitor);
        }

        debitor.loggedSave(getCurrentUser());
        flash.success(Messages.get("successfullySaved", Messages.get("debitor")));
        index(DebitorStatus.DUE.toString(), 1, null, null, null);
    }

    public static void discountAmountDue(Long debitorId) {
        notFoundIfNull(debitorId);
        Debitor debitor = Debitor.findById(debitorId);
        notFoundIfNull(debitor);

        Entry entry = debitor.buildDiscountEntry();
        entry.save();

        closeDebitor(debitor, entry, "debitor.successfullyDiscounted");
    }
    
    public static void chargeOffAmountDue(Long debitorId) {
        notFoundIfNull(debitorId);
        Debitor debitor = Debitor.findById(debitorId);
        notFoundIfNull(debitor);

        Entry entry = debitor.buildChargeOffEntry();
        entry.save();

        closeDebitor(debitor, entry, "debitor.successfullyChargedOff");
    }
    
    private static void closeDebitor(Debitor debitor, Entry amountDueEntry, String messageKey) {
        debitor.close(amountDueEntry);

        flash.success(Messages.get(messageKey));
        index(DebitorStatus.DUE.toString(), 1, null, null, null);
    }
}
