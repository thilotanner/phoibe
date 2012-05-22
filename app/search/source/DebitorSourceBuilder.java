package search.source;

import models.Debitor;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;

public class DebitorSourceBuilder implements SourceBuilder<Debitor> {

    @Override
    public XContentBuilder buildSource(Debitor debitor) throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();

        builder.startObject()
                .field("id", debitor.id)
                .field("report.order.description", debitor.report.order.description)
                .field(SourceBuilder.SORTABLE_INDEX_PREFIX + "report.order.description", debitor.report.order.description)
                .field("report.order.comments", debitor.report.order.comments)
                .field("debitorStatus", debitor.debitorStatus.toString())

                // billing address
                .field("report.order.billingContact.company", debitor.report.order.billingContact.company)
                .field("report.order.billingContact.title", debitor.report.order.billingContact.title)
                .field("report.order.billingContact.firstName", debitor.report.order.billingContact.firstName)
                .field("report.order.billingContact.lastName", debitor.report.order.billingContact.lastName)
                .field("report.order.billingContact.street", debitor.report.order.billingContact.street)
                .field("report.order.billingContact.postOfficeBox", debitor.report.order.billingContact.postOfficeBox)
                .field("report.order.billingContact.postalCode", debitor.report.order.billingContact.postalCode)
                .field("report.order.billingContact.city", debitor.report.order.billingContact.city)
                .field("report.order.billingContact.countryCode", debitor.report.order.billingContact.countryCode)
                .field("report.order.billingContact.phone", debitor.report.order.billingContact.phone)
                .field("report.order.billingContact.fax", debitor.report.order.billingContact.fax)
                .field("report.order.billingContact.mobile", debitor.report.order.billingContact.mobile)
                .field("report.order.billingContact.email", debitor.report.order.billingContact.mobile)
                .field("report.order.billingContact.website", debitor.report.order.billingContact.website)
                .field("report.order.billingContact.comments", debitor.report.order.billingContact.comments)
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

                .startObject("report.order.description")
                .field("type", "string")
                .endObject()

                .startObject(SourceBuilder.SORTABLE_INDEX_PREFIX + "report.order.description")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("report.order.comments")
                .field("type", "string")
                .endObject()

                .startObject("debitorStatus")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                // --- billing contact ---
                .startObject("report.order.billingContact.company")
                .field("type", "string")
                .endObject()

                .startObject("report.order.billingContact.title")
                .field("type", "string")
                .endObject()

                .startObject("report.order.billingContact.firstName")
                .field("type", "string")
                .endObject()

                .startObject("report.order.billingContact.lastName")
                .field("type", "string")
                .endObject()

                .startObject("report.order.billingContact.street")
                .field("type", "string")
                .endObject()

                .startObject("report.order.billingContact.postOfficeBox")
                .field("type", "string")
                .endObject()

                .startObject("report.order.billingContact.postalCode")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("report.order.billingContact.city")
                .field("type", "string")
                .endObject()

                .startObject("report.order.billingContact.countryCode")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("report.order.billingContact.phone")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("report.order.billingContact.fax")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("report.order.billingContact.mobile")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("report.order.billingContact.email")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("report.order.billingContact.website")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("report.order.billingContact.comments")
                .field("type", "string")
                .endObject()
                // --- end billing contact ---

                .endObject()
                .endObject()
        ;

        return builder.endObject();
    }
}
