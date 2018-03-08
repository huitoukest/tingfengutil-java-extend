package com.tingfeng.util.java.extend.common.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

public class ZipUtils {
		/**
		 * 功能：把 sourceDir 目录下的所有文件或者自己进行 zip 格式的压缩，保存为指定 zip 文件
		 * 但是如果此文件夹中不存在文件的话,zip文件将会出错!
		 * @param sourceDir
		 * @param zipFile
		 */
		public static boolean getZip(String sourceDir, String zipFile) {
			OutputStream os;
			BufferedOutputStream bos;
			ZipOutputStream zos;
			try {
				os = new FileOutputStream(zipFile);
				bos = new BufferedOutputStream(os);
				zos = new ZipOutputStream(bos);
				File file = new File(sourceDir);
				String basePath = null;
				if (file.isDirectory()) {
					basePath = file.getPath();
				} else {//直接压缩单个文件时，取父目录
					basePath = file.getParent();
				}
				boolean ok=zipFile(file, basePath, zos);
				zos.closeEntry();
				zos.close();
				return ok;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

		}

		/**
	     * 生成ZIP文件，把一系列的文件生成到一个ZIP里，使用无目录的形式。
	     * @param fileFullPathList 要压缩的文件列表
	     * @param outputFullFileName 压缩成的文件的输出路径  
	     * @author Beiling Yu 2008年10月27日 14时59分18秒
	     * @return
	     */
	    public static boolean getZipByPathList(List<String> fileFullPathList,String outputFullFileName){
	       List<File> fileList=new ArrayList<File>();
	       for(String  path:fileFullPathList){
	    	   File file=new File(path);
	    	   fileList.add(file);   
	       }
	    	return getZipByFileList(fileList, outputFullFileName);
	    }
		
	    public static boolean getZipByPathList(String[] fileFullPathList,String outputFullFileName){
		       List<File> fileList=new ArrayList<File>();
		       for(String  path:fileFullPathList){
		    	   File file=new File(path);
		    	   fileList.add(file);   
		       }
		    	return getZipByFileList(fileList, outputFullFileName);
		    }
	    public static boolean getZipByFileList(List<File> fileList,String outputFullFileName){
	    	try {
				ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputFullFileName));
				return getZipByFileList(fileList, zos);			
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			}
	    }
	    /**
	     * 生成ZIP文件，把一系列的文件生成到一个ZIP里，使用无目录的形式。
	     * @param fileList 要压缩的文件列表
	     * @param outputFullFileName 压缩成的文件的输出路径  
	     * @author Beiling Yu 2008年10月27日 14时59分18秒
	     * @return
	     */
	    private static boolean getZipByFileList(List<File> fileList,ZipOutputStream zos){
	        try {        
	            for (Iterator<File> i = fileList.iterator(); i.hasNext();) {
	                File file =i.next();
	                String basePath ="";	  
					if (file.isDirectory()) {
						basePath = file.getPath();
					} else {//直接压缩单个文件时，取父目录
						basePath = file.getParent();
					}
					boolean ok=zipFile(file, basePath, zos);
					if(!ok){
						return false;
					}
                }
	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	        return true;
	    }
	    
		/**
		 * 功能：执行文件压缩成zip文件
		 * @param source
		 * @param basePath  待压缩文件根目录
		 * @param zos
		 */

		private static boolean zipFile(File source, String basePath,ZipOutputStream zos) {

			File[] files = new File[0];
			if (source.isDirectory()) {
				files = source.listFiles();
			} else {
				files = new File[1];
				files[0] = source;
			}
			String pathName;//存相对路径(相对于待压缩的根目录)
			byte[] buf = new byte[1024];
			int length = 0;
			try {
				for (File file : files) {
					if (file.isDirectory()) {
						pathName = file.getPath().substring(basePath.length() + 1)+ "/";
						zos.putNextEntry(new ZipEntry(pathName));						
						zipFile(file, basePath, zos);
					} else {
						pathName = file.getPath().substring(basePath.length() + 1);
						InputStream is = new FileInputStream(file);
						BufferedInputStream bis = new BufferedInputStream(is);
						zos.putNextEntry(new ZipEntry(pathName));
						while ((length = bis.read(buf)) > 0) {
							zos.write(buf, 0, length);
						}
						is.close();
					}
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		/**
		 * 功能：解压 zip 文件，只能解压 zip 文件
		 * @param zipfile
		 * @param destDir
		 * @throws IOException 
		 */

		public static void unZip(String zipfile, String destDir) throws IOException {

			destDir = destDir.endsWith("\\") ? destDir : destDir + "\\";
			byte b[] = new byte[4096];
			int length;
			ZipFile zipFile = null;
			OutputStream outputStream = null;
			InputStream inputStream = null;
			try {

				zipFile = new ZipFile(new File(zipfile),"UTF-8");
				Enumeration<ZipEntry> enumeration = zipFile.getEntries();
				ZipEntry zipEntry = null;
   			    while (enumeration.hasMoreElements()) {
					zipEntry = (ZipEntry) enumeration.nextElement();
					File loadFile = new File(destDir + zipEntry.getName());
					if (zipEntry.isDirectory()) {
						loadFile.mkdirs();
					} else {
						if (!loadFile.getParentFile().exists()){

							loadFile.getParentFile().mkdirs();						
						}
						outputStream = new FileOutputStream(loadFile);

						inputStream = zipFile.getInputStream(zipEntry);

						while ((length = inputStream.read(b)) > 0)

							outputStream.write(b, 0, length);
					}
				}
			}finally {
			    try {
    			    if(null != outputStream) {
                            outputStream.close();
    			    }
    			    if(null != inputStream) {
                        inputStream.close();
                    }
    			    if(null != zipFile) {
    			        zipFile.close();
    			    }
			    } catch (IOException e) {
                    e.printStackTrace();
                }
			}

		}
}
