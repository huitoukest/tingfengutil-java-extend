package com.tingfeng.util.java.extend.common.utils;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;

public class XMLUtils {

	// String filepath="resource/train.txt";//注意filepath的内容；
	// File file=new File(filepath);
	public static String SAVE_XMLFILE_PATH = "";

	public XMLUtils() {
		// xmlFile =
		// this.getClass().getResource("/").getPath()+SAVE_XMLFILE_PATH;
		// URL xmlpath =
		// this.getClass().getClassLoader().getResource("Provinces.xml");
		this.SAVE_XMLFILE_PATH = this.getClass().getResource("/").getPath() + "resource/";
	}

	/**
	 * 创建一个XML文档
	 * 
	 * @return doc 返回该文档
	 */
	public Document createXMLDocument() {
		Document doc = null;
		doc = DocumentHelper.createDocument();
		doc.addComment("edited with XMLSpy v2005 rel. 3 U (http://www.altova.com) by  ()");
		// doc.addDocType("class","//By Jack Chen","saveXML.xsd");
		Element root = doc.addElement("class");
		Element company = root.addElement("company");
		Element person = company.addElement("person");
		person.addAttribute("id", "11");
		person.addElement("name").setText("Jack Chen");
		person.addElement("sex").setText("男");
		person.addElement("date").setText("2001-04-01");
		person.addElement("email").setText("chen@163.com");
		person.addElement("QQ").setText("2366001");

		return doc;
	}

	/**
	 * 解析XML文档
	 * 
	 * @param xmlFile
	 * @return XML文档
	 * @throws DocumentException
	 * @throws FileNotFoundException
	 */
	public Document parse(String xmlFile) throws DocumentException, FileNotFoundException {
		SAXReader reader = new SAXReader();
		String filepath = this.SAVE_XMLFILE_PATH + xmlFile;
		Document doc = reader.read(new File(filepath));
		return doc;
	}

	@SuppressWarnings("unchecked")
	public List<?> parseToList(String fileName) throws FileNotFoundException, DocumentException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Document doc = this.parse(fileName);
		Element root = doc.getRootElement();
		List<Element> elements = root.elements();
		for (Iterator<Element> it = elements.iterator(); it.hasNext();) {
			Element element = it.next();
			List<Attribute> attributes = element.attributes();
			for (int i = 0; i < attributes.size(); i++) {
				Attribute attribute = attributes.get(i);
				if ("ID".equals(attribute.getName())) {

					Map map = new HashMap<String, Object>();
					map.put("ID", element.attributeValue("ID"));
					map.put("NAME", element.getText());
					list.add(map);
					// System.out.println(element.attributeValue("ID")+":"+element.getName()
					// + "  :  "
					// + element.getText());
				}
			}
		}
		return list;
	}

	public List<?> getSubList(String fileName, String pID) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			Document doc = this.parse(fileName);
			// SAXReader saxReader = new SAXReader(); //使用SAXReader方式读取XML文件
			// //加载数据库XML配置文件，得到Document对象
			// Document document = saxReader.read(new
			// File("src/DBConnect.xml"));
			Element root = doc.getRootElement(); // 获得根节点

			List<Element> elements = root.elements();
			for (Iterator<Element> it = elements.iterator(); it.hasNext();) {
				Element element = it.next();
				List<Attribute> attributes = element.attributes();
				for (int i = 0; i < attributes.size(); i++) {
					Attribute attribute = attributes.get(i);
					if ("ID".equals(attribute.getName())) {
						if (element.attributeValue("PID").equals(pID)) {
							Map map = new HashMap<String, Object>();
							map.put("ID", element.attributeValue("ID"));
							map.put("NAME", element.getText());
							list.add(map);
						}
					}
				}
			}

			// 得到database节点
			// Element citys = (Element)root.selectSingleNode("Cities");
			// list = citys.elements(); //得到database元素下的子元素集合
			//
			// for(Object obj:list){
			// Element element = (Element)obj;
			// //getName()是元素名,getText()是元素值
			// System.out.println(element.getName()+": "+element.getText());
			// }

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/***
	 * 将XML文档输出到控制台
	 * 
	 * @param doc
	 * @throws IOException
	 */
	public void printDocument(Document doc) throws IOException {
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(new OutputStreamWriter(System.out), format);
		writer.write(doc);
		writer.close();
	}

	/**
	 * 保存XML文档
	 * 
	 * @param doc
	 * @throws IOException
	 */
	public void saveDocument(Document doc) throws IOException {
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(new FileOutputStream(SAVE_XMLFILE_PATH), format);
		writer.write(doc);
		writer.close();
	}

	/**
	 * 验证XML文档和schemaURL
	 * 
	 * @param xmlFile
	 * @param schemaUrl
	 * @return XML文档
	 * @throws SAXException
	 * @throws DocumentException
	 */
	public Document validate(String xmlFile, String schemaUrl) throws SAXException, DocumentException {
		SAXReader reader = new SAXReader(true);
		System.out.println("validate by: " + schemaUrl);

		reader.setFeature("http://apache.org/xml/features/validation/schema", true);

		reader.setFeature("http://xml.org/sax/features/validation", true);

		reader.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", schemaUrl);

		reader.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
		Document doc = reader.read(new File(xmlFile));

		return doc;
	}

	/**
	 * 查找 xml
	 * 
	 * @param doc
	 * @throws IOException
	 */

	public void listDocument(Document doc) throws IOException {

		String xpath = "/class/company/person[@id=\"11\"]";
		Element list = (Element) doc.selectSingleNode(xpath);
		System.out.println(list.getName());

		// if (list.element("name").getName().equals("name")) {

		System.out.println("name:" + list.element("name").getText());
		System.out.println("sex:" + list.element("sex").getText());
		System.out.println("date:" + list.element("date").getText());
		System.out.println("email:" + list.element("email").getText());
		System.out.println("QQ:" + list.element("QQ").getText());
	}

	/***
	 * 利用XPATH查找元素，然后修改
	 * 
	 * @param doc
	 * @throws IOException
	 */
	public void updateDocByXPATH(Document doc) throws IOException {
		String xpath = "/class/company/person[@id=\"11\"]";
		Element list = (Element) doc.selectSingleNode(xpath);
		System.out.println(list.getName());

		// if (list.element("name").getName().equals("name")) {

		list.element("name").setText("1123");
		list.element("sex").setText("男");
		list.element("date").setText("1800-01-01");
		list.element("email").setText("163@163.com");
		list.element("QQ").setText("12345");
		// }
		saveDocument(doc);

	}

	/**
	 * 从根节点遍历，来修改XML文件,并保存。
	 * 
	 * @param doc
	 * @throws IOException
	 */
	public void updateDocument(Document doc) throws IOException {

		Element root = doc.getRootElement();
		// System.out.println(root.asXML());

		for (Iterator i = root.elementIterator(); i.hasNext();) {
			Element e = (Element) i.next();
			System.out.println(e.getName());
			System.out.println(e.getPath());
			if (e.element("person").element("name").getName().equals("name")) {
				e.element("person").element("name").setText("ChenJI");
				e.element("person").element("QQ").setText("123456");

			}
			System.out.println(e.getText().toString());
		}
		saveDocument(doc);
	}

	public static void main(String[] args) throws FileNotFoundException, DocumentException {
		XMLUtils cfg = new XMLUtils();
		cfg.parse(SAVE_XMLFILE_PATH);

	}

}
