package com.crihexe;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.json.JSONObject;

public class Utils {
	
	public static BufferedImage getImage(byte[] bytes) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		return ImageIO.read(bais);
	}
	
	public static String saveImageAsPNG(BufferedImage image, String filename) {
		filename += ".png";
		try {
			ImageIO.write(image, "png", new File(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filename;
	}
	
	public static void saveJSONtoFile(JSONObject obj, int indentFactor, File file) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		
		bw.write(obj.toString(indentFactor));
		
		bw.close();
	}
	
	public static JSONObject generateErrorJSON(String message) {
		return new JSONObject().put("error", true).put("message", message);
	}
	
}
