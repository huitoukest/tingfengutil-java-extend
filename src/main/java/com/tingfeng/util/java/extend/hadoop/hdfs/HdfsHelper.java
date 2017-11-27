package com.tingfeng.util.java.extend.hadoop.hdfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

public class HdfsHelper {
	private Configuration conf =null;
	private FileSystem hdfsFileSystem;
	private String defaultFS="";
	private String nameServices="";
	private String namenodes="";
	private String rpcAddr="";
	private String provider="";
	/**
	 * 
	 * @param defaultFS hdfs虚拟路径
	 * @param nameservices nameservice名称
	 * @param namenodes nameservice的activiti节点名称
	 * @param rpcAddress activiti的nameNode的地址和端口
	 * @throws IOException 
	 */
	public HdfsHelper(String defaultFS,String nameservices,String namenodes,String rpcAddress,String hadoopHome) throws IOException{
		  conf = new Configuration();
		  conf.set("fs.defaultFS", defaultFS);
		  conf.set("dfs.nameservices", nameservices);
		  conf.set("dfs.ha.namenodes."+nameservices, namenodes);
		  conf.set("dfs.namenode.rpc-address."+nameservices+"."+namenodes, rpcAddress);
		  conf.set("dfs.client.failover.proxy.provider."+nameservices, "org.apache.hadoop.hdfsFileSystem.server.namenode.ha.ConfiguredFailoverProxyProvider");
		  setHadoopHome(hadoopHome);
		  initFileSystem();
		  initConf();
	}
	
	public HdfsHelper(Configuration configuration,String hadoopHome) throws IOException{
		conf = configuration;
		setHadoopHome(hadoopHome);
		initFileSystem();
		initConf();
	}
	
	private void setHadoopHome(String hadoopHome){
		//System.setProperty("hadoop.home.dir", "D:/hadoop-2.6.4");
		System.setProperty("hadoop.home.dir", hadoopHome);
	}
	private void initFileSystem() throws IOException{
		hdfsFileSystem=FileSystem.get(conf);
	}
	private void  initConf(){
		defaultFS=conf.get("fs.defaultFS");
		nameServices=conf.get("dfs.nameservices");
		namenodes=conf.get("dfs.ha.namenodes."+nameServices);
		rpcAddr=conf.get("dfs.namenode.rpc-address."+nameServices+"."+namenodes);
		provider=conf.get("dfs.client.failover.proxy.provider."+nameServices);
		
	}
	/**
	 * 打印出root目录下文件/目录
	 */
	public void writeRootPaths(){
		FileSystem fs = hdfsFileSystem;
		  try {
		     FileStatus[] list = fs.listStatus(new Path("/"));
		     for (FileStatus file : list) {
		       System.out.println(file.getPath().getName());
		      }
		  } catch (IOException e) {
		     e.printStackTrace();
		  } finally{
		      try {
		        fs.close();
		      } catch (IOException e) {
		        e.printStackTrace();
		      }
		  }
	}
	public  FileSystem getFileSystem(){
		return hdfsFileSystem;
	}
	public void uploadLocalFileToHdfs(String localPath,String hdfsPath) throws IOException{
		//将本地文件上传到hdfs
		  String target=hdfsPath;
		  FileInputStream fis=new FileInputStream(new File(localPath));//读取本地文件
		  FileSystem fs = hdfsFileSystem;
		  OutputStream os=fs.create(new Path(target));
		  //copy
		  IOUtils.copyBytes(fis, os, 4096, true);
		 
	}
	public void uploadLocalFileToHdfs(String localPath,String hdfsPath,boolean deleteIfExist) throws IOException{
		 if(deleteIfExist&&isExist(hdfsPath))
		 {
			this.deleteFile(hdfsPath);
		 }
		 //将本地文件上传到hdfs
		  String target=hdfsPath;
		  FileInputStream fis=new FileInputStream(new File(localPath));//读取本地文件
		  FileSystem fs = hdfsFileSystem;
		  OutputStream os=fs.create(new Path(target));
		  //copy
		  IOUtils.copyBytes(fis, os, 4096, true);
		 
	}
	
	public boolean isFile(String hdfsPath) throws IOException{
		Path path=new Path(hdfsPath);
		return hdfsFileSystem.isFile(path);
	}
	
	public boolean isDirectory(String hdfsPath) throws IOException{
		Path path=new Path(hdfsPath);
		return hdfsFileSystem.isDirectory(path);
	}
	public boolean isExist(String hdfsPath) throws IOException{
		if(isDirectory(hdfsPath)) return true;
		if(isFile(hdfsPath)) return true;
		return false;
	}
	 //create a direction
	 public void createDir(String dir) throws IOException {	
	        Path path = new Path(dir);
	        hdfsFileSystem.mkdirs(path);
	        System.out.println("new dir \t" + conf.get("fs.default.name") + dir);
	
	    }     
	    //create a new file
	    public void createFile(String fileName, String fileContent) throws IOException {	
	        Path dst = new Path(fileName);
	        byte[] bytes = fileContent.getBytes();	
	        FSDataOutputStream output = hdfsFileSystem.create(dst);
	        output.write(bytes);
	        System.out.println("new file \t" + conf.get("fs.default.name") + fileName);
	    }

	    public void listFiles(String dirName) throws IOException {	
	        Path f = new Path(dirName);
	        FileStatus[] status = hdfsFileSystem.listStatus(f);	
	        System.out.println(dirName + " has all files:");	
	        for (int i = 0; i< status.length; i++) {	
	            System.out.println(status[i].getPath().toString());
	        }	
	    }

	    public void deleteFile(String fileName) throws IOException {	
	        Path f = new Path(fileName);
	        boolean isExists = hdfsFileSystem.exists(f);
	        if (isExists) { //if exists, delete	
	            boolean isDel = hdfsFileSystem.delete(f,true);	
	            System.out.println(fileName + "  delete? \t" + isDel);	
	        } else {
	            System.out.println(fileName + "  exist? \t" + isExists);	
	        }	
	    }
	
	public static void main(String[] args) throws IOException {	 
		HdfsHelper hdfsHelper=new HdfsHelper("hdfs://hadoop-test", "hadoop-test", "nn1", "192.168.3.180:9000","D:/hadoop-2.6.4");
		hdfsHelper.writeRootPaths();
		hdfsHelper.uploadLocalFileToHdfs("D:\\Desktop\\hadoop2.x学习笔记20160225\\day-3\\笔记和代码\\4第四天笔记\\a.txt", "hdfs://hadoop-test/tmp/a.txt");
	}

	public Configuration getConf() {
		return conf;
	}

	public FileSystem getHdfsFileSystem() {
		return hdfsFileSystem;
	}
	public String getDefaultFS() {
		return defaultFS;
	}
	public String getNameServices() {
		return nameServices;
	}
	public String getNamenodes() {
		return namenodes;
	}
	public String getRpcAddr() {
		return rpcAddr;
	}
	public String getProvider() {
		return provider;
	}	
}
