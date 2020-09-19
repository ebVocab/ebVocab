package de.ebuchner.vocab.fx.platform;

import de.ebuchner.vocab.config.Config;
import de.ebuchner.vocab.model.font.VocabFont;
import de.ebuchner.vocab.model.io.ImageEncoderBehaviour;
import de.ebuchner.vocab.model.io.ImagePath;
import de.ebuchner.vocab.model.io.VocabIOConstants;
import de.ebuchner.vocab.model.lessons.entry.VocabEntry;
import de.ebuchner.vocab.model.lessons.entry.VocabEntryList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FxImgEncoder implements ImageEncoderBehaviour {

    public void encode(VocabFont vocabFont, ZipOutputStream outputStream, VocabEntryList entries) {
        try {
            Font font = FxUIPlatform.instance().toFont(vocabFont);
            for (VocabEntry entry : entries.entries()) {
                String id = entry.getId();

                for (String fieldName : VocabIOConstants.VOCAB_IMAGE_FIELDS) {
                    String fieldValue = entry.getFieldValue(fieldName);

                    ZipEntry zipEntry = new ZipEntry(new ImagePath(id, fieldName).toPath());
                    outputStream.putNextEntry(zipEntry);

                    encodeToImage(outputStream, fieldValue, font, Config.instance().getExtraTextFieldHeight());

                    outputStream.closeEntry();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void encodeToImage(OutputStream out, String text, Font font, int extraTextFieldHeight) {
        Text textCtrl = new Text(text);
        //  VOCAB-171: Text is clipped in mobile version
        textCtrl.setFont(new Font(textCtrl.getFont().getName(), font.getSize()));
        textCtrl.setFill(Color.WHITE);
        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.BLACK);

        try {
            ImageIO.write(
                    SwingFXUtils.fromFXImage(
                            textCtrl.snapshot(
                                    sp,
                                    null /*null creates a new image*/
                            ),
                            null /*optional AWT buffered image instance*/
                    ),
                    VocabIOConstants.VOCAB_IMAGE_FORMAT,
                    out
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
