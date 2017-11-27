package com.tingfeng.util.java.extend.common.utils.execl;

import java.io.IOException;
import java.util.List;

public interface ExeclWriterI {
	public void startWriteData(String sheetName) throws IOException;  
	public void writeData(List<?> mapList,int startRowNumber) throws IOException;  
	/**
	 * 正常运行后会自动掉头用deleteTempFile()删除临时文件。
	 * @throws IOException
	 */
	public void endWriteData() throws IOException;
	/*public void deleteTempFile() throws IOException;*/
	/**
	 * 插入一行数据
	 * @param rowNumber
	 * @param rowData
	 */
	public void insertDataToExeclRow(int rowNumber,List<?> rowData)  throws IOException;
	/**
	 * 插入一列数据
	 * @param rowNumber
	 * @param columnNumber
	 * @param rowData
	 */
	public void insertDataToExeclCell(int rowNumber,int columnNumber,Object rowData) throws IOException;
}
