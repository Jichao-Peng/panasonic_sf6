import java.util.Enumeration;
import gnu.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.TitledBorder;
import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage;

public class RXTXcomm 
{
	OutputStream os = null;
	InputStream is = null;
	int baudRate;
	int dataBits;
	int stopBits;
	int parity;

	static int sayOpenSpSuccessfully=1;
	static boolean needCommunicateWithDevice=true;
	static boolean stepFailed=false;

	static String CMS_SerialNumber="";
	static String PLCdata="";

	static int difference13=0;
	static int difference1=0;
	static long gain=0;
	
	static String HallSegmentTimeLower = "";
	static String HallSegmentTimeUpper = "";
	static  List<Float> HallSegmentTime = new ArrayList<Float>(11);
	static float HallSegmentTimePercentage = 0;
	static float HallSegmentTimeAverage = 0;
	static float HallSegmentTimeSum = 0;

	static int recCount = 0;
	static int sendCount = 0;

	JFrame frame = new JFrame();
	JButton jbtSendData = new JButton("\u53D1\u9001\u6570\u636E");
	private JButton jbtClosePort = new JButton("\u5173\u95ED\u4E32\u53E3");
	JButton jbtOpenPort = new JButton("\u6253\u5F00\u4E32\u53E3");
	private JButton jbtClean = new JButton("\u6E05\u7A7A\u663E\u793A\u6846");
	private JLabel jlbPortList = new JLabel("\u4E32\u53E3\u540D");
	private JLabel label = new JLabel("   \u6CE2\u7279\u7387");
	private JScrollPane spRecArea = new JScrollPane();
	private JLabel jlbDataBits = new JLabel("\u6570\u636E\u4F4D");
	private JComboBox cmbDataBits = new JComboBox();
	private JLabel jlbStopBits = new JLabel("   \u505C\u6B62\u4F4D");
	private JComboBox cmbStopBits = new JComboBox();
	private JLabel jlbParity = new JLabel("\u6821\u68C0\u4F4D");
	private JComboBox cmbParity = new JComboBox();
	private JScrollPane spSendArea = new JScrollPane();
	private JPanel paremiterPanel = new JPanel();
	JComboBox cmbBaudRate = new JComboBox();
	public JComboBox portList = new JComboBox();
	JTextArea recArea;
	private CommPortIdentifier portId;
	private SerialPort sPort;
	JTextArea editArea = new JTextArea();

	//仅仅串口调试中用到了，在上位机中重写了一个Timer用来刷新PortList
	public Timer reFreshPortList = new Timer(0, new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			CommPortIdentifier portId;
			Enumeration en = CommPortIdentifier.getPortIdentifiers();
			portList.removeAllItems();
			while (en.hasMoreElements()) {
				portId = (CommPortIdentifier) en.nextElement();
				if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) 
				{
					portList.addItem(portId.getName());//将所有portID存入portList
				}
			}
			reFreshPortList.stop();
		}
	});	
	

	//在串口调试时用到了，但在上位机中通过doclick函数调用button进而调用time.start()
	public Timer readPort = new Timer(1, new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			int tp = 0;//整型
			int len = 1;

			try 
			{
				if (is.available() > 0) //如果输入流有数据的话
				{
					for (int i = 0; i < len; i++) //每次读一个字节
					{
						tp += is.read();
						tp *= 10;
					}
					tp /= 10;//接收十进制数

					String changeToTwoBit= Integer.toHexString(tp);//转化成十六进制数
					if (changeToTwoBit.length()==1) 
					{
						changeToTwoBit="0"+changeToTwoBit;
					}

					recArea.setText(recArea.getText() + changeToTwoBit + "  ");//连续不断地打印

					String temp=recArea.getText();//从接收数据框中读取数据，temp应该为接收到的数据

					if (!temp.substring(0, 2).equals("f0")&&!temp.substring(0, 2).equals("06")
							&&!temp.substring(0, 2).equals("07")&&!temp.substring(0, 2).equals("df")
							&&!temp.substring(0, 1).equals("b")&&!temp.substring(0, 1).equals("c"))
					{
						Frame.report.setText(Frame.report.getText()+"\n"+recArea.getText().toUpperCase()+"running");
						recArea.setText("");
					}//只有这些是命令

					temp=temp.replace(" ", "");//去除空格
					if ((temp.length()==12)||((temp.length()==6)&&(temp.equals("f02900")))) 
					{						
						if (com.equals(Frame.DEVICE_COM)) //如果是设备串口
						{
							Frame.report.setText(Frame.report.getText()+"\n"+"Device: "+recArea.getText().toUpperCase());							
							if (needCommunicateWithDevice) //当step进行到第七步就不需要再与设备进行通讯了,20多步的时候又打开了
							{
								//match()函数负责解析命令输出固定格式的report
								//Frame.report.setText(Frame.report.getText()+Frame.match(Frame.hexStringToBytes(editArea.getText().replace(" ", "")), recArea.getText().toUpperCase()));
								
								if (Frame.ifWriteData)//按条调试发送
								{
									switch (temp.substring(0,2)) 
									{
									case "df"://解锁和上锁命令
									case "07"://写数据命令
										Frame.ifProceedInter=false;
										break;
									default:
										break;
									}
								}

								if (Frame.automatically) //按开始按钮启动自动运行
								{	
									Frame.report.setText(Frame.report.getText()+"\n"+"temp: "+temp);
									if(!temp.substring(0,2).equals("c1"))
									{
										if (Frame.readCMS) //刚刚启动时为redaCMS为true，读完之后为false
										{
											if (temp.substring(0,2).equals("06")) 
											{
												CMS_SerialNumber=temp.substring(6, 8)+CMS_SerialNumber;//字符串拼凑起来
											}
											Frame.ifProceedInter=false;
										}
										else//如果不是读写命令 
										{
											switch (Frame.step)
											{
											case 101:	
												if (temp.substring(0,10).equals(Frame.Instruction.replace(" ", "")))//temp是不带空格的大写，Instruction是带空格的 小写
												{}
												else
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;												
												}
												Frame.ifProceedInter=false;
											break;
											
											case 102:											
												if (temp.substring(0,10).equals(Frame.Instruction.replace(" ", "")))
												{}
												else
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;												
												}
												Frame.ifProceedInter=false;
											break;
											
											case 103:											
												if (temp.substring(0,2).equals("f0"))// get Data Channel values
												{
													String yy,zz,xx,ww;
													zz=temp.substring(2, 4);
													yy=temp.substring(4, 6);
													xx=temp.substring(6, 8);
													ww=temp.substring(8,10);
													Frame.Channel_0_1=hexToDecimal(yy+zz);
													Frame.report.setText(Frame.report.getText()+"\n"+"Channel_0_1 is "+Integer.toString(Frame.Channel_0_1));
													Frame.Channel_1_1=hexToDecimal(ww+xx);
													Frame.report.setText(Frame.report.getText()+"\n"+"Channel_1_1 is "+Integer.toString(Frame.Channel_1_1));
												}
												else
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;												
												}
												Frame.ifProceedInter=false;
											break;
											
											case 104:
											case 105:
											case 106:
											case 107:
											case 108:
											case 109:
											case 110:
											break;
											
											case 111:
												if (temp.substring(0,10).equals(Frame.Instruction.replace(" ", "")))
												{}
												else
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;												
												}
												Frame.ifProceedInter=false;
											break;
												
											case 112:
												if (temp.substring(0,10).equals(Frame.Instruction.replace(" ", "")))
												{
													Frame.sendDEVICE("f0 29 00 00 00");
												}
												else
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;												
												}
												Frame.ifProceedInter=false;
											break;	
											
											case 500:
												if (temp.substring(0,2).equals("f0"))
												{}
												else
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
											break;
												
											case 113:
											break;
											
											case 114:											
												if (temp.substring(0,2).equals("f0"))// get Data Channel values
												{
													String yy,zz,xx,ww;
													zz=temp.substring(2, 4);
													yy=temp.substring(4, 6);
													xx=temp.substring(6, 8);
													ww=temp.substring(8,10);
													Frame.Channel_0_DH=hexToDecimal(yy+zz);
													Frame.report.setText(Frame.report.getText()+"\n"+"Channel_0_DH is "+Integer.toString(Frame.Channel_0_DH));
													Frame.Channel_1_DH=hexToDecimal(ww+xx);
													Frame.report.setText(Frame.report.getText()+"\n"+"Channel_1_DH is "+Integer.toString(Frame.Channel_1_DH));
												}
												else
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;												
												}
												Frame.ifProceedInter=false;
											break;	
											
											case 115:
											case 116:
											case 117:
											case 118:
											case 119:
											break;
											
											case 120:
												if (temp.substring(0,10).equals(Frame.Instruction.replace(" ", "")))
												{}
												else
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;												
												}
												Frame.ifProceedInter=false;
											break;
											
											case 121:
												if (temp.substring(0,10).equals(Frame.Instruction.replace(" ", "")))
												{
													Frame.sendDEVICE("f0 29 00 00 00");
												}
												else
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;												
												}
												Frame.ifProceedInter=false;
											break;
											
											case 501:
												if (temp.substring(0,2).equals("f0"))
												{}
												else
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
												
											case 1:  //13档离合器盖位置校正  
												if (temp.equals("f09200010063")) 
												{}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
	
											case 3:  //1档离合器盖位置确认   
												if (temp.equals("f09100010060")||temp.substring(6,8).equals("02")) 
												{}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
												
//											case 3:  //7档离合器盖位置确认   
//												if (temp.equals("f09100070066")||temp.substring(6,8).equals("06")||temp.substring(6,8).equals("08")) 
//												{}
//												else 
//												{
//													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
//													stepFailed=true;
//												}
//												Frame.ifProceedInter=false;
//												break;
	
											case 4:  //15档离合器盖位置确认
												if (temp.equals("f09100ff009e")||temp.equals("f091000f006e")||temp.substring(6,8).equals("0e")) 
												{}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
	
											case 5:  //梅花钻档离合器盖位置确认
												if (temp.equals("f09100ff009e")||temp.equals("f09100fe009f")||temp.substring(6,8).equals("ff")||temp.substring(6,8).equals("fd")) 
												{}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
	
											case 6:  //变速板“1”位置确认 
												if (temp.equals("f020010100d0")) //get raw raw value of potentiometer
												{}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
	
											case 7:  //变速板“2”位置确认 
												if (temp.equals("f020010200d3")) //get raw raw value of potentiometer
												{}
												else
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
	
											case 8:  //读取CMS序列号，第八步根本收不到数据的
												if (temp.substring(0,2).equals("06")) //读取CMS序列号
												{}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
												
											//这一部分是师兄插入的
											case 17:  
												if (temp.substring(0,2).equals("f0"))// get Data Channel values
												{
													String yy,zz,xx,ww;
													zz=temp.substring(2, 4);
													yy=temp.substring(4, 6);
													xx=temp.substring(6, 8);
													ww=temp.substring(8,10);
													difference13=hexToDecimal(ww+xx)-hexToDecimal(yy+zz);										
												}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
											
											case 18: 
												if (temp.substring(0,2).equals("f0")) // get Data Channel values
												{
													String yy,zz,xx,ww;
													zz=temp.substring(2, 4);
													yy=temp.substring(4, 6);
													xx=temp.substring(6, 8);
													ww=temp.substring(8,10);
													difference1=hexToDecimal(ww+xx)-hexToDecimal(yy+zz);											
												}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
											
											case 19:  
												if (temp.substring(0,6).equals("f09400")) //set Data Channel 0 value
												{}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
											
											//这一部分是我插入的
												
											//Read Hall Segment 0 Timing
											case 20:  
												if (temp.substring(0,6).equals("061804")) //set Data Channel 0 value
												{
													String xx,yy;
													xx=temp.substring(6, 8);
													yy=temp.substring(8, 10);
													HallSegmentTimeLower = yy+xx;
												}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
												
											case 21:  
												if (temp.substring(0,6).equals("061a04")) //set Data Channel 0 value
												{
													String xx,yy;
													xx=temp.substring(6, 8);
													yy=temp.substring(8, 10);
													HallSegmentTimeUpper = yy+xx;
													
													HallSegmentTime.add(hexToDecimal(HallSegmentTimeUpper+HallSegmentTimeLower) * 0.0416666f);
													Frame.report.setText(Frame.report.getText()+"\n"+"The Hall segment 0 timing is "+ String.valueOf(HallSegmentTime.get(0)));
												}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
											
											//Read Hall Segment 1 Timing
											case 22:  
												if (temp.substring(0,6).equals("061c04")) //set Data Channel 0 value
												{
													String xx,yy;
													xx=temp.substring(6, 8);
													yy=temp.substring(8, 10);
													HallSegmentTimeLower =yy+xx;
												}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
												
											case 23:  
												if (temp.substring(0,6).equals("061e04")) //set Data Channel 0 value
												{
													String xx,yy;
													xx=temp.substring(6, 8);
													yy=temp.substring(8, 10);
													HallSegmentTimeUpper = yy+xx;
													
													HallSegmentTime.add(hexToDecimal(HallSegmentTimeUpper+HallSegmentTimeLower) * 0.0416666f);
													Frame.report.setText(Frame.report.getText()+"\n"+"The Hall segment 1 timing is "+ String.valueOf(HallSegmentTime.get(1)));
												}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
												
											//Read Hall Segment 2 Timing
											case 24:  
												if (temp.substring(0,6).equals("062004")) //set Data Channel 0 value
												{
													String xx,yy;
													xx=temp.substring(6, 8);
													yy=temp.substring(8, 10);
													HallSegmentTimeLower =yy+xx;
												}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
													
											case 25:  
												if (temp.substring(0,6).equals("062204")) //set Data Channel 0 value
												{
													String xx,yy;
													xx=temp.substring(6, 8);
													yy=temp.substring(8, 10);
													HallSegmentTimeUpper = yy+xx;
													
													HallSegmentTime.add(hexToDecimal(HallSegmentTimeUpper+HallSegmentTimeLower) * 0.0416666f);
													Frame.report.setText(Frame.report.getText()+"\n"+"The Hall segment 2 timing is "+ String.valueOf(HallSegmentTime.get(2)));
												}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
												
											//Read Hall Segment 3 Timing
											case 26:  
												if (temp.substring(0,6).equals("062404")) //set Data Channel 0 value
												{
													String xx,yy;
													xx=temp.substring(6, 8);
													yy=temp.substring(8, 10);
													HallSegmentTimeLower =yy+xx;
												}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
												
											case 27:  
												if (temp.substring(0,6).equals("062604")) //set Data Channel 0 value
												{
													String xx,yy;
													xx=temp.substring(6, 8);
													yy=temp.substring(8, 10);
													HallSegmentTimeUpper = yy+xx;
													
													HallSegmentTime.add(hexToDecimal(HallSegmentTimeUpper+HallSegmentTimeLower) * 0.0416666f);
													Frame.report.setText(Frame.report.getText()+"\n"+"The Hall segment 3 timing is "+ String.valueOf(HallSegmentTime.get(3)));
												}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
												
											//Read Hall Segment 4 Timing
											case 28:  
												if (temp.substring(0,6).equals("062804")) //set Data Channel 0 value
												{
													String xx,yy;
													xx=temp.substring(6, 8);
													yy=temp.substring(8, 10);
													HallSegmentTimeLower =yy+xx;
												}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
												
											case 29:  
												if (temp.substring(0,6).equals("062a04")) //set Data Channel 0 value
												{
													String xx,yy;
													xx=temp.substring(6, 8);
													yy=temp.substring(8, 10);
													HallSegmentTimeUpper = yy+xx;
													
													HallSegmentTime.add(hexToDecimal(HallSegmentTimeUpper+HallSegmentTimeLower) * 0.0416666f);
													Frame.report.setText(Frame.report.getText()+"\n"+"The Hall segment 4 timing is "+ String.valueOf(HallSegmentTime.get(4)));
												}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;	
	
											//Read Hall Segment 5 Timing
											case 30:  
												if (temp.substring(0,6).equals("062c04")) //set Data Channel 0 value
												{
													String xx,yy;
													xx=temp.substring(6, 8);
													yy=temp.substring(8, 10);
													HallSegmentTimeLower =yy+xx;
												}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
												
											case 31:  
												if (temp.substring(0,6).equals("062e04")) //set Data Channel 0 value
												{
													String xx,yy;
													xx=temp.substring(6, 8);
													yy=temp.substring(8, 10);
													HallSegmentTimeUpper = yy+xx;
													
													HallSegmentTime.add(hexToDecimal(HallSegmentTimeUpper+HallSegmentTimeLower) * 0.0416666f);
													Frame.report.setText(Frame.report.getText()+"\n"+"The Hall segment 5 timing is "+ String.valueOf(HallSegmentTime.get(5)));
												}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
										
											//Read Hall Segment 6 Timing
											case 32:  
												if (temp.substring(0,6).equals("063004")) //set Data Channel 0 value
												{
													String xx,yy;
													xx=temp.substring(6, 8);
													yy=temp.substring(8, 10);
													HallSegmentTimeLower =yy+xx;
												}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
												
											case 33:  
												if (temp.substring(0,6).equals("063204")) //set Data Channel 0 value
												{
													String xx,yy;
													xx=temp.substring(6, 8);
													yy=temp.substring(8, 10);
													HallSegmentTimeUpper = yy+xx;
													
													HallSegmentTime.add(hexToDecimal(HallSegmentTimeUpper+HallSegmentTimeLower) * 0.0416666f);
													Frame.report.setText(Frame.report.getText()+"\n"+"The Hall segment 6 timing is "+ String.valueOf(HallSegmentTime.get(6)));
												}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
													
											//Read Hall Segment 7 Timing
											case 34:  
												if (temp.substring(0,6).equals("063404")) //set Data Channel 0 value
												{
													String xx,yy;
													xx=temp.substring(6, 8);
													yy=temp.substring(8, 10);
													HallSegmentTimeLower =yy+xx;
												}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
												
											case 35:  
												if (temp.substring(0,6).equals("063604")) //set Data Channel 0 value
												{
													String xx,yy;
													xx=temp.substring(6, 8);
													yy=temp.substring(8, 10);
													HallSegmentTimeUpper = yy+xx;
													
													HallSegmentTime.add(hexToDecimal(HallSegmentTimeUpper+HallSegmentTimeLower) * 0.0416666f);
													Frame.report.setText(Frame.report.getText()+"\n"+"The Hall segment 7 timing is "+ String.valueOf(HallSegmentTime.get(7)));
												}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
												
											//Read Hall Segment 8 Timing
											case 36:  
												if (temp.substring(0,6).equals("063804")) //set Data Channel 0 value
												{
													String xx,yy;
													xx=temp.substring(6, 8);
													yy=temp.substring(8, 10);
													HallSegmentTimeLower =yy+xx;
												}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
												
											case 37:  
												if (temp.substring(0,6).equals("063a04")) //set Data Channel 0 value
												{
													String xx,yy;
													xx=temp.substring(6, 8);
													yy=temp.substring(8, 10);
													HallSegmentTimeUpper = yy+xx;
													
													HallSegmentTime.add(hexToDecimal(HallSegmentTimeUpper+HallSegmentTimeLower) * 0.0416666f);
													Frame.report.setText(Frame.report.getText()+"\n"+"The Hall segment 8 timing is "+ String.valueOf(HallSegmentTime.get(8)));
												}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
												
											//Read Hall Segment 9 Timing
											case 38:  
												if (temp.substring(0,6).equals("063c04")) //set Data Channel 0 value
												{
													String xx,yy;
													xx=temp.substring(6, 8);
													yy=temp.substring(8, 10);
													HallSegmentTimeLower =yy+xx;
												}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
												
											case 39:  
												if (temp.substring(0,6).equals("063e04")) //set Data Channel 0 value
												{
													String xx,yy;
													xx=temp.substring(6, 8);
													yy=temp.substring(8, 10);
													HallSegmentTimeUpper = yy+xx;
													
													HallSegmentTime.add(hexToDecimal(HallSegmentTimeUpper+HallSegmentTimeLower) * 0.0416666f);
													Frame.report.setText(Frame.report.getText()+"\n"+"The Hall segment 9 timing is "+ String.valueOf(HallSegmentTime.get(9)));
												}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
												
											//Read Hall Segment 10 Timing
											case 40:  
												if (temp.substring(0,6).equals("064004")) //set Data Channel 0 value
												{
													String xx,yy;
													xx=temp.substring(6, 8);
													yy=temp.substring(8, 10);
													HallSegmentTimeLower =yy+xx;
												}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
												
											case 41:  
												if (temp.substring(0,6).equals("064204")) //set Data Channel 0 value
												{
													String xx,yy;
													xx=temp.substring(6, 8);
													yy=temp.substring(8, 10);
													HallSegmentTimeUpper = yy+xx;
													
													HallSegmentTime.add(hexToDecimal(HallSegmentTimeUpper+HallSegmentTimeLower) * 0.0416666f);
													Frame.report.setText(Frame.report.getText()+"\n"+"The Hall segment 10 timing is "+ String.valueOf(HallSegmentTime.get(10)));
												}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
												
											//Read Hall Segment 11 Timing
											case 42:  
												if (temp.substring(0,6).equals("064404")) //set Data Channel 0 value
												{
													String xx,yy;
													xx=temp.substring(6, 8);
													yy=temp.substring(8, 10);
													HallSegmentTimeLower =yy+xx;
												}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
												
											case 43:  
												if (temp.substring(0,6).equals("064604")) //set Data Channel 0 value
												{
													String xx,yy;
													xx=temp.substring(6, 8);
													yy=temp.substring(8, 10);
													HallSegmentTimeUpper = yy+xx;
													
													HallSegmentTime.add(hexToDecimal(HallSegmentTimeUpper+HallSegmentTimeLower) * 0.0416666f);
													Frame.report.setText(Frame.report.getText()+"\n"+"The Hall segment 11 timing is "+ String.valueOf(HallSegmentTime.get(11)));
												}
												else 
												{
													Frame.report.setText(Frame.report.getText()+"\n"+"Device response error ");
													stepFailed=true;
												}
												Frame.ifProceedInter=false;
												break;
												
											case 44:
												break;
												
											default:
												Frame.report.setText(Frame.report.getText()+"\n"+"step error! ");
												break;
											}
										}
									}
									else
									{
										Frame.ifProceedInter=false;										
										Frame.report.setText(Frame.report.getText()+"\n"+"Error Step:"+Frame.step);
										Frame.report.setText(Frame.report.getText()+"\n"+"Error!!! C1 51");
									}
								}
							}
							recArea.setText("");
						}
						else //如果是PLC串口
						{
							Frame.report.setText(Frame.report.getText()+"\n"+"PLC: "+recArea.getText().toUpperCase());
							//this is used to start and reset the program
							switch (temp.substring(0, 2)) 
							{
							case "c0":
								Frame.btnStart.doClick();
								break;
							case "c1":
								Frame.btnReset.doClick();
								break;
							default:
								break;
							}

							if (Frame.automatically) //step的变化是根据PLC来控制的，根据PLC发来的指令决定下一步是什么
							{
								//判断是否需要跳过某些功能，如果租后两位是01则直接跳到相应的步骤
								if (temp.substring(10, 12).equals("01")) 
								{
									switch (temp.substring(0,2)) 
									{
									case "b9":
										Frame.step=8;
										break;
									case "ba":
										Frame.step=9;
										break;
									case "bb":
										Frame.step=10;
										break;
									case "bc":
										Frame.step=11;
										break;
									case "bd":
										Frame.step=45;
										break;
									case "be":
										Frame.step=13;
										break;
									case "bf":
										Frame.step=46;
										break;
									case "b0":
										Frame.step=15;
										break;
									default:
										break;
									}
								}
								
								
								switch (Frame.step) 
								{								
								case 100:
									if (temp.substring(0,2).equals("c0")) //启动
									{
										Frame.report.setText(Frame.report.getText()+"\n"+"Start the program!");
									} 
									else 
									{
										if (temp.equals("c40000000000")) //1档就位
										{
											Frame.ifProceed=false;
											Frame.step+=1;
										}
										else 
										{ 
											Frame.step0.setBackground(Color.RED);
											Frame.sendPLCwithTwoBytes("d4 ff");
										}
									}
								break;
									
								case 101:
								case 102:
								case 103:									
								case 104:
								case 105:
								case 106:
								case 107:
								case 108:
								case 109:
								case 110:
								case 111:
								case 112:
								break;	
									
								case 113:
									if (temp.equals("c50000000000")) //13档就位
									{
										Frame.ifProceed=false;
										Frame.step = 103;
										Frame.Saturation_0 = false;
										Frame.Saturation_1 = false;
									}
									else if(temp.equals("c50000000002"))
									{
										Frame.ifProceed=false;
										Frame.step+= 1;										
									}
									else 
									{ 
										Frame.step0.setBackground(Color.RED);
										Frame.sendPLCwithTwoBytes("d4 ff");
									}
								break;									
									
								case 114:
								case 115:
								case 116:
								case 117:
								case 118:
								case 119:
								case 120:
								case 121:
								break;
									
								case 0:
									if (temp.equals("b10000000000")) //13档就位
									{
										Frame.ifProceed=false;
										Frame.step+=1;
									}
									else 
									{ 
										Frame.step1.setBackground(Color.RED);
										Frame.sendPLCwithTwoBytes("a1 ff");
									}
									break;
									
								case 1:	
									if (temp.equals("b20000000000")) //1档就位
									{
										Frame.ifProceed=false;
										Frame.step+=2;
									}
									else 
									{
										Frame.step2.setBackground(Color.RED);
										Frame.sendPLCwithTwoBytes("a2 ff");
									}
									break;

//								case 2:	
//									if (temp.equals("b30000000000")) //7档就位
//									{
//										Frame.ifProceed=false;
//										Frame.step+=1;
//									}
//									else 
//									{
//										Frame.step3.setBackground(Color.RED);
//										Frame.sendPLCwithTwoBytes("a3 ff");
//									}
//									break;

								case 3:	
									if (temp.equals("b40000000000")) //15档就位
									{
										Frame.ifProceed=false;
										Frame.step+=1;
									}
									else 
									{
										Frame.step4.setBackground(Color.RED);
										Frame.sendPLCwithTwoBytes("a4 ff");
									}
									break;

								case 4:	
									if (temp.equals("b50000000000")) //梅花档就位
									{
										Frame.ifProceed=false;
										Frame.step+=1;
									}
									else 
									{
										Frame.step5.setBackground(Color.RED);
										Frame.sendPLCwithTwoBytes("a5 ff");
									}
									break;

								case 5:	
									if (temp.equals("b60000000000")) //变速板1就位
									{
										Frame.ifProceed=false;
										Frame.step+=1;
									}
									else 
									{
										Frame.step6.setBackground(Color.RED);
										Frame.sendPLCwithTwoBytes("a6 ff");
									}
									break;


								case 6:	
									if (temp.equals("b70000000000")) //变速板2就位
									{
										Frame.ifProceed=false;
										Frame.step+=1;
									}
									else 
									{
										Frame.step7.setBackground(Color.RED);
										Frame.sendPLCwithTwoBytes("a7 ff");
									}
									break;

								case 8:
									if (temp.substring(0,4).equals("b900"))//力矩数据发送给PC
									{
										Frame.db13torque=temp.substring(4, 8);//力矩数据
										if (temp.substring(8,10).equals("ff"))//如果不合格,在这里错误数据存入Access
										{
											Frame.step9.setBackground(Color.RED);
											Frame.sendPLCwithTwoBytes("a9 ff");
											Frame.dbValueJudgement="NG";
											Frame.testAccessConnect.updateType="13档力矩";
											Frame.db13torque=Frame.Stringinsert(Frame.db13torque, ".", 2);

											if(Frame.db13torque.substring(0,1).equals("0"))
											{
												Frame.db13torque=Frame.db13torque.substring(1,5);
											}
											Frame.testAccessConnect.updateTypeValue=Frame.db13torque+"/"+Frame.dbValueJudgement;//将不合格的数据存入Access
											Frame.testAccessConnect.updataLastLine();
										}
										else //如果合格，数据将在Frame的线程线程中被存入Aceess 
										{
											Frame.dbValueJudgement="OK";
											Frame.ifProceed=false;
											Frame.step+=1;
										}
									}
									else
									{
									}
									break;

								case 9:
									if (temp.substring(0,4).equals("ba00")) //正转判定
									{
										if (temp.substring(8,10).equals("00")) 
										{
											Frame.dbForwardTurn="OK";
											Frame.ifProceed=false;
											Frame.step+=1;
										} 
										else 
										{
											Frame.step10.setBackground(Color.RED);
											Frame.sendPLCwithTwoBytes("aa ff");
											Frame.dbForwardTurn="NG";
											Frame.testAccessConnect.updateType="正转判定";
											Frame.testAccessConnect.updateTypeValue=Frame.dbForwardTurn;
											Frame.testAccessConnect.updataLastLine();
										}
									}
									else 
									{
									}
									break;

								case 10:
									if (temp.substring(0,4).equals("bb00")) //低速转速数据发给PC
									{
										Frame.dbLowSpeedRPM=temp.substring(4, 8);
										if (temp.substring(8,10).equals("ff")) 
										{
											Frame.step11.setBackground(Color.RED);
											Frame.sendPLCwithTwoBytes("ab ff");
											Frame.dbValueJudgement="NG";
											Frame.testAccessConnect.updateType="低速转速";

											if(Frame.dbLowSpeedRPM.substring(0,1).equals("0"))
											{
												Frame.dbLowSpeedRPM=Frame.dbLowSpeedRPM.substring(1,4);
											}
											Frame.testAccessConnect.updateTypeValue=Frame.dbLowSpeedRPM+"/"+Frame.dbValueJudgement;
											Frame.testAccessConnect.updataLastLine();
										}
										else 
										{
											Frame.dbValueJudgement="OK";
											Frame.ifProceed=false;
											Frame.step+=1;
										}
									}
									else 
									{
									}
									break;


								case 11:
									if (temp.substring(0,4).equals("bc00")) //低速电流数据发给PC
									{
										Frame.dbLowSpeedCurrent=temp.substring(4, 8);
										if (temp.substring(8,10).equals("ff")) 
										{
											Frame.step12.setBackground(Color.RED);
											Frame.sendPLCwithTwoBytes("ac ff");
											Frame.dbValueJudgement="NG";
											Frame.testAccessConnect.updateType="低速电流";
											Frame.dbLowSpeedCurrent=Frame.Stringinsert(Frame.dbLowSpeedCurrent, ".", 2);
											if(Frame.dbLowSpeedCurrent.substring(0,1).equals("0"))
											{
												Frame.dbLowSpeedCurrent=Frame.dbLowSpeedCurrent.substring(1,5);
											}
											Frame.testAccessConnect.updateTypeValue=Frame.dbLowSpeedCurrent+"/"+Frame.dbValueJudgement;
											Frame.testAccessConnect.updataLastLine();
										}
										else
										{
											Frame.ifProceed=false;
											Frame.step+=1;
											Frame.dbValueJudgement="OK";
										}
									}
									else 
									{
									}
									break;
									
								case 12:
									if (temp.equals("c60000000000")) //电机低速停止转速
									{
										Frame.ifProceed=false;
										Frame.step = 45;
										Frame.dbValueJudgement="OK";
									}
									else 
									{
										Frame.step13.setBackground(Color.RED);
										Frame.sendPLCwithTwoBytes("d6 ff");
									}
								break;									
									
								case 45://后期插入
									if (temp.substring(0,4).equals("bd00")) //高速转速数据发给PC
									{
										Frame.dbHighSpeedRPM=temp.substring(4, 8);
										if (temp.substring(8,10).equals("ff")) 
										{
											Frame.step14.setBackground(Color.RED);
											Frame.sendPLCwithTwoBytes("ad ff");
											Frame.dbValueJudgement="NG";
											Frame.testAccessConnect.updateType="高速转速";
											if(Frame.dbHighSpeedRPM.substring(0,1).equals("0"))
											{
												Frame.dbHighSpeedRPM=Frame.dbHighSpeedRPM.substring(1,4);
											}
											Frame.testAccessConnect.updateTypeValue=Frame.dbHighSpeedRPM+"/"+Frame.dbValueJudgement;
											Frame.testAccessConnect.updataLastLine();
										}
										else 
										{
											Frame.ifProceed=false;
											Frame.step = 13;
											Frame.dbValueJudgement="OK";
										}
									}
									else 
									{
									}
									break;
									
								case 13:
									if (temp.substring(0,4).equals("be00")) //高速电流数据发给PC
									{
										Frame.dbHighSpeedCurrent=temp.substring(4, 8);
										if (temp.substring(8,10).equals("ff")) 
										{
											Frame.step15.setBackground(Color.RED);
											Frame.sendPLCwithTwoBytes("ae ff");
											Frame.dbValueJudgement="NG";
											Frame.testAccessConnect.updateType="高速电流";
											Frame.dbHighSpeedCurrent=Frame.Stringinsert(Frame.dbHighSpeedCurrent, ".", 2);

											if(Frame.dbHighSpeedCurrent.substring(0,1).equals("0"))
											{
												Frame.dbHighSpeedCurrent=Frame.dbHighSpeedCurrent.substring(1,5);
											}
											Frame.testAccessConnect.updateTypeValue=Frame.dbHighSpeedCurrent+"/"+Frame.dbValueJudgement;
											Frame.testAccessConnect.updataLastLine();
										}
										else 
										{
											Frame.ifProceed=false;
											Frame.step+=1;
											Frame.dbValueJudgement="OK";
										}
									}
									else 
									{
									}
									break;
									
								case 14:
									if (temp.equals("c70000000000")) //电机高速停止转速
									{
										Frame.ifProceed=false;
										Frame.step = 46;
									}
									else 
									{
										Frame.step16.setBackground(Color.RED);
										Frame.sendPLCwithTwoBytes("d7 ff");
									}
									break;										
									
								case 46://后期插入
									if (temp.substring(0,4).equals("bf00")) //LED灯判定
									{
										if (temp.substring(8,10).equals("00")) 
										{
											Frame.ifProceed=false;
											Frame.step = 15;
											Frame.dbLED="OK";
										} 
										else
										{
											Frame.step17.setBackground(Color.RED);
											Frame.sendPLCwithTwoBytes("af ff");
											Frame.dbLED="NG";
											Frame.testAccessConnect.updateType="LED判定";
											Frame.testAccessConnect.updateTypeValue=Frame.dbLED;
											Frame.testAccessConnect.updataLastLine();
										}
									}
									else
									{
									}
									break;
									
								case 15:
									if (temp.substring(0,4).equals("b000"))//反转判定
									{
										if (temp.substring(8,10).equals("00"))
										{
											Frame.ifProceed=false;
											Frame.step+=1;
											Frame.dbReverseTurn="OK";
										} 
										else 
										{
											Frame.step18.setBackground(Color.RED);
											Frame.dbReverseTurn="NG";
											Frame.testAccessConnect.updateType="反转判定";
											Frame.testAccessConnect.updateTypeValue=Frame.dbReverseTurn;
											Frame.testAccessConnect.updataLastLine();
										}
									}
									else 
									{
									}
									break;
									
								case 16:
									break;											
									
									
								//这里是师兄后来插入的部分
								case 17:	
									if (temp.equals("c30000000000"))//已经转到1档
									{
										Frame.step+=1;//第17步到第18步是通过这里递进的
										Frame.ifProceed=false;
										Frame.dbValueJudgement="OK";
									}
									else 
									{
										Frame.step1.setBackground(Color.RED);
										Frame.sendPLCwithTwoBytes("d1 ff");
									}
									break;
									
								case 18:
								case 19:
								case 20:
								case 21:
								case 22:
								case 23:
								case 24:
								case 25:
								case 26:
								case 27:
								case 28:
								case 29:
								case 30:
								case 31:
								case 32:
								case 33:
								case 34:
								case 35:
								case 36:
								case 37:
								case 38:
								case 39:
								case 40:
								case 41:
								case 42:
								case 43:
								case 44:
									break;
								default:
									Frame.report.setText(Frame.report.getText()+"\n"+"step error! "	);
									break;
								}
							}
							recArea.setText("");
						}
					}
					recCount += len;
					jlbRecCount.setText("已接收:" + recCount);
				}
			} 
			catch (IOException e1) 
			{
				jbtClosePort.doClick();
				JOptionPane.showMessageDialog(frame, "IO Exception");
				readPort.stop();
			}

		}
	});
	
	
	
	private final JPanel messagePanelButtonPanel = new JPanel();
	private final JPanel showMessagePanel = new JPanel();
	private final JLabel label_1 = new JLabel("");
	private final JLabel label_2 = new JLabel("");
	private final JLabel label_3 = new JLabel("");
	private final JLabel jlbSendCount = new JLabel("\u5DF2\u53D1\u9001\uFF1A");
	private final JLabel jlbRecCount = new JLabel("\u5DF2\u63A5\u6536\uFF1A");
	protected Listener listener;
	final JRadioButton radioButton = new JRadioButton("\u5341\u516D\u8FDB\u5236");
	String com;

	public RXTXcomm() 
	{
		spSendArea.setMinimumSize(new Dimension(0, 200));
		frame.getContentPane().setLayout(new BorderLayout());
		paremiterPanel.setBorder(new TitledBorder(null,
				"\u53C2\u6570\u9009\u62E9", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		paremiterPanel.setBounds(136, 223, 381, 83);
		frame.getContentPane().add(paremiterPanel, BorderLayout.SOUTH);
		paremiterPanel.setLayout(new GridLayout(0, 4, 0, 0));
		paremiterPanel.add(jlbPortList);
		paremiterPanel.add(portList);
		paremiterPanel.add(label);
		cmbBaudRate.setModel(new DefaultComboBoxModel(new String[] { "300",
				"2400", "9600", "14400", "19200", "28800", "38400", "56000",
				"57600", "115200", "128000", "256000" }));
		cmbBaudRate.setSelectedIndex(2);
		paremiterPanel.add(cmbBaudRate);
		paremiterPanel.add(jlbDataBits);
		cmbDataBits.setModel(new DefaultComboBoxModel(new String[] { "5", "6",
				"7", "8" }));
		cmbDataBits.setSelectedIndex(3);
		paremiterPanel.add(cmbDataBits);
		paremiterPanel.add(jlbStopBits);
		cmbStopBits.setModel(new DefaultComboBoxModel(new String[] { "1",
				"1.5", "2" }));
		paremiterPanel.add(cmbStopBits);
		paremiterPanel.add(jlbParity);
		cmbParity.setModel(new DefaultComboBoxModel(new String[] { "None",
				"Even", "Odd", "Mark", "Space" }));
		paremiterPanel.add(cmbParity);
		paremiterPanel.add(label_1);
		paremiterPanel.add(label_2);
		paremiterPanel.add(label_3);
		
		paremiterPanel.add(jbtOpenPort);
		
		jbtOpenPort.addActionListener(new ActionListener() 		{
			public void actionPerformed(ActionEvent e) 
			{
				try {
					portId = CommPortIdentifier.getPortIdentifier(com);
					if (portId.getCurrentOwner() != null) 
					{
						System.out.println(portId.getCurrentOwner());
						if (!portId.getCurrentOwner().equals(this.getClass().toString())
								&& JOptionPane.showConfirmDialog(frame,"端口被占用") != 0) 
						{
							return;
						}
					}
					sPort = (SerialPort) portId.open(getClass().toString(),1000);//打开串口
					setParameter();
					sPort.setSerialPortParams(baudRate, dataBits, stopBits,parity);//配置串口
					os = sPort.getOutputStream();//输出缓冲流
					is = sPort.getInputStream();//输入缓冲流

					portOpened();
					listener = new Listener();
					portId.addPortOwnershipListener(listener);

					if (sayOpenSpSuccessfully==1) 
					{
						JOptionPane.showMessageDialog(frame, "串口打开成功");
						sayOpenSpSuccessfully=2;
					}
				} 
				catch (NoSuchPortException e1) 
				{
					JOptionPane.showMessageDialog(frame,"please choose a port");
					jbtClosePort.doClick();
					return;
				} 
				catch (UnsupportedCommOperationException e1) 
				{
					JOptionPane.showMessageDialog(frame,"Un upported Comm Operation");
					sPort.close();
					jbtClosePort.doClick();
					return;
				} 
				catch (PortInUseException e1) 
				{
					JOptionPane.showMessageDialog(frame, "Port in used err!");
					jbtClosePort.doClick();
					return;
				}
				catch (IOException e3) 
				{
					JOptionPane.showMessageDialog(frame, "IO Exception");
					jbtClosePort.doClick();
					return;
				}
				if (listener != null) 
				{
					portId.removePortOwnershipListener(listener);
				}
			}
		});
		
		jbtOpenPort.setBounds(101, 272, 93, 23);
		paremiterPanel.add(jbtClosePort);
		jbtClosePort.setEnabled(false);
		jbtClosePort.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				try 
				{
					readPort.stop();
					sPort.close();
					is.close();
					os.close();
				} 
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}
				portClosed();
			}
		});
		
		jbtClosePort.setBounds(228, 272, 93, 23);
		JPanel messagePanel = new JPanel();
		messagePanel.setBounds(40, 23, 185, 180);
		frame.getContentPane().add(messagePanel, BorderLayout.CENTER);
		messagePanel.setLayout(new BorderLayout(0, 0));
		messagePanel.add(messagePanelButtonPanel, BorderLayout.EAST);
		messagePanelButtonPanel.setLayout(new GridLayout(0, 1, 0, 0));
		messagePanelButtonPanel.add(jbtSendData);
		jbtSendData.setEnabled(false);
		messagePanelButtonPanel.add(radioButton);
		messagePanelButtonPanel.add(jlbSendCount);
		messagePanelButtonPanel.add(jlbRecCount);
		messagePanelButtonPanel.add(jbtClean);
		messagePanel.add(showMessagePanel, BorderLayout.CENTER);
		showMessagePanel.setLayout(new GridLayout(0, 1, 0, 0));
		showMessagePanel.add(spSendArea);
		spSendArea.setViewportView(editArea);
		recArea = new JTextArea();
		recArea.setEditable(false);
		recArea.setForeground(Color.BLUE);
		showMessagePanel.add(spRecArea);
		spRecArea.setViewportView(recArea);
		recArea.setLineWrap(true);
		recArea.setColumns(10);
		
		jbtClean.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				if (radioButton.isSelected()) 
				{
					recArea.setText("");
				}
			}
		});
		
		jbtSendData.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				if (radioButton.isSelected()) 
				{
					sendHexData(editArea.getText());//发送十六进制数
				} 
				else 
				{
					sendCharData(editArea.getText());//发送字节
				}
			}
		});
		frame.setTitle("\u4E32\u53E3\u8C03\u8BD5");
		frame.setLocationRelativeTo(null);
		frame.repaint();
	}

	public static void main(String[] args) 
	{
		RXTXcomm port = new RXTXcomm();
		port.reFreshPortList.start();
		port.frame.setResizable(true);
		port.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		port.frame.setVisible(true);
		port.frame.setSize(412, 343);
	}

	protected void setParameter() 
	{
		baudRate = Integer.parseInt((String) cmbBaudRate.getSelectedItem());

		if (cmbDataBits.getSelectedItem().equals("5")) 
		{
			dataBits = SerialPort.DATABITS_5;
		}
		else if (cmbDataBits.getSelectedItem().equals("6")) 
		{
			dataBits = SerialPort.DATABITS_6;
		} 
		else if (cmbDataBits.getSelectedItem().equals("7")) 
		{
			dataBits = SerialPort.DATABITS_7;
		} 
		else if (cmbDataBits.getSelectedItem().equals("8")) 
		{
			dataBits = SerialPort.DATABITS_8;
		}
		if (cmbStopBits.getSelectedItem().equals("1")) 
		{
			stopBits = 1;
		} 
		else if (cmbStopBits.getSelectedItem().equals("1.5")) 
		{
			stopBits = 3;
		} 
		else if (cmbStopBits.getSelectedItem().equals("2")) 
		{
			stopBits = 2;
		}
		if (cmbParity.getSelectedItem().equals("None")) 
		{
			parity = SerialPort.PARITY_NONE;
		} 
		else if (cmbParity.getSelectedItem().equals("Even")) 
		{
			parity = SerialPort.PARITY_EVEN;
		} 
		else if (cmbParity.getSelectedItem().equals("Odd")) 
		{
			parity = SerialPort.PARITY_ODD;
		} 
		else if (cmbParity.getSelectedItem().equals("Mark")) 
		{
			parity = SerialPort.PARITY_MARK;
		} 
		else if (cmbParity.getSelectedItem().equals("Space")) 
		{
			parity = SerialPort.PARITY_SPACE;
		}
	}

	public void sendCharData(Object o) 
	{
		String str = o + "";
		byte[] cs = str.getBytes();
		for (int i = 0; i < cs.length; i++) 
		{
			try 
			{
				os.write(cs[i]);
				sendCount++;
			} 
			catch (IOException e) 
			{
				JOptionPane.showMessageDialog(frame, "IO Exception");
				break;
			}
		}
		jlbSendCount.setText("�ѷ��ͣ�" + sendCount);
	}

	public void sendHexData(String s)
	{

		int[] cs = strToInt(s);
		for (int i = 0; i < cs.length; i++) 
		{
			try 
			{
				os.write(cs[i]);
				sendCount++;
			} 
			catch (IOException e) 
			{
				JOptionPane.showMessageDialog(frame, "IO Exception");
				break;
			}
		}
		jlbSendCount.setText("已发送" + sendCount);
	}

	private int[] strToInt(String s) 
	{
		int re[] = new int[(s.length() + 1) / 3];
		for (int i = 0; i < (s.length() + 1) / 3; i++)
		{
			re[i] = hexToDecimal(s.substring(3 * i, 3 * (i + 1) - 1));
		}
		return re;
	}

	public  int hexToDecimal(String hex) 
	{
		hex = hex.toUpperCase();
		int decimalValue = 0;
		for (int i = 0; i < hex.length(); i++)
		{
			char hexChar = hex.charAt(i);
			decimalValue = decimalValue * 16 + hexCharToDecimal(hexChar);
		}
		return decimalValue;
	}

	public  int hexCharToDecimal(char ch)
	{
		if (ch >= 'A' && ch <= 'F')
			return 10 + ch - 'A';
		else
			return ch - '0';
	}

	private void portClosed() 
	{
		jbtOpenPort.setEnabled(true);
		jbtClosePort.setEnabled(false);
		jbtSendData.setEnabled(false);
		readPort.stop();
	}

	private void portOpened()
	{
		jbtOpenPort.setEnabled(false);
		jbtClosePort.setEnabled(true);
		jbtSendData.setEnabled(true);
		readPort.start();
	}

	class Listener implements CommPortOwnershipListener
	{
		public void ownershipChange(int arg0)
		{
			if (arg0 == CommPortOwnershipListener.PORT_OWNERSHIP_REQUESTED
					&& !portId.getCurrentOwner().equals(portId.getClass().toString())) 
			{
				if (JOptionPane.showConfirmDialog(frame, "����"
						+ "������Ӧ�ó������ڳ��Դ򿪴���"
						+ portId.getName() + "\n���Ƿ���Ҫ�����ô��ڵĿ���Ȩ�ޣ�") == 0)
				{
					jbtClosePort.doClick();
				}
			}
		}
	}
}
