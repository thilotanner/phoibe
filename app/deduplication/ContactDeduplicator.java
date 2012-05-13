package deduplication;

import models.Contact;
import no.priv.garshol.duke.ConfigLoader;
import no.priv.garshol.duke.Configuration;
import no.priv.garshol.duke.Processor;
import no.priv.garshol.duke.RecordImpl;
import no.priv.garshol.duke.datasources.InMemoryDataSource;
import play.Play;

import java.util.List;
import java.util.Map;

public class ContactDeduplicator {

    public List<Map<Long,Contact>> deduplicate() throws Exception {
        Configuration config = ConfigLoader.load(Play.getFile("conf/duke-contacts.xml").getAbsolutePath());
        Processor processor = new Processor(config);
        MapMatchListener<Contact> listener = new MapMatchListener<Contact>(Contact.class);
        processor.addMatchListener(listener);
        processor.deduplicate();
        processor.close();

        return listener.getDuplicates();
    }

    public List<Map<Long,Contact>> findDuplicates(Contact contact) throws Exception {
        Configuration config = ConfigLoader.load(Play.getFile("conf/duke-contacts.xml").getAbsolutePath());
        config.addDataSource(1, config.getDataSources().iterator().next());

        RecordImpl record = new RecordImpl();
        record.addValue("id", "0"); // test record has id 0
        record.addValue("company", contact.company);
        record.addValue("firstName", contact.firstName);
        record.addValue("lastName", contact.lastName);
        record.addValue("street", contact.street);
        record.addValue("city", contact.city);
        InMemoryDataSource dataSource = new InMemoryDataSource();
        dataSource.add(record);
        config.addDataSource(2, dataSource);


        Processor processor = new Processor(config);
        MapMatchListener<Contact> listener = new MapMatchListener<Contact>(Contact.class);
        processor.addMatchListener(listener);
        processor.link();
        processor.close();

        return listener.getDuplicates();
    }
}
