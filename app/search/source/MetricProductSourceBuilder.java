package search.source;

import models.MetricProduct;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;

public class MetricProductSourceBuilder implements SourceBuilder<MetricProduct> {
    @Override
    public XContentBuilder buildSource(MetricProduct metricProduct) throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();

        builder.startObject()
                .field("id", metricProduct.id)
                .field("name", metricProduct.name)
                .field(SourceBuilder.SORTABLE_INDEX_PREFIX + "name", metricProduct.name)
                .field("description", metricProduct.description)

                // supplier
                .field("supplier.company", metricProduct.supplier.company)
                .field("supplier.title", metricProduct.supplier.title)
                .field("supplier.firstName", metricProduct.supplier.firstName)
                .field("supplier.lastName", metricProduct.supplier.lastName)
                .field("supplier.street", metricProduct.supplier.street)
                .field("supplier.postOfficeBox", metricProduct.supplier.postOfficeBox)
                .field("supplier.postalCode", metricProduct.supplier.postalCode)
                .field("supplier.city", metricProduct.supplier.city)
                .field("supplier.countryCode", metricProduct.supplier.countryCode)
                .field("supplier.phone", metricProduct.supplier.phone)
                .field("supplier.fax", metricProduct.supplier.fax)
                .field("supplier.mobile", metricProduct.supplier.mobile)
                .field("supplier.email", metricProduct.supplier.mobile)
                .field("supplier.website", metricProduct.supplier.website)
                .field("supplier.comments", metricProduct.supplier.comments)

                .field("orderNumber", metricProduct.orderNumber)
                .field("comments", metricProduct.comments)
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

                .startObject("name")
                .field("type", "string")
                .endObject()

                .startObject(SourceBuilder.SORTABLE_INDEX_PREFIX + "name")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("description")
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

                .startObject("orderNumber")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("comments")
                .field("type", "string")
                .endObject()

                .endObject()
                .endObject()
        ;

        return builder.endObject();
    }
}
