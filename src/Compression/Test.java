package Compression;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class Test {

	public static void main(String[] args) {
		File file=new File("E://java//test/bit1.txt");
		try {
			OutputStream output=new FileOutputStream(file);
			output.write(97);
			output.write(10);
			output.write(98);
			output.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void write(){
		try {
			String str="10000000";
			BufferedBitWriter writer =new BufferedBitWriter("E://java//test/bit.txt");
			for(int i=0;i<str.length();i++){
				char c=str.charAt(i);
				String s=String.valueOf(c);
				writer.writeBit(Integer.valueOf(s.toString()));
			}
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void read(){
		try {
			BufferedBitReader reader=new BufferedBitReader("E://java//test/bit.txt");
			int i;
			while((i=reader.readBit())!=-1){
				System.out.print(i);
			}
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
