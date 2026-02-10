package com.example.ddd.infrastructure.security;

import lombok.Data;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 验证码工具类
 *
 * @author DDD Demo
 */
public class CaptchaUtil {

    /**
     * 验证码字符集
     */
    private static final char[] CODE_SEQUENCE = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M',
            'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '2', '3', '4', '5', '6', '7', '8', '9'
    };

    /**
     * 验证码长度
     */
    private static final int CODE_LENGTH = 4;

    /**
     * 验证码图片宽度
     */
    private static final int WIDTH = 120;

    /**
     * 验证码图片高度
     */
    private static final int HEIGHT = 40;

    /**
     * 随机数生成器
     */
    private static final Random RANDOM = new Random();

    /**
     * 生成验证码
     *
     * @return 验证码对象
     */
    public static CaptchaResult generateCaptcha() {
        // 创建图像
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();

        // 填充背景
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, WIDTH, HEIGHT);

        // 生成验证码
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CODE_SEQUENCE[RANDOM.nextInt(CODE_SEQUENCE.length)]);
        }

        // 绘制验证码
        drawCode(graphics, code.toString());

        // 绘制干扰线
        drawInterferenceLines(graphics);

        // 绘制干扰点
        drawInterferencePoints(graphics);

        graphics.dispose();

        return new CaptchaResult(code.toString(), image);
    }

    /**
     * 绘制验证码
     */
    private static void drawCode(Graphics graphics, String code) {
        graphics.setFont(new Font("Arial", Font.BOLD, 28));

        for (int i = 0; i < code.length(); i++) {
            // 随机颜色
            graphics.setColor(getRandomColor(50, 150));

            // 随机旋转
            Graphics2D g2d = (Graphics2D) graphics;
            int angle = RANDOM.nextInt(30) - 15;

            // 计算字符位置
            int x = 20 + i * 25;
            int y = 30;

            g2d.rotate(Math.toRadians(angle), x, y);
            g2d.drawString(String.valueOf(code.charAt(i)), x, y);
            g2d.rotate(-Math.toRadians(angle), x, y);
        }
    }

    /**
     * 绘制干扰线
     */
    private static void drawInterferenceLines(Graphics graphics) {
        for (int i = 0; i < 5; i++) {
            graphics.setColor(getRandomColor(100, 200));

            int x1 = RANDOM.nextInt(WIDTH);
            int y1 = RANDOM.nextInt(HEIGHT);
            int x2 = RANDOM.nextInt(WIDTH);
            int y2 = RANDOM.nextInt(HEIGHT);

            graphics.drawLine(x1, y1, x2, y2);
        }
    }

    /**
     * 绘制干扰点
     */
    private static void drawInterferencePoints(Graphics graphics) {
        for (int i = 0; i < 50; i++) {
            graphics.setColor(getRandomColor(150, 220));

            int x = RANDOM.nextInt(WIDTH);
            int y = RANDOM.nextInt(HEIGHT);

            graphics.fillOval(x, y, 1, 1);
        }
    }

    /**
     * 获取随机颜色
     */
    private static Color getRandomColor(int min, int max) {
        int r = min + RANDOM.nextInt(max - min);
        int g = min + RANDOM.nextInt(max - min);
        int b = min + RANDOM.nextInt(max - min);

        return new Color(r, g, b);
    }

    /**
     * 验证码结果
     */
    @Data
    public static class CaptchaResult {
        /**
         * 验证码
         */
        private final String code;

        /**
         * 验证码图片
         */
        private final BufferedImage image;
    }
}
