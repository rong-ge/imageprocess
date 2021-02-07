package sample.utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtils {

    //切圆角
    public static BufferedImage roundImage(BufferedImage image, int width, int height, int cornerRadius) {
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = outputImage.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, width, height, cornerRadius, cornerRadius));
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return outputImage;
    }

    public static BufferedImage removeBackground(File file) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file);

            int alpha = 255;
            String removeRgb = "";

//            int rgb1 = bufferedImage.getRGB(bufferedImage.getMinX(), bufferedImage.getMinY());

            // 遍历Y轴的像素
            for (int y = bufferedImage.getMinY(); y < bufferedImage.getHeight(); y++) {

                // 遍历X轴的像素
                for (int x = bufferedImage.getMinX(); x < bufferedImage.getWidth(); x++) {
                    int rgb = bufferedImage.getRGB(x, y);
                    // 取图片边缘颜色作为对比对象
                    if (y == bufferedImage.getMinY() && x == bufferedImage.getMinX()) {
                        removeRgb = convertRgbStr(rgb);
                        System.out.println("first " + removeRgb);
                    }
                    // 设置为透明背景
                    if (removeRgb.equals(convertRgbStr(rgb))) {
//                        System.out.println("匹配");

                        alpha = 0;
                    } else {
                        alpha = 255;
                    }
                    rgb = (alpha << 24) | (rgb & 0x00ffffff);
                    bufferedImage.setRGB(x, y, rgb);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bufferedImage;
    }

    public static BufferedImage transferAlpha2Byte(File file) {
//        ByteArrayOutputStream byteArrayOutputStream = null;
//        File file = new File(imgSrc);
//        InputStream is = null;
        BufferedImage bufferedImage = null;
        try {
//            is = new FileInputStream(file);
            // 如果是MultipartFile类型，那么自身也有转换成流的方法：is = file.getInputStream();
            BufferedImage bi = ImageIO.read(file);
            Image image = bi;
            ImageIcon imageIcon = new ImageIcon(image);
            bufferedImage = new BufferedImage(imageIcon.getIconWidth(), imageIcon.getIconHeight(),
                    BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g2D = (Graphics2D) bufferedImage.getGraphics();
            g2D.drawImage(imageIcon.getImage(), 0, 0, imageIcon.getImageObserver());
            int alpha = 0;
            for (int j1 = bufferedImage.getMinY(); j1 < bufferedImage.getHeight(); j1++) {
                for (int j2 = bufferedImage.getMinX(); j2 < bufferedImage.getWidth(); j2++) {
                    int rgb = bufferedImage.getRGB(j2, j1);

                    int R = (rgb & 0xff0000) >> 16;
                    int G = (rgb & 0xff00) >> 8;
                    int B = (rgb & 0xff);
                    if (((255 - R) < 30) && ((255 - G) < 30) && ((255 - B) < 30)) {
                        rgb = ((alpha + 1) << 24) | (rgb & 0x00ffffff);
                    }

                    bufferedImage.setRGB(j2, j1, rgb);

                }
            }

            g2D.drawImage(bufferedImage, 0, 0, imageIcon.getImageObserver());
//            byteArrayOutputStream = new ByteArrayOutputStream();
//            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);//转换成byte数组
//            result = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return bufferedImage;


    }

    public static BufferedImage transferAlpha2Byte2(File file, int allow_distance) {
        //        ByteArrayOutputStream byteArrayOutputStream = null;
//        File file = new File(imgSrc);
//        InputStream is = null;
        BufferedImage bufferedImage = null;
        try {
//            is = new FileInputStream(file);
            // 如果是MultipartFile类型，那么自身也有转换成流的方法：is = file.getInputStream();
//            String removeRgb = "";
            BufferedImage bi = ImageIO.read(file);
            Image image = bi;
            ImageIcon imageIcon = new ImageIcon(image);
            bufferedImage = new BufferedImage(imageIcon.getIconWidth(), imageIcon.getIconHeight(),
                    BufferedImage.TYPE_4BYTE_ABGR);

            int[] srcRgb;
            Graphics2D g2D = (Graphics2D) bufferedImage.getGraphics();
            g2D.drawImage(imageIcon.getImage(), 0, 0, imageIcon.getImageObserver());
            int alpha = 0;
//            String removeRgb = convertRgbStr(bufferedImage.getRGB(bufferedImage.getMinY(), bufferedImage.getMinX()));
            int[] removeRgb = convertRgbs(bufferedImage.getRGB(bufferedImage.getMinY(), bufferedImage.getMinX()));
            for (int j1 = bufferedImage.getMinY(); j1 < bufferedImage.getHeight(); j1++) {
                for (int j2 = bufferedImage.getMinX(); j2 < bufferedImage.getWidth(); j2++) {

                    int rgb = bufferedImage.getRGB(j2, j1);

//                    if (j1 == bufferedImage.getMinY() && j2 == bufferedImage.getMinX()) {
////                        removeRgb = convertRgbStr(rgb);
//                        int[] ints = convertRgbs(rgb);
//                    }

                    // 设置为透明背景
//                    if (removeRgb.equals(convertRgbStr(rgb))) {
//                        rgb = ((alpha) << 24) | (rgb & 0x00ffffff);
//                    }

                    srcRgb = convertRgbs(rgb);


//                    int R = (rgb & 0xff0000) >> 16;
//                    int G = (rgb & 0xff00) >> 8;
//                    int B = (rgb & 0xff);
                    //30就是容差值

                    if (((removeRgb[0] - srcRgb[0]) <= allow_distance) && ((removeRgb[1] - srcRgb[1]) <= allow_distance) && ((removeRgb[2] - srcRgb[2]) <= allow_distance)) {
                        rgb = ((alpha + 1) << 24) | (rgb & 0x00ffffff);
                    }

                    bufferedImage.setRGB(j2, j1, rgb);

                }
            }

            g2D.drawImage(bufferedImage, 0, 0, imageIcon.getImageObserver());
//            byteArrayOutputStream = new ByteArrayOutputStream();
//            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);//转换成byte数组
//            result = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return bufferedImage;


    }

    //去除背景
    public static BufferedImage transferAlpha2Byte2(File file) {
        return transferAlpha2Byte2(file, 1);
    }

    public static String convertRgbStr(int color) {
        int red = (color & 0xff0000) >> 16;// 获取color(RGB)中R位
        int green = (color & 0x00ff00) >> 8;// 获取color(RGB)中G位
        int blue = (color & 0x0000ff);// 获取color(RGB)中B位
        return red + "," + green + "," + blue;
    }

    public static int[] convertRgbs(int color) {
        int red = (color & 0xff0000) >> 16;// 获取color(RGB)中R位
        int green = (color & 0x00ff00) >> 8;// 获取color(RGB)中G位
        int blue = (color & 0x0000ff);// 获取color(RGB)中B位
        return new int[]{red, green, blue};
    }

    public static BufferedImage cutHeadImages(File file) {
        try {
            BufferedImage avatarImage = ImageIO.read(file);
            avatarImage = scaleByPercentage(avatarImage, avatarImage.getWidth(), avatarImage.getWidth());
            int width = avatarImage.getWidth();
            // 透明底的图片
            BufferedImage formatAvatarImage = new BufferedImage(width, width, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D graphics = formatAvatarImage.createGraphics();
            //把图片切成一个园
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            //留一个像素的空白区域，这个很重要，画圆的时候把这个覆盖
            int border = 1;
            //图片是一个圆型
            Ellipse2D.Double shape = new Ellipse2D.Double(border, border, width - border * 2, width - border * 2);
            //需要保留的区域
            graphics.setClip(shape);
            graphics.drawImage(avatarImage, border, border, width - border * 2, width - border * 2, null);
            graphics.dispose();
            //在圆图外面再画一个圆
            //新创建一个graphics，这样画的圆不会有锯齿
            graphics = formatAvatarImage.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int border1 = 3;
            //画笔是4.5个像素，BasicStroke的使用可以查看下面的参考文档
            //使画笔时基本会像外延伸一定像素，具体可以自己使用的时候测试
            Stroke s = new BasicStroke(5F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            graphics.setStroke(s);
            graphics.setColor(Color.WHITE);
            graphics.drawOval(border1, border1, width - border1 * 2, width - border1 * 2);
            graphics.dispose();
//            OutputStream os = new FileOutputStream("C:\\Users\\EDZ\\Desktop\\剪裁图片\\13000.png");//发布项目时，如：Tomcat 他会在服务器本地tomcat webapps文件下创建此文件名
//            ImageIO.write(formatAvatarImage, "PNG", os);
            return formatAvatarImage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 缩小Image，此方法返回源图像按给定宽度、高度限制下缩放后的图像
     *
     * @param inputImage ：压缩后宽度
     *                   ：压缩后高度
     * @throws java.io.IOException return
     */
    public static BufferedImage scaleByPercentage(BufferedImage inputImage, int newWidth, int newHeight) {
        // 获取原始图像透明度类型
        try {
            int type = inputImage.getColorModel().getTransparency();
            int width = inputImage.getWidth();
            int height = inputImage.getHeight();
            // 开启抗锯齿
            RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // 使用高质量压缩
            renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            BufferedImage img = new BufferedImage(newWidth, newHeight, type);
            Graphics2D graphics2d = img.createGraphics();
            graphics2d.setRenderingHints(renderingHints);
            graphics2d.drawImage(inputImage, 0, 0, newWidth, newHeight, 0, 0, width, height, null);
            graphics2d.dispose();
            return img;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
