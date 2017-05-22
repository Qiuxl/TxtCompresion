package Compression;

import java.io.IOException;


public class HuffmanUtils {

	/**
	 * 将需要写入的内容以Bit为单位，写到目标文件中。
	 * @param fileName
	 * @param source
	 */
	public static void write(String fileName,String source){
		BufferedBitWriter writer=null;
		try {
			writer=new BufferedBitWriter(fileName);
			for(int i=0;i<source.length();i++){
				char c=source.charAt(i);
				String s=String.valueOf(c);
				writer.writeBit(Integer.valueOf(s.toString()));
			}
			writer.close();
			writer = null;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(writer!=null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 以Bit为单位读取指定文件的内容到StringBuffer
	 * @param fileName
	 * @return
	 */
	public static StringBuffer read(String fileName){
		StringBuffer result=new StringBuffer();
		BufferedBitReader reader=null;
		try {
			reader=new BufferedBitReader(fileName);
			int i;
			while((i=reader.readBit())!=-1){
				result.append(i);  //这里append的是用32位表示的 0或者1
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

}
