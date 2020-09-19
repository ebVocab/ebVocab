package de.ebuchner.vocab.fx.project;

import de.ebuchner.vocab.config.VocabLanguage;
import de.ebuchner.vocab.model.project.ProjectConfiguration;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractProjectCellFactory {
    Map<String, Image> imageMap = new HashMap<>();

    protected Image imageOf(VocabLanguage language) {
        if (language == null)
            return null;

        return imageOf(language.getCode());
    }

    protected Image imageOf(String languageCode) {
        Image image = imageMap.get(languageCode);
        if (image == null) {
            URL imgUrl = ProjectConfiguration.configImageURL(languageCode);
            if (imgUrl == null)
                return null;
            image = new Image(imgUrl.toExternalForm(), 24, 16, true, true);

            imageMap.put(languageCode, image);
        }
        return image;
    }


}
