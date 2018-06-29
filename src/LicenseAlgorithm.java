import java.util.*;

public class LicenseAlgorithm 
{
	public static final List<Integer> PI_KEY = new ArrayList<Integer>(Arrays.asList(0x3141,0x5926,0x5358,0x9793,0x2384,0x6264));
	public static final List<Integer> E_KEY = new ArrayList<Integer>(Arrays.asList(0x2718,0x2818,0x2845,0x9045,0x2353,0x6028));
	
	public static String MactoConCode(String Mac)
	{
		String ConCode = "";
		List<Integer> MacBuff = new ArrayList<Integer>();
		List<String> ConCodeBuff = new ArrayList<String>();
		MacBuff.add(Integer.valueOf(Mac.substring(0,2),16));
		MacBuff.add(Integer.valueOf(Mac.substring(3,5),16));
		MacBuff.add(Integer.valueOf(Mac.substring(6,8),16));
		MacBuff.add(Integer.valueOf(Mac.substring(9,11),16));
		MacBuff.add(Integer.valueOf(Mac.substring(12,14),16));
		MacBuff.add(Integer.valueOf(Mac.substring(15,17),16));
		
		for(int i = 0 ; i < 6 ; i++)
		{
			ConCodeBuff.add(Integer.toHexString(MacBuff.get(i)^PI_KEY.get(i)));
		}
		
		for(int i = 0 ; i < 6 ; i++)
		{
			if(ConCodeBuff.get(i).length()%2 != 0)
			{
				ConCodeBuff.set(i,"0" + ConCodeBuff.get(i));
			}
		}
		
		for(int i = 0 ; i < 6 ; i++)
		{
			if(i<5)
			{
				ConCode = ConCode + ConCodeBuff.get(i) + "-";
			}
			else
			{
				ConCode = ConCode + ConCodeBuff.get(i);
			}
		}
			
		return ConCode;
	}

	public static String ConCodetoMac(String ConCode)
	{
		String Mac = "";
		
		List<Integer> ConCodeBuff = new ArrayList<Integer>();
		List<String> MacBuff = new ArrayList<String>();
		ConCodeBuff.add(Integer.valueOf(ConCode.substring(0,4),16));
		ConCodeBuff.add(Integer.valueOf(ConCode.substring(5,9),16));
		ConCodeBuff.add(Integer.valueOf(ConCode.substring(10,14),16));
		ConCodeBuff.add(Integer.valueOf(ConCode.substring(15,19),16));
		ConCodeBuff.add(Integer.valueOf(ConCode.substring(20,24),16));
		ConCodeBuff.add(Integer.valueOf(ConCode.substring(25,29),16));
		
		for(int i = 0 ; i < 6 ; i++)
		{
			MacBuff.add(Integer.toHexString(ConCodeBuff.get(i)^PI_KEY.get(i)));
		}

		for(int i = 0 ; i < 6 ; i++)
		{
			if(MacBuff.get(i).length() == 1)
			{
				MacBuff.set(i,"0" + MacBuff.get(i));
			}
		}
		
		for(int i = 0 ; i < 6 ; i++)
		{
			if(i<5)
			{
				Mac = Mac + MacBuff.get(i) + "-";
			}
			else
			{
				Mac = Mac + MacBuff.get(i);
			}
		}
		
		return Mac;
	}	
	
	public static String ConCodeandActCode(String RawCode)
	{
		String TranCode = "";
		
		List<Integer> RawCodeBuff = new ArrayList<Integer>();
		List<String> TranCodeBuff = new ArrayList<String>();
		RawCodeBuff.add(Integer.valueOf(RawCode.substring(0,4),16));
		RawCodeBuff.add(Integer.valueOf(RawCode.substring(5,9),16));
		RawCodeBuff.add(Integer.valueOf(RawCode.substring(10,14),16));
		RawCodeBuff.add(Integer.valueOf(RawCode.substring(15,19),16));
		RawCodeBuff.add(Integer.valueOf(RawCode.substring(20,24),16));
		RawCodeBuff.add(Integer.valueOf(RawCode.substring(25,29),16));
		
		for(int i = 0 ; i < 6 ; i++)
		{
			TranCodeBuff.add(Integer.toHexString(RawCodeBuff.get(i)^E_KEY.get(i)));
		}
		
		for(int i = 0 ; i < 6 ; i++)
		{
			if(TranCodeBuff.get(i).length() == 1)
			{
				TranCodeBuff.set(i,"000" + TranCodeBuff.get(i));
			}
			if(TranCodeBuff.get(i).length() == 2)
			{
				TranCodeBuff.set(i,"00" + TranCodeBuff.get(i));
			}
			if(TranCodeBuff.get(i).length() == 3)
			{
				TranCodeBuff.set(i,"0" + TranCodeBuff.get(i));
			}
		}
		
		for(int i = 0 ; i < 6 ; i++)
		{
			if(i<5)
			{
				TranCode = TranCode + TranCodeBuff.get(i) + "-";
			}
			else
			{
				TranCode = TranCode + TranCodeBuff.get(i);
			}
		}
		
		return TranCode;
	}
	
	public static void main(String[] args) 
	{
		String X = "31e5-5939-532a-976c-2332-6286";
		String Y = ConCodeandActCode(X);
		System.out.print(Y+'\n');	
	}
	
}
