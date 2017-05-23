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
import java.nio.channels.NonWritableChannelException;
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

import org.omg.IOP.ENCODING_CDR_ENCAPS;

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
	
	
	public boolean isEdited = false; //��Ϊ�༭���ı�� 
	public boolean isSave = false;  // ��Ϊ�������ı��
	private boolean DoubleScrollEnable = true;  //���ͬ�������Ĺ��ܿ�����񣬱༭��ʱ��ͬ����������Ϊfalse�����û��Լ�������ʱ��ſ���
	private boolean isOpen = false;
	private boolean isCode = false;
	
    private int EncodeWay = 0;
    private int Humman = 1;  //��¼ѹ���ķ�ʽ
    private int LZW = 2;
	private Lock EditLock = new ReentrantLock();   //�༭�����õ�ʱ���lock
	private JScrollBar jscrollBar1;
	private ArrayList<Integer> recordline = new ArrayList<Integer>();

	private String currentFile;  //����ָ����������·������
    private Image mainIcon;
    private boolean enableEditlistern = true;  
    private int sroll1Pos,sroll1Max;  //������1��λ�ú����ֵ
    private int sroll2Pos=0,sroll2Max=0;  //������2��λ�ú����ֵ
    
    private static HuffmanCompress huffmanCompress = new HuffmanCompress();
    private static HuffmanUnCompress huffmanUnCompress = new HuffmanUnCompress();
    private static HuffmanUnCompress decodeHuffman = new HuffmanUnCompress();
    private static LZWcompress lzWcompress = new LZWcompress();
    
    
	JMenuBar topMenu;
	Container c;  
	Font textStyle = new Font("΢���ź�",Font.BOLD, 12);
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
		lzWcompress.SetGui(this);
		showResult.setLineWrap(true);
		
		areaShow.setEditable(false);// �ұ߲��ɱ༭
		InitializeMenu();
		InitializeTable();
		InitiaBottom();
		
		
	//	textedit.setColumns(35);
	 //   textedit.setLineWrap(true);        //�����Զ����й��� 
	//    textedit.setWrapStyleWord(true);            // ������в����ֹ���
		lineArea.setText("1\n"); 
		lineArea.setEditable(false);    //���ܱ༭
		
		lineArea.setBackground(new Color(0xe0,0xee,0xe0));   //E0EEE0
		lineArea.setFont(new Font("����", Font.PLAIN, 16));
		lineArea.setForeground(new Color(0x4F,0x4F,0x4F)); //7FFF00  4F4F4F
		lineArea.setCaretColor(new Color(0x99,0x99,0x99)); //C6E2FF 
	    this.setJMenuBar(topMenu);
		this.add(topTool,BorderLayout.NORTH);
		this.pack();
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
            //��ȡ���� 
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
            	//���»���ͼ��
            	lineArea.setText("");
            	for(int i=1;i<=lines;i++)
            		lineArea.append(i+"\n");
            }
		} catch (Exception e2) {
			// TODO: handle exception
			warn.setText("No cursor info");
		}
		
	}
	//��ʼ������Ĳ˵���
	private void InitializeMenu()
	{
        //�����������  
        
		KeyStroke openKS = KeyStroke.getKeyStroke("ctrl O");// ����һ��ctrl + b�Ŀ�ݼ� 
		KeyStroke saveAsKS = KeyStroke.getKeyStroke("shift s");// ����һ��ctrl + b�Ŀ�ݼ� 
        c=this.getContentPane();  
        
		topMenu = new JMenuBar();  //���Ĳ˵�
		topTool = new JToolBar();  //������
		JButton OpenBtn = new JButton(new ImageIcon(this.getClass().getResource("/Icon/icon_open1.png")));
		JButton SaveBtn = new JButton(new ImageIcon(this.getClass().getResource("/Icon/icon_save.png")));
		
		
		
		OpenBtn.setBorderPainted(false);
		SaveBtn.setBorderPainted(false);
		
		JMenu file = new JMenu("File");
		JMenu help = new JMenu("Help");
		JMenu Encode = new JMenu("Encode");
		JMenu Decode = new JMenu("Decode");  //����css�ļ�����ʹ��Ĭ���ļ��Ľӿ�
		
		
		JMenuItem haffItem = new JMenuItem("Haffman");
		JMenuItem LZWitem = new JMenuItem("LZW");
		JMenuItem downItem = new JMenuItem("Pull");
		JMenuItem DisconItem = new JMenuItem("Disconnect");
		
		JMenuItem openItem = new JMenuItem("Open",icon_open);	
		JMenuItem saveAsItem = new JMenuItem("Save as");
		JMenuItem closeItem = new JMenuItem("Close");
		JMenuItem LzwDecode = new JMenuItem("LZW decode");   // �ṩ���û������Լ���css�ļ��İ�ť			
		JMenuItem HuffmanDecode = new JMenuItem("Huffman");	
		JMenuItem AutoRefresh = new JMenuItem("����");
		JMenuItem RefreshByhand = new JMenuItem("�ر�");
		
		
		AutoRefresh.setFont(textStyle);
		RefreshByhand.setFont(textStyle);	
		JMenuItem About = new JMenuItem("About this");
		
		saveAsItem.setAccelerator(saveAsKS);
		openItem.setAccelerator(openKS);
	//	exportItem.setAccelerator(KeyStroke.getKeyStroke('W', InputEvent.CTRL_MASK));
//��Ӽ����¼�
		openItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Open_file();
			}
		});
		
//save as�ļ����¼�
		saveAsItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				save_as();
			}
		});
//save �ļ����¼�
		SaveBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				save_as();
			}
		});
		OpenBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Open_file();
			}
		});
//����html�ļ�
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
		LZWitem.addActionListener( e ->{
			if(!isOpen)
				return;
			lzWcompress.initialize();
			lzWcompress.compress(currentFile);
			EncodeWay = LZW;
			isCode = true;
		});
		LzwDecode.addActionListener(e->{
			if(!isOpen)
				return;
			lzWcompress.Decode(currentFile);
			isCode = true;
		});
		file.setMnemonic('F');
//		file.add(NewFileItem);
//		file.addSeparator();
		file.add(openItem);
		file.addSeparator();
		file.add(saveAsItem);
		file.addSeparator();
		file.add(closeItem);
		
		
		Decode.add(HuffmanDecode);
		Decode.addSeparator();
		Decode.add(LzwDecode);
		
		Encode.add(haffItem);
		Encode.add(LZWitem);
	
		
		
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
	
	//��ʼ��Ĭ�ϵ�css��ʾ��ʽ
	public void  InitializeDefaultCss() {
	    HTMLEditorKit kit = new HTMLEditorKit();
//��ӹ���
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
	
// ����ʹ��table laytout����ʼ��	
	public void InitializeTable()
	{
	
	//����һ��һ��һ�е�tablelayout
		
		double size[][] ={
				{0.20,0.39,5,TableLayout.FILL}, 
				{TableLayout.FILL}};
		double size1[][] ={     //������ʾ�м���к�
				{30,TableLayout.FILL}, 
				{TableLayout.FILL}};
		
		JPanel middlepane = new JPanel(new TableLayout(size1));
		lineArea.setColumns(4);
	    lineArea.setLineWrap(true);        //�����Զ����й��� 
		middlepane.add(lineArea, "0,0");
		middlepane.add(textedit,"1,0");

		InitializeDefaultCss();
		
		areaShow.setAutoscrolls(true);
		textedit.setFont(new Font("΢���ź�", Font.PLAIN, 14));
		textedit.setEditable(false);
		JScrollPane sroll2 = new JScrollPane(middlepane);
		JScrollPane sroll3 = new JScrollPane(showResult);
		
		areaShow.setAutoscrolls(true);
		
		sroll2.setVerticalScrollBarPolicy( 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);  //�������ǳ��֣����ڼ���
		
		/**
		 *Function:ʵ��������view��ͬ������ 
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
		boolean judge = isOpen && isCode && !isSave; //�����ļ����ҽ�����������벢û�б��棬����������Ҫ��ʾ����
		if(!judge)
		{

			enableEditlistern = false;
			StringBuffer contextBuffer = new StringBuffer();
			FileDialog OpenDia = new FileDialog(this, "Open File"); // ��Ҫ
			OpenDia.setVisible(true);
			String path = OpenDia.getDirectory() + OpenDia.getFile(); // ��Ҫ
			if (!path.equals("nullnull")) 
			{
				currentFile = path;
				String context = "";
				BufferedReader fReader = null;
				try {
			//����������ڴ����ı��ļ������ֱ��ʹ��reader
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

				textedit.setCaretPosition(0);
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
				else if(index1>0)  //�Ѿ����ӵ�Զ��
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
// ���ļ�,ֻ�гɹ����˲����޸�isEdited����

		
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
	 //����ֱ�Ӹ�����д
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
			else if(EncodeWay == LZW)
			{
				lzWcompress.SaveFile(path);
			}
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

   /*
    * funtion�������������ʱ���м���Ļ���ƶ�
    * 
    * 
    **/
   public void sroll_sreen(int position) {
	
	   textedit.setCaretPosition(position);
   }
  
   public static void main(String[] args) {
		// TODO Auto-generated method stub
		Gui myGui = new Gui();
		myGui.setTitle("My Compressor");
		myGui.setSize(800,600);
		myGui.setLocationRelativeTo(null);
		myGui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myGui.setVisible(true);
	}
   
}
