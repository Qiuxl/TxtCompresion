package Compression;

import java.io.IOException;


public class HuffmanUtils {

	/**
	 * ����Ҫд���������BitΪ��λ��д��Ŀ���ļ��С�
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
	 * ��BitΪ��λ��ȡָ���ļ������ݵ�StringBuffer
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
				result.append(i);  //����append������32λ��ʾ�� 0����1
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
