/**
 * 文件操作
 * 
 * @author 贺亮
 * 
 */
package com.bluetooth.le.soloman;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;

public class FileUtils {
	public String SDPATH;

	private int FILESIZE = 4 * 1024;

	public String getSDPATH() {
		return SDPATH;
	}

	public FileUtils() {
		// 得到当前外部存储设备的目录( /SDCARD )
		SDPATH = Environment.getExternalStorageDirectory() + "/";
	}

	/**
	 * 在SD卡上创建文件
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public File createSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + fileName);
		file.createNewFile();
		return file;
	}

	public File createFile(String fileName) throws IOException {
		File file = new File(fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * 在SD卡上创建目录
	 * 
	 * @param dirName
	 * @return
	 */
	public File createSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		// dir.mkdir(); //单级目录
		dir.mkdirs(); // 多级目录
		return dir;
	}

	public File createDir(String dirName) {
		File dir = new File(dirName);
		// dir.mkdir(); //单级目录
		dir.mkdirs(); // 多级目录
		return dir;
	}

	/**
	 * 在SD卡上删除目录
	 * 
	 * @param dirName
	 * @return
	 */
	public void deleteFile(String fileName) {
		File file = new File(fileName);
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					//递归调用
					this.deleteFile(files[i].toString());
					this.delFolder(files[i].toString());
				}
			}
			file.delete();
		} else {
			System.out.println(fileName + "所删除的文件不存在！" + '\n');
		}
	}

	/**
	 * 删除文件夹
	 * 
	 * @param folderPath
	 *            String sd文件夹路径及名称 如mnt/sdcard/ordering/ct
	 * @return
	 */
	public void delFolder(String folderPath) {
		try {
			deleteFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
			System.out.println("删除文件夹" + filePath + "操作成功");

		} catch (Exception e) {
			System.out.println("删除文件夹操作出错");
			e.printStackTrace();

		}
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 * 
	 * @param fileName
	 * @return 1：存在 0：不存在
	 */
	public boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.exists();
	}

	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 * 
	 * @param path
	 * @param fileName
	 * @param input
	 * @return
	 */
	public File write2SDFromInput(String path, String fileName,
			InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			createSDDir(path);// 建立文件夹
			file = createSDFile(path + fileName);// 建文件
			output = new FileOutputStream(file);
			byte[] buffer = new byte[FILESIZE];
			int count;// count为实际读取的字节数
			while ((count = input.read(buffer)) != -1) { // 直到读完
				output.write(buffer, 0, count);
			}

			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// input.close();
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	
	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 * 
	 * @param path
	 * @param fileName
	 * @param input
	 * @return
	 */
	public File write2SDFromInput(String path, String fileName, String input) {
		File file = null;
		OutputStream output = null;
		try {
			createSDDir(path);// 建立文件夹
			file = createSDFile(path + fileName);// 建文件
			output = new FileOutputStream(file);
			byte [] bytes = input.getBytes();   
			output.write(bytes);
			output.close();
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// input.close();
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	public File writeFromInput(String path, String fileName, InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			createDir(path);// 建立文件夹
			file = createFile(path + fileName);// 建文件
			output = new FileOutputStream(file);
			byte[] buffer = new byte[FILESIZE];
			int count;// count为实际读取的字节数
			while ((count = input.read(buffer)) != -1) { // 直到读完
				output.write(buffer, 0, count);
			}

			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// input.close();
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	
	public File writeFromInput(String path, String fileName, String input) {
		File file = null;
		OutputStream output = null;
		try {
			createDir(path);// 建立文件夹
			file = createFile(path + fileName);// 建文件
			output = new FileOutputStream(file);
			byte [] bytes = input.getBytes();   
			output.write(bytes);
			output.close();
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// input.close();
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	public String readFile(String filePath) {
		//String rtn = "";
		StringBuilder rtnb = new StringBuilder();  

		try {
			FileReader fr = new FileReader(filePath);// 创建FileReader对象，用来读取字符流
			BufferedReader br = new BufferedReader(fr); // 缓冲指定文件的输入
			// FileWriter fw = new
			// FileWriter("f:/jackie.txt");//创建FileWriter对象，用来写入字符流
			// BufferedWriter bw = new BufferedWriter(fw); //将缓冲对文件的输出
			String myreadline; // 定义一个String类型的变量,用来每次读取一行
			while (br.ready()) {
				myreadline = br.readLine();// 读取一行
				//rtn += myreadline;//使用+=效率不高，特别是换行多的时候
				rtnb.append(myreadline);
				
				// bw.write(myreadline); //写入文件
				// bw.newLine();
				//System.out.println(myreadline);//在屏幕上输出
			}
			// bw.flush(); //刷新该流的缓冲
			// bw.close();
			br.close();
			// fw.close();
			br.close();
			fr.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return rtnb.toString();
	}
}
