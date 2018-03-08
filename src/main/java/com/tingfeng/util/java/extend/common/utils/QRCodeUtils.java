package com.tingfeng.util.java.extend.common.utils;

import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.tingfeng.util.java.extend.common.bean.BufferedImageLuminanceSource;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;
import java.util.Map;

public class QRCodeUtils {
    private static final String CHARSET = "utf-8";



    public static BufferedImage createImage(String content,int imgWidth,int imgHeight,String logImgPath) throws Exception {
        BufferedImage image = ImageIO.read(new File(logImgPath));
        return createImage(content,imgWidth,imgHeight,image,true);
    }


    public static BufferedImage createImage(String content,int imgWidth,int imgHeight,Image logImg,boolean needCompress) throws Exception {
        Map<EncodeHintType,Object> hints = new Hashtable<>();
        // 设置二维码排错率，可选L(7%)、M(15%)、Q(25%)、H(30%)，排错率越高可存储的信息越少，但对二维码清晰度的要求越小
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.QR_CODE, imgWidth, imgHeight, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000
                        : 0xFFFFFFFF);
            }
        }
        if(null != logImg) {
            QRCodeUtils.insertImage(image,logImg, needCompress);
        }
        return image;
    }

    public static void insertImage(BufferedImage qrCodeImg,Image logImg,boolean needCompress) throws Exception {
        int width = logImg.getWidth(null);
        int height = logImg.getHeight(null);
        if (needCompress) { // 压缩LOGO
                width = qrCodeImg.getWidth() / 5;
                height = qrCodeImg.getHeight() / 5;
            Image image = logImg.getScaledInstance(width, height,
                    Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图
            g.dispose();
            logImg = image;
        }
        // 插入LOGO
        Graphics2D graph = qrCodeImg.createGraphics();
        int x = (qrCodeImg.getWidth() - width) / 2;
        int y = (qrCodeImg.getHeight() - height) / 2;
        graph.drawImage(logImg, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }


    public static String readQrCode(final BufferedImage qrCodeImg) throws Exception {
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(qrCodeImg)));
        Result result;
        Map<DecodeHintType,Object> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
        result = new MultiFormatReader().decode(bitmap, hints);
        String resultStr = result.getText();
        return resultStr;
    }


    public static void main(String[] args) throws Exception {
        JFrame jFrame = new JFrame();
        jFrame.setBounds(400, 400, 250, 250);
        ImageIcon img = new ImageIcon(QRCodeUtils.createImage("123", 150, 150, "D:/tmp/20170809165650142.png"));
        JLabel background = new JLabel(img);
        jFrame.add(background);
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}
