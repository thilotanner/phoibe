package search.source;

import models.Contact;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;

public class ContactSourceBuilder implements SourceBuilder<Contact> {

    @Override
    public XContentBuilder buildSource(Contact contact) throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();

        builder.startObject()
                .field("id", contact.id)
                .field("company", contact.company)
                .field("#company", contact.company)
                .field("title", contact.title)
                .field("firstName", contact.firstName)
                .field("#firstName", contact.firstName)
                .field("lastName", contact.lastName)
                .field("#lastName", contact.lastName)
                .field("street", contact.street)
                .field("#street", contact.street)
                .field("postOfficeBox", contact.postOfficeBox)
                .field("postalCode", contact.postalCode)
                .field("city", contact.city)
                .field("#city", contact.city)
                .field("countryCode", contact.countryCode)
                .field("phone", contact.phone)
                .field("fax", contact.fax)
                .field("mobile", contact.mobile)
                .field("email", contact.mobile)
                .field("website", contact.website)
                .field("comments", contact.comments)
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

                .startObject("company")
                .field("type", "string")
                .endObject()

                .startObject("#company")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("title")
                .field("type", "string")
                .endObject()

                .startObject("firstName")
                .field("type", "string")
                .endObject()

                .startObject("#firstName")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("lastName")
                .field("type", "string")
                .endObject()

                .startObject("#lastName")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("street")
                .field("type", "string")
                .endObject()

                .startObject("#street")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("postOfficeBox")
                .field("type", "string")
                .endObject()

                .startObject("postalCode")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("city")
                .field("type", "string")
                .endObject()

                .startObject("#city")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("countryCode")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("phone")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("fax")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("mobile")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("email")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("website")
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
