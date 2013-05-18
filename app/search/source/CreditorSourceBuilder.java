package search.source;

import models.Creditor;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;

public class CreditorSourceBuilder implements SourceBuilder<Creditor> {
    @Override
    public XContentBuilder buildSource(Creditor creditor) throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();

        builder.startObject()
                .field("id", creditor.id)
                .field("reference", creditor.reference)
                .field(SourceBuilder.SORTABLE_INDEX_PREFIX + "reference", creditor.reference)
                .field("creditorStatus", creditor.creditorStatus.toString())
                .field("amount", creditor.amount.getLabel().replaceAll("'", ""))

                // supplier
                .field("supplier.company", creditor.supplier.company)
                .field("supplier.title", creditor.supplier.title)
                .field("supplier.firstName", creditor.supplier.firstName)
                .field("supplier.lastName", creditor.supplier.lastName)
                .field("supplier.street", creditor.supplier.street)
                .field("supplier.postOfficeBox", creditor.supplier.postOfficeBox)
                .field("supplier.postalCode", creditor.supplier.postalCode)
                .field("supplier.city", creditor.supplier.city)
                .field("supplier.countryCode", creditor.supplier.countryCode)
                .field("supplier.phone", creditor.supplier.phone)
                .field("supplier.fax", creditor.supplier.fax)
                .field("supplier.mobile", creditor.supplier.mobile)
                .field("supplier.email", creditor.supplier.mobile)
                .field("supplier.website", creditor.supplier.website)
                .field("supplier.comments", creditor.supplier.comments)
        ;

        return builder.endObject();
    }

    @Override
    public XContentBuilder buildMapping() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();

        builder.startObject()

                .startObject("id")
                .startObject("properties")

                .startObject("id")
                .field("type", "long")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("reference")
                .field("type", "string")
                .endObject()

                .startObject(SourceBuilder.SORTABLE_INDEX_PREFIX + "reference")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("creditorStatus")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("amount")
                .field("type", "string")
                .endObject()

                // --- supplier ---
                .startObject("supplier.company")
                .field("type", "string")
                .endObject()

                .startObject("supplier.title")
                .field("type", "string")
                .endObject()

                .startObject("supplier.firstName")
                .field("type", "string")
                .endObject()

                .startObject("supplier.lastName")
                .field("type", "string")
                .endObject()

                .startObject("supplier.street")
                .field("type", "string")
                .endObject()

                .startObject("supplier.postOfficeBox")
                .field("type", "string")
                .endObject()

                .startObject("supplier.postalCode")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("supplier.city")
                .field("type", "string")
                .endObject()

                .startObject("supplier.countryCode")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("supplier.phone")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("supplier.fax")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("supplier.mobile")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("supplier.email")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("supplier.website")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("supplier.comments")
                .field("type", "string")
                .endObject()
                // --- end supplier ---

                .endObject()
                .endObject()
        ;

        return builder.endObject();
    }
}
