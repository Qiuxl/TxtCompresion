package Compression;

import java.awt.Checkbox;
import java.awt.List;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberInputStream;
import java.rmi.server.UID;
import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.print.attribute.standard.RequestingUserName;
import javax.swing.text.MaskFormatter;

import org.omg.CORBA.FREE_MEM;
import org.omg.PortableInterceptor.IORInterceptor;
/**
 * 
 * @date: 2017-05-23
 * @author Qzh
 * */

public class LZWcompress {

	private HashMap<String, Integer> dictionary = new HashMap<String, Integer>();
	private ArrayList<Integer> encodeList = new ArrayList<Integer>();
	private ArrayList<Integer> decodeList = new ArrayList<Integer>();
	private int maxBits = 8; // 不会超过16
	private int DictSize = 256;
	private StringBuffer encodeResult = new StringBuffer();
	private StringBuffer decodeResult = new StringBuffer();
	private Gui uGui = null;
	public void SetGui(Gui str)
	{
		this.uGui = str;
	}
	public void initialize()
	{
		dictionary.clear();
		DictSize = 256;
	//	System.out.println(dictionary.size());
		encodeList.clear();
		for(int i = 0; i < 256; i++)
		{
			dictionary.put(""+(char)i, i);
		}
		if(encodeResult.length() > 0)
		{
			encodeResult.delete(0, encodeResult.length());
		}
	}
	public LZWcompress() {
		// TODO Auto-generated constructor stub
	}
	//根据编码list和每个code的长度输出比特串
	public void generateBitStream()
	{
		//用5个bit表示
		int mask = 16;
		//将code长度信息存入bit流最前端
		while(mask > 0)
		{
			if((mask & maxBits) > 0)
			{
				encodeResult.append("1");
			}
			else
			{
				encodeResult.append("0");
			}
			mask = (mask >> 1);
		}
		int temp;
		for(Integer eInteger : encodeList)
		{
			temp = eInteger.intValue();
			mask = (1 << (maxBits - 1));
			for(int i = 0; i < maxBits; i++)
			{
				
				if((mask & temp) > 0)
				{
					encodeResult.append("1");
				}
				else{
					encodeResult.append("0");
				}
				mask = (mask >> 1);
			}
		}
	}
	//将bit串写入
	public void Decode(String filePath)
	{
		if(filePath == null)
		{
			return;
		}
		HashMap<Integer, String> decodeMap = new HashMap<Integer,String>();
		decodeList.clear();
		if(decodeResult.length() > 1)
			decodeResult.delete(0, decodeResult.length()-1);
		for(int i = 0; i < 256; i++)
		{
			decodeMap.put(i, (char)i + "");
		}
		
		int bitNum = 0;
		char c;
		int size = 256;
		//先得到编码位数
		StringBuffer readedStr = HuffmanUtils.read(filePath);
		String source = readedStr.toString();
		int temp;
		int sum = 0;
		//从bit流中取出前5位，提取code长度信息
		for(int i = 0; i < 5; i++)
		{
			c = source.charAt(i);
			String s=String.valueOf(c);
			temp = Integer.valueOf(s);
			bitNum = (bitNum << 1) + temp;
			
		}
		temp = 0;
		int count = 0;
		String entry = null; 
		String current =  null;
		for(int i = 5; i < source.length(); i++)
		{
			c = source.charAt(i);
			String s=String.valueOf(c);
			temp = Integer.valueOf(s);
			sum = (sum << 1) + temp;
			count ++;
			// 每bitnum长度位为一个code，进行解析
			if(count == bitNum)
			{
				entry = decodeMap.get(sum);
				if(entry == null)
				{
					entry = current + current.charAt(0);
				}
				decodeResult.append(entry);
				if(current != null)
				{
					decodeMap.put(size++, current+entry.charAt(0));
				}
				current = entry;
				count = 0;
				sum = 0;
			}
		}
		if(uGui != null)
			uGui.showResult.setText(decodeResult.toString());
		
	}
	
	//debug使用，用于检测是不是译码表生成出bug
	public void Check()
	{
		HashMap<Integer, String> decodeMap = new HashMap<Integer,String>();
		Iterator<java.util.Map.Entry<String, Integer>> iter = dictionary.entrySet().iterator();

		while(iter.hasNext())
		{
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			Integer val = (Integer) entry.getValue();
			decodeMap.put(val, key.toString());
		}
		StringBuffer sb = new StringBuffer();
		
		for(Integer eInteger : decodeList)
		{
			sb.append(decodeMap.get(eInteger));
		}
	
	}
	public void SaveFile(String path)
	{
		String targetPath = path + ".lzw";
		HuffmanUtils.write(targetPath, encodeResult.toString());
	}
	
	public void compress(String path) {
		// TODO Auto-generated constructor stub
		StringBuffer temp = new StringBuffer();
		String input = ReadFile(path).toString();
		String current = "";
		for(char next : input.toCharArray())
		{
			String combine = current + next;
			if(dictionary.containsKey(combine))
			{
				current = combine;
			}
			else{
				encodeList.add(dictionary.get(current));
				dictionary.put(combine, DictSize++);
				current = ""+next;
			}
		}
		setBit();
		generateBitStream();
		showResult();
		if(uGui != null)
			uGui.showResult.setText(encodeResult.toString());
	}
	
	public void setBit()
	{
		// accoding to the dictionary number,set the max bit
		//顺便把bit信息放入到 hashmap中去
		int mask = 1 << 30;
		int num = 0;

		while((mask & DictSize) == 0)
		{
			num ++ ;
			mask  = (mask >> 1);
		}
		maxBits = 31 - num;
	}
	
	public void showResult()
	{
		System.out.println("dictionary size " + DictSize +  " " + encodeList.size());
		
	}
	
	public String ReadFile(String path)
	{
		if(path == null)
			return "";
		StringBuffer result = new StringBuffer();
		BufferedReader fReader = null;
		try {
			fReader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
			String message = null;
			while((message = fReader.readLine())!=null)
			{
				result.append(message);
				result.append(System.getProperty("line.separator"));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if(fReader != null)
				try {
					fReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return result.toString();
	}

}
