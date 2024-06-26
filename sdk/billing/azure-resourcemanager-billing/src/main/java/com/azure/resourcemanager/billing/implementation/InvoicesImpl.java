// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.billing.implementation;

import com.azure.core.http.rest.PagedIterable;
import com.azure.core.http.rest.Response;
import com.azure.core.http.rest.SimpleResponse;
import com.azure.core.util.Context;
import com.azure.core.util.logging.ClientLogger;
import com.azure.resourcemanager.billing.fluent.InvoicesClient;
import com.azure.resourcemanager.billing.fluent.models.DownloadUrlInner;
import com.azure.resourcemanager.billing.fluent.models.InvoiceInner;
import com.azure.resourcemanager.billing.models.DownloadUrl;
import com.azure.resourcemanager.billing.models.Invoice;
import com.azure.resourcemanager.billing.models.Invoices;
import java.util.List;

public final class InvoicesImpl implements Invoices {
    private static final ClientLogger LOGGER = new ClientLogger(InvoicesImpl.class);

    private final InvoicesClient innerClient;

    private final com.azure.resourcemanager.billing.BillingManager serviceManager;

    public InvoicesImpl(InvoicesClient innerClient, com.azure.resourcemanager.billing.BillingManager serviceManager) {
        this.innerClient = innerClient;
        this.serviceManager = serviceManager;
    }

    public PagedIterable<Invoice> listByBillingAccount(
        String billingAccountName, String periodStartDate, String periodEndDate) {
        PagedIterable<InvoiceInner> inner =
            this.serviceClient().listByBillingAccount(billingAccountName, periodStartDate, periodEndDate);
        return Utils.mapPage(inner, inner1 -> new InvoiceImpl(inner1, this.manager()));
    }

    public PagedIterable<Invoice> listByBillingAccount(
        String billingAccountName, String periodStartDate, String periodEndDate, Context context) {
        PagedIterable<InvoiceInner> inner =
            this.serviceClient().listByBillingAccount(billingAccountName, periodStartDate, periodEndDate, context);
        return Utils.mapPage(inner, inner1 -> new InvoiceImpl(inner1, this.manager()));
    }

    public PagedIterable<Invoice> listByBillingProfile(
        String billingAccountName, String billingProfileName, String periodStartDate, String periodEndDate) {
        PagedIterable<InvoiceInner> inner =
            this
                .serviceClient()
                .listByBillingProfile(billingAccountName, billingProfileName, periodStartDate, periodEndDate);
        return Utils.mapPage(inner, inner1 -> new InvoiceImpl(inner1, this.manager()));
    }

    public PagedIterable<Invoice> listByBillingProfile(
        String billingAccountName,
        String billingProfileName,
        String periodStartDate,
        String periodEndDate,
        Context context) {
        PagedIterable<InvoiceInner> inner =
            this
                .serviceClient()
                .listByBillingProfile(billingAccountName, billingProfileName, periodStartDate, periodEndDate, context);
        return Utils.mapPage(inner, inner1 -> new InvoiceImpl(inner1, this.manager()));
    }

    public Response<Invoice> getWithResponse(String billingAccountName, String invoiceName, Context context) {
        Response<InvoiceInner> inner = this.serviceClient().getWithResponse(billingAccountName, invoiceName, context);
        if (inner != null) {
            return new SimpleResponse<>(
                inner.getRequest(),
                inner.getStatusCode(),
                inner.getHeaders(),
                new InvoiceImpl(inner.getValue(), this.manager()));
        } else {
            return null;
        }
    }

    public Invoice get(String billingAccountName, String invoiceName) {
        InvoiceInner inner = this.serviceClient().get(billingAccountName, invoiceName);
        if (inner != null) {
            return new InvoiceImpl(inner, this.manager());
        } else {
            return null;
        }
    }

    public Response<Invoice> getByIdWithResponse(String invoiceName, Context context) {
        Response<InvoiceInner> inner = this.serviceClient().getByIdWithResponse(invoiceName, context);
        if (inner != null) {
            return new SimpleResponse<>(
                inner.getRequest(),
                inner.getStatusCode(),
                inner.getHeaders(),
                new InvoiceImpl(inner.getValue(), this.manager()));
        } else {
            return null;
        }
    }

    public Invoice getById(String invoiceName) {
        InvoiceInner inner = this.serviceClient().getById(invoiceName);
        if (inner != null) {
            return new InvoiceImpl(inner, this.manager());
        } else {
            return null;
        }
    }

    public DownloadUrl downloadInvoice(String billingAccountName, String invoiceName, String downloadToken) {
        DownloadUrlInner inner = this.serviceClient().downloadInvoice(billingAccountName, invoiceName, downloadToken);
        if (inner != null) {
            return new DownloadUrlImpl(inner, this.manager());
        } else {
            return null;
        }
    }

    public DownloadUrl downloadInvoice(
        String billingAccountName, String invoiceName, String downloadToken, Context context) {
        DownloadUrlInner inner =
            this.serviceClient().downloadInvoice(billingAccountName, invoiceName, downloadToken, context);
        if (inner != null) {
            return new DownloadUrlImpl(inner, this.manager());
        } else {
            return null;
        }
    }

    public DownloadUrl downloadMultipleBillingProfileInvoices(String billingAccountName, List<String> downloadUrls) {
        DownloadUrlInner inner =
            this.serviceClient().downloadMultipleBillingProfileInvoices(billingAccountName, downloadUrls);
        if (inner != null) {
            return new DownloadUrlImpl(inner, this.manager());
        } else {
            return null;
        }
    }

    public DownloadUrl downloadMultipleBillingProfileInvoices(
        String billingAccountName, List<String> downloadUrls, Context context) {
        DownloadUrlInner inner =
            this.serviceClient().downloadMultipleBillingProfileInvoices(billingAccountName, downloadUrls, context);
        if (inner != null) {
            return new DownloadUrlImpl(inner, this.manager());
        } else {
            return null;
        }
    }

    public PagedIterable<Invoice> listByBillingSubscription(String periodStartDate, String periodEndDate) {
        PagedIterable<InvoiceInner> inner =
            this.serviceClient().listByBillingSubscription(periodStartDate, periodEndDate);
        return Utils.mapPage(inner, inner1 -> new InvoiceImpl(inner1, this.manager()));
    }

    public PagedIterable<Invoice> listByBillingSubscription(
        String periodStartDate, String periodEndDate, Context context) {
        PagedIterable<InvoiceInner> inner =
            this.serviceClient().listByBillingSubscription(periodStartDate, periodEndDate, context);
        return Utils.mapPage(inner, inner1 -> new InvoiceImpl(inner1, this.manager()));
    }

    public Response<Invoice> getBySubscriptionAndInvoiceIdWithResponse(String invoiceName, Context context) {
        Response<InvoiceInner> inner =
            this.serviceClient().getBySubscriptionAndInvoiceIdWithResponse(invoiceName, context);
        if (inner != null) {
            return new SimpleResponse<>(
                inner.getRequest(),
                inner.getStatusCode(),
                inner.getHeaders(),
                new InvoiceImpl(inner.getValue(), this.manager()));
        } else {
            return null;
        }
    }

    public Invoice getBySubscriptionAndInvoiceId(String invoiceName) {
        InvoiceInner inner = this.serviceClient().getBySubscriptionAndInvoiceId(invoiceName);
        if (inner != null) {
            return new InvoiceImpl(inner, this.manager());
        } else {
            return null;
        }
    }

    public DownloadUrl downloadBillingSubscriptionInvoice(String invoiceName, String downloadToken) {
        DownloadUrlInner inner = this.serviceClient().downloadBillingSubscriptionInvoice(invoiceName, downloadToken);
        if (inner != null) {
            return new DownloadUrlImpl(inner, this.manager());
        } else {
            return null;
        }
    }

    public DownloadUrl downloadBillingSubscriptionInvoice(String invoiceName, String downloadToken, Context context) {
        DownloadUrlInner inner =
            this.serviceClient().downloadBillingSubscriptionInvoice(invoiceName, downloadToken, context);
        if (inner != null) {
            return new DownloadUrlImpl(inner, this.manager());
        } else {
            return null;
        }
    }

    public DownloadUrl downloadMultipleBillingSubscriptionInvoices(List<String> downloadUrls) {
        DownloadUrlInner inner = this.serviceClient().downloadMultipleBillingSubscriptionInvoices(downloadUrls);
        if (inner != null) {
            return new DownloadUrlImpl(inner, this.manager());
        } else {
            return null;
        }
    }

    public DownloadUrl downloadMultipleBillingSubscriptionInvoices(List<String> downloadUrls, Context context) {
        DownloadUrlInner inner =
            this.serviceClient().downloadMultipleBillingSubscriptionInvoices(downloadUrls, context);
        if (inner != null) {
            return new DownloadUrlImpl(inner, this.manager());
        } else {
            return null;
        }
    }

    private InvoicesClient serviceClient() {
        return this.innerClient;
    }

    private com.azure.resourcemanager.billing.BillingManager manager() {
        return this.serviceManager;
    }
}
