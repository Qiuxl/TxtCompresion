package Compression;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Image;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.InputEvent;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.InputStreamReader;

import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import layout.TableLayout;

/**
 *
 *   /Icon/app_icon.png
 *   /Icon/app_icon.png
 *   /Icon/icon_open.png
 * 
 * 
 */
public class Gui extends JFrame {
	
	
	public boolean isEdited = false; //作为编辑与否的标记 
	public boolean isSave = false;  // 作为保存与否的标记
	private boolean DoubleScrollEnable = true;  //标记同步滚动的功能开启与否，编辑的时候同步滚动设置为false，当用户自己滚动的时候才开启
	private boolean isOpen = false;
	private boolean isCode = false;
	
    private int EncodeWay = 0;
    private int Humman = 1;  //记录压缩的方式
    
	private Lock EditLock = new ReentrantLock();   //编辑框设置的时候的lock
	private JScrollBar jscrollBar1;
	private ArrayList<Integer> recordline = new ArrayList<Integer>();

	private String currentFile;  //这里指的是完整的路径名称
    private Image mainIcon;
    private boolean enableEditlistern = true;  
    private int sroll1Pos,sroll1Max;  //滚动条1的位置和最大值
    private int sroll2Pos=0,sroll2Max=0;  //滚动条2的位置和最大值
    
    private static HuffmanCompress huffmanCompress = new HuffmanCompress();
    private static HuffmanUnCompress huffmanUnCompress = new HuffmanUnCompress();
    private static HuffmanUnCompress decodeHuffman = new HuffmanUnCompress();
    
	JMenuBar topMenu;
	Container c;  
	Font textStyle = new Font("微软雅黑",Font.BOLD, 12);
	Font textStyle1 = new Font("SansSerif", Font.BOLD, 11);
	Icon icon_open = new ImageIcon("./Icon/icon_open1.png");
	JToolBar topTool;
	
	JLabel numofchar = new JLabel("Characters: "+"0");
	JLabel cols = new JLabel("Column: "+"1");
	JLabel lines = new JLabel("Lines: "+ "1");
	JLabel warn = new JLabel();
	
	DefaultListModel<String> defaultListModel = new DefaultListModel<String>();   
    
	
	TextArea naviArea = new TextArea(1000,40);
	JEditorPane areaShow = new JEditorPane();
	JTextArea textedit  = new JTextArea();
	JTextArea lineArea = new JTextArea();
	JTextArea showResult = new JTextArea();
    
	public Gui() {
		// TODO Auto-generated constructor stub
		try {
			mainIcon = ImageIO.read(new File("./Icon/app_icon.png"));
			
			setIconImage(mainIcon);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		huffmanCompress.setUI(this);
		huffmanUnCompress.setUI(this);
		showResult.setLineWrap(true);
		
		areaShow.setEditable(false);// 右边不可编辑
		InitializeMenu();
		InitializeTable();
		InitiaBottom();
		
		
	//	textedit.setColumns(35);
	 //   textedit.setLineWrap(true);        //激活自动换行功能 
	//    textedit.setWrapStyleWord(true);            // 激活断行不断字功能
		lineArea.setText("1\n"); 
		lineArea.setEditable(false);    //不能编辑
		
		lineArea.setBackground(new Color(0xe0,0xee,0xe0));   //E0EEE0
		lineArea.setFont(new Font("楷体", Font.PLAIN, 16));
		lineArea.setForeground(new Color(0x4F,0x4F,0x4F)); //7FFF00  4F4F4F
		lineArea.setCaretColor(new Color(0x99,0x99,0x99)); //C6E2FF 
	    this.setJMenuBar(topMenu);
		this.add(topTool,BorderLayout.NORTH);
		this.pack();
	//   设置事件监听器，动态显示行数和列数  
		textedit.addCaretListener(new CaretListener() {
			
			@Override
			public void caretUpdate(CaretEvent e) {
				if(enableEditlistern==false)
				{
					System.out.println("Lock by user");
					return;
				}
				// TODO Auto-generated method stub
				try {
					int pos = textedit.getCaretPosition();
	                int lines = textedit.getLineOfOffset(pos) + 1; 
	                //获取列数 
	                int col = pos - textedit.getLineStartOffset(lines-1);
	                cols.setText("Column "+col);
	                Gui.this.lines.setText("Line "+lines);

				} catch (Exception e2) {
					// TODO: handle exception
					warn.setText("No cursor info");
				}
			}
		});
	}
	
	public String getText()
	{
		return textedit.getText();
	}
	public void setText(String str)
	{
		EditLock.lock();
		textedit.setText(str);
		EditLock.unlock();
	}
	
	public void caretUpdate() {
		// TODO Auto-generated method stub
		try {
			int pos = textedit.getCaretPosition();
            int lines = textedit.getLineOfOffset(pos) + 1; 
            //获取列数 
            int col = pos - textedit.getLineStartOffset(lines-1);
            cols.setText("Column "+col);
            Gui.this.lines.setText("Line "+lines);
            int line2 = lineArea.getLineCount();
            if(line2>=999)
            	;
            if(line2<=lines)
            {
            	for(;line2<=lines;line2++)
            		lineArea.append(line2+"\n");
            }
            else{
            	//重新绘制图案
            	lineArea.setText("");
            	for(int i=1;i<=lines;i++)
            		lineArea.append(i+"\n");
            }
		} catch (Exception e2) {
			// TODO: handle exception
			warn.setText("No cursor info");
		}
		
	}
	//初始化窗体的菜单栏
	private void InitializeMenu()
	{
        //创建内容面板  
        
		KeyStroke openKS = KeyStroke.getKeyStroke("ctrl O");// 定义一个ctrl + b的快捷键 
		KeyStroke saveAsKS = KeyStroke.getKeyStroke("shift s");// 定义一个ctrl + b的快捷键 
        c=this.getContentPane();  
        
		topMenu = new JMenuBar();  //最顶层的菜单
		topTool = new JToolBar();  //工具栏
		JButton OpenBtn = new JButton(new ImageIcon(this.getClass().getResource("/Icon/icon_open1.png")));
		JButton SaveBtn = new JButton(new ImageIcon(this.getClass().getResource("/Icon/icon_save.png")));
		JButton exportBtn = new JButton(new ImageIcon(this.getClass().getResource("/Icon/icon_export.png")));	
		
		
		
		OpenBtn.setBorderPainted(false);
		exportBtn.setBorderPainted(false);
		SaveBtn.setBorderPainted(false);
		
		JMenu file = new JMenu("File");
		JMenu help = new JMenu("Help");
		JMenu Encode = new JMenu("Encode");
		JMenu Decode = new JMenu("Decode");  //导入css文件或者使用默认文件的接口
		
		
		JMenuItem haffItem = new JMenuItem("Haffman");
		JMenuItem uploadItem = new JMenuItem("Upload");
		JMenuItem downItem = new JMenuItem("Pull");
		JMenuItem DisconItem = new JMenuItem("Disconnect");
		
		JMenuItem openItem = new JMenuItem("Open",icon_open);	
		JMenuItem saveItem = new JMenuItem("Save",new ImageIcon("./Icon/icon_save.png"));
		JMenuItem saveAsItem = new JMenuItem("Save as");
		JMenuItem closeItem = new JMenuItem("Close");
		JMenuItem ImportCss = new JMenuItem("Import Css File");   // 提供给用户导入自己的css文件的按钮
			
		JMenuItem HuffmanDecode = new JMenuItem("Huffman");
		
		JMenuItem AutoRefresh = new JMenuItem("开启");
		JMenuItem RefreshByhand = new JMenuItem("关闭");
		AutoRefresh.setFont(textStyle);
		RefreshByhand.setFont(textStyle);
		
		
		
		JMenuItem About = new JMenuItem("About this");
		


		
		saveAsItem.setAccelerator(saveAsKS);
		openItem.setAccelerator(openKS);
		uploadItem.setAccelerator(KeyStroke.getKeyStroke('R', InputEvent.CTRL_MASK));  //刷新的功能
		saveItem.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_MASK));
	//	exportItem.setAccelerator(KeyStroke.getKeyStroke('W', InputEvent.CTRL_MASK));
//添加监听事件
		openItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Open_file();
			}
		});
//save as的监听事件
		saveAsItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				save_as();
			}
		});
//save 的监听事件
		saveItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				save();
			}
		});
		SaveBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				save();
			}
		});
		OpenBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Open_file();
			}
		});
//导出html文件
		closeItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(!isEdited)
				{
					textedit.setText("");
					areaShow.setText("");
					isEdited = false;
				}
				else{
					new SaveDialog(Gui.this, "Save or Not?");
				}
			}
		});
		About.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				new MessegeDialog(Gui.this, "Any Question or bug Please send email to xlq1120@yahoo.com");
			}
		});
		haffItem.addActionListener( e->{
			if(!isOpen)
				return;
			huffmanCompress.Intialize();
			huffmanCompress.Compress(currentFile);
			isCode = true;
			EncodeWay = Humman;
			
		});
		HuffmanDecode.addActionListener(e->{
			if(!isOpen)
				return;
			huffmanUnCompress.Decode(currentFile);
			isCode = true;
			
		});
		file.setMnemonic('F');
//		file.add(NewFileItem);
//		file.addSeparator();
		file.add(openItem);
		file.addSeparator();
		file.add(saveItem);
		file.addSeparator();
		file.add(saveAsItem);
		file.addSeparator();
		file.add(closeItem);
		
		
		Decode.add(HuffmanDecode);
		Decode.addSeparator();
		Decode.add(ImportCss);
		
		Encode.add(haffItem);
		Encode.add(uploadItem);
		Encode.add(downItem);
		Encode.add(DisconItem);
		
		
		help.add(About);
		
		Decode.setFont(textStyle);
		file.setFont(textStyle);
		help.setFont(textStyle);
		Encode.setFont(textStyle);
		topMenu.setFont(textStyle);
		topMenu.add(file);
		
		topMenu.add(Encode);
		topMenu.add(Decode);

		topMenu.add(help);
		topTool.setBackground(new Color(234, 234, 234));
		topTool.add(OpenBtn);
		topTool.addSeparator();
		topTool.add(SaveBtn);
		topTool.addSeparator();
	
		
	}
	
	//初始化默认的css显示样式
	public void  InitializeDefaultCss() {
	    HTMLEditorKit kit = new HTMLEditorKit();
//添加规则
	    StyleSheet styleSheet = kit.getStyleSheet();
	     
	    
	    styleSheet.addRule("table {border-collapse: collapse; border: solid #000000; border-width: 1px 0 0 1px;}");
	    styleSheet.addRule("table caption {font-size: 12px; font-weight: bolder;}");
	    styleSheet.addRule("table td {white-space: word-wrap; font-size: 10px; height: 10px; border: solid #000000; border-width: 0 1px 1px 0; padding: 2px; text-align: left; vertical-align: center}");
	    styleSheet.addRule("code, tt {margin: 0 0px; padding: 0px 0px; white-space: nowrap; border: 1px solid #eaeaea; background-color: #f8f8f8;border-radius: 3px;}");
	    styleSheet.addRule("pre>code {margin: 0;padding: 0;white-space: pre;border: none;background: transparent;}");
	    styleSheet.addRule("pre, code, tt {font-size: 12px;font-family: Consolas, \"Liberation Mono\", Courier, monospace;}");
	    styleSheet.addRule("pre {background-color: #f8f8f8;border: 1px solid #ccc;font-size: 13px;line-height: 19px;overflow: auto;padding: 6px 10px;border-radius: 3px;}");
	    
	    
	    areaShow.setEditorKit(kit);
		areaShow.setEditorKit(new HTMLEditorKit());
	}
	
// 这里使用table laytout来初始化	
	public void InitializeTable()
	{
	
	//创建一个一行一列的tablelayout
		
		
		double size[][] ={
				{0.20,0.39,5,TableLayout.FILL}, 
				{TableLayout.FILL}};
		double size1[][] ={     //用于显示中间的行号
				{30,TableLayout.FILL}, 
				{TableLayout.FILL}};
		
		JPanel middlepane = new JPanel(new TableLayout(size1));
		lineArea.setColumns(4);
	    lineArea.setLineWrap(true);        //激活自动换行功能 
		middlepane.add(lineArea, "0,0");
		middlepane.add(textedit,"1,0");

		InitializeDefaultCss();
		
		areaShow.setAutoscrolls(true);
	
//		Markdown4jProcessor myProcessor = new Markdown4jProcessor();
//		try {
//			after = myProcessor.process(primary);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		textedit.setText(after);
//		areaShow.setText(after);

	//	JSplitPane pane1 = new 
		textedit.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		textedit.setEditable(false);
		JScrollPane sroll2 = new JScrollPane(middlepane);
	//	JScrollPane sroll3 = new JScrollPane(areaShow);
		JScrollPane sroll3 = new JScrollPane(showResult);
		
		areaShow.setAutoscrolls(true);
		
		sroll2.setVerticalScrollBarPolicy( 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);  //设置总是出现，便于监听
		
		/**
		 *Function:实现了两个view的同步滚动 
		 *@author qzh
		 *date:2016-12-07
		 *@version:1.0
		 **/
		jscrollBar1 = sroll2.getVerticalScrollBar();
		if (jscrollBar1 != null) 
		jscrollBar1.addAdjustmentListener(new AdjustmentListener() {
			
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				// TODO Auto-generated method stub
				JScrollBar jscrollBar2 = sroll3.getVerticalScrollBar();
				if(jscrollBar2 == null)
					return ;
				if(DoubleScrollEnable == false)
					return;
				sroll1Pos = jscrollBar1.getValue();
				sroll1Max = jscrollBar1.getMaximum();
				sroll2Pos = jscrollBar2.getValue();
				sroll2Max = jscrollBar2.getMaximum();
				if(sroll1Pos == 0)
					jscrollBar2.setValue(0);
				else{
					int temp= (int) ((long)sroll2Max*(long)sroll1Pos/sroll1Max);
					jscrollBar2.setValue(temp);
				}
			}
		});
		JSplitPane rightpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true);
		rightpane.setLeftComponent(sroll2);
		rightpane.setRightComponent(sroll3);
		rightpane.setDividerLocation(400);
		rightpane.setDividerSize(2);
		rightpane.setVisible(true);
		this.getContentPane().add(rightpane);
	
				
	}
	public void InitiaBottom() {
		
		double size[][] ={
				{130,100,100,100,TableLayout.FILL}, 
				{TableLayout.FILL}};
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new TableLayout(size));
		bottomPanel.setBounds(0, 290, 400, 5);
		bottomPanel.setFont(textStyle1);
		
		numofchar.setFont(textStyle1);
		bottomPanel.add(numofchar,"0,0");
		bottomPanel.setBackground(new Color(194,194,194));
		this.getContentPane().add(bottomPanel,BorderLayout.SOUTH);	
		
	}
	
	
/**
 *Function: open file Function 
 *@author qzh 
 *@return null
 *@version 1.0
 */
	public void Open_file() {
		boolean judge = isOpen && isCode && !isSave; //打开了文件并且进编码或者译码并没有保存，满足条件需要提示保存
		if(!judge)
		{

			enableEditlistern = false;
			StringBuffer contextBuffer = new StringBuffer();
			FileDialog OpenDia = new FileDialog(this, "Open File"); // 重要
			OpenDia.setVisible(true);
			String path = OpenDia.getDirectory() + OpenDia.getFile(); // 重要
			if (!path.equals("nullnull")) 
			{
				currentFile = path;
				String context = "";
				BufferedReader fReader = null;
				try {
			//这里仅仅用于处理文本文件，因此直接使用reader
					try {
						fReader = new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8"));
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} 
					String message = null;
					try {
						while ((message = fReader.readLine()) != null) {
							contextBuffer.append(message);	
							contextBuffer.append("\n");
						}
						fReader.close();
					} 
					catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						}
					
					}
				catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				context = contextBuffer.toString();
				textedit.setText(context);
				showResult.setText("");
				enableEditlistern = true;
			//	textedit.setCaretPosition(context.length());
		//		update_lineTag();

				textedit.setCaretPosition(0);
		//		System.out.println(curPosition);
				isEdited = false;
				String currentTitle = Gui.this.getTitle();
				int index = currentTitle.indexOf("As a server");
				if(index<0)
				{
					Gui.this.setTitle(path);
				}
				else 
				{
					String substr = currentTitle.substring(index, currentTitle.length());
					Gui.this.setTitle(path+" "+substr);
				}
				int index1 = currentTitle.indexOf("connect");
				if(index1<0&&index<0)
				{
					Gui.this.setTitle(path);
				}
				else if(index1>0)  //已经连接到远程
				{
					String substr = currentTitle.substring(index1, currentTitle.length());
					Gui.this.setTitle(path+" "+substr);
				}
				isCode = false;
				isSave = false;
				isOpen = true;
				EncodeWay = -1;
			}
		}
		else
		{
			new OpenDialog(this, "Save compressed file or Not?");
		}
// 打开文件,只有成功打开了才能修改isEdited变量

		
	}
	
/**
 * Function: fetch the content out of area show,and save
 * @author qzh
 * @date: 2016-12-05
 * @version: 1.0
 */	
   public void save() {
	   
	   if(isEdited == false)
		   return;
	   if(currentFile == null)
	   {
		   save_as();
	   }
	 //否则直接覆盖重写
	   else{
	   BufferedOutputStream buffout;
	   try {
		   buffout = new BufferedOutputStream(new FileOutputStream(currentFile));
		   try {
			   buffout.write(textedit.getText().getBytes());
			   buffout.close();
		   } catch (IOException e) {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
		   }
		   try {
			buffout.close();
		   }
		   catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		   }
		   isEdited = false;
	   	} 
	   catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
	   		e.printStackTrace();
	   	}
	   }
   }
   
   /**
    * save as another file
    * 
    * */
   public void save_as() {
	
		FileDialog dia2 = new FileDialog(this, "Save", FileDialog.SAVE);
		dia2.setFile("unTitled.txt");
	//	dia2.setFilenameFilter((FilenameFilter)new Filter());
		dia2.setVisible(true);
		String path = dia2.getDirectory() + dia2.getFile();
		if (!path.equals("nullnull")) {
			isEdited = false;
			if(currentFile == null)
				currentFile = path;
			if(EncodeWay == Humman)
				huffmanCompress.saveFile(path);
			else {
				try {
					BufferedOutputStream buffout = new BufferedOutputStream(new FileOutputStream(path));
					buffout.write(showResult.getText().getBytes());
					buffout.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			this.setTitle(currentFile);
			isSave = true;
		}
			
   }
// 关闭的时候的响应函数，当被编辑过的时候
   public void save_on_close()
   {
	  // new OpenDialog(this, "Save or Not");
	//   new CloseDialog(this, "Save or Not?");
   }   
   public void update_lineTag()
   {
//       int line2 = textedit.getLineCount();
//       
//       if(totallines <= line2)
//       {
//       	for(;totallines<=line2-1;totallines++)
//       		lineArea.append(totallines+2+"\n");
//       }
//       else{
       	//重新绘制图案
       	lineArea.setText("");
       	for(int i=1;i<=textedit.getLineCount();i++)
       		lineArea.append(i+"\n");
//       }
   }
   /*
    * funtion：点击导航栏的时候中间屏幕的移动
    * 
    * 
    **/
   public void sroll_sreen(int position) {
	
	   textedit.setCaretPosition(position);
   }
  
   public static void main(String[] args) {
		// TODO Auto-generated method stub
		Gui myGui = new Gui();
		myGui.setTitle("My markdown");
		myGui.setSize(800,600);
		myGui.setLocationRelativeTo(null);
		myGui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myGui.setVisible(true);
	}
   
}
