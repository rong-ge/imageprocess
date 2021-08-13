package com.baidu.translate;

import com.baidu.translate.data.Config;
import com.baidu.translate.data.Language;
import com.baidu.translate.pic.PicTranslate;
import com.baidu.translate.http.HttpStringCallback;
import com.google.gson.Gson;
import sample.bean.BaiduTranslateBean;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
//        Config config = new Config(Const.APPID, Const.SECRET_KEY);
//        config.lang(Language.EN, Language.ZH);
//        config.pic(Const.FILE_PATH);
//        config.erase(Config.ERASE_NONE);
//        config.paste(Config.PASTE_FULL);
//
//        PicTranslate picTranslate = new PicTranslate();
//        picTranslate.setConfig(config);
//
//        picTranslate.trans(new HttpStringCallback() {
//            @Override
//            protected void onSuccess(String response) {
//                super.onSuccess(response);
//                System.out.println("response:" + response);
//            }
//
//            @Override
//            protected void onFailure(Throwable e) {
//                super.onFailure(e);
//            }
//        });

        //读取图片文件，得到BufferedImage对象
//        BufferedImage bimg = null;
//        try {
//            bimg = ImageIO.read(new FileInputStream(Const.FILE_PATH));
//            //得到Graphics2D 对象
//            Graphics2D g2d = (Graphics2D) bimg.getGraphics();
//            //设置颜色和画笔粗细
//            g2d.setColor(Color.WHITE);
////            g2d.setStroke(new BasicStroke(3));
//            String rect = "1963 380 278 41";
//            String[] split = rect.split(" ");
//            int rectX = Integer.valueOf(split[0]);
//            int rectY = Integer.valueOf(split[1]);
//            int rectW = Integer.valueOf(split[2]);
//            int rectH = Integer.valueOf(split[3]);
//            //绘制图案或文字
////            g2d.drawString("0019607544", Integer.valueOf(split[0]), Integer.valueOf(split[1]));
//            g2d.fillRect(rectX, rectY, rectW, rectH);
////            "points":[{"x":1959,"y":379},{"x":2245,"y":379},{"x":2245,"y":424},{"x":1959,"y":424}]
//
//            Font font = new Font("宋体", Font.BOLD, 45);
//
//            g2d.setFont(font);
//            g2d.setColor(Color.RED);
//            g2d.drawString("0019607544", 1959, 424);
//
//            g2d.dispose();
//            ImageIO.write(bimg, "jpg", new FileOutputStream("cusfanyi.jpg"));
//
//            System.out.println("ok");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        Config config = new Config(Const.APPID, Const.SECRET_KEY);
        config.lang(Language.EN, Language.ZH);
        config.pic(Const.FILE_PATH);
        config.erase(Config.ERASE_NONE);
        config.paste(Config.PASTE_NONE);
        System.out.println(Thread.currentThread().getName());
        PicTranslate picTranslate = new PicTranslate();
        picTranslate.setConfig(config);

        picTranslate.trans(new HttpStringCallback() {
            @Override
            protected void onSuccess(String response) {
                super.onSuccess(response);
                System.out.println(Thread.currentThread().getName());
                try {
                    //读取图片文件，得到BufferedImage对象
                    BufferedImage bimg = ImageIO.read(new FileInputStream(Const.FILE_PATH));
                    //得到Graphics2D 对象
                    Graphics2D g2d = (Graphics2D) bimg.getGraphics();

                    BaiduTranslateBean baiduTranslateBean = new Gson().fromJson(response, BaiduTranslateBean.class);
                    for (BaiduTranslateBean.DataBean.ContentBean contentBean : baiduTranslateBean.getData().getContent()) {
                        //设置颜色和画笔粗细
                        g2d.setColor(Color.WHITE);
                        String rect = contentBean.getRect();
                        String[] split = rect.split(" ");
                        int rectX = Integer.valueOf(split[0]);
                        int rectY = Integer.valueOf(split[1]);
                        int rectW = Integer.valueOf(split[2]);
                        int rectH = Integer.valueOf(split[3]);
                        //绘制图案或文字
                        g2d.fillRect(rectX, rectY, rectW, rectH);
                        System.out.println("rectX " + rectX);

                        BaiduTranslateBean.DataBean.ContentBean.PointsBean pointsBeanStart = contentBean.getPoints().get(0);
                        BaiduTranslateBean.DataBean.ContentBean.PointsBean pointsBeanEnd = contentBean.getPoints().get(3);

                        Font font = new Font("宋体", Font.BOLD, pointsBeanEnd.getY() - pointsBeanStart.getY());
                        g2d.setFont(font);
                        g2d.setColor(Color.RED);
                        g2d.drawString(contentBean.getDst(), contentBean.getPoints().get(3).getX(), contentBean.getPoints().get(3).getY());
                        System.out.println("dst " + contentBean.getDst());

                    }

                    g2d.dispose();
                    ImageIO.write(bimg, "jpg", new FileOutputStream("cusfanyi1.jpg"));
                    System.out.println("ok");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected void onFailure(Throwable e) {
                super.onFailure(e);
            }
        });
    }
}
