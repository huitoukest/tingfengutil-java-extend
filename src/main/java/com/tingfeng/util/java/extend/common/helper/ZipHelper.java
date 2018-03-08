package com.tingfeng.util.java.extend.common.helper;

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
/**
 * 和zipUtils不同的是,此Helper中可以设置进度监听;
 * @author huitoukest
 *
 */
public class ZipHelper {	
        private ZipUpdateCallBack zipUpdateCallBack;
        /**
         * 要压缩的文件的总的大小
         */
        private long filesSize=0L;
        /**
         * 当前已经压缩的文件的大小
         */
        private long nowZipSize=0L;
        /**
         * 上次更新的时候的百分比大小;
         */
        private double lastUpdateRaio=0d;
        /**
         * 每%updateSpace,调用一次更新的回调函数;
         */
        private double updateSpace=0.5;
	    
        public ZipUpdateCallBack getZipUpdateCallBack() {
			return zipUpdateCallBack;
		}

		public void setOnZipUpdateCallBack(ZipUpdateCallBack zipUpdateCallBack) {
			synchronized(this){
				this.zipUpdateCallBack = zipUpdateCallBack;
			}			
		}		
		/**
         * 每%updateSpace,调用一次更新的回调函数;
         */
		public double getUpdateSpace() {
			return updateSpace;
		}
		/**
         * 每%updateSpace,调用一次更新的回调函数;
         */
		public void setUpdateSpace(double updateSpace) {
			this.updateSpace = updateSpace;
		}
		/**
	     * 监听当前的zip压缩/解压的进度的接口;
	     * @author huitoukest
	     *
	     */
	    public interface ZipUpdateCallBack{
	    	public void updateRadio(double radio);
	    }
		/**
		 * 功能：把 sourceDir 目录下的所有文件或者自己进行 zip 格式的压缩，保存为指定 zip 文件
		 * 但是如果此文件夹中不存在文件的话,zip文件将会出错!
		 * @param sourceDir
		 * @param zipFile
		 */
		public synchronized boolean toZip(String sourceDir, String zipFile) {
			filesSize=getAllFilesSize(new File(sourceDir));
			zipUpdateCallBack.updateRadio(0);
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
				zipUpdateCallBack.updateRadio(100);
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
	    public boolean toZipByPathList(List<String> fileFullPathList,String outputFullFileName){
	    	
	       List<File> fileList=new ArrayList<File>();
	       for(String  path:fileFullPathList){
	    	   File file=new File(path);
	    	   fileList.add(file);   
	       }
	    	return toZipByFileList(fileList, outputFullFileName);
	    }
		
	    public boolean toZipByPathList(String[] fileFullPathList,String outputFullFileName){
		       List<File> fileList=new ArrayList<File>();
		       for(String  path:fileFullPathList){
		    	   File file=new File(path);
		    	   fileList.add(file);   
		       }
		    	return toZipByFileList(fileList, outputFullFileName);
		    }
	    public synchronized boolean toZipByFileList(List<File> fileList,String outputFullFileName){
	    	filesSize=getAllFilesSize(fileList);
	    	zipUpdateCallBack.updateRadio(0);
	    	try {
				ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputFullFileName));				
				return toZipByFileList(fileList, zos);			
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
	    private boolean toZipByFileList(List<File> fileList,ZipOutputStream zos){
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
	     * 返回指定文件/文件夹下面所有文件的总的大小
	     * @param file
	     * @return
	     */
	    public long getAllFilesSize(File file){
	    	if(!file.exists())
	    		return 0L;
	    	if(file.isFile()){
	    		return file.length();
	    	}else{
	    		long tempSize=0;
	    		File[] files=file.listFiles();
	    		if(files!=null){
	    			for(File f:files){
	    				tempSize+=getAllFilesSize(f); 
	    			}
	    		}
	    		return tempSize;
	    	}
	    }
	    
	    public long getAllFilesSize(List<File> files){
	    	long tempSize=0;
	    	for(File f:files){
	    		tempSize+=getAllFilesSize(f); 
	    	}
	    	return tempSize;
	    }
	    
	    public long getAllFilesSizeByPaths(List<String> paths){
	    	long tempSize=0;
	    	for(String  path:paths){
	    		tempSize+=getAllFilesSize(new File(path)); 
	    	}
	    	return tempSize;
	    }
	    
		/**
		 * 功能：执行文件压缩成zip文件
		 * @param source
		 * @param basePath  待压缩文件根目录
		 * @param zos
		 */

		private boolean zipFile(File source, String basePath,ZipOutputStream zos) {

			File[] files = new File[0];
			if (source.isDirectory()) {
				files = source.listFiles();
			} else {
				files = new File[1];
				files[0] = source;
			}
			String pathName;//存相对路径(相对于待压缩的根目录)
			byte[] buf = new byte[4096];
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
							setUpdateState(length);
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
       public void setUpdateState(int length){
    	   if(zipUpdateCallBack!=null){
				nowZipSize+=length;
				double nowPercent=1.0*nowZipSize/this.filesSize*100;
				if(lastUpdateRaio<100&&nowPercent-lastUpdateRaio>=this.updateSpace){
					zipUpdateCallBack.updateRadio(nowPercent);
					lastUpdateRaio=nowPercent;
				}				
			}
       }
		/**
		 * 功能：解压 zip 文件，只能解压 zip 文件
		 * @param zipfile
		 * @param destDir
		 */

		@SuppressWarnings("resource")
        public synchronized boolean unZip(String zipfile, String destDir) {
            filesSize=getAllFilesSize(new File(zipfile));
            zipUpdateCallBack.updateRadio(0);
			destDir = destDir.endsWith("\\") ? destDir : destDir + "\\";
			byte b[] = new byte[4096];
			int length;
			ZipFile zipFile;
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
						OutputStream outputStream = new FileOutputStream(loadFile);
						InputStream inputStream = zipFile.getInputStream(zipEntry);

						while ((length = inputStream.read(b))!=-1)

							{ outputStream.write(b, 0, length);
						      setUpdateState(length);
						    }
						outputStream.close();
						inputStream.close();
					   }
					
				}
   			 zipUpdateCallBack.updateRadio(100);
   			 return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
}
