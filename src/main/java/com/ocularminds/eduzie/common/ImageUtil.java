package com.ocularminds.eduzie.common;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import javax.imageio.ImageIO;

/*
 * @author Jejelowo Festus
 *
 */
public class ImageUtil {

	public static final int AVATAR_SMALL_WIDTH = 50;
	public static final int AVATAR_SMALL_HEIGHT = 50;
	public static final int AVATAR_WIDTH = 110;
	public static final int AVATAR_HEIGHT = 110;

	public static void main(String [] args){

		try{

			BufferedImage originalImage = ImageIO.read(new File("c:\\image\\mkyong.jpg"));
			int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

			BufferedImage resizeImageJpg = resizeImage(originalImage, type);
			ImageIO.write(resizeImageJpg, "jpg", new File("c:\\image\\mkyong_jpg.jpg"));

			BufferedImage resizeImagePng = resizeImage(originalImage, type);
			ImageIO.write(resizeImagePng, "png", new File("c:\\image\\mkyong_png.jpg"));

			/*BufferedImage resizeImageHintJpg = resizeImageWithHint(originalImage, type);
			ImageIO.write(resizeImageHintJpg, "jpg", new File("c:\\image\\mkyong_hint_jpg.jpg"));

			BufferedImage resizeImageHintPng = resizeImageWithHint(originalImage, type);
			ImageIO.write(resizeImageHintPng, "png", new File("c:\\image\\mkyong_hint_png.jpg"));*/

		}catch(IOException e){
			System.out.println(e.getMessage());
		}

    }

    public static BufferedImage resizeImage(BufferedImage originalImage, int type){

		BufferedImage resizedImage = new BufferedImage(AVATAR_WIDTH, AVATAR_HEIGHT, type);
		resizedImage.getGraphics().drawImage(resizedImage.getScaledInstance(AVATAR_WIDTH, AVATAR_HEIGHT, Image.SCALE_SMOOTH), 0, 0, null);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, AVATAR_WIDTH, AVATAR_HEIGHT, null);
		g.dispose();

		return resizedImage;
    }

    public static void scale(String inFile, String outFile,int width,int height,String extension) {

		try {

		   File f = new File(inFile);
			   if (!f.exists()) {
			   return;
		   }

			Image src = javax.imageio.ImageIO.read(f);
			BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
			bi.getGraphics().drawImage(src.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);

			FileOutputStream out = new FileOutputStream(outFile);
			ImageIO.write(bi, extension, out);
			out.close();

		} catch (IOException ex) {
		   ex.printStackTrace();
		}
    }
}