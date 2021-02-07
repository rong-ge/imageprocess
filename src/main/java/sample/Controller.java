package sample;


import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import org.apache.commons.io.FileUtils;
import sample.utils.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Controller {

    public TextField tf_type;
    public TextField tf_width;
    public TextField tf_height;
    public ImageView iv_show;
    //    public TextField tf_file_name;
    public CheckBox cb_keepAspectRatio;
    public TextArea ta_mChooseFiles;
    public Label lb_out_path;
    public TextField tf_allow_dis;
    private Window stage;
    private File initialDirectory;
    //    private String newFileName;
    private List<File> mChooseFiles;

    public void setStage(Stage primaryStage) {
        stage = primaryStage;
    }

    private boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public void switchFormat(ActionEvent actionEvent) {
        try {
//            newFileName = tf_file_name.getText() + "." + tf_type.getText();
            Thumbnails.Builder<File> builder = Thumbnails.of(mChooseFiles.toArray(new File[mChooseFiles.size()]));

            builder.size(Integer.valueOf(tf_width.getText().trim()), Integer.valueOf(tf_height.getText().trim()));
            builder.keepAspectRatio(cb_keepAspectRatio.isSelected());

            if (!isEmpty(tf_type.getText().trim())) {
                builder.outputFormat(tf_type.getText());
            }
//            builder.toFile(newFileName);

            File out = new File("switch");
            FileUtils.forceMkdir(out);

            builder.toFiles(out, Rename.NO_CHANGE);

//            iv_show.setImage(new Image(new File(newFileName).toURI().toURL().toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void selectFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        if (initialDirectory == null) {
            initialDirectory = new File(System.getProperty("user.dir"));
        }

        fileChooser.setInitialDirectory(initialDirectory);

        mChooseFiles = fileChooser.showOpenMultipleDialog(stage);
        initialDirectory = fileChooser.getInitialDirectory();

        if (mChooseFiles != null) {
            ta_mChooseFiles.clear();
            for (File mChooseFile : mChooseFiles) {
                ta_mChooseFiles.appendText(mChooseFile.toString() + "\n");
            }
        }

    }

    public void removeBackground(ActionEvent actionEvent) {
        for (File mChooseFile : mChooseFiles) {
            removeBackground(mChooseFile);

        }
    }

    private void removeBackground(File file) {
        String absolutePath = file.getAbsolutePath();
        int lastSplit = absolutePath.lastIndexOf("\\");
        int lastDot = absolutePath.lastIndexOf(".");
        absolutePath = absolutePath.substring(lastSplit + 1, lastDot);

        System.out.println(absolutePath);

        BufferedImage bufferedImage = ImageUtils.transferAlpha2Byte2(file, Integer.valueOf(tf_allow_dis.getText()));
        try {
            File round = new File("removeBackground");
            FileUtils.forceMkdir(round);

            ImageIO.write(bufferedImage, "png", new File(round, absolutePath + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void round(ActionEvent actionEvent) {
        for (File mChooseFile : mChooseFiles) {
            round(mChooseFile);
        }


    }

    private void round(File file) {

        String absolutePath = file.getAbsolutePath();
        int lastSplit = absolutePath.lastIndexOf("\\");
        int lastDot = absolutePath.lastIndexOf(".");
        absolutePath = absolutePath.substring(lastSplit + 1, lastDot);

        BufferedImage bufferedImage = ImageUtils.cutHeadImages(file);
        try {
            File round = new File("round");
            FileUtils.forceMkdir(round);

            ImageIO.write(bufferedImage, "png", new File(round, absolutePath + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
