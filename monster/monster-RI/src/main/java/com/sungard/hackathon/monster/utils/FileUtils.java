package com.sungard.hackathon.monster.utils;

import static com.sungard.hackathon.monster.utils.Constants.FOLDER_TRAIN_IMG;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

	public static void initDirs() {
		mkdirs(Constants.FOLDER_TRAIN_IMG);
		mkdirs(Constants.FOLDER_TEST_IMG);
	}

	public static void mkdirs(String folderString) {
		File folder = new File(folderString);
		if (!folder.exists()) {
			folder.mkdirs();
		}
	}

	public static void cleanWorkspace() {
		File trainFolder = new File(Constants.FOLDER_TRAIN_IMG);
		if (trainFolder.exists()) {
			for (File file : trainFolder.listFiles()) {
				file.delete();
			}
		}
		File testFolder = new File(Constants.FOLDER_TEST_IMG);
		if (testFolder.exists()) {
			for (File file : trainFolder.listFiles()) {
				file.delete();
			}
		}

	}

	public static String getPersonWorkSpace(String personName) {
		String spacedest = FOLDER_TRAIN_IMG + File.separator + personName;
		File spacefolder = new File(spacedest);
		if (!spacefolder.exists()) {
			spacefolder.mkdirs();
		}
		return spacedest;
	}

	public static String genTestName(String suffix) {
		return Constants.FOLDER_TEST_IMG + File.separator + System.nanoTime()
				+ "." + suffix;
	}

	public static void saveImage(String imageName, byte[] data) {
		File imgFile = new File(imageName);
		if (imgFile.exists()) {
			imgFile.delete();
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(imgFile);
			fos.write(data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
			}
		}
	}
}
