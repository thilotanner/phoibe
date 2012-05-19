package deduplication;

import models.Contact;
import no.priv.garshol.duke.Column;
import no.priv.garshol.duke.ConfigLoader;
import no.priv.garshol.duke.Configuration;
import no.priv.garshol.duke.Processor;
import no.priv.garshol.duke.Property;
import no.priv.garshol.duke.RecordImpl;
import no.priv.garshol.duke.comparators.DiceCoefficientComparator;
import no.priv.garshol.duke.comparators.Levenshtein;
import no.priv.garshol.duke.comparators.PersonNameComparator;
import no.priv.garshol.duke.datasources.InMemoryDataSource;
import no.priv.garshol.duke.datasources.JDBCDataSource;
import play.Play;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContactDeduplicator {

    public List<Map<Long,Contact>> deduplicate() throws Exception {
        Configuration configuration = new Configuration();
        configuration.setThreshold(0.9057339039936381d);
        configuration.setMaybeThreshold(0.9057339039936381d);

        JDBCDataSource dataSource = new JDBCDataSource();
        dataSource.setDriverClass(Play.configuration.getProperty("db.driver"));
        dataSource.setConnectionString(Play.configuration.getProperty("db.url"));
        dataSource.setUserName(Play.configuration.getProperty("db.user"));
        dataSource.setPassword(Play.configuration.getProperty("db.pass"));
        dataSource.setQuery("select id,company,firstName,lastName,street,city from Contact");
        dataSource.addColumn(new Column("id", "id", null, null));
        dataSource.addColumn(new Column("company", "company", null, null));
        dataSource.addColumn(new Column("firstName", "firstName", null, null));
        dataSource.addColumn(new Column("lastName", "lastName", null, null));
        dataSource.addColumn(new Column("street", "street", null, null));
        dataSource.addColumn(new Column("city", "city", null, null));
        configuration.addDataSource(0, dataSource);

        List<Property> properties = new ArrayList<Property>();
        properties.add(new Property("id"));

        Property company = new Property("company", new Levenshtein(), 0.46558278093899846d, 0.980293137592496d);
        properties.add(company);

        Property firstName = new Property("firstName", new Levenshtein(), 0.3894780895403918d, 0.737258653002584d);
        properties.add(firstName);

        Property lastName = new Property("lastName", new PersonNameComparator(), 0.33473200176209683d, 0.6895391273091356d);
        properties.add(lastName);

        Property street = new Property("street", new Levenshtein(), 0.030475349613534597d, 0.7644584183350849d);
        properties.add(street);

        Property city = new Property("city", new DiceCoefficientComparator(), 0.4590544044617058d, 0.6784963225856417d);
        properties.add(city);

        configuration.setProperties(properties);

        Processor processor = new Processor(configuration);
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
