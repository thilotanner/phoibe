package controllers;

import models.Account;
import models.Debitor;
import models.DebitorPaymentReceipt;
import models.DebitorStatus;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.i18n.Messages;
import util.i18n.CurrencyProvider;

public class DebitorPaymentReceipts extends ApplicationController {
    public static void create(Long debitorId) {
        notFoundIfNull(debitorId);
        Debitor debitor = Debitor.findById(debitorId);
        notFoundIfNull(debitor);

        DebitorPaymentReceipt debitorPaymentReceipt = new DebitorPaymentReceipt();
        debitorPaymentReceipt.debitor = debitor;

        initRenderArgs();
        render("@form", debitorPaymentReceipt);
    }

    public static void form(Long id) {
        notFoundIfNull(id);
        DebitorPaymentReceipt debitorPaymentReceipt = DebitorPaymentReceipt.findById(id);
        notFoundIfNull(debitorPaymentReceipt);

        initRenderArgs();
        render(debitorPaymentReceipt);
    }

    public static void save(@Valid DebitorPaymentReceipt debitorPaymentReceipt) {
        if(Validation.hasErrors()) {
            initRenderArgs();
            render("@form", debitorPaymentReceipt);
        }

        debitorPaymentReceipt.buildEntry();
        debitorPaymentReceipt.paymentEntry.save();

        debitorPaymentReceipt.loggedSave(getCurrentUser());

        if(debitorPaymentReceipt.debitor.getAmountDue().value == 0l) {
            // close debitor
            debitorPaymentReceipt.debitor.close(null);
        }

        flash.success(Messages.get("successfullySaved", Messages.get("debitorPaymentReceipt")));
        Debitors.show(debitorPaymentReceipt.debitor.id);
    }

    private static void initRenderArgs() {
        renderArgs.put("defaultCurrency", CurrencyProvider.getDefaultCurrency());
        renderArgs.put("paymentAccounts", Account.getPaymentAccounts());
    }
}
