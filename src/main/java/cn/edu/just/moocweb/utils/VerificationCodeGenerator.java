package cn.edu.just.moocweb.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Random;

public class VerificationCodeGenerator {
    public static HashMap<String,Object> generator(){
        BufferedImage image=new BufferedImage(100, 30, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        Random ran=new Random();
        graphics.setColor(new Color(ran.nextInt(255),ran.nextInt(255),ran.nextInt(255)));
        graphics.fillRect(0, 0, 100, 30);
        String num = getRandomNum(4);
        graphics.setColor(new Color(0, 0, 0));
        graphics.setFont(new Font(null, Font.BOLD, 24));
        graphics.drawString(num, 15, 25);
        //（7）绘制多条干扰线
        for (int i = 0; i < 8; i++) {
            graphics.setColor(new Color(ran.nextInt(255),ran.nextInt(255),ran.nextInt(255)));
            graphics.drawLine(ran.nextInt(100), ran.nextInt(30), ran.nextInt(100), ran.nextInt(30));
        }
        HashMap<String,Object> map=new HashMap<>();
        map.put("number", num);
        map.put("image", image);
        return map;
    }

    private static String getRandomNum(int size){
        StringBuffer sb = new StringBuffer();
        char[] str = {'A','B','C','D','E','F','G','H','I','G','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','1','2','3','4','5','6','7','8','9'};
        for(int i=0;i<size;i++){
            sb.append(str[new Random().nextInt(str.length-1)]);
        }
        return sb.toString();
    }
}
