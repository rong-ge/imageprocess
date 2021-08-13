package sample;


import com.baidu.translate.Const;
import com.baidu.translate.data.Config;
import com.baidu.translate.data.Language;
import com.baidu.translate.pic.PicTranslate;
import com.google.gson.Gson;
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
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import sample.bean.BaiduTranslateBean;
import sample.utils.FontUtil;
import sample.utils.ImageUtils;
import sun.font.FontDesignMetrics;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
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

            lb_out_path.setText("已完成，请到switch目录下查看");
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

        lb_out_path.setText("已完成，请到round目录下查看");

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

    public void translate(File file) {
        String absolutePath = file.getAbsolutePath();
        Config config = new Config(Const.APPID, Const.SECRET_KEY);
        config.lang(Language.EN, Language.ZH);
        config.pic(absolutePath);
        config.erase(Config.ERASE_NONE);
        config.paste(Config.PASTE_FULL);
        PicTranslate picTranslate = new PicTranslate();
        picTranslate.setConfig(config);

        int fontSize = 30;
        Font font = new Font("宋体", Font.BOLD, fontSize);

        int rectX, rectY, rectW, rectH;
        try {

            //读取图片文件，得到BufferedImage对象
            BufferedImage bimg = ImageIO.read(new FileInputStream(file));
            //得到Graphics2D 对象
            Graphics2D g2d = (Graphics2D) bimg.getGraphics();

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


            Response trans = picTranslate.trans();
            if (trans.isSuccessful()) {
                String string = trans.body().string();

                BaiduTranslateBean baiduTranslateBean = new Gson().fromJson(string, BaiduTranslateBean.class);
                for (BaiduTranslateBean.DataBean.ContentBean contentBean : baiduTranslateBean.getData().getContent()) {

                    String rect = contentBean.getRect();
                    String[] split = rect.split(" ");
                    rectX = Integer.valueOf(split[0]);
                    rectY = Integer.valueOf(split[1]);
                    rectW = Integer.valueOf(split[2]);
                    rectH = Integer.valueOf(split[3]);
                    //设置颜色和画笔粗细
                    g2d.setColor(Color.WHITE);
                    //绘制图案或文字
                    g2d.fillRect(rectX, rectY, rectW, rectH);

//                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
                    //设置颜色和画笔粗细
                    g2d.setColor(Color.RED);
                    //绘制图案或文字
                    g2d.drawRect(rectX, rectY, rectW, rectH);


                    String dst = contentBean.getDst();
                    String dst1;

                    fontSize = FontUtil.getRightSize(dst, rectW);
                    font = new Font("宋体", Font.BOLD, fontSize);
                    g2d.setFont(font);
                    g2d.setColor(Color.RED);
                    FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);
                    int i = 1;
                    int len;
                    while (true) {
                        boolean maxStr;
//                        if (contentBean.getLineCount() > 1) {
//                            maxStr = FontUtil.isMaxStr(font, dst, contentBean.getPoints().get(1).getX() - contentBean.getPoints().get(0).getX());
//                        }

                        maxStr = FontUtil.isMaxStr(font, dst, contentBean.getPoints().get(1).getX() - contentBean.getPoints().get(0).getX());

                        if (!maxStr) {
                            len = FontUtil.getSplitMaxLenth(font, dst, contentBean.getPoints().get(1).getX() - contentBean.getPoints().get(0).getX());
                            dst1 = dst.substring(0, len);
                            g2d.drawString(dst1, rectX, rectY + i * fontSize - metrics.getDescent() * 2);
                            dst = dst.substring(len, dst.length());

                        } else {
                            g2d.drawString(dst, rectX, rectY + i * fontSize - metrics.getDescent() * 2);
                            break;
                        }
                        i++;
                    }

                }

                g2d.dispose();

                int lastDot = absolutePath.lastIndexOf(".");

                File translate = new File("translate");
                FileUtils.forceMkdir(translate);

                file = new File(translate, file.getName());
                ImageIO.write(bimg, absolutePath.substring(lastDot + 1), file);

                FileUtils.writeByteArrayToFile(new File(file,"baidu-"+file.getName()), Base64.getDecoder().decode(baiduTranslateBean.getData().getPasteImg()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void translate(ActionEvent actionEvent) {
        for (File mChooseFile : mChooseFiles) {
            translate(mChooseFile);
        }

        lb_out_path.setText("已完成，请到translate目录下查看");

    }

}
