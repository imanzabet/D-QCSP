package QUAY_CRANES_2;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JOptionPane;


@SuppressWarnings("unchecked")
public class Func{
	
	public int ticker = 5000;
	public static Queue<String>[] print_queue = (LinkedList<String>[])new LinkedList [DB.AG_MAXNUM]; 
 	public int[][] D_Nov;
	public int[][][] D_Ov;
//	private static LoggerThread LogT;

	public Func(){

		/*		initialize array of LinkedList			*/
		try{
			for(int i=0; i<print_queue.length; i++) {
				print_queue[i] = new LinkedList<String>();
			}
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
//		LogT = LoggerThread.getLogger();
//		LogT.setName("Logger Thread");
//		Thread.State TrState = LogT.getState();
//		System.out.println("The Logger State is:	"+TrState.toString());
//		LogT.start();
//		System.out.println("The Logger State is:	"+TrState.toString());
	}
	
	public String convertint2str(int i) {
		return String.format("%d", i);

	}

	public static boolean is_Str_Equal(String st1, String st2) {
		if (st1.equals(st2))
			return true;
		else
			return false;
    }
	
	public static String getDateTime(){
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public boolean is_empty (int[] Arr){
		for (int i=0; i<Arr.length; i++)
			if (Arr[i] != 0)
				return false;
		return true;
	}
	
	public boolean is_empty (int[][] Arr){
		for (int i=0; i<Arr.length; i++)
			for(int j=0; j<Arr[i].length; j++)
			if (Arr[i][j] != 0)
				return false;
		return true;
	}

	public boolean is_empty (int[][][] Arr){
		for (int i=0; i<Arr.length; i++)
			for(int j=0; j<Arr[i].length; j++)
				for (int k=0; k<Arr[i][j].length; k++)
					if (Arr[i][j][k] != 0)
						return false;
		return true;
	}

	public static void show_msg (String msg) {
		int joption = JOptionPane.showConfirmDialog(null,msg, "Information", JOptionPane.OK_CANCEL_OPTION);
		joption++;	//for remove warning
	}

	public static void show_msg (String msg, String title) {
		int joption = JOptionPane.showConfirmDialog(null,msg, title, JOptionPane.OK_CANCEL_OPTION);
		joption++;	//for remove warning
	}

	public static int show_msg_return (String msg, String title) {
		int joption = JOptionPane.showConfirmDialog(null,msg, title, JOptionPane.YES_NO_CANCEL_OPTION);
		return joption;
	}

/*
	public static void print_msg (String msg, int ag) {
		
		if (ag>=0 && ag<DB.AG_MAXNUM) {
		String tmp = getDateTime()+"	QCA_"+IntToStr(ag)+":	"+msg;
		try {
			while (! print_queue[ag].add(tmp))
				Thread.sleep(5);
			Thread.State TrState = LogT.getState();
			System.out.println("The Logger State is:	"+TrState.toString());
			if (LogT.isAlive()) {
				LogT.run();
			}else {
				System.out.println("$$$$$$$$$$ The Logger is down !!!");
			}
		}
		catch (Exception e) {
			System.out.println(e.toString());
		}
		}else
			System.out.println("#####	The ag Number can not resolved (out of bound)");
	}
*/

//	public static void print_msg (String msg, int ag) {
//		System.out.println(getDateTime()+"	QCA_"+IntToStr(ag)+":	"+msg);
//	}
	public static void print_msg (String msg) {
		System.out.println(getDateTime()+"	"+msg);
	}
	
	public static String IntToStr (int input) {
		return Integer.toString(input);
	}
	/**
	 * This function convert the first character of an string to integer number
	 * This just work for numbers 0~9
	 * @param input
	 * @return
	 */
	public int StrToInt (String input) {
		return Integer.valueOf(input.toCharArray()[0]) - 48;
	}
}
