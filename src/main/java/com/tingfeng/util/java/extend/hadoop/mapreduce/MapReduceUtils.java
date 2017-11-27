package com.tingfeng.util.java.extend.hadoop.mapreduce;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
/**
 * 
 * @author huitoukest
 * 主要作用是将本地Mapreduce以及所依赖的jar包上传到hdfs中;
 * 然后运行分布式任务;
 */
public class MapReduceUtils {
		private String jarDir_3rdPart = "lib";
		private String classPath = "bin";
		/**
		 * classPath的绝对路径
		 */
		private String classPathAbsolutePath="";
		private String[] args=null;
		
		/**
		 * 当前包含与依赖的所有har包的名称(Key)和路径(value),防止jar包重复
		 */
		private Map<String,String> jarsMap=new HashMap<String,String>();
		/**
		 *tmpdir的实际位置
		 *On Windows: java.io.tmpdir:[C:\DOCUME~1\joshua\LOCALS~1\Temp\]
		 *On Solaris: java.io.tmpdir:[/var/tmp/]
		 *On Linux: java.io.tmpdir: [/tmp]
		 *On Mac OS X: java.io.tmpdir: [/tmp]
		 */
		private  final String TEMP_DIR = System.getProperty("java.io.tmpdir");
		private  Log log = LogFactory.getLog(MapReduceUtils.class);
		private Tool tool=null;
		private File tmpJarFile=null;
		/**
		 * @param hadoopHome本地的hadoop目录
		 * @param tool 实现tool的工具类
		 * @param args main函数的args
		 * @param classes classPath的路径 注意:如果此文件夹不需要被包含到classpath
		 * @param jardir_3rdpart 第三方jar包路径,多个包以逗号分隔
		 * @return
		 * @throws Exception
		 */
		private MapReduceUtils(Tool tool, String[] args, String classes, String jardir_3rdpart) throws Exception{
			this.jarDir_3rdPart = jardir_3rdpart;
			this.classPath = classes;
			this.args= args;
			this.classPathAbsolutePath=new File(this.classPath).getAbsolutePath();	
			this.tool=tool;
		}
		private int run() throws Exception {
			try{
				//第一步,将classPath中的的jar文件加入到list中;
				addClassPathJarsToMap(this.classPath);
				//第二步,将第三方指定jar包加入到list中;
				add3rdPartJarsToMap(this.jarDir_3rdPart);
				//第三步,将用户命令行参数中指定的jar包加入到list中;
				configLibJars(this.args);
			}catch(Exception e){
				printGenericCommandUsage(System.out);
				throw e;
			}
			//第四部,创建零时jar文件包
		 try{
				String tmpJarPath=createTempJar();
				tmpJarFile=new File(tmpJarPath);
				Configuration conf= tool.getConf();
				if (conf == null) {
					conf = new Configuration(true);
				}
				GenericOptionsParser parser = new GenericOptionsParser(conf,args);
				addTmpJar(tmpJarPath,conf);
				tool.setConf(conf);
				String[] toolArgs = parser.getRemainingArgs();			
				return tool.run(toolArgs);
			}finally{			
				//在此次虚拟机运行任务完毕的时候,执行addShutdownHook中的任务;
				Runtime.getRuntime().addShutdownHook(new Thread() {
					@Override
					public void run() {
						if(tmpJarFile!=null&&tmpJarFile.exists())
						 tmpJarFile.delete();
					}
				});
			}
		}

		public static int run (Tool tool, String[] args, String classPath, String jardir_3rdpart) throws Exception{
			MapReduceUtils mapReduceUtils=new MapReduceUtils(tool, args, classPath, jardir_3rdpart);
		    return mapReduceUtils.run();	
		}
		
		public static int run (Tool tool, String[] args, String classPath) throws Exception{
			MapReduceUtils mapReduceUtils=new MapReduceUtils(tool, args, classPath,null);
		    return mapReduceUtils.run();	
		}
		/**
		 * 默认classPath是bin目录;
		 * @param tool
		 * @param args
		 * @return
		 * @throws Exception
		 */
		public static int run (Tool tool, String[] args) throws Exception{
			MapReduceUtils mapReduceUtils=new MapReduceUtils(tool, args,"bin",null);
		    return mapReduceUtils.run();	
		}
		
		/**
		 * 和本地classpath不同的是,此参数是由逗号分隔的jar或者文件夹的集合
		 * @param jarDir_3rdPart2
		 */
		private void add3rdPartJarsToMap(String jarDir_3rdPartPath) {
			if(jarDir_3rdPartPath==null) return;
			String[] paths=jarDir_3rdPartPath.split(",");
			if(jarDir_3rdPartPath==null||jarDir_3rdPartPath.trim().length()<1)
				return;
			for(String p:paths){
				File file=new File(p);
				if(!file.exists()) continue;
				addJarsToMap(p, jarsMap);
			}
			
		}
		/**
		 * 将classPath中的的jar文件加入到list中
		 * @param classPath
		 */
		private void addClassPathJarsToMap(String classPath){
			addJarsToMap(classPath, jarsMap);
		}
		/**
		 * 将指定path对应的jar/class文件或者文件夹中的jar/class文件添加到map中,
		 * @param path
		 */
		private void addJarsToMap(String path,Map<String,String> map){
			if(path==null||path.trim().length()<1) 
				return;
			File file=new File(path);
			if(!file.exists()) 
				return;
			if(file.isDirectory()){
				File[] fs=file.listFiles();
				for(File f:fs){
					addJarsToMap(f.getPath(), map);
				}
			}else{
			     	if(path.endsWith(".jar")){
			     		map.put(getFileName(path),path);
			     	}else if(path.endsWith(".class")){			     		
			     		map.put(path, this.classPathAbsolutePath);
			     	}
			}
		}
		
		private String getFileName(String path){
			String fileSeparator=System.getProperty("file.separator");
			String tmp=path;
			String name="";
			if(fileSeparator.equals("\\"))
			{
				String[] pp=path.split("\\\\");
				if(pp.length>1)
				{
					name=pp[pp.length-1];
				}else{
					name=pp[0];
				}
				
			}else {
				name=path.substring(tmp.lastIndexOf("/")+1);
			}
			return name;
		}
		
		private  void addTmpJar(String jarPath, Configuration conf) throws IOException {  
		    System.setProperty("path.separator", ":");  
		    FileSystem fs = FileSystem.getLocal(conf);  
		    String newJarPath = new Path(jarPath).makeQualified(fs).toString();  
		    String tmpjars = conf.get("tmpjars");  
		    if (tmpjars == null || tmpjars.length() == 0) {  
		        conf.set("tmpjars", newJarPath);  
		    } else {  
		        conf.set("tmpjars", tmpjars + "," + newJarPath);  
		    }  
		}  
		/**
		 * 将命令行中指定的libjar加入到classpath中,指定的jar集合以逗号分割;
		 * @param args
		 * @return
		 * @throws ParseException
		 */
		private  void configLibJars(String[] args) throws ParseException {
			String[] fileArr = null;
			CommandLine commandLine = getCommandLine(args);
			if (commandLine.hasOption("libjars")) {
				String files = commandLine.getOptionValue("libjars");
				log.info("find libjars :" + files);
				fileArr = files.split(",");
			}
			for (int i = 0; fileArr != null && i < fileArr.length; i++) {
				addJarsToMap(fileArr[i], jarsMap);
			}
		}
		
		/**
		 * 将命令行参数转换为相关的类
		 * @param args
		 * @return
		 * @throws ParseException
		 */
		private  CommandLine getCommandLine(String[] args) throws ParseException {
			CommandLineParser parser = new GnuParser();
			@SuppressWarnings("static-access")
			Option libjars = OptionBuilder.withArgName("paths").hasArg().withDescription("comma separated jar files to include in the classpath.")
					.create("libjars");
			Options opts = new Options();
			opts.addOption(libjars);
			CommandLine commandLine = parser.parse(opts, args, true);
			return commandLine;
		}
		/**
		 * 创建一个零时的jar文件;
		 * 此jar文件中包括指定classpath中的jar文件
		 * 命令行/依赖等指定的jar文件以及自定的源代码编译而成的class文件
		 * @return 返回临时jar文件路径
		 * @throws IOException
		 */
		private  String createTempJar() throws IOException {
			Manifest manifest = new Manifest();
			manifest.getMainAttributes().putValue("Manifest-Version", "1.0");
			final File jarFile = File.createTempFile("MagicRunnerJob", ".jar", new File(TEMP_DIR));	      
			//向一个jar文件中写入数据
			JarOutputStream out = new JarOutputStream(new FileOutputStream(jarFile), manifest);
			Set<String> jarNames=jarsMap.keySet();
			for(String name:jarNames){
				if(!name.endsWith(".jar"))
				{//非jar文件的写入需要创建相关的文件夹
					String value=jarsMap.get(name);
					String string=new File(name).getAbsolutePath();
					String path=string.substring(string.indexOf(value)+value.length()+1);
					writeToTempJar(out,new File(name),path);
				}else{
					String path=jarsMap.get(name);
					writeToTempJar(out, new File(path),name);
				}
			}
			out.flush();
			out.close();
			String toReturn = jarFile.toURI().toString();
			return processJarPath(toReturn);
		}
		/**
		 * 得到jar文件的路径,去掉file等前缀
		 * @param toReturn
		 * @return
		 * @throws UnsupportedEncodingException
		 */
		private static String processJarPath(String toReturn) throws UnsupportedEncodingException {
			if (toReturn.startsWith("file:\\")) {
				toReturn = toReturn.substring("file:\\".length());
			}
			if (toReturn.startsWith("file:")) {
				toReturn = toReturn.substring("file:".length());
			}
			toReturn = toReturn.replaceAll("\\+", "%2B");
			toReturn = URLDecoder.decode(toReturn, "UTF-8");
			return toReturn.replaceAll("!.*$", "");
		}
		/**
		 * 将rootDir文件或者文件夹中的所有内容压缩到out中
		 */
		private  void writeToTempJar(JarOutputStream out, File file, String relativepath) throws IOException {
			if (file.isDirectory()) {
				File[] fl = file.listFiles();
				if (relativepath.length() > 0) {
					relativepath = relativepath + "/";
				}
				for (int i = 0; i < fl.length; i++) {
					writeToTempJar(out, fl[i], relativepath + fl[i].getName());
				}
			} else {
				out.putNextEntry(new JarEntry(relativepath));
				FileInputStream in = new FileInputStream(file);
				byte[] buffer = new byte[2048];
				int n = in.read(buffer);
				while (n != -1) {
					out.write(buffer, 0, n);
					n = in.read(buffer);
				}
				in.close();
			}
		}

		private void printGenericCommandUsage(PrintStream out) {
			out.println("Generic options supported are");
			out.println("-libjars <comma separated list of jars>    "
					+ "This item must at first!!!!\nspecify comma separated jar files to include in the classpath.");
			out.println("-conf <configuration file>     specify an application configuration file");
			out.println("-D <property=value>            use value for given property");
			out.println("-fs <local|namenode:port>      specify a namenode");
			out.println("-jt <local|jobtracker:port>    specify a job tracker");
			out.println("-files <comma separated list of files>    " + "specify comma separated files to be copied to the map reduce cluster");
			out.println("-archives <comma separated list of archives>    " + "specify comma separated archives to be unarchived"
					+ " on the compute machines.\n");
			out.println("The general command line syntax is");
			out.println("bin/hadoop command [genericOptions] [commandOptions]\n");
		}
}
