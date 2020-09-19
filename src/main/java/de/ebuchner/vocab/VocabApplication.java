package de.ebuchner.vocab;

import java.util.ResourceBundle;

public class VocabApplication {

    private VocabApplication() {

    }

    public static void main(String[] args) throws Exception {

        ResourceBundle res = ResourceBundle.getBundle("de/ebuchner/vocab/init");
        ApplicationInitializer initializer = (ApplicationInitializer)
                Class.forName(
                        res.getString(
                                "application.initializer.class.fx"
                        )

                ).newInstance();
        initializer.startVocab(args);
    }

}
