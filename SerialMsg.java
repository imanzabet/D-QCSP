package QUAY_CRANES_2;

import java.util.LinkedList;
import java.util.List;

import com.sun.corba.se.spi.orbutil.proxy.LinkedInvocationHandler;

@SuppressWarnings({ "serial", "unused" })
public class SerialMsg implements java.io.Serializable  {

	//DB db = new DB();
	
	public int SenderNum;
	public String SenderName;
	public String Msg_Type;
	public String InternalMsg;

	public int[] Ag_State;
 	public int[] Ag_Values;
 	public double[] Ag_Time;
 	
 	public LinkedList<Integer>[] ag_view_R;//	= (LinkedList<Integer>[]) new LinkedList[db.AG_MAXNUM];
 	public LinkedList<Integer>[] ag_view_L;//	= (LinkedList<Integer>[]) new LinkedList[db.AG_MAXNUM];
 	
 	public int[][] D_Nov;
	public int[][][] D_Ov;

}

