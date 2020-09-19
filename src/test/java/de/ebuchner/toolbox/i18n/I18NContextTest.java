package de.ebuchner.toolbox.i18n;

import de.ebuchner.toolbox.i18n.nosys.NoSys;
import junit.framework.TestCase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class I18NContextTest extends TestCase {

    private static final String INT_DATE_FMT = "dd.MM.yyyy";

    public void testGermanContext() throws ParseException {
        I18NContext i18n = new I18NContext(Locale.GERMAN, I18NContextTest.class);
        assertEquals("###TestUiEntry (de)###",
                i18n.getString("ui_entry"));
        assertEquals("###TestSysEntry###",
                i18n.getString("sys_entry"));
        assertEquals("###TestNoSuchEntry###",
                i18n.getOptionalString("no_such_entry", "###TestNoSuchEntry###"));

        Date d = new SimpleDateFormat(INT_DATE_FMT).parse("17.10.1963");
        assertEquals("Do, 17.10.63", i18n.formatDate("format.dateformat.1", d));

        assertEquals("1.024,10", i18n.formatNumber("format.numformat.1", 1024.1));
        assertEquals("0,00", i18n.formatNumber("format.numformat.1", 0.0));
    }

    public void testEnglishContext() throws ParseException {
        I18NContext i18n = new I18NContext(Locale.ENGLISH, I18NContextTest.class);
        assertEquals("###TestUiEntry (en)###",
                i18n.getString("ui_entry"));
        assertEquals("###TestSysEntry###",
                i18n.getString("sys_entry"));
        assertEquals("###TestNoSuchEntry###",
                i18n.getOptionalString("no_such_entry", "###TestNoSuchEntry###"));


        Date d = new SimpleDateFormat(INT_DATE_FMT).parse("17.10.1963");
        assertEquals("Thu, 10/17/63", i18n.formatDate("format.dateformat.1", d));

        assertEquals("1,024.10", i18n.formatNumber("format.numformat.1", 1024.1));
        assertEquals("0.00", i18n.formatNumber("format.numformat.1", 0.0));
    }

    public void testNoSysEnglishProperties() {
        I18NContext i18n = new I18NContext(Locale.ENGLISH, "de.ebuchner.toolbox.i18n.nosys.some");
        assertEquals("ENGLISH", i18n.getString("some.property"));
    }

    public void testNoSysGermanProperties() {
        I18NContext i18n = new I18NContext(Locale.GERMAN, "de.ebuchner.toolbox.i18n.nosys.some");
        assertEquals("DEUTSCH", i18n.getString("some.property"));
    }

    public void testNoSysPropertiesFromClass() {
        I18NContext i18n = new I18NContext(Locale.GERMAN, NoSys.class);
        assertEquals("success", i18n.getString("from.ui.property"));
    }


}