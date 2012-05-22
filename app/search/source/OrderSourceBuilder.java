package search.source;

import models.Order;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;

public class OrderSourceBuilder implements SourceBuilder<Order> {
    @Override
    public XContentBuilder buildSource(Order order) throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();

        builder.startObject()
                .field("id", order.id)
                .field("description", order.description)
                .field(SourceBuilder.SORTABLE_INDEX_PREFIX + "description", order.description)
                .field("comments", order.comments)

                // ordering contact
                .field("orderingContact.company", order.orderingContact.company)
                .field("orderingContact.title", order.orderingContact.title)
                .field("orderingContact.firstName", order.orderingContact.firstName)
                .field("orderingContact.lastName", order.orderingContact.lastName)
                .field("orderingContact.street", order.orderingContact.street)
                .field("orderingContact.postOfficeBox", order.orderingContact.postOfficeBox)
                .field("orderingContact.postalCode", order.orderingContact.postalCode)
                .field("orderingContact.city", order.orderingContact.city)
                .field("orderingContact.countryCode", order.orderingContact.countryCode)
                .field("orderingContact.phone", order.orderingContact.phone)
                .field("orderingContact.fax", order.orderingContact.fax)
                .field("orderingContact.mobile", order.orderingContact.mobile)
                .field("orderingContact.email", order.orderingContact.mobile)
                .field("orderingContact.website", order.orderingContact.website)
                .field("orderingContact.comments", order.orderingContact.comments)

                // shipping contact
                .field("shippingContact.company", order.shippingContact.company)
                .field("shippingContact.title", order.shippingContact.title)
                .field("shippingContact.firstName", order.shippingContact.firstName)
                .field("shippingContact.lastName", order.shippingContact.lastName)
                .field("shippingContact.street", order.shippingContact.street)
                .field("shippingContact.postOfficeBox", order.shippingContact.postOfficeBox)
                .field("shippingContact.postalCode", order.shippingContact.postalCode)
                .field("shippingContact.city", order.shippingContact.city)
                .field("shippingContact.countryCode", order.shippingContact.countryCode)
                .field("shippingContact.phone", order.shippingContact.phone)
                .field("shippingContact.fax", order.shippingContact.fax)
                .field("shippingContact.mobile", order.shippingContact.mobile)
                .field("shippingContact.email", order.shippingContact.mobile)
                .field("shippingContact.website", order.shippingContact.website)
                .field("shippingContact.comments", order.shippingContact.comments)

                // billing contact
                .field("billingContact.company", order.billingContact.company)
                .field("billingContact.title", order.billingContact.title)
                .field("billingContact.firstName", order.billingContact.firstName)
                .field("billingContact.lastName", order.billingContact.lastName)
                .field("billingContact.street", order.billingContact.street)
                .field("billingContact.postOfficeBox", order.billingContact.postOfficeBox)
                .field("billingContact.postalCode", order.billingContact.postalCode)
                .field("billingContact.city", order.billingContact.city)
                .field("billingContact.countryCode", order.billingContact.countryCode)
                .field("billingContact.phone", order.billingContact.phone)
                .field("billingContact.fax", order.billingContact.fax)
                .field("billingContact.mobile", order.billingContact.mobile)
                .field("billingContact.email", order.billingContact.mobile)
                .field("billingContact.website", order.billingContact.website)
                .field("billingContact.comments", order.billingContact.comments)

                .field("orderStatus", order.orderStatus.toString())
        ;

        if(order.currentReport != null) {
            builder.field("currentReport.reportType.id", order.currentReport.reportType.id);
        }

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

                .startObject("description")
                .field("type", "string")
                .endObject()

                .startObject(SourceBuilder.SORTABLE_INDEX_PREFIX + "description")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("comments")
                .field("type", "string")
                .endObject()

                // --- orderingContact ---
                .startObject("orderingContact.company")
                .field("type", "string")
                .endObject()

                .startObject("orderingContact.title")
                .field("type", "string")
                .endObject()

                .startObject("orderingContact.firstName")
                .field("type", "string")
                .endObject()

                .startObject("orderingContact.lastName")
                .field("type", "string")
                .endObject()

                .startObject("orderingContact.street")
                .field("type", "string")
                .endObject()

                .startObject("orderingContact.postOfficeBox")
                .field("type", "string")
                .endObject()

                .startObject("orderingContact.postalCode")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("orderingContact.city")
                .field("type", "string")
                .endObject()

                .startObject("orderingContact.countryCode")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("orderingContact.phone")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("orderingContact.fax")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("orderingContact.mobile")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("orderingContact.email")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("orderingContact.website")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("orderingContact.comments")
                .field("type", "string")
                .endObject()
                // --- end orderingContact ---

                // --- shippingContact ---
                .startObject("shippingContact.company")
                .field("type", "string")
                .endObject()

                .startObject("shippingContact.title")
                .field("type", "string")
                .endObject()

                .startObject("shippingContact.firstName")
                .field("type", "string")
                .endObject()

                .startObject("shippingContact.lastName")
                .field("type", "string")
                .endObject()

                .startObject("shippingContact.street")
                .field("type", "string")
                .endObject()

                .startObject("shippingContact.postOfficeBox")
                .field("type", "string")
                .endObject()

                .startObject("shippingContact.postalCode")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("shippingContact.city")
                .field("type", "string")
                .endObject()

                .startObject("shippingContact.countryCode")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("shippingContact.phone")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("shippingContact.fax")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("shippingContact.mobile")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("shippingContact.email")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("shippingContact.website")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("shippingContact.comments")
                .field("type", "string")
                .endObject()
                // --- end shippingContact ---

                // --- billingContact ---
                .startObject("billingContact.company")
                .field("type", "string")
                .endObject()

                .startObject("billingContact.title")
                .field("type", "string")
                .endObject()

                .startObject("billingContact.firstName")
                .field("type", "string")
                .endObject()

                .startObject("billingContact.lastName")
                .field("type", "string")
                .endObject()

                .startObject("billingContact.street")
                .field("type", "string")
                .endObject()

                .startObject("billingContact.postOfficeBox")
                .field("type", "string")
                .endObject()

                .startObject("billingContact.postalCode")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("billingContact.city")
                .field("type", "string")
                .endObject()

                .startObject("billingContact.countryCode")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("billingContact.phone")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("billingContact.fax")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("billingContact.mobile")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("billingContact.email")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("billingContact.website")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("billingContact.comments")
                .field("type", "string")
                .endObject()
                // --- end billingContact ---

                .startObject("orderStatus")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("currentReport.reportType.id")
                .field("type", "long")
                .field("index", "not_analyzed")
                .endObject()

                .endObject()
                .endObject()
        ;

        return builder.endObject();
    }
}
