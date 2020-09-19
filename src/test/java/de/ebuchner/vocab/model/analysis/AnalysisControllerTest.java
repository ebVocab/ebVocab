package de.ebuchner.vocab.model.analysis;

import junit.framework.TestCase;

public class AnalysisControllerTest extends TestCase {

    class MyWindow implements AnalysisWindowBehaviour {
        private String result;
        private String unicode;

        public void showResult(String result) {
            this.result = result;
        }

        public void showUnicode(String unicode) {
            this.unicode = unicode;
        }
    }

    public void testOnConvert() throws Exception {
        MyWindow window = new MyWindow();
        AnalysisController controller = new AnalysisController(window);

        controller.onConvert(null);
        assertEquals("", window.result);
        assertEquals("", window.unicode);
        controller.onConvert("");
        assertEquals("", window.result);
        assertEquals("", window.unicode);
        controller.onConvert("  ");
        assertEquals("", window.result);
        assertEquals("", window.unicode);

        controller.onConvert("1");
        assertEquals("1", window.result);
        assertEquals("\\u0031", window.unicode);
        controller.onConvert(" 1 ");
        assertEquals("1", window.result);
        assertEquals("\\u0031", window.unicode);
        controller.onConvert(" abc ");
        assertEquals("a b c", window.result);
        assertEquals("\\u0061 \\u0062 \\u0063", window.unicode);
        controller.onConvert(" abc def ");
        assertEquals("a b c   d e f", window.result);
        assertEquals("\\u0061 \\u0062 \\u0063 - \\u0064 \\u0065 \\u0066", window.unicode);
    }
}
