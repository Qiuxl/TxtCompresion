package Compression;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * huffman解压
 * @author andy
 *
 */
public class HuffmanUnCompress {

	private static byte[] temp = new byte[1];
	private static StringBuffer decodeResult = new StringBuffer();
	private static Gui uiGui;
	public void setUI(Gui ptr)
	{
		uiGui = ptr;
	}
	public void Decode(String path) //这里需要注意的是给出的路径需要
	{
		if(path == "nullnull")
			return;
		if(decodeResult.length() > 0)
			decodeResult.delete(0, decodeResult.length()-1);
		
		String huffmanHeadFile=path + ".head";
		StringBuffer readedStr=readHead(huffmanHeadFile);
		
		Map<String,String> map=convertStringToMap(readedStr.toString());
		String content=HuffmanCompress.readContent(path);
		char[] c=content.toCharArray();
		StringBuffer unCompressBuffer=new StringBuffer();
		StringBuffer s=new StringBuffer();
		for(int i=0;i<c.length;i++){
			String key=String.valueOf(c[i]);
			Object value=null;
			if(s.length()>0){
				value=map.get(s.toString());
			}else{
				value=map.get(key);
			}
			if(value!=null){
				s.delete(0, s.length());
				if(value.toString().equals(" ")){
					unCompressBuffer.append(" ");
					decodeResult.append(new String(temp));	

				}else if(value.toString().equals("10")){
					unCompressBuffer.append(System.getProperty("line.separator"));
					decodeResult.append(new String(System.getProperty("line.separator").getBytes()));
				}else{
					unCompressBuffer.append(value.toString());
					temp[0] = (byte) Integer.parseInt(value.toString());
					decodeResult.append(new String(temp));	
				}
			}else{
				s.append(key);
				value=map.get(s.toString());
				if(value!=null){
					s.delete(0, s.length());
					if(value.toString().equals("")){
						unCompressBuffer.append(" ");
						temp[0] = 32;
						decodeResult.append(new String(temp));		
					}else if(value.toString().equals("10")){
						unCompressBuffer.append(System.getProperty("line.separator"));
						decodeResult.append(new String(System.getProperty("line.separator").getBytes()));
					}else{
						unCompressBuffer.append(value.toString());
						temp[0] = (byte) Integer.parseInt(value.toString());
						decodeResult.append(new String(temp));
					}
				}
			}
		}
		uiGui.showResult.setText(decodeResult.toString());
		 
	}
	public static void main(String[] args) {
		long beginTime=System.currentTimeMillis();
		System.out.println("beginTime:"+beginTime);
		String huffmanHeadFile="C:\\Users\\Qzh\\Documents\\Java\\TxtCompression\\test.txt.huffman.head";
		String huffmanContentFile="C:\\Users\\Qzh\\Documents\\Java\\TxtCompression\\test.txt.huffman";
		String targertFile="C:\\Users\\Qzh\\Documents\\Java\\TxtCompression\\test_uncompress1.txt";
		try {
			FileOutputStream output=new FileOutputStream(new File(targertFile));
			StringBuffer readedStr=readHead(huffmanHeadFile);
			Map<String,String> map=convertStringToMap(readedStr.toString());
			String content=HuffmanCompress.readContent(huffmanContentFile);
			System.out.println("after read content:"+System.currentTimeMillis());
			char[] c=content.toCharArray();
			StringBuffer unCompressBuffer=new StringBuffer();
			StringBuffer s=new StringBuffer();
			for(int i=0;i<c.length;i++){
				String key=String.valueOf(c[i]);
				Object value=null;
				if(s.length()>0){
					value=map.get(s.toString());
				}else{
					value=map.get(key);
				}
				if(value!=null){
					s.delete(0, s.length());
					if(value.toString().equals(" ")){
						unCompressBuffer.append(" ");
						output.write(32);
						decodeResult.append(new String(temp));	

					}else if(value.toString().equals("10")){
						unCompressBuffer.append(System.getProperty("line.separator"));
						output.write(System.getProperty("line.separator").getBytes());
						decodeResult.append(new String(System.getProperty("line.separator").getBytes()));
					}else{
						unCompressBuffer.append(value.toString());
						temp[0] = (byte) Integer.parseInt(value.toString());
						decodeResult.append(new String(temp));	
						output.write(Integer.parseInt(value.toString()));
					}
				}else{
					s.append(key);
					value=map.get(s.toString());
					if(value!=null){
						s.delete(0, s.length());
						if(value.toString().equals("")){
							unCompressBuffer.append(" ");
							output.write(32);
							temp[0] = 32;
							decodeResult.append(new String(temp));		
						}else if(value.toString().equals("10")){
							unCompressBuffer.append(System.getProperty("line.separator"));
							output.write(System.getProperty("line.separator").getBytes());
							decodeResult.append(new String(System.getProperty("line.separator").getBytes()));
						}else{
							unCompressBuffer.append(value.toString());
							output.write(Integer.parseInt(value.toString()));
							temp[0] = (byte) Integer.parseInt(value.toString());
							decodeResult.append(new String(temp));
							output.write(Integer.parseInt(value.toString()));
						}
					}
				}
				
			}
			//}
			System.out.println("after for:"+System.currentTimeMillis());
			System.out.println("=======================================");
			System.out.println("the content after uncompress is as below:");
			if(unCompressBuffer.toString().getBytes().length>10000){
				System.out.println("the file size is more than 10000B");
			}else{
				System.out.println(decodeResult.toString());
			}
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			BufferedOutputStream abcd = new BufferedOutputStream(new FileOutputStream(new File("./1111.txt")));
			abcd.write(decodeResult.toString().getBytes());
			abcd.flush();
			abcd.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("===================================end===");
		long endTime=System.currentTimeMillis();
		System.out.println("解压到"+targertFile+" 耗时："+(endTime-beginTime)+"ms");

	}

	static String getKeyByIndex(char[] c,int index){
		StringBuffer str=new StringBuffer();
		for(int i=0;i<=index;i++){
			str.append(String.valueOf(c[i]));
		}
		return str.toString();
	}


	static StringBuffer readHead(String fileName){
		StringBuffer result=new StringBuffer();
		try {
			File f=new File(fileName);
			InputStream output=new FileInputStream(f);
			byte[] bytes = new byte[(int)f.length()];
			while(output.read(bytes)!=-1){
				result.append(new String(bytes));
			}
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 转换Huffman编码表到HashMap
	 * @param readedStr
	 * @return
	 */
	static Map<String,String> convertStringToMap(String readedStr){
		readedStr = readedStr.substring(1, readedStr.length()-1);   //去掉开头和结尾的括号        
		String[] keyValuePairs = readedStr.split(",");
		Map<String,String> map = new HashMap<>();               

		for(String pair : keyValuePairs){
			if(pair!=null && pair.length()>0){
				String[] entry = pair.split("="); 
				if(entry.length==1){
					map.put(entry[0].trim(), " ");
				}else{
					map.put(entry[0].trim(), entry[1].trim());
				}
			}
		}
		return map;
	}

}