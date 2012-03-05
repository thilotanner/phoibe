package controllers;

import models.Account;
import models.Creditor;
import models.CreditorPayment;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.i18n.Messages;
import util.i18n.CurrencyProvider;

public class CreditorPayments extends ApplicationController {

    public static void show(Long id) {
        notFoundIfNull(id);
        CreditorPayment creditorPayment = CreditorPayment.findById(id);
        notFoundIfNull(creditorPayment);

        render(creditorPayment);
    }

    public static void create(Long creditorId) {
        notFoundIfNull(creditorId);
        Creditor creditor = Creditor.findById(creditorId);
        notFoundIfNull(creditor);

        CreditorPayment creditorPayment = new CreditorPayment();
        creditorPayment.creditor = creditor;

        initRenderArgs();
        render("@form", creditorPayment);
    }

    public static void form(Long id) {
        notFoundIfNull(id);
        CreditorPayment creditorPayment = CreditorPayment.findById(id);
        notFoundIfNull(creditorPayment);

        initRenderArgs();
        render(creditorPayment);
    }

    public static void save(@Valid CreditorPayment creditorPayment) {
        if(Validation.hasErrors()) {
            initRenderArgs();
            render("@form", creditorPayment);
        }

        creditorPayment.buildEntry();
        creditorPayment.paymentEntry.save();

        creditorPayment.loggedSave(getCurrentUser());

        if(creditorPayment.creditor.getAmountDue().value == 0l) {
            // close creditor
            creditorPayment.creditor.close();
        }

        flash.success(Messages.get("successfullySaved", Messages.get("debitorPaymentReceipt")));
        Creditors.show(creditorPayment.creditor.id);
    }

    private static void initRenderArgs() {
        renderArgs.put("defaultCurrency", CurrencyProvider.getDefaultCurrency());
        renderArgs.put("paymentAccounts", Account.getPaymentAccounts());
    }
}
