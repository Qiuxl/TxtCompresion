package Compression;

import java.awt.List;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.omg.CORBA.FREE_MEM;

public class LZWcompress {

	private HashMap<String, Integer> dictionary = new HashMap<String, Integer>();
	private ArrayList<Integer> encodeList = new ArrayList<Integer>();
	private int maxBits = 8; // ²»»á³¬¹ý16
	private int DictSize = 256;
	public void initialize()
	{
		dictionary.clear();
		encodeList.clear();
		for(int i = 0; i < 256; i++)
		{
			dictionary.put(""+(char)i, i);
		}
	}
	public LZWcompress() {
		// TODO Auto-generated constructor stub
	}
	public void compress(String input) {
		// TODO Auto-generated constructor stub
		StringBuffer temp = new StringBuffer();
		String current = "";
	//	System.out.println(input);
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
		
	}
	public void showResult()
	{
		System.out.println("dictionary size " + DictSize +  " " + encodeList.size());
		for(Integer e:encodeList)
		{
			System.out.println(e);
		//	System.out.print(" ");
		}
		
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
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LZWcompress temp = new LZWcompress();
		temp.initialize();
		temp.compress(temp.ReadFile("./test.txt"));
		temp.showResult();
	}

}
