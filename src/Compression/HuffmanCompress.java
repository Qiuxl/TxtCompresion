package Compression;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 哈夫曼压缩实现类
 * 参考资料：
 * http://bill56.iteye.com/blog/2272332
 * http://www.cs.dartmouth.edu/~traviswp/cs10/lab/lab4/lab4.html
 * http://freesourcecode.net/javaprojects/79503/Huffman-coding-in-java#.V1Aajfl97IV
 * @author andy
 *
 */
public class HuffmanCompress {
	
	static Map<String,String> encodeMap=new HashMap<String,String>();
	static Map<String,String> decodeMap=new HashMap<String,String>();
	private List<HuffmanTree> allHuffmanNodes = null;
	private HuffmanTree tree = null;
	private List<String> encodeList = null;
	private StringBuffer encodeResult = new StringBuffer();
	private Gui ui;
	public void setUI(Gui uiTemp)
	{
		this.ui = uiTemp;
	}
	public static void main(String[] args) {
		

	}
	public void saveFile(String targetPath)
	{
		try {
			writeHead(targetPath+".Huffman", decodeMap);
			
			writeContent(targetPath+".Huffman", encodeList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public void Intialize() {
		encodeMap.clear();
		decodeMap.clear();
		if(encodeResult.length() > 0 )
		{
			encodeResult.delete(0, encodeResult.length()-1);
		}
		
	}
	public void Compress(String sourcePath){
		
		if(ui == null)
		{
			System.err.println("haven't initialize Gui!");
			return;
		}
		ui.areaShow.setText("");
		
		long beginTime=System.currentTimeMillis();
		HashMap<Byte,Integer> mapResult=countASSCIBySourceFile(new File(sourcePath));
		
		//初始化huffman节点
		allHuffmanNodes=initializeHuffmanNode(mapResult);
		//构造huffman树
		tree=constructHuffmanTree(allHuffmanNodes);
		encodingByHuffman(tree, new StringBuffer());

		//初始化解码表
		initDecodeMap(encodeMap);

		encodeList=generateHuffmanCodeByInput(fileContentList);

		//将Huffma编码对象写入文件
		ui.areaShow.setText(encodeResult.toString());
		ui.showResult.setText(encodeResult.toString());
		System.out.println("编码文件写入成功，文件路径："+new File(sourcePath+".Huffman").getAbsolutePath());
		System.out.println("解码表：");
		System.out.println(decodeMap);
	//	readContent(sourcePath+".Huffman");
		long endTime=System.currentTimeMillis();
		System.out.println("压缩耗时："+(endTime-beginTime)+"ms");
	}
	/**
	 * 将Huffman编码Map写入头文件
	 * @param fileName
	 * @param decodeMap
	 */
	static void writeHead(String fileName,Map<String,String> decodeMap){
		try {
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(new File("aa.txt")));
			objectOutputStream.writeObject(decodeMap);
			OutputStream output=new FileOutputStream(new File(fileName+".head"));
			output.write(decodeMap.toString().getBytes());
			//output.write("HUFF".getBytes());
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


	/**
	 * 写入压缩内容到文件
	 * @param string
	 * @param encodeList
	 */
	private static void writeContent(String compressFile, List<String> encodeList) {
		StringBuffer sb=new StringBuffer();
		for(String str:encodeList){
			sb.append(str);
		}
		HuffmanUtils.write(compressFile, sb.toString());
		System.out.println("=============================");
		System.out.println("压缩后文件大小："+new File(compressFile).length()+"字节");
		System.out.println("=============================");
		System.out.println("写入压缩内容成功！");
		System.out.println("=============================");
		System.out.println("压缩后文件大小："+new File(compressFile).length()+"字节");
		System.out.println("=============================");
		System.out.println("写入压缩内容成功！");
	}

	public static String readContent(String commpressFile){
		return HuffmanUtils.read(commpressFile).toString();
	}



	/**
	 * 生成哈夫曼编码
	 * @param dictionary
	 * @return
	 */
	public List<String> generateHuffmanCodeByInput(List<Byte> dictionary){
		List<String> result=new ArrayList<String>();
		for(Byte str:dictionary){
			String content=encodeMap.get(String.valueOf(str));
			if(content!=null){
				result.add(content);
				encodeResult.append(content);
			}
		}

		return result;
	}
	static List<Byte> fileContentList=new ArrayList<Byte>();

	static void initDecodeMap(Map<String,String> encodeMap){
		Iterator<Entry<String, String>> iter = encodeMap.entrySet().iterator();
		while (iter.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			decodeMap.put(val.toString(), key.toString());
		}
	}

	/**
	 * Huffman 编码哈希表
	 * key=ASSCI
	 * value=Huffman编码
	 */


	/**
	 * 递归对Huffman树中的叶子节点编码
	 * @param root
	 * @param prefix
	 */
	static void encodingByHuffman(HuffmanTree root,StringBuffer prefix){
		if(root==null){
			return;
		}else{
			//叶子节点
			if(root.left==null && root.right==null){
				//System.out.println(root.value + "\t" + root.weight + "\t" + prefix);
				encodeMap.put(root.value.toString(),prefix.toString());
			}

			//递归遍历左子树
			prefix.append('0');
			encodingByHuffman(root.left, prefix);
			prefix.deleteCharAt(prefix.length()-1);
			//退出的时候删除之前添加的0
			
			//遍历右子树
			prefix.append('1');
			encodingByHuffman(root.right, prefix);
			prefix.deleteCharAt(prefix.length()-1);
		}
	}
	/**
	 * 递归构造Huffman树
	 * @param list
	 * @return
	 */
	static HuffmanTree constructHuffmanTree(List<HuffmanTree> list){
		if(list==null){
			return null;
		}
		if(list.size()==1){
			return list.get(0);
		}
		if(list.size()>=2){
			HuffmanTree leftSymbol=list.get(0);
			HuffmanTree rightSymbol=list.get(1);
			Integer rootValue=leftSymbol.weight+rightSymbol.weight;
			HuffmanTree root=new HuffmanTree(leftSymbol, rightSymbol, rootValue, rootValue);
			list.remove(leftSymbol);
			list.remove(rightSymbol);
			list.add(root);
			return constructHuffmanTree(sortHuffmanTree(list));

		}
		return null;
	}

	
	/**
	 * 根据字节出现的频率初始化Huffman树节点
	 * @param map
	 * @return
	 */
	static List<HuffmanTree> initializeHuffmanNode(Map<Byte,Integer> map){
		map=sortByValue(map);
		List<HuffmanTree> list=new ArrayList<HuffmanTree>();
		Iterator<Entry<Byte, Integer>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			list.add(new HuffmanTree(null,null,key,new Integer(val.toString())));
		}
		return list;
	}

	/** 
	 * 统计字节对应的ASCII码在源文件中出现的次数:
	 * 例如a的ascii码是97,b是98
	 * @param sourceFile:源文件
	 * @return HashMap {97:1,98:2}
	 */  
	public static HashMap<Byte, Integer> countASSCIBySourceFile(File sourceFile) {  
		// 判断文件是否存在  
		if (!sourceFile.exists()) {  
			// 不存在，直接返回null
			return null;  
		}  
		// 执行到这表示文件存在  
		HashMap<Byte, Integer> byteCountMap = new HashMap<>();  
		FileInputStream fis = null;  
		try {  
			// 创建文件输入流  
			fis = new FileInputStream(sourceFile);
			System.out.println("=============================");
			System.out.println("原始文件大小："+sourceFile.length()+"字节");
			System.out.println("=============================");
			// 保存每次读取的字节  
			byte[] buf = new byte[1024];  
			int size = 0;  
			// 每次读取1024个字节  
			while ((size = fis.read(buf)) != -1) {  
				// 循环每次读到的真正字节  
				for (int i = 0; i < size; i++) {  
					// 获取缓冲区的字节  
					byte b = buf[i];
					//忽略回车
					if(b!=13){
						fileContentList.add(b);
						// 如果map中包含了这个字节，则取出对应的值，自增一次  
						if (byteCountMap.containsKey(b)) {  
							// 获得原值  
							int old = byteCountMap.get(b);  
							// 先自增后入  
							byteCountMap.put(b, ++old);  
						} else {  
							// map中不包含这个字节，则直接放入，且出现次数为1  
							byteCountMap.put(b, 1);  
						}  
					}
				}  
			}  
		} catch (FileNotFoundException e) {  
			e.printStackTrace();  
		} catch (IOException e) {  
			e.printStackTrace();  
		} finally {  
			if (fis != null) {  
				try {  
					fis.close();  
				} catch (IOException e) {  
					fis = null;  
				}  
			}  
		}  
		return byteCountMap;
	}  

	public static List<HuffmanTree> sortHuffmanTree(List<HuffmanTree> treeList){
		Collections.sort(treeList, new Comparator<HuffmanTree>() {

			@Override
			public int compare(HuffmanTree o1, HuffmanTree o2) {
				if(o1.weight-o2.weight==0){
					return o1.value.toString().compareTo(o2.value.toString());
				}
				return o1.weight-o2.weight;
			}

		});
		return treeList;
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map ){
		List<Map.Entry<K, V>> list =
				new LinkedList<>( map.entrySet() );
		Collections.sort( list, new Comparator<Map.Entry<K, V>>(){
			@Override
			public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 ){
				return ( o1.getValue() ).compareTo( o2.getValue() );
			}
		} );

		Map<K, V> result = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : list){
			result.put( entry.getKey(), entry.getValue() );
		}
		return result;
	}

}
