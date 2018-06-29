import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Collections;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import gnu.io.CommPortIdentifier;

import java.awt.Component;

public class Frame extends JFrame 
{
	static int step=100;
	static boolean ifProceed=true;
	static boolean ifProceedInter=true;
	static boolean automatically=false;
	static boolean readCMS=true;
	static Thread startRun;
	static boolean ifWriteData=false;

	static String dbTime="";
	static String dbCMS="";
	static String dbTorquePosition="";
	static String dbSpeedPositon="";
	static String db13torque="";
	static String dbValueJudgement="OK";
	static String dbForwardTurn="";
	static String dbReverseTurn="";
	static String dbLED="";
	static String dbLowSpeedRPM="";
	static String dbLowSpeedCurrent="";
	static String dbHighSpeedRPM="";
	static String dbHighSpeedCurrent="";
	static String dbHallSegmentTime="";


	static String ReceivedPLC="";
	static String ReceivedDEV="";
	private JPanel contentPane;
	private static JButton ButtonSave;
	static JTextArea report;
	static String comvalue="\r\n,";
	static String PLC_COM="";
	static String DEVICE_COM="";
	static byte[] writeIn={0x00,0x00,0x00,0x00,0x00,0x00};
	static String SerialNumberShouldBeStored="";
	static String LocalMac = "";
	static String ConCode = "";
	static String ActCode = "";
	static int Offset_Ch0 = 0;
	static int Offset_Ch1 = 0;
	static int Variation_Ch0 = 0;
	static int Variation_Ch1 = 0;
	static String Instruction,TempInstruction;
	static int Channel_0_1 = 0;
	static int Channel_1_1 = 0;
	static int Channel_0_DH = 0;
	static int Channel_1_DH = 0;
	static boolean Saturation_0 = false;
	static boolean Saturation_1 = false;
	static int Middle_Point_Ch0 = 0;
	static int Middle_Point_Ch1 = 0;
	static int Delta_Offset_Ch0 = 0;
	static int Delta_Offset_Ch1 = 0;
	static int CircleCnt = 0;
	static int TorqueCoefficinet = 0;
	
	static TestAccessConnect testAccessConnect;
	static RXTXcomm deviceCom;
	static RXTXcomm plcCom;
	private JTextArea stateAreaDemo;
	static JButton btnReset;
	static JButton btnStart;
	static JLabel step0;
	static JLabel step1;
	static JLabel step2;
	static JLabel step3;
	static JLabel step4;
	static JLabel step5;
	static JLabel step6;
	static JLabel step7;
	static JLabel step8;
	static JLabel step9;
	static JLabel step10;
	static JLabel step11;
	static JLabel step12;
	static JLabel step13;
	static JLabel step14;
	static JLabel step15;
	static JLabel step16;
	static JLabel step17;
	static JLabel step18;
	static JLabel step19;
	static JTextField dataBaseUsed;
	private JComboBox comboBoxPLC;
	private JComboBox comboBoxDEVICE;
	
	//将txt文本转化成字符串
	public static String txt2String(File file)
	{
		String result = "";
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
			String s = null;
			while((s = br.readLine())!=null){//使用readLine方法，一次读一行
				result = result + "\n" +s;
			}
			br.close();    
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	//刷新portList，在main函数中调用
	public Timer reFreshPortList = new Timer(0, new ActionListener()
	{
		public void actionPerformed(ActionEvent e) 
		{
			CommPortIdentifier portId;
			Enumeration en = CommPortIdentifier.getPortIdentifiers();

			comboBoxDEVICE.removeAllItems();
			comboBoxPLC.removeAllItems();

			while (en.hasMoreElements()) 
			{
				portId = (CommPortIdentifier) en.nextElement();
				if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) 
				{
					comboBoxDEVICE.addItem(portId.getName());
					comboBoxPLC.addItem(portId.getName());
				}
			}
			reFreshPortList.stop();
		}
	});	
	private JTextField OffsetCh0;
	private JTextField OffsetCh1;
	private JTextField VariationCh0;
	private JTextField VariationCh1;
	private JTextField Coefficient;


	//将字符串嵌入字符串当中
	public static String Stringinsert(String a,String bString,int t)
	{ 
		return a.substring(0,t)+bString+a.substring(t,a.length());
	}
	
	public static void main(String[] args) 
	{
		testAccessConnect=new TestAccessConnect();//建立Access连接
		deviceCom=new RXTXcomm();
		deviceCom.reFreshPortList.start();
		deviceCom.frame.setResizable(true);
		deviceCom.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		deviceCom.frame.setSize(20, 20);
		
		plcCom=new RXTXcomm();
		plcCom.reFreshPortList.start();
		plcCom.frame.setResizable(true);
		plcCom.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		plcCom.frame.setSize(20, 20);

		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					Frame frame = new Frame();
					frame.setVisible(true);
					frame.reFreshPortList.start();
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});  
	}	
	
	public static void startProgram()
	{
		btnStart.doClick();
	}

	public static void codedtoHex(String s) 
	{
		int x = Integer.parseInt(s);
		if (x<0)
		{
			x=x+256;
		}
		String string=Integer.toHexString(x);
		if (string.length()==3)
		{
			string="0"+string;
		}
		if (string.length()==2)
		{
			string="00"+string;
		}
		if (string.length()==1) 
		{
			string="000"+string;
		}
		byte[] bs=hexStringToBytes(string);
		writeIn[3]=bs[1];
		writeIn[4]=bs[0];
	}

	public static String codedtoHexString(int x) 
	{
		if (x<0)
		{
			x=x+256;
		}
		String string=Integer.toHexString(x);
		if (string.length()==3)
		{
			string="0"+string;
		}
		if (string.length()==2)
		{
			string="00"+string;
		}
		if (string.length()==1)
		{
			string="000"+string;
		}
		return string;
	}

	//函数负责解析命令输出固定格式的report
	public static String match(byte[] src,String receive)
	{
		String total="";
		String description="";
		String variable="";
		String dimension="";
		String update="";
		int number=0;
		int numberusedto9200=0;
		float number1=0;
		String sendUpperCase= bytesToHexString(src).toUpperCase();//将发送的数据，byte型数组转化成string，并统一成大写
		String labelposition=sendUpperCase.substring(0,2);//控制位
		String sendDatalow=sendUpperCase.substring(6,8);//低位数据
		String sendDatahigh=sendUpperCase.substring(8,10);//高位数据
		String sendDatanormal=sendDatahigh+sendDatalow;//正常数据
		if (labelposition.equals("07")) 
		{
			update="updated to ";
		}
		String thewholeAddress=sendUpperCase.substring(2, 6);//地址位
		
		receive=receive.replace(" ", "");//接收到的数据
		byte[] received=hexStringToBytes(receive);//将字符串转换成字节
		byte[] normal=new byte[6];
		normal[0]=received[0];
		normal[4]=received[3];
		normal[3]=received[4];
		normal[2]=received[1];
		normal[1]=received[2];
		normal[5]=received[5];
		receive= bytesToHexString(normal);//将字节又转换成字符串		
		String usedin3001=receive.substring(2,6);		
		String low=receive.substring(8,10);
		String high=receive.substring(6,8);
		receive=receive.substring(6, 10);//收到的数据值
		
		switch (thewholeAddress) 
		{
		case "1000":         
			description="get raw value of measured battery voltage";
			variable="raw value of voltage";
			number=Integer.parseInt(receive,16);
			dimension="digit";
		break;

		case "10BD":         
			description="get calculated value of battery voltage";
			variable="voltage";
			number=Integer.parseInt(receive,16);
			dimension="mV";
		break;

		case "1100":         
			description="get raw value of measured battery temperature";
			variable="raw value of temperature";
			number=Integer.parseInt(receive,16);
			dimension="digit";
		break;

		case "11BD":         
			description="get calculated temperature of battery";
			variable="temperature";
			number=Integer.parseInt(receive,16);
			if (number>128) 
			{
				number=number-256;
			}
			dimension="℃";
		break;

		case "1200":         
			description="get raw value of measured current";
			variable="raw value of measured current";
			number=Integer.parseInt(receive,16);
			dimension="digit";
		break;

		case "12BD":         
			description="get calculated current";
			variable="current";
			number1= (float)(Integer.parseInt(receive,16))/65536;
			number1=(float)(Math.round(number1*10000))/10000;
			dimension="A";
		break;

		case "1300":         
			description="get battery current correction offset";
			variable="raw value";
			number=Integer.parseInt(receive,16);
			dimension="digit";
		break;

		case "1400":         
			description="calibrate battery current correction factor";
			variable="battery current correction factor";
			number=Integer.parseInt(sendDatanormal,16);
			dimension="";
		break;

		case "1500":         
			description="calibrate battery voltage measurement offset";
			variable="battery voltage measurement offset";
			number=Integer.parseInt(sendDatanormal,16);
			dimension="";
		break;

		case "2000":         
			description="get raw raw value of potentiometer";
			variable="raw value";
			number=Integer.parseInt(receive,16);
			dimension="digit";
		break;

		case "20BD":         
			description="get calculated value of potentiometer";
			variable="position";
			number=Integer.parseInt(receive,16);
			dimension="";
		break;

		case "2100":         
			description="get raw value der elo ntc";
			variable="raw value";
			number=Integer.parseInt(receive,16);
			dimension="digit";
		break;

		case "21BD":         
			description="get calculated elo-temperature";
			variable="temperature";
			number=Integer.parseInt(receive,16);
			if (number>128) {
				number=number-256;
			}
			dimension="℃";
		break;

		case "2200":         
			description="get raw value of reference voltage";
			variable="raw value";
			number=Integer.parseInt(receive,16);
			dimension="digit";
		break;

		case "2300":         
			description="get raw value gate supply";
			variable="raw value";
			number=Integer.parseInt(receive,16);
			dimension="digit";
		break;

		case "23BD":         
			description="get gate supply voltage";
			variable="voltage";
			number=Integer.parseInt(receive,16);
			dimension="mV";
		break;

		case "5000":         
			description="get motor speed";
			variable="motor-speed";
			number=Integer.parseInt(receive,16);
			dimension="RPM";
		break;

		case "5200":       
			if (low.equals("01")) 
			{
				description="enable MosFET testsignal on highside";
				variable="duty cycle";
				number=Integer.parseInt(high,16);
			} 
			else
			{
				description="enable MosFET testsignal on lowside";
				variable="duty cycle";
				number=Integer.parseInt(high,16);
			}
		break;

		case "5F00":         
			description="disable all motor mosfets";
		break;

		case "2900":        
			description="disable selfsupply (attention: answer is only sent in error case!)";
		break;

		case "7300":        
			description="get state of main contact";
			variable="state";
			number=Integer.parseInt(high,16);
			if (number==0) 
			{
				dimension="OFF";
			}
			else 
			{
				dimension="ON";
			}
		break;

		case "7400":       
			description="enable PIN FAILURE_FF";
		break;

		case "7500":        
			description="disable PIN FAILURE_FF";
		break;

		case "7600":        
			description="get state of FAULT pin";
			variable="state";
			number=Integer.parseInt(high,16);
			if (number==0)
			{
				dimension="Pin low";
			}
			else 
			{
				dimension="Pin high";
			}
		break;

		case "7800":      
			description="switch working light";
			variable="duty cycle";
			number=Integer.parseInt(high,16);
			break;

		case "8000":      
			description="get sensor ID of gyro";
			variable="sensor ID";
			number=Integer.parseInt(low,16);
			break;

		case "8100":      
			description="get measured gyro-value";
			variable="raw value";
			number=Integer.parseInt(receive,16);
			if (number>32768)
			{
				number=number-65536;
			}
			dimension="";
		break;

		case "8200":       
			description="save gyro offset	";
			variable="raw offset";
			number=Integer.parseInt(receive,16);
			if (number>32768)
			{
				number=number-65536;
			}
			dimension="";
		break;

		case "8300":        
			description="start min max acquisition (with reset min max values before)";
			dimension="";
		break;

		case "8400":        
			description="stop min max acquisition and reset min max values";
			dimension="";
		break;

		case "8401":        
			description="stop min max acquisition and request min value";
			variable="gyro min value";
			number=Integer.parseInt(receive,16);
			dimension="";
		break;

		case "8402":       
			description="stop min max acquisition and return max value";
			variable="gyro max value";
			number=Integer.parseInt(receive,16);
			dimension="";
		break;

		case "9100":         
			description="get Torque setting position";
			variable="Torque Setting Position";
			number=Integer.parseInt(low,16);
		break;

		case "9200":         
			description="calculate Torque setting offset";
			variable="Selected Setting Position";
			numberusedto9200=Integer.parseInt(sendDatalow,16);
			number=Integer.parseInt(low,16);

			switch (number)
			{
			case 1:  
				dimension="Succeed!";
			break;
			case 0:  
				dimension="Fail!";
			break;
			default:
			break;
			}
			return description+","+variable+" is "+numberusedto9200+",Success information: "+dimension;
			
		case "9300":         
			description="set Torque setting offset (reset value yyzz = 8000)";
			variable="torque setting offset";
			number=Integer.parseInt(receive,16);
			dimension="";
		break;

		case "9400":        
			description="set Data Channel 0 value";
			variable="Data Channel 0";
			number=Integer.parseInt(receive,16);
			dimension="";
		break;

		case "9500":       
			description="set Data Channel 1 value";
			variable="Data Channel 1";
			number=Integer.parseInt(receive,16);
			dimension="";
		break;

		case "0101":         
			description="switch gearbox LED on";
		break;

		case "0201":         
			description="switch gearbox LED off";
		break;

		case "0301":         
			description="switch torque LED on";
		break;

		case "0401":        
			description="switch torque LED off";
		break;

		case "1001":         
			description="get voltage of motor NTC";
			variable="mean value of Motor NTC voltage";
			number=Integer.parseInt(receive,16);
			dimension="mV";
		break;

		case "10B1":    
			description="get calculated motor-temperature";
			variable="temperature";
			number=Integer.parseInt(receive,16);
			if (number>128) {
				number=number-256;
			}
			dimension="℃";
		break;

		case "2001":      
			description="get selected gear";
			variable="number of selected gear";
			number=Integer.parseInt(receive,16);
		break;


		case "F100":      
			description="mark test as passed";
			variable="test number";
			number=Integer.parseInt(receive,16);
		break;
		
		case "F000":      
			description="mark test as failed";
			variable="test number";
			number=Integer.parseInt(receive,16);
		break;

		case "3001":         
			description="get Data Channel values,";
			int tempnum=Integer.parseInt(usedin3001,16);
			number=Integer.parseInt(receive,16);
			return description+"Data Channel 0 is "+tempnum+", "+"Data Channel 1 is "+number;			
			
		default:
			if (thewholeAddress.equals("4001")||thewholeAddress.equals("4101")) 
			{
				return "set lcd channel";
			}			
			
			if (labelposition.equals("06")) 
			{
				return "read data";
			}
			else if (labelposition.equals("07")) 
			{
				return "write data";
			}
			else if (labelposition.equals("DF")) 
			{
				if (low.equals("5a"))
				{
					return "unlock";
				}
				else
				{
					return "lock";
				}
			}
			else 
			{
				return "can't find";
			}
		}

		if (variable.equals("")) //如果没有数据值，直接描述就ok
		{
			total=description;
		}
		else //否则按固定格式输出
		{
			if (number1!=0) 
			{
				total=description+","+variable+" is "+update+number1+dimension;
			}
			else
			{
				if (thewholeAddress.equals("7300")||thewholeAddress.equals("7600"))
				{
					total=description+","+variable+" is "+update+dimension;
				}
				else 
				{
					total=description+","+variable+" is "+update+number+dimension;
				}
			}
		}
		return "\n"+total;
	}
	
	
	public static void setlistPort()
	{  
		CommPortIdentifier cpid;  
		Enumeration en = CommPortIdentifier.getPortIdentifiers(); 
		while(en.hasMoreElements())
		{  
			cpid = (CommPortIdentifier)en.nextElement();  
			if(cpid.getPortType() == CommPortIdentifier.PORT_SERIAL)
			{  
				comvalue+=cpid.getName() + "\r\n,";
			}  
		}  
	}  

	//发送给PLC的基础函数，应该用不到
	public static void sendPLC(String s)
	{
		s=s.replace(" ", "");
		byte[] bs=new byte[]{0x00,0x00,0x00,0x00,0x00,0x00};
		byte[] bs1=hexStringToBytes(s);
		bs[5]=(byte) (bs1[0]^bs1[1]^bs1[2]^bs1[3]^bs1[4]);
		bs[0]=bs1[0];
		bs[1]=bs1[1];
		bs[2]=bs1[2];
		bs[3]=bs1[3];
		bs[4]=bs1[4];
		s=bytesToHexStringWithBlanket(bs);
		plcCom.editArea.setText(s);
		plcCom.jbtSendData.doClick();
	}

	//发送给PLC两个字节的基础函数
	public static void sendPLCwithTwoBytes(String s)
	{
		s=s.replace(" ", "");
		byte[] bs1=hexStringToBytes(s);
		s=bytesToHexStringWithBlanket(bs1);
		Frame.report.setText(Frame.report.getText()+"\n"+"PC to PLC: "+s.toUpperCase());
		plcCom.editArea.setText(s);
		plcCom.jbtSendData.doClick();
	}

	//发送给设备的基础函数
	public static void sendDEVICE(String s)
	{
		s=s.replace(" ", "");
		byte[] bs=new byte[]{0x00,0x00,0x00,0x00,0x00,0x00};
		byte[] bs1=hexStringToBytes(s);
		bs[5]=(byte) (bs1[0]^bs1[1]^bs1[2]^bs1[3]^bs1[4]);
		bs[0]=bs1[0];
		bs[1]=bs1[1];
		bs[2]=bs1[2];
		bs[3]=bs1[3];
		bs[4]=bs1[4];
		s=bytesToHexStringWithBlanket(bs);//将数据转化成下位机数据能够识别的格式
		Frame.report.setText(Frame.report.getText()+"\n"+"PC to Device: "+s.toUpperCase());
		deviceCom.editArea.setText(s);
		deviceCom.jbtSendData.doClick();
	}
	
	//判断是不是偶数
	public static boolean isOdd(int i)
	{
		return (i%2)!=0;
	}

	//将字符串转换成字节
	public static byte[] hexStringToBytes(String hexString)
	{
		if (isOdd(hexString.length())) 
		{
			hexString="0"+hexString;
		}
		if (hexString == null || hexString.equals(""))
		{
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) 
		{
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}


	private static byte charToByte(char c)
	{
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static String bytesToHexString(byte[] src)
	{
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) 
		{
			return null;
		}
		for (int i = 0; i < src.length; i++)
		{
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) 
			{
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	public static String bytesToHexStringWithBlanket(byte[] src)
	{
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0)
		{
			return null;
		}
		for (int i = 0; i < src.length; i++)
		{
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);//将十六进制转化成字符型
			if (hv.length() < 2)
			{
				stringBuilder.append(0);
			}
			stringBuilder.append(hv).append(" ");//添加空格
		}
		return stringBuilder.toString().trim();
	}

	
	public Frame() 
	{
		setTitle("SF(H)6 tool function test");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 1280, 768); 
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel_4 = new JPanel();
		contentPane.add(panel_4, BorderLayout.CENTER);
		panel_4.setLayout(new GridLayout(1, 0, 0, 0));

		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		panel_4.add(panel_5);

		JPanel panel_6 = new JPanel();
		
		JLabel lblDatabaseOperation = new JLabel("DataBase Operation/\u6570\u636E\u5E93\u64CD\u4F5C");
		lblDatabaseOperation.setOpaque(true);
		lblDatabaseOperation.setHorizontalAlignment(SwingConstants.CENTER);
		lblDatabaseOperation.setForeground(Color.BLACK);
		lblDatabaseOperation.setFont(new Font("微软雅黑", Font.BOLD, 16));
		lblDatabaseOperation.setBackground(Color.CYAN);
		JLabel lblNewLabel = new JLabel("\u5F53\u524D\u6570\u636E\u5E93\uFF1A");
		lblNewLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));

		dataBaseUsed = new JTextField(txt2String(new File("."+File.separator+"config.txt")).trim());//从config文本中读到access路径
		dataBaseUsed.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		dataBaseUsed.setColumns(10);

		JButton btnCreat = new JButton("\u751F\u6210\u6570\u636E\u5E93");
		btnCreat.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0)
			{
				new Thread(new Runnable()
				{
					public void run() 
					{
						File fileOld = new File("."+File.separator+"newTestFile"+File.separator+"test1.accdb");  
						File fileNew = new File("."+File.separator+Frame.dataBaseUsed.getText());
						if(fileOld.exists()&&!fileNew.exists())
						{  
							try
							{  
								FileInputStream fis = new FileInputStream(fileOld);  
								FileOutputStream fos = new FileOutputStream(fileNew);  
								int read = 0;  
								while ((read = fis.read()) != -1) 
								{  
									fos.write(read);  
									fos.flush();  
								}  
								fos.close();  
								fis.close();  
							} 
							catch (FileNotFoundException e)
							{  
								e.printStackTrace();  
							} 
							catch (IOException e) 
							{  
								e.printStackTrace();  
							}  
						}
						try
						{
							BufferedWriter writer = new BufferedWriter(new FileWriter( new File("."+File.separator+"config.txt")  ));

							writer.write(Frame.dataBaseUsed.getText().trim());

							writer.close();
						}						
						catch(Exception e)
						{
							
						}
					}
				}).start();
				JOptionPane.showMessageDialog(null, "成功生成新数据库文件！", "提示框", JOptionPane.DEFAULT_OPTION);
			}
		});
		
		btnCreat.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		GroupLayout gl_panel_6 = new GroupLayout(panel_6);
		gl_panel_6.setHorizontalGroup(
			gl_panel_6.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_6.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_6.createParallelGroup(Alignment.LEADING)
						.addComponent(lblDatabaseOperation, GroupLayout.PREFERRED_SIZE, 374, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_panel_6.createSequentialGroup()
							.addComponent(lblNewLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(dataBaseUsed, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(btnCreat, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGap(3)))
					.addContainerGap())
		);
		gl_panel_6.setVerticalGroup(
			gl_panel_6.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_panel_6.createSequentialGroup()
					.addGap(8)
					.addComponent(lblDatabaseOperation, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel_6.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(btnCreat, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
						.addComponent(dataBaseUsed, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(144, Short.MAX_VALUE))
		);
		panel_6.setLayout(gl_panel_6);

		JPanel panel_7 = new JPanel();
		JLabel label_7 = new JLabel("Preparations/运行程序前准备");
		label_7.setOpaque(true);
		label_7.setHorizontalAlignment(SwingConstants.CENTER);
		label_7.setForeground(Color.BLACK);
		label_7.setFont(new Font("微软雅黑", Font.BOLD, 16));
		label_7.setBackground(Color.CYAN);

		JTextArea txtrpleaseChooseThe_1 = new JTextArea();
		txtrpleaseChooseThe_1.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		txtrpleaseChooseThe_1.setText("2.Please choose the device COM:\r\n  \u8BF7\u9009\u62E9\u4E0E\u7535\u94BB\u76F8\u8FDE\u63A5\u7684COM");
		txtrpleaseChooseThe_1.setEditable(false);
		txtrpleaseChooseThe_1.setBackground(Color.ORANGE);

		JTextArea txtrpleaseChooseThe = new JTextArea();
		txtrpleaseChooseThe.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		txtrpleaseChooseThe.setText("1.Please choose the PLC COM:\r\n  \u8BF7\u9009\u62E9\u4E0EPLC\u76F8\u8FDE\u63A5\u7684COM");
		txtrpleaseChooseThe.setEditable(false);
		txtrpleaseChooseThe.setBackground(Color.ORANGE);

		comboBoxDEVICE = new JComboBox();
		comboBoxDEVICE.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		comboBoxDEVICE.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e) 
			{
				DEVICE_COM=(String) comboBoxDEVICE.getSelectedItem();
			}
		});

		comboBoxPLC = new JComboBox();
		comboBoxPLC.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		comboBoxPLC.addItemListener(new ItemListener() 
		{
			public void itemStateChanged(ItemEvent e) 
			{
				PLC_COM=(String) comboBoxPLC.getSelectedItem();
			}
		});

		JButton btnOpenPort = new JButton("Open Port/\u6253\u5F00\u4E32\u53E3");
		btnOpenPort.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		btnOpenPort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnOpenPort.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				report.setText("Results:");
				deviceCom.com=DEVICE_COM;
				plcCom.com=PLC_COM;
				deviceCom.radioButton.setSelected(true);
				plcCom.radioButton.setSelected(true);
				deviceCom.jbtOpenPort.doClick();
				plcCom.jbtOpenPort.doClick();
				
				
				//如果需要启动验证码就反注释
//				File LicenseFile = new File("."+File.separator+"license.txt");
//				if(LicenseFile.isFile() && LicenseFile.exists())
//				{
//					try 
//					{
//						InputStreamReader LicenseRead = new InputStreamReader(new FileInputStream(LicenseFile));
//						BufferedReader LicenseBuff = new BufferedReader(LicenseRead);
//						String License = "";
//						if((License = LicenseBuff.readLine()) != null)
//						{
//							if(GetLocalMac.getMacAddress(GetLocalMac.getIpAddress()).equals(LicenseAlgorithm.ConCodetoMac(LicenseAlgorithm.ConCodeandActCode(License))))
//							{
//								report.setText("Results:");
//								deviceCom.com=DEVICE_COM;
//								plcCom.com=PLC_COM;
//								deviceCom.radioButton.setSelected(true);
//								plcCom.radioButton.setSelected(true);
//								deviceCom.jbtOpenPort.doClick();
//								plcCom.jbtOpenPort.doClick();	
//							}
//							else
//							{
//								report.setText(Frame.report.getText()+"\n"+"License does not exist or is wrong. Please get and enter the correct License！1");
//							}
//						}
//						else
//						{
//							report.setText("License does not exist or is wrong. Please get and enter the correct License！2");
//						}
//						LicenseBuff.close();
//					} 
//					catch (FileNotFoundException e1) 
//					{
//						report.setText("License does not exist or is wrong. Please get and enter the correct License！3");
//					} 
//					catch (IOException e1) 
//					{
//						report.setText("License does not exist or is wrong. Please get and enter the correct License！4");
//					}				
//				}
//				else
//				{
//					report.setText("License does not exist or is wrong. Please get and enter the correct License！5");
//				}							
			}
		});
		
		GroupLayout gl_panel_7 = new GroupLayout(panel_7);
		gl_panel_7.setHorizontalGroup(
			gl_panel_7.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel_7.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_7.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnOpenPort, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
						.addComponent(label_7, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
						.addGroup(Alignment.LEADING, gl_panel_7.createSequentialGroup()
							.addGroup(gl_panel_7.createParallelGroup(Alignment.LEADING, false)
								.addComponent(txtrpleaseChooseThe, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(txtrpleaseChooseThe_1, GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel_7.createParallelGroup(Alignment.TRAILING)
								.addComponent(comboBoxPLC, 0, 116, Short.MAX_VALUE)
								.addComponent(comboBoxDEVICE, Alignment.LEADING, 0, 116, Short.MAX_VALUE))))
					.addContainerGap())
		);
		gl_panel_7.setVerticalGroup(
			gl_panel_7.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_7.createSequentialGroup()
					.addContainerGap()
					.addComponent(label_7, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addGroup(gl_panel_7.createParallelGroup(Alignment.LEADING)
						.addComponent(txtrpleaseChooseThe, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
						.addComponent(comboBoxPLC, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
					.addGap(6)
					.addGroup(gl_panel_7.createParallelGroup(Alignment.LEADING)
						.addComponent(txtrpleaseChooseThe_1, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
						.addComponent(comboBoxDEVICE, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnOpenPort, GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panel_7.linkSize(SwingConstants.VERTICAL, new Component[] {comboBoxDEVICE, comboBoxPLC});
		gl_panel_7.linkSize(SwingConstants.VERTICAL, new Component[] {txtrpleaseChooseThe_1, txtrpleaseChooseThe});
		gl_panel_7.linkSize(SwingConstants.HORIZONTAL, new Component[] {txtrpleaseChooseThe_1, txtrpleaseChooseThe});
		panel_7.setLayout(gl_panel_7);
		
		JPanel panel = new JPanel();
		GroupLayout gl_panel_5 = new GroupLayout(panel_5);
		gl_panel_5.setHorizontalGroup(
			gl_panel_5.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_5.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_5.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(panel_7, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(panel_6, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_panel_5.setVerticalGroup(
			gl_panel_5.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_5.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel_7, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 241, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(panel_6, GroupLayout.PREFERRED_SIZE, 229, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		gl_panel_5.linkSize(SwingConstants.HORIZONTAL, new Component[] {panel_6, panel_7});
		
		JLabel lblSetParameter = new JLabel("Set Parameter/\u53C2\u6570\u8BBE\u7F6E");
		lblSetParameter.setOpaque(true);
		lblSetParameter.setHorizontalAlignment(SwingConstants.CENTER);
		lblSetParameter.setForeground(Color.BLACK);
		lblSetParameter.setFont(new Font("微软雅黑", Font.BOLD, 16));
		lblSetParameter.setBackground(Color.CYAN);
		
		OffsetCh0 = new JTextField();
		OffsetCh0.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		OffsetCh0.setText("42000");
		OffsetCh0.setColumns(10);
		
		JLabel lblOffsetch = new JLabel("Offset_Ch0\uFF1A");
		lblOffsetch.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		
		OffsetCh1 = new JTextField();
		OffsetCh1.setText("42000");
		OffsetCh1.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		OffsetCh1.setColumns(10);
		
		JLabel lblOffsetch_1 = new JLabel("Offset_Ch1\uFF1A");
		lblOffsetch_1.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		
		JLabel lblVariationch = new JLabel("Variation_Ch0\uFF1A");
		lblVariationch.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		
		VariationCh0 = new JTextField();
		VariationCh0.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		VariationCh0.setText("1732");
		VariationCh0.setColumns(10);
		
		JLabel lblVariationch_1 = new JLabel("Variation_Ch1\uFF1A");
		lblVariationch_1.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		
		VariationCh1 = new JTextField();
		VariationCh1.setText("1220");
		VariationCh1.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		VariationCh1.setColumns(10);
		
		JLabel lblCoefficient = new JLabel("Coefficient\uFF1A");
		lblCoefficient.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		
		Coefficient = new JTextField();
		Coefficient.setText("2738");
		Coefficient.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		Coefficient.setColumns(10);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblSetParameter, GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
						.addGroup(gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(lblOffsetch)
								.addComponent(lblVariationch, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblVariationch_1, GroupLayout.PREFERRED_SIZE, 129, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblOffsetch_1, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblCoefficient, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
								.addComponent(VariationCh1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
								.addComponent(OffsetCh0, GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
								.addComponent(VariationCh0, GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
								.addComponent(OffsetCh1, GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
								.addComponent(Coefficient, GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE))))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblSetParameter, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblOffsetch, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
						.addComponent(OffsetCh0, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE))
					.addGap(9)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(OffsetCh1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblOffsetch_1, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblVariationch, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
						.addComponent(VariationCh0, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addGap(8)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(VariationCh1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblVariationch_1, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(Coefficient, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblCoefficient, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_panel.linkSize(SwingConstants.VERTICAL, new Component[] {OffsetCh0, OffsetCh1, VariationCh0, VariationCh1, Coefficient});
		panel.setLayout(gl_panel);
		panel_5.setLayout(gl_panel_5);
		JPanel panel_8 = new JPanel();
		panel_8.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		panel_4.add(panel_8);
		JPanel panel_10 = new JPanel();

		step2 = new JLabel("Step2 torque2/ 2\u6863\u4F4D\u7F6E\u786E\u8BA4");
		step2.setOpaque(true);
		step2.setHorizontalAlignment(SwingConstants.LEFT);
		step2.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		step2.setBorder(new BevelBorder(BevelBorder.RAISED, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN));
		step2.setBackground(Color.YELLOW);
		
		step19 = new JLabel("Step19 read Hall segment time/ \u8BFB\u53D6\u970D\u5C14\u6BB5\u65F6\u95F4");
		step19.setOpaque(true);
		step19.setHorizontalAlignment(SwingConstants.LEFT);
		step19.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		step19.setBorder(new BevelBorder(BevelBorder.RAISED, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN));
		step19.setBackground(Color.YELLOW);
		JPanel panel_11 = new JPanel();

		btnStart = new JButton("Start/开始");//开始按钮
		btnStart.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				stateAreaDemo.setBackground(Color.orange);
				stateAreaDemo.setText("   running...\r\n   检测中……");
				Offset_Ch0 = Integer.parseInt(OffsetCh0.getText());
				Offset_Ch1 = Integer.parseInt(OffsetCh1.getText());
				Variation_Ch0 = Integer.parseInt(VariationCh0.getText());
				Variation_Ch1 = Integer.parseInt(VariationCh1.getText());
				TorqueCoefficinet = Integer.parseInt(Coefficient.getText());
				report.setText(Frame.report.getText()+"\n"+"Offset_Ch0 is "+Integer.toString(Offset_Ch0));
				report.setText(Frame.report.getText()+"\n"+"Offset_Ch1 is "+Integer.toString(Offset_Ch1));
				report.setText(Frame.report.getText()+"\n"+"Variation_Ch0 is "+Integer.toString(Variation_Ch0));
				report.setText(Frame.report.getText()+"\n"+"Variation_Ch1 is "+Integer.toString(Variation_Ch1));
				automatically=true;
				Runnable runnable = new Runnable() 
				{
					public void run() 
					{
						sendDEVICE("06 1C 00 00 00");//读取0x0018数据
						while(ifProceedInter)
						{
							try
							{
								Thread.sleep(5);
							} 
							catch (InterruptedException e)
							{
								e.printStackTrace();
							}
						}
						ifProceedInter=true;


						sendDEVICE("06 1D 00 00 00");//读取0x0019数据
						while(ifProceedInter)
						{
							try 
							{
								Thread.sleep(5);
							} 
							catch (InterruptedException e)
							{
								e.printStackTrace();
							}
						}
						ifProceedInter=true;


						sendDEVICE("06 1E 00 00 00");//读取0x001A数据
						while(ifProceedInter)
						{
							try 
							{
								Thread.sleep(5);
							} 
							catch (InterruptedException e)
							{
								e.printStackTrace();
							}
						}
						ifProceedInter=true;


						sendDEVICE("06 1F 00 00 00");//读取0x001B数据
						while(ifProceedInter)
						{
							try
							{
								Thread.sleep(5);
							}
							catch (InterruptedException e) 
							{
								e.printStackTrace();
							}
						}
						ifProceedInter=true;
						
						dbCMS=RXTXcomm.CMS_SerialNumber;
						dbCMS=Integer.toString(deviceCom.hexToDecimal(dbCMS));//16进制转成10进制
						switch (dbCMS.length())
						{
						case 7:
							dbCMS="00"+dbCMS;
							break;
						case 8:
							dbCMS="0"+dbCMS;
							break;
						default:
							break;
						}
						readCMS=false;
						
						testAccessConnect.addDate();
						testAccessConnect.updateType="序列号";
						testAccessConnect.updateTypeValue=dbCMS;
						testAccessConnect.updataLastLine();

						sendPLCwithTwoBytes("A0 00");//准备就绪
						for (int i = 0; i < 300; i++) //循环45次,这个次数要随着循环次数的改变而改变
						{
							while(ifProceed)//如果再进程中，线程挂起10ms，等待PLC处理信息，因为step递进都是通过PLC决定的，因此就相当于过程停止
							{
								try 
								{
									Thread.sleep(5);
								}
								catch (InterruptedException e) 
								{

									e.printStackTrace();
								}
							}
							report.setText(Frame.report.getText()+"\n"+"【"+Integer.toString(step)+"】");
							switch (step) 
							{
							case 101:								
								TempInstruction = codedtoHexString(Offset_Ch0);
								Instruction = "f0 40 01 "+TempInstruction.substring(2,4)+" "+TempInstruction.substring(0,2);
								sendDEVICE(Instruction);
								while(ifProceedInter)//等待Device回复和处理信息，这个相当于中断
								{
									try
									{
										Thread.sleep(5);
									} 
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceed=true;
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("d4 ff");
									testAccessConnect.updateType="预前校正";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step0.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;//跳到第103步
									ifProceed=false;
								}
								break;
								
							case 102:
								TempInstruction = codedtoHexString(Offset_Ch1);
								Instruction = "f0 41 01 "+TempInstruction.substring(2,4)+" "+TempInstruction.substring(0,2);
								sendDEVICE(Instruction);
								while(ifProceedInter)//等待Device回复和处理信息，这个相当于中断
								{
									try
									{
										Thread.sleep(5);
									} 
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceed=true;
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("d4 ff");
									testAccessConnect.updateType="预前校正";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step0.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;//跳到第54步
									ifProceed=false;
								}
								break;
								
							case 103:
								ifProceed=true;
								CircleCnt+= 1;								
								Frame.report.setText(Frame.report.getText()+"\n"+"Attention:ESC Precalibration Circle "+Integer.toString(CircleCnt) + " times");
								if(CircleCnt < 5)
								{
									sendDEVICE("f0 30 01 00 00");//calculate torque setting offset 0d代表13档									
									while(ifProceedInter)//等待Device回复和处理信息，这个相当于中断
									{
										try
										{
											Thread.sleep(5);
										} 
										catch (InterruptedException e) 
										{
											e.printStackTrace();
										}
									}
									ifProceedInter=true;
									if (RXTXcomm.stepFailed)
									{
										sendPLCwithTwoBytes("d4 ff");
										testAccessConnect.updateType="预前校正";
										testAccessConnect.updateTypeValue="NG";
										testAccessConnect.updataLastLine();
	
										SwingUtilities.invokeLater(new Runnable() 
										{
											public void run()
											{
												step0.setBackground(Color.RED);
											}
										});
									}
									else
									{
										step=step+1;//跳到第102步
										ifProceed=false;
									}
								}
								else
								{
									sendPLCwithTwoBytes("d4 ff");
									testAccessConnect.updateType="预前校正";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step0.setBackground(Color.RED);
										}
									});
								}
								break;
								
							case 104:
								ifProceed=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("d4 ff");
									testAccessConnect.updateType="预前校正";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step0.setBackground(Color.RED);
										}
									});
								}
								else
								{
									if(Channel_0_1 == 4095)
									{
										Saturation_0 = true;
										Offset_Ch0 = Offset_Ch0 + 4000;
										Frame.report.setText(Frame.report.getText()+"\n"+"Offset_Ch0 = Offset_Ch0 + 4000 =  "+Integer.toString(Offset_Ch0));
									}									
									step=step+1;//跳到第102步
									ifProceed=false;
								}
								break;
								
							case 105:
								ifProceed=true;
								if(Channel_1_1 == 4095)
								{
									Saturation_1 = true;
									Offset_Ch1 = Offset_Ch1 + 4000;
									Frame.report.setText(Frame.report.getText()+"\n"+"Offset_Ch1 = Offset_Ch1 + 4000 =  "+Integer.toString(Offset_Ch1));
								}									
								step=step+1;//跳到第102步
								ifProceed=false;
								break;
								
							case 106:
								ifProceed=true;
								if(Channel_0_1 == 0)
								{
									Saturation_0 = true;
									Offset_Ch0 = Offset_Ch0 - 4000;
									Frame.report.setText(Frame.report.getText()+"\n"+"Offset_Ch0 = Offset_Ch0 - 4000 =  "+Integer.toString(Offset_Ch0));
								}									
								step=step+1;//跳到第102步
								ifProceed=false;
								break;
								
							case 107:
								ifProceed=true;
								if(Channel_1_1 == 0)
								{
									Saturation_1 = true;
									Offset_Ch1 = Offset_Ch1 - 4000;
									Frame.report.setText(Frame.report.getText()+"\n"+"Offset_Ch1 = Offset_Ch1 - 4000 =  "+Integer.toString(Offset_Ch1));
								}									
								step=step+1;//跳到第102步
								ifProceed=false;
								break;
								
							case 108:
								ifProceed=true;
								if(Saturation_0 == false)
								{
									Middle_Point_Ch0 = Channel_0_1 + Variation_Ch0/2;									
									Delta_Offset_Ch0 = 2048 - Middle_Point_Ch0;
									Offset_Ch0 = Offset_Ch0 - Delta_Offset_Ch0;
									Frame.report.setText(Frame.report.getText()+"\n"+"Middle_Point_Ch0 = Channel_0_1 + Variation_Ch0/2 =  "+Integer.toString(Middle_Point_Ch0));
									Frame.report.setText(Frame.report.getText()+"\n"+"Delta_Offset_Ch0 = 2048 - Middle_Point_Ch0 =  "+Integer.toString(Delta_Offset_Ch0));
									Frame.report.setText(Frame.report.getText()+"\n"+"Offset_Ch0 = Offset_Ch0 - Delta_Offset_Ch0 =  "+Integer.toString(Offset_Ch0));
								}
								step=step+1;
								ifProceed=false;
								break;
								
							case 109:
								ifProceed=true;
								if(Saturation_1 == false)
								{
									Middle_Point_Ch1 = Channel_1_1 - Variation_Ch1/2;
									Delta_Offset_Ch1 = 2048 - Middle_Point_Ch1;
									Offset_Ch1 = Offset_Ch1 - Delta_Offset_Ch1;
									Frame.report.setText(Frame.report.getText()+"\n"+"Middle_Point_Ch1 = Channel_1_1 - Variation_Ch1/2 =  "+Integer.toString(Middle_Point_Ch1));
									Frame.report.setText(Frame.report.getText()+"\n"+"Delta_Offset_Ch1 = 2048 - Middle_Point_Ch1 =  "+Integer.toString(Delta_Offset_Ch1));
									Frame.report.setText(Frame.report.getText()+"\n"+"Offset_Ch1 = Offset_Ch1 - Delta_Offset_Ch1 =  "+Integer.toString(Offset_Ch1));
								}
								step=step+1;
								ifProceed=false;
								break;
								
							case 110:
								ifProceed=true;
								if(Offset_Ch0>63000 || Offset_Ch0<2000 || Offset_Ch1>63000 || Offset_Ch1<2000)
								{
									sendPLCwithTwoBytes("d4 ff");
									report.setText(Frame.report.getText()+"\n"+"ERROR: THE TOOL HAS A FAILURE ");
									testAccessConnect.updateType="预前校正";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step0.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;
									ifProceed=false;
								}
								break;
								
							case 111:
								TempInstruction = codedtoHexString(Offset_Ch0);
								Instruction = "f0 40 01 "+TempInstruction.substring(2,4)+" "+TempInstruction.substring(0,2);
								sendDEVICE(Instruction);
								while(ifProceedInter)//等待Device回复和处理信息，这个相当于中断
								{
									try
									{
										Thread.sleep(5);
									} 
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceed=true;
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("d4 ff");
									testAccessConnect.updateType="预前校正";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step0.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;//跳到第103步
									ifProceed=false;
								}
								break;
								
							case 112:
								TempInstruction = codedtoHexString(Offset_Ch1);
								Instruction = "f0 41 01 "+TempInstruction.substring(2,4)+" "+TempInstruction.substring(0,2);
								sendDEVICE(Instruction);
								while(ifProceedInter)//等待Device回复和处理信息，这个相当于中断
								{
									try
									{
										Thread.sleep(5);
									} 
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceed=true;
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("d4 ff");
									testAccessConnect.updateType="预前校正";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step0.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=500;//跳到第500步,等待回复f0 29
									ifProceed=false;
								}
								break;
								
							case 500:
								while(ifProceedInter)//等待Device回复和处理信息，这个相当于中断
								{
									try
									{
										Thread.sleep(5);
									} 
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceed=true;
								ifProceedInter=true;
								if(RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("d4 ff");
									testAccessConnect.updateType="预前校正";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step0.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=113;
									ifProceed=false;
								}
								break;
								
							case 113:
								ifProceed=true;
								report.setText(Frame.report.getText()+"\n"+"Saturation_0 = "+String.valueOf(Saturation_0)+" and "+"Saturation_1 = "+String.valueOf(Saturation_1));								
								if(Saturation_0 == false && Saturation_1 == false)
								{
									sendPLCwithTwoBytes("d4 02");
								}
								else
								{
									sendPLCwithTwoBytes("d4 00");
								}								
								break;
								
							case 114:
								sendDEVICE("f0 30 01 00 00");//calculate torque setting offset 0d代表13档
								while(ifProceedInter)//等待Device回复和处理信息，这个相当于中断
								{
									try
									{
										Thread.sleep(5);
									} 
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceed=true;
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("d5 ff");
									testAccessConnect.updateType="预前校正";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step0.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;//跳到第115步
									ifProceed=false;
								}
								break;	
								
							case 115:
								ifProceed=true;
								if(Channel_0_DH == 4095)
								{
									sendPLCwithTwoBytes("d5 ff");
									report.setText(Frame.report.getText()+"\n"+"ERROR: THE TOOL HAS A FAILURE ");
									testAccessConnect.updateType="预前校正";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step0.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;//跳到第102步
									ifProceed=false;
								}
								break;	
								
							case 116:
								ifProceed=true;
								if(Channel_1_DH == 4095)
								{
									sendPLCwithTwoBytes("d5 ff");
									report.setText(Frame.report.getText()+"\n"+"ERROR: THE TOOL HAS A FAILURE ");
									testAccessConnect.updateType="预前校正";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step0.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;//跳到第102步
									ifProceed=false;
								}
								break;
								
							case 117:
								ifProceed=true;
								if(Channel_0_DH == 0)
								{
									sendPLCwithTwoBytes("d5 ff");
									report.setText(Frame.report.getText()+"\n"+"ERROR: THE TOOL HAS A FAILURE ");
									testAccessConnect.updateType="预前校正";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step0.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;//跳到第102步
									ifProceed=false;
								}
								break;
								
							case 118:
								ifProceed=true;
								if(Channel_1_DH == 0)
								{
									sendPLCwithTwoBytes("d5 ff");
									report.setText(Frame.report.getText()+"\n"+"ERROR: THE TOOL HAS A FAILURE ");
									testAccessConnect.updateType="预前校正";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step0.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;//跳到第102步
									ifProceed=false;
								}
								break;
								
							case 119:
								ifProceed=true;
								Variation_Ch0 = Math.abs(Channel_0_DH - Channel_0_1 - Delta_Offset_Ch0);
								Variation_Ch1 = Math.abs(Channel_1_1 - Channel_1_DH + Delta_Offset_Ch1);
								Middle_Point_Ch0 = Channel_0_DH - Variation_Ch0/2;
								Middle_Point_Ch1 = Channel_1_DH + Variation_Ch1/2;
								Delta_Offset_Ch0 = 2048 - Middle_Point_Ch0;
								Offset_Ch0 = Offset_Ch0 - Delta_Offset_Ch0;
								Delta_Offset_Ch1 = 2048 - Middle_Point_Ch1;
								Offset_Ch1 = Offset_Ch1 - Delta_Offset_Ch1;

								Frame.report.setText(Frame.report.getText()+"\n"+"Variation_Ch0 = absolute(Channel_0_DH - Channel_0_1 - Delta_Offset_Ch0) =  "+Integer.toString(Variation_Ch0));
								Frame.report.setText(Frame.report.getText()+"\n"+"Middle_Point_Ch0 = Channel_0_DH - Variation_Ch0/2; =  "+Integer.toString(Middle_Point_Ch0));
								Frame.report.setText(Frame.report.getText()+"\n"+"Delta_Offset_Ch0 = 2048 - Middle_Point_Ch0 =  "+Integer.toString(Delta_Offset_Ch0));
								Frame.report.setText(Frame.report.getText()+"\n"+"Offset_Ch0 = Offset_Ch0 - Delta_Offset_Ch0 =  "+Integer.toString(Offset_Ch0));
								Frame.report.setText(Frame.report.getText()+"\n"+"Variation_Ch1 = absolute(Channel_1_1 - Channel_1_DH + Delta_Offset_Ch1) =  "+Integer.toString(Variation_Ch1));
								Frame.report.setText(Frame.report.getText()+"\n"+"Middle_Point_Ch1 = Channel_1_DH + Variation_Ch1/2; =  "+Integer.toString(Middle_Point_Ch1));
								Frame.report.setText(Frame.report.getText()+"\n"+"Delta_Offset_Ch1 = 2048 - Middle_Point_Ch1 =  "+Integer.toString(Delta_Offset_Ch1));
								Frame.report.setText(Frame.report.getText()+"\n"+"Offset_Ch1 = Offset_Ch1 - Delta_Offset_Ch1 =  "+Integer.toString(Offset_Ch1));
								
								step = step+1;								
								ifProceed=false;

							case 120:
								TempInstruction = codedtoHexString(Offset_Ch0);
								Instruction = "f0 40 01 "+TempInstruction.substring(2,4)+" "+TempInstruction.substring(0,2);
								sendDEVICE(Instruction);
								while(ifProceedInter)//等待Device回复和处理信息，这个相当于中断
								{
									try
									{
										Thread.sleep(5);
									} 
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceed=true;
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("d5 ff");
									testAccessConnect.updateType="预前校正";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step0.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;//跳到第103步
									ifProceed=false;
								}
								break;

							case 121:
								TempInstruction = codedtoHexString(Offset_Ch1);
								Instruction = "f0 41 01 "+TempInstruction.substring(2,4)+" "+TempInstruction.substring(0,2);
								sendDEVICE(Instruction);
								while(ifProceedInter)//等待Device回复和处理信息，这个相当于中断
								{
									try
									{
										Thread.sleep(5);
									} 
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceed=true;
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("d5 ff");
									testAccessConnect.updateType="预前校正";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step0.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step = 501;//跳到第501步
									ifProceed=false;
								}
							break;
							
							case 501:
								while(ifProceedInter)//等待Device回复和处理信息，这个相当于中断
								{
									try
									{
										Thread.sleep(5);
									} 
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceed=true;
								ifProceedInter=true;
								if(RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("d5 ff");
									testAccessConnect.updateType="预前校正";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step0.setBackground(Color.RED);
										}
									});
								}
								else
								{
									sendPLCwithTwoBytes("d5 00");
									testAccessConnect.updateType="预前校正";
									testAccessConnect.updateTypeValue="OK";
									testAccessConnect.updataLastLine();
									
									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run() 
										{
											step0.setBackground(Color.GREEN);
										}
									});
									step=0;
									ifProceed=false;
								}
								break;
								
							case 0:
								ifProceed=true;
							break;
								
							case 1:
								sendDEVICE("f0 92 00 0d 00");//calculate torque setting offset 0d代表13档
								while(ifProceedInter)//等待Device回复和处理信息，这个相当于中断
								{
									try
									{
										Thread.sleep(5);
									} 
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceed=true;
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a1 ff");
									testAccessConnect.updateType="离合盖确认";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step1.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+16;//直接跳到第17步
									ifProceed=false;
								}
								break;

							case 3:
								sendDEVICE("f0 91 00 00 00");//get torque setting position
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e)
									{
										e.printStackTrace();
									}
								}
								ifProceed=true;
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a2 ff");
									testAccessConnect.updateType="离合盖确认";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable()
									{
										public void run() 
										{
											step2.setBackground(Color.RED);
										}
									});
								}
								else 
								{
									sendPLCwithTwoBytes("a2 00");
									SwingUtilities.invokeLater(new Runnable()
									{		
										public void run() 
										{
											step2.setBackground(Color.GREEN);
										}
									});
								}
								break;

//							case 3:
//								sendDEVICE("f0 91 00 00 00");
//								while(ifProceedInter){
//									try 
//									{
//										Thread.sleep(10);
//									} 
//									catch (InterruptedException e)
//									{
//										e.printStackTrace();
//									}
//								}
//								ifProceed=true;
//								ifProceedInter=true;
//								if (RXTXcomm.stepFailed)
//								{
//									sendPLCwithTwoBytes("a3 ff");
//									testAccessConnect.updateType="离合盖确认";
//									testAccessConnect.updateTypeValue="NG";
//									testAccessConnect.updataLastLine();
//
//									SwingUtilities.invokeLater(new Runnable() 
//									{
//										public void run() 
//										{
//											step3.setBackground(Color.RED);
//										}
//									});
//								}
//								else 
//								{
//									sendPLCwithTwoBytes("a3 00");
//									SwingUtilities.invokeLater(new Runnable() 
//									{
//										public void run() 
//										{
//											step3.setBackground(Color.GREEN);
//										}
//									});
//								}
//
//								break;

							case 4:
								sendDEVICE("f0 91 00 00 00");
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e)
									{
										e.printStackTrace();
									}
								}
								ifProceed=true;
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a4 ff");

									testAccessConnect.updateType="离合盖确认";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();


									SwingUtilities.invokeLater(new Runnable() 
									{
										@Override
										public void run() 
										{
											step4.setBackground(Color.RED);
										}
									});
								}
								else
								{
									sendPLCwithTwoBytes("a4 00");
									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step4.setBackground(Color.GREEN);
										}
									});
								}

								break;

							case 5:
								sendDEVICE("f0 91 00 00 00");
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									} 
									catch (InterruptedException e) 
									{

										e.printStackTrace();
									}
								}
								ifProceed=true;
								ifProceedInter=true;
								if (RXTXcomm.stepFailed) 
								{
									sendPLCwithTwoBytes("a5 ff");

									testAccessConnect.updateType="离合盖确认";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();
									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{

											step5.setBackground(Color.RED);
										}
									});
								}
								else
								{
									sendPLCwithTwoBytes("a5 00");
									testAccessConnect.updateType="离合盖确认";
									testAccessConnect.updateTypeValue="OK";
									testAccessConnect.updataLastLine();
									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step5.setBackground(Color.GREEN);
										}
									});
								}

								break;

							case 6:
								sendDEVICE("f0 20 01 00 00");
								while(ifProceedInter)
								{
									try
									{
										Thread.sleep(5);
									} catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceed=true;
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a6 ff");
									testAccessConnect.updateType="变速板确认";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable()
									{
										public void run()
										{
											step6.setBackground(Color.RED);
										}
									});
								}
								else
								{
									sendPLCwithTwoBytes("a6 00");
									SwingUtilities.invokeLater(new Runnable()
									{
										public void run() 
										{
											step6.setBackground(Color.GREEN);
										}
									});
								}

								break;

							case 7:
								sendDEVICE("f0 20 01 00 00");
								while(ifProceedInter){
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e)
									{
										e.printStackTrace();
									}
								}
								ifProceed=true;
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a7 ff");
									testAccessConnect.updateType="变速板确认";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();
									SwingUtilities.invokeLater(new Runnable()
									{
										public void run()
										{
											step7.setBackground(Color.RED);
										}
									});
								}
								else
								{
									sendPLCwithTwoBytes("a7 00");
									RXTXcomm.needCommunicateWithDevice=false;
									testAccessConnect.updateType="变速板确认";
									testAccessConnect.updateTypeValue="OK";
									testAccessConnect.updataLastLine();
									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step7.setBackground(Color.GREEN);
										}
									});
								}

								step+=1;


							case 8:
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a8 ff");
									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step8.setBackground(Color.RED);
										}
									});
								}
								else
								{
									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run() 
										{
											step8.setBackground(Color.GREEN);
										}
									});
								}
								break;
								
								
							case 9:
								ifProceed=true;
								sendPLCwithTwoBytes("a9 00");
								testAccessConnect.updateType="13档力矩";
								db13torque=Stringinsert(db13torque, ".", 2);

								if(db13torque.substring(0,1).equals("0"))
								{
									db13torque=db13torque.substring(1,5);
								}

								testAccessConnect.updateTypeValue=db13torque+"/"+dbValueJudgement;
								testAccessConnect.updataLastLine();
								SwingUtilities.invokeLater(new Runnable()
								{
									public void run()
									{
										step9.setBackground(Color.GREEN);
									}
								});
								break;


							case 10:
								ifProceed=true;
								sendPLCwithTwoBytes("aa 00");
								testAccessConnect.updateType="正转判定";
								testAccessConnect.updateTypeValue=dbForwardTurn;
								testAccessConnect.updataLastLine();
								SwingUtilities.invokeLater(new Runnable() 
								{
									public void run()
									{
										step10.setBackground(Color.GREEN);
									}
								});
								break;

							case 11:
								ifProceed=true;
								sendPLCwithTwoBytes("ab 00");
								testAccessConnect.updateType="低速转速";

								if(dbLowSpeedRPM.substring(0,1).equals("0"))
								{
									dbLowSpeedRPM=dbLowSpeedRPM.substring(1,4);
								}

								testAccessConnect.updateTypeValue=dbLowSpeedRPM+"/"+dbValueJudgement;
								testAccessConnect.updataLastLine();
								SwingUtilities.invokeLater(new Runnable()
								{
									public void run() 
									{
										step11.setBackground(Color.GREEN);
									}
								});
								break;

							case 12:
								ifProceed=true;
								sendPLCwithTwoBytes("ac 00");
								testAccessConnect.updateType="低速电流";
								dbLowSpeedCurrent=Stringinsert(dbLowSpeedCurrent, ".", 2);
								if(dbLowSpeedCurrent.substring(0,1).equals("0"))
								{
									dbLowSpeedCurrent=dbLowSpeedCurrent.substring(1,5);
								}
								testAccessConnect.updateTypeValue=dbLowSpeedCurrent+"/"+dbValueJudgement;
								testAccessConnect.updataLastLine();
								SwingUtilities.invokeLater(new Runnable() 
								{
									public void run()
									{
										step12.setBackground(Color.GREEN);
									}
								});
								break;
								
							case 45:
								ifProceed=true;
								sendPLCwithTwoBytes("d6 00");
								testAccessConnect.updateType="低速电机停止转速";
								testAccessConnect.updateTypeValue="OK";
								testAccessConnect.updataLastLine();
								SwingUtilities.invokeLater(new Runnable() 
								{
									public void run()
									{
										step13.setBackground(Color.GREEN);
									}
								});
								break;

							case 13:
								ifProceed=true;
								sendPLCwithTwoBytes("ad 00");
								testAccessConnect.updateType="高速转速";
								if(dbHighSpeedRPM.substring(0,1).equals("0"))
								{
									dbHighSpeedRPM=dbHighSpeedRPM.substring(1,4);
								}
								testAccessConnect.updateTypeValue=dbHighSpeedRPM+"/"+dbValueJudgement;
								testAccessConnect.updataLastLine();
								SwingUtilities.invokeLater(new Runnable()
								{
									public void run() 
									{
										step14.setBackground(Color.GREEN);
									}
								});
								break;

							case 14:
								ifProceed=true;
								sendPLCwithTwoBytes("ae 00");
								testAccessConnect.updateType="高速电流";
								dbHighSpeedCurrent=Stringinsert(dbHighSpeedCurrent, ".", 2);

								if(dbHighSpeedCurrent.substring(0,1).equals("0"))
								{
									dbHighSpeedCurrent=dbHighSpeedCurrent.substring(1,5);
								}
								testAccessConnect.updateTypeValue=dbHighSpeedCurrent+"/"+dbValueJudgement;
								testAccessConnect.updataLastLine();
								SwingUtilities.invokeLater(new Runnable() 
								{
									public void run() 
									{
										step15.setBackground(Color.GREEN);
									}
								});
								break;
								
							case 46:
								ifProceed=true;
								sendPLCwithTwoBytes("d7 00");
								testAccessConnect.updateType="高速电机停止转速";
								testAccessConnect.updateTypeValue="OK";
								testAccessConnect.updataLastLine();
								SwingUtilities.invokeLater(new Runnable() 
								{
									public void run()
									{
										step16.setBackground(Color.GREEN);
									}
								});
							break;		
							
							case 15:
								ifProceed=true;
								sendPLCwithTwoBytes("af 00");
								testAccessConnect.updateType="LED判定";
								testAccessConnect.updateTypeValue=dbLED;
								testAccessConnect.updataLastLine();
								SwingUtilities.invokeLater(new Runnable()
								{
									public void run()
									{
										step17.setBackground(Color.GREEN);
									}
								});
								break;

							case 16:
								ifProceed=true;
								testAccessConnect.updateType="反转判定";
								testAccessConnect.updateTypeValue=dbReverseTurn;
								testAccessConnect.updataLastLine();
								SwingUtilities.invokeLater(new Runnable() 
								{
									public void run()
									{
										step18.setBackground(Color.GREEN);
									}
								});
								step = step + 4;//直接跳到step20
								ifProceed=false;
								RXTXcomm.needCommunicateWithDevice=true;
								break;
								
							//这里是师兄后来插入的部分	
							case 17:
								ifProceed=true;
								sendDEVICE("f0 30 01 00 00");//get Data Channel values
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									} 
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a1 ff");

									testAccessConnect.updateType="离合盖确认";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run() 
										{
											step1.setBackground(Color.RED);
										}
									});
								}
								else
								{
									sendPLCwithTwoBytes("a1 00");//第一步13档确认完成，请转到1档
								}
								break;
								
							case 18:
								ifProceed=true;
								sendDEVICE("f0 30 01 00 00");//get Data Channel values
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a1 ff");

									testAccessConnect.updateType="离合盖确认";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step1.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;//第18步到第19步在此递进，在这个过程中如果顺利不需要给PLC发消息
									ifProceed=false;
								}
								break;
								
							case 19:
								ifProceed=true;
								RXTXcomm.gain=Math.round(1000*TorqueCoefficinet*1.0/(RXTXcomm.difference1-RXTXcomm.difference13));//计算13档和1档误差
								TempInstruction=codedtoHexString((int)RXTXcomm.gain);
								Instruction="f0 94 00 "+TempInstruction.substring(2, 4)+" "+TempInstruction.substring(0,2);
								sendDEVICE(Instruction);//set Data Channel 0 value
								while(ifProceedInter)
								{
									try
									{
										Thread.sleep(5);
									} 
									catch (InterruptedException e)
									{
										e.printStackTrace();
									}
								}
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a1 ff");

									testAccessConnect.updateType="离合盖确认";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run() 
										{
											step1.setBackground(Color.RED);
										}
									});
								}
								else 
								{
									step=step-18;
									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step1.setBackground(Color.GREEN);
										}
									});
									sendPLCwithTwoBytes("d1 00");//PC校验完成，可以断电重启
								}
								break;
								
							//这里是我后来插入的部分
							case 20:
								ifProceed=true;
								sendDEVICE("06 18 04 00 00");//get Data Channel values
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a8 ff");

									testAccessConnect.updateType="读取霍尔时间段";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step19.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;
									ifProceed=false;
								}
								break;	
								
							case 21:
								ifProceed=true;
								sendDEVICE("06 1A 04 00 00");//get Data Channel values
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a8 ff");

									testAccessConnect.updateType="读取霍尔时间段";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step19.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;
									ifProceed=false;
								}
								break;	

							case 22:
								ifProceed=true;
								sendDEVICE("06 1C 04 00 00");//get Data Channel values
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a8 ff");

									testAccessConnect.updateType="读取霍尔时间段";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step19.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;
									ifProceed=false;
								}
								break;								

							case 23:
								ifProceed=true;
								sendDEVICE("06 1E 04 00 00");//get Data Channel values
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a8 ff");

									testAccessConnect.updateType="读取霍尔时间段";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step19.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;
									ifProceed=false;
								}
								break;								

							case 24:
								ifProceed=true;
								sendDEVICE("06 20 04 00 00");//get Data Channel values
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a8 ff");

									testAccessConnect.updateType="读取霍尔时间段";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step19.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;
									ifProceed=false;
								}
								break;								

							case 25:
								ifProceed=true;
								sendDEVICE("06 22 04 00 00");//get Data Channel values
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a8 ff");

									testAccessConnect.updateType="读取霍尔时间段";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step19.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;
									ifProceed=false;
								}
								break;								

							case 26:
								ifProceed=true;
								sendDEVICE("06 24 04 00 00");//get Data Channel values
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a8 ff");

									testAccessConnect.updateType="读取霍尔时间段";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step19.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;
									ifProceed=false;
								}
								break;
								
							case 27:
								ifProceed=true;
								sendDEVICE("06 26 04 00 00");//get Data Channel values
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a8 ff");

									testAccessConnect.updateType="读取霍尔时间段";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step19.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;
									ifProceed=false;
								}
								break;								
								
							case 28:
								ifProceed=true;
								sendDEVICE("06 28 04 00 00");//get Data Channel values
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a8 ff");

									testAccessConnect.updateType="读取霍尔时间段";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step19.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;
									ifProceed=false;
								}
								break;								
								
							case 29:
								ifProceed=true;
								sendDEVICE("06 2A 04 00 00");//get Data Channel values
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a8 ff");

									testAccessConnect.updateType="读取霍尔时间段";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step19.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;
									ifProceed=false;
								}
								break;								
								
							case 30:
								ifProceed=true;
								sendDEVICE("06 2C 04 00 00");//get Data Channel values
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a8 ff");

									testAccessConnect.updateType="读取霍尔时间段";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step19.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;
									ifProceed=false;
								}
								break;								

							case 31:
								ifProceed=true;
								sendDEVICE("06 2E 04 00 00");//get Data Channel values
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a8 ff");

									testAccessConnect.updateType="读取霍尔时间段";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step19.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;
									ifProceed=false;
								}
								break;								
								
							case 32:
								ifProceed=true;
								sendDEVICE("06 30 04 00 00");//get Data Channel values
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a8 ff");

									testAccessConnect.updateType="读取霍尔时间段";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step19.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;
									ifProceed=false;
								}
								break;								
								
							case 33:
								ifProceed=true;
								sendDEVICE("06 32 04 00 00");//get Data Channel values
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a8 ff");

									testAccessConnect.updateType="读取霍尔时间段";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step19.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;
									ifProceed=false;
								}
								break;								
								
							case 34:
								ifProceed=true;
								sendDEVICE("06 34 04 00 00");//get Data Channel values
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a8 ff");

									testAccessConnect.updateType="读取霍尔时间段";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step19.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;
									ifProceed=false;
								}
								break;								

							case 35:
								ifProceed=true;
								sendDEVICE("06 36 04 00 00");//get Data Channel values
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a8 ff");

									testAccessConnect.updateType="读取霍尔时间段";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step19.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;
									ifProceed=false;
								}
								break;								
								
							case 36:
								ifProceed=true;
								sendDEVICE("06 38 04 00 00");//get Data Channel values
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a8 ff");

									testAccessConnect.updateType="读取霍尔时间段";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step19.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;
									ifProceed=false;
								}
								break;								
								
							case 37:
								ifProceed=true;
								sendDEVICE("06 3A 04 00 00");//get Data Channel values
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a8 ff");

									testAccessConnect.updateType="读取霍尔时间段";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step19.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;
									ifProceed=false;
								}
								break;								
								
							case 38:
								ifProceed=true;
								sendDEVICE("06 3C 04 00 00");//get Data Channel values
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a8 ff");

									testAccessConnect.updateType="读取霍尔时间段";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step19.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;
									ifProceed=false;
								}
								break;								

							case 39:
								ifProceed=true;
								sendDEVICE("06 3E 04 00 00");//get Data Channel values
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a8 ff");

									testAccessConnect.updateType="读取霍尔时间段";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step19.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;
									ifProceed=false;
								}
								break;								
								
							case 40:
								ifProceed=true;
								sendDEVICE("06 40 04 00 00");//get Data Channel values
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a8 ff");

									testAccessConnect.updateType="读取霍尔时间段";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step19.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;
									ifProceed=false;
								}
								break;								
								
							case 41:
								ifProceed=true;
								sendDEVICE("06 42 04 00 00");//get Data Channel values
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a8 ff");

									testAccessConnect.updateType="读取霍尔时间段";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step19.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;
									ifProceed=false;
								}
								break;								
								
							case 42:
								ifProceed=true;
								sendDEVICE("06 44 04 00 00");//get Data Channel values
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a8 ff");

									testAccessConnect.updateType="读取霍尔时间段";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step19.setBackground(Color.RED);
										}
									});
								}
								else
								{
									step=step+1;
									ifProceed=false;
								}
								break;								

							case 43:
								ifProceed=true;
								sendDEVICE("06 46 04 00 00");//get Data Channel values
								while(ifProceedInter)
								{
									try 
									{
										Thread.sleep(5);
									}
									catch (InterruptedException e) 
									{
										e.printStackTrace();
									}
								}
								ifProceedInter=true;
								if (RXTXcomm.stepFailed)
								{
									sendPLCwithTwoBytes("a8 ff");

									testAccessConnect.updateType="读取霍尔时间段";
									testAccessConnect.updateTypeValue="NG";
									testAccessConnect.updataLastLine();

									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run()
										{
											step19.setBackground(Color.RED);											
										}
									});
								}
								else
								{
									step=step+1;
									ifProceed=false;
								}
								break;
								
							case 44:
								ifProceed=true;								
								Collections.sort(RXTXcomm.HallSegmentTime);
								for(int j = 0;j<RXTXcomm.HallSegmentTime.size();j++)
								{
									RXTXcomm.HallSegmentTimeSum += RXTXcomm.HallSegmentTime.get(j);
								}
								RXTXcomm.HallSegmentTimeAverage = RXTXcomm.HallSegmentTimeSum/RXTXcomm.HallSegmentTime.size();									
								RXTXcomm.HallSegmentTimePercentage = ((RXTXcomm.HallSegmentTime.get(10) - RXTXcomm.HallSegmentTimeAverage)+(RXTXcomm.HallSegmentTimeAverage - RXTXcomm.HallSegmentTime.get(0))) / (RXTXcomm.HallSegmentTimeAverage)*100;								
								Frame.report.setText(Frame.report.getText()+"\n"+"The min Hall segment timing is "+ String.valueOf(RXTXcomm.HallSegmentTime.get(0)));
								Frame.report.setText(Frame.report.getText()+"\n"+"The max Hall segment timing is "+ String.valueOf(RXTXcomm.HallSegmentTime.get(10)));
								Frame.report.setText(Frame.report.getText()+"\n"+"The average of Hall segment timing is "+ String.valueOf(RXTXcomm.HallSegmentTimeAverage));
								Frame.report.setText(Frame.report.getText()+"\n"+"The percentage of Hall segment timing is "+ String.valueOf(RXTXcomm.HallSegmentTimePercentage));
								dbHallSegmentTime = String.valueOf(RXTXcomm.HallSegmentTimePercentage);								
								testAccessConnect.updateType="读取霍尔时间段";
								testAccessConnect.updateTypeValue=dbHallSegmentTime;
								testAccessConnect.updataLastLine();
								SwingUtilities.invokeLater(new Runnable() 
								{
									public void run()
									{
										step19.setBackground(Color.GREEN);
										report.setText(Frame.report.getText()+"\n"+"Finish!");
									}
								});
								sendPLCwithTwoBytes("a8 00");
								break;								
								
							default:
								break;
							}
						}
					}
				};
				startRun=new Thread(runnable);
				startRun.start();
			}
		});		
		
		
		btnStart.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		btnStart.setFocusTraversalKeysEnabled(false);
		btnStart.setFocusPainted(false);
		JPanel panel_9 = new JPanel();
		JTextArea stateArea = new JTextArea();
		stateArea.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		stateArea.setText("          Status/\u72B6\u6001 ");
		stateArea.setEditable(false);
		stateArea.setBackground(Color.CYAN);
		stateAreaDemo = new JTextArea();
		stateAreaDemo.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		stateAreaDemo.setText("      Not used/\u672A\u542F\u7528 ");
		stateAreaDemo.setEditable(false);
		stateAreaDemo.setBackground(Color.RED);
		GroupLayout gl_panel_8 = new GroupLayout(panel_8);
		gl_panel_8.setHorizontalGroup(
			gl_panel_8.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_8.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_8.createParallelGroup(Alignment.TRAILING)
						.addComponent(panel_11, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 394, Short.MAX_VALUE)
						.addComponent(panel_9, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(panel_10, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel_8.setVerticalGroup(
			gl_panel_8.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_8.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel_11, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_10, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_9, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		panel_9.setLayout(new GridLayout(0, 2, 0, 0));
		panel_9.add(stateArea);
		panel_9.add(stateAreaDemo);
		panel_10.setLayout(new GridLayout(0, 1, 0, 0));
				
				step0 = new JLabel("Step0 ESC  precalibration/ \u9884\u524D\u6821\u6B63\r\n");
				step0.setOpaque(true);
				step0.setHorizontalAlignment(SwingConstants.LEFT);
				step0.setFont(new Font("微软雅黑", Font.PLAIN, 16));
				step0.setBorder(new BevelBorder(BevelBorder.RAISED, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN));
				step0.setBackground(Color.YELLOW);
				panel_10.add(step0);
		
				step1 = new JLabel("Step1 torque13 and difference/ 13\u6863\u6821\u6B63\u53CA\u504F\u5DEE\r\n");
				step1.setOpaque(true);
				step1.setHorizontalAlignment(SwingConstants.LEFT);
				step1.setFont(new Font("微软雅黑", Font.PLAIN, 16));
				step1.setBorder(new BevelBorder(BevelBorder.RAISED, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN));
				step1.setBackground(Color.YELLOW);
				panel_10.add(step1);
		panel_10.add(step2);
																												
																														step3 = new JLabel("Step3 torque7/ 7\u6863\u4F4D\u7F6E\u786E\u8BA4");
																														step3.setOpaque(true);
																														step3.setHorizontalAlignment(SwingConstants.LEFT);
																														step3.setFont(new Font("微软雅黑", Font.PLAIN, 16));
																														step3.setBorder(new BevelBorder(BevelBorder.RAISED, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN));
																														step3.setBackground(Color.YELLOW);
																														panel_10.add(step3);
																										
																												step4 = new JLabel("Step4 torque15/ 15档位置确认");
																												step4.setOpaque(true);
																												step4.setHorizontalAlignment(SwingConstants.LEFT);
																												step4.setFont(new Font("微软雅黑", Font.PLAIN, 16));
																												step4.setBorder(new BevelBorder(BevelBorder.RAISED, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN));
																												step4.setBackground(Color.YELLOW);
																												panel_10.add(step4);
																								
																										step5 = new JLabel("Step5 torque bit/ \u6885\u82B1\u6863\u4F4D\u7F6E\u786E\u8BA4");
																										step5.setOpaque(true);
																										step5.setHorizontalAlignment(SwingConstants.LEFT);
																										step5.setFont(new Font("微软雅黑", Font.PLAIN, 16));
																										step5.setBorder(new BevelBorder(BevelBorder.RAISED, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN));
																										step5.setBackground(Color.YELLOW);
																										panel_10.add(step5);
																						
																								step6 = new JLabel("Step6 speed 1 /变速板1位置确认");
																								step6.setOpaque(true);
																								step6.setHorizontalAlignment(SwingConstants.LEFT);
																								step6.setFont(new Font("微软雅黑", Font.PLAIN, 16));
																								step6.setBorder(new BevelBorder(BevelBorder.RAISED, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN));
																								step6.setBackground(Color.YELLOW);
																								panel_10.add(step6);
																				
																						step7 = new JLabel("Step7 speed 2 /变速板2位置确认");
																						step7.setOpaque(true);
																						step7.setHorizontalAlignment(SwingConstants.LEFT);
																						step7.setFont(new Font("微软雅黑", Font.PLAIN, 16));
																						step7.setBorder(new BevelBorder(BevelBorder.RAISED, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN));
																						step7.setBackground(Color.YELLOW);
																						panel_10.add(step7);
																		
																				step8 = new JLabel("Step8 read CMS SN/ 读取CMS序列号");
																				step8.setOpaque(true);
																				step8.setHorizontalAlignment(SwingConstants.LEFT);
																				step8.setFont(new Font("微软雅黑", Font.PLAIN, 16));
																				step8.setBorder(new BevelBorder(BevelBorder.RAISED, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN));
																				step8.setBackground(Color.YELLOW);
																				panel_10.add(step8);
																
																		step9 = new JLabel("Step9 13 torque/ 13档输出力矩");
																		step9.setOpaque(true);
																		step9.setHorizontalAlignment(SwingConstants.LEFT);
																		step9.setFont(new Font("微软雅黑", Font.PLAIN, 16));
																		step9.setBorder(new BevelBorder(BevelBorder.RAISED, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN));
																		step9.setBackground(Color.YELLOW);
																		panel_10.add(step9);
														
																step10 = new JLabel("Step10 forward turn judgement/ 正转判定");
																step10.setOpaque(true);
																step10.setHorizontalAlignment(SwingConstants.LEFT);
																step10.setFont(new Font("微软雅黑", Font.PLAIN, 16));
																step10.setBorder(new BevelBorder(BevelBorder.RAISED, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN));
																step10.setBackground(Color.YELLOW);
																panel_10.add(step10);
												
														step11 = new JLabel("Step11 speed 1 no load RPM/ 15档无负荷转速");
														step11.setOpaque(true);
														step11.setHorizontalAlignment(SwingConstants.LEFT);
														step11.setFont(new Font("微软雅黑", Font.PLAIN, 16));
														step11.setBorder(new BevelBorder(BevelBorder.RAISED, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN));
														step11.setBackground(Color.YELLOW);
														panel_10.add(step11);
										
												step12 = new JLabel("Step12 speed 1 no load A/ 15档无负荷电流");
												step12.setOpaque(true);
												step12.setHorizontalAlignment(SwingConstants.LEFT);
												step12.setFont(new Font("微软雅黑", Font.PLAIN, 16));
												step12.setBorder(new BevelBorder(BevelBorder.RAISED, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN));
												step12.setBackground(Color.YELLOW);
												panel_10.add(step12);
										
										step13 = new JLabel("Step13 speed 1 motor stop RPM/ \u7535\u673A\u505C\u6B62\u8F6C\u901F");
										step13.setOpaque(true);
										step13.setHorizontalAlignment(SwingConstants.LEFT);
										step13.setFont(new Font("微软雅黑", Font.PLAIN, 16));
										step13.setBorder(new BevelBorder(BevelBorder.RAISED, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN));
										step13.setBackground(Color.YELLOW);
										panel_10.add(step13);
								
										step14 = new JLabel("Step14 speed 2 no load RPM/ 15\u6863\u65E0\u8D1F\u8377\u8F6C\u901F2\r\n");
										step14.setOpaque(true);
										step14.setHorizontalAlignment(SwingConstants.LEFT);
										step14.setFont(new Font("微软雅黑", Font.PLAIN, 16));
										step14.setBorder(new BevelBorder(BevelBorder.RAISED, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN));
										step14.setBackground(Color.YELLOW);
										panel_10.add(step14);
						
								step15 = new JLabel("Step15 speed 2 no load A/ 15\u6863\u65E0\u8D1F\u8377\u7535\u6D412");
								step15.setOpaque(true);
								step15.setHorizontalAlignment(SwingConstants.LEFT);
								step15.setFont(new Font("微软雅黑", Font.PLAIN, 16));
								step15.setBorder(new BevelBorder(BevelBorder.RAISED, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN));
								step15.setBackground(Color.YELLOW);
								panel_10.add(step15);
						
						step16 = new JLabel("Step16 speed 2 motor stop RPM/ \u7535\u673A\u505C\u6B62\u8F6C\u901F");
						step16.setOpaque(true);
						step16.setHorizontalAlignment(SwingConstants.LEFT);
						step16.setFont(new Font("微软雅黑", Font.PLAIN, 16));
						step16.setBorder(new BevelBorder(BevelBorder.RAISED, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN));
						step16.setBackground(Color.YELLOW);
						panel_10.add(step16);
				
						step17 = new JLabel("Step17 LED judgement/ LED\u706F\u5224\u5B9A\r\n\r\n");
						step17.setOpaque(true);
						step17.setHorizontalAlignment(SwingConstants.LEFT);
						step17.setFont(new Font("微软雅黑", Font.PLAIN, 16));
						step17.setBorder(new BevelBorder(BevelBorder.RAISED, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN));
						step17.setBackground(Color.YELLOW);
						panel_10.add(step17);
		
				step18 = new JLabel("Step18 reverse turn judgement/ \u53CD\u8F6C\u5224\u5B9A");
				step18.setOpaque(true);
				step18.setHorizontalAlignment(SwingConstants.LEFT);
				step18.setFont(new Font("微软雅黑", Font.PLAIN, 16));
				step18.setBorder(new BevelBorder(BevelBorder.RAISED, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN));
				step18.setBackground(Color.YELLOW);
				panel_10.add(step18);
		panel_10.add(step19);

		btnReset = new JButton("Reset/\u590D\u4F4D");//复位按钮
		btnReset.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run() 
					{
						RXTXcomm.stepFailed=false;
								
						deviceCom.recArea.setText("");
						deviceCom.editArea.setText("");
						plcCom.recArea.setText("");
						plcCom.editArea.setText("");

						RXTXcomm.needCommunicateWithDevice=true;
						RXTXcomm.CMS_SerialNumber="";
						dbTime="";
						dbCMS="";
						dbTorquePosition="";
						dbSpeedPositon="";
						db13torque="";
						dbForwardTurn="";
						dbReverseTurn="";
						dbHallSegmentTime="";
						dbLED="";
						dbLowSpeedRPM="";
						dbValueJudgement="OK";
						dbLowSpeedCurrent="";
						dbHighSpeedRPM="";
						dbHighSpeedCurrent="";
						RXTXcomm.stepFailed=false;
						ifProceed=true;
						ifProceedInter=true;
						readCMS=true;
						startRun.stop();
						step=100;
						report.setText("Report:");
						stateAreaDemo.setBackground(Color.RED);
						stateAreaDemo.setText("    Not used \r\n      未启用 ");
						automatically=false;
						Offset_Ch0 = 0;
						Offset_Ch1 = 0;
						Variation_Ch0 = 0;
						Variation_Ch1 = 0;
						Channel_0_1 = 0;
						Channel_1_1 = 0;
						Channel_0_DH = 0;
						Channel_1_DH = 0;
						Saturation_0 = false;
						Saturation_1 = false;
						Middle_Point_Ch0 = 0;
						Middle_Point_Ch1 = 0;
						Delta_Offset_Ch0 = 0;
						Delta_Offset_Ch1 = 0;
						RXTXcomm.HallSegmentTimePercentage = 0;
						RXTXcomm.HallSegmentTimeAverage = 0;
						RXTXcomm.HallSegmentTimeSum = 0;
						RXTXcomm.HallSegmentTime.clear();
						CircleCnt = 0;
						step0.setBackground(Color.YELLOW);
						step1.setBackground(Color.YELLOW);
						step2.setBackground(Color.YELLOW);
						step3.setBackground(Color.YELLOW);
						step4.setBackground(Color.YELLOW);
						step5.setBackground(Color.YELLOW);
						step6.setBackground(Color.YELLOW);
						step7.setBackground(Color.YELLOW);
						step8.setBackground(Color.YELLOW);
						step9.setBackground(Color.YELLOW);
						step10.setBackground(Color.YELLOW);
						step11.setBackground(Color.YELLOW);
						step12.setBackground(Color.YELLOW);
						step13.setBackground(Color.YELLOW);
						step14.setBackground(Color.YELLOW);
						step15.setBackground(Color.YELLOW);
						step16.setBackground(Color.YELLOW);
						step17.setBackground(Color.YELLOW);
						step18.setBackground(Color.YELLOW);
						step19.setBackground(Color.YELLOW);
					}
				});
			}
		});
		
		btnReset.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		GroupLayout gl_panel_11 = new GroupLayout(panel_11);
		gl_panel_11.setHorizontalGroup(
			gl_panel_11.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_11.createSequentialGroup()
					.addComponent(btnStart, GroupLayout.PREFERRED_SIZE, 172, GroupLayout.PREFERRED_SIZE)
					.addGap(49)
					.addComponent(btnReset)
					.addGap(94))
		);
		gl_panel_11.setVerticalGroup(
			gl_panel_11.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_11.createSequentialGroup()
					.addGap(5)
					.addGroup(gl_panel_11.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnStart, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnReset)))
		);
		gl_panel_11.linkSize(SwingConstants.VERTICAL, new Component[] {btnStart, btnReset});
		gl_panel_11.linkSize(SwingConstants.HORIZONTAL, new Component[] {btnStart, btnReset});
		panel_11.setLayout(gl_panel_11);
		panel_8.setLayout(gl_panel_8);
		JPanel panel_12 = new JPanel();
		panel_12.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		panel_4.add(panel_12);

		ButtonSave = new JButton("save/保存");
		ButtonSave.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e) 
			{
				JFileChooser jf=new JFileChooser("C:/Users/Administrator/Desktop");
				int value=jf.showSaveDialog(null);

				if(value==JFileChooser.APPROVE_OPTION){    

					File getPath=jf.getSelectedFile();      

					String string=report.getText();
					WriteFileExample.write(string,getPath.toString()+".txt");
				}
				else
				{}
			}
		});
		ButtonSave.setFont(new Font("微软雅黑", Font.PLAIN, 16));

		JScrollPane scrollPane = new JScrollPane();
		JPanel panel_13 = new JPanel();
		panel_13.setBackground(Color.CYAN);
		JLabel label_24 = new JLabel("Report/报告");
		label_24.setHorizontalAlignment(SwingConstants.CENTER);
		label_24.setForeground(Color.BLACK);
		label_24.setFont(new Font("微软雅黑", Font.BOLD, 16));
		panel_13.add(label_24);

		report = new JTextArea();
		report.setFont(new Font("微软雅黑", Font.PLAIN, 13));
		report.setEditable(false);
		report.setLineWrap(true);
		scrollPane.setViewportView(report);
		GroupLayout gl_panel_12 = new GroupLayout(panel_12);
		gl_panel_12.setHorizontalGroup(
			gl_panel_12.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_12.createSequentialGroup()
					.addGap(10)
					.addComponent(panel_13, GroupLayout.PREFERRED_SIZE, 394, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_panel_12.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
					.addContainerGap())
				.addGroup(gl_panel_12.createSequentialGroup()
					.addContainerGap()
					.addComponent(ButtonSave, GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panel_12.setVerticalGroup(
			gl_panel_12.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_12.createSequentialGroup()
					.addGap(23)
					.addComponent(panel_13, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 522, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(ButtonSave, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
					.addGap(77))
		);
		panel_12.setLayout(gl_panel_12);

	}

	protected void unlock()//改变参数之前需要调用unlock命令
	{
		String temp="";
		writeIn[0]=(byte) 0xDF;
		writeIn[1]=0x00;
		writeIn[2]=0x00;
		writeIn[3]=0x5A;
		writeIn[4]=0x00;
		writeIn[5]=(byte) (writeIn[0]^writeIn[1]^writeIn[2]^writeIn[3]^writeIn[4]);
		temp=bytesToHexStringWithBlanket(writeIn);

		deviceCom.editArea.setText(temp);
		deviceCom.jbtSendData.doClick();
	}

	protected void lock()//当参数改变后需要调用lock命令
	{
		String temp="";
		writeIn[0]=(byte) 0xDF;
		writeIn[1]=0x00;
		writeIn[2]=0x00;
		writeIn[3]=(byte) 0xA5;
		writeIn[4]=0x00;
		writeIn[5]=(byte) (writeIn[0]^writeIn[1]^writeIn[2]^writeIn[3]^writeIn[4]);
		temp=bytesToHexStringWithBlanket(writeIn);

		deviceCom.editArea.setText(temp);
		deviceCom.jbtSendData.doClick();
	}
}
