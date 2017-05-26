package QUAY_CRANES_2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.sun.org.apache.xpath.internal.functions.FuncBoolean;

//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//import java.util.Stack;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Iterator;

@SuppressWarnings({ "unchecked", "unused" })
public class DB{
	
 	private final int AG_THIS;
 	private final int AG_LEFT;
 	private final int AG_RIGHT;

 	public final int TresholdTime = 5;
	public final int S_ACTIVE = 0;
	public final int S_PASSIVE = 1;
	public final int S_DONE= 2;
	public final static int Loop_Sleep_Time 	= 50;			// main loop
	public final static int Loop_Sleep_Time_2	= 2;			// agent_view
	public final static int OPL_Sleep_Time		= 1;
	public final static int Logger_Sleep_Time		= 40;
	public final static int Send_Counter	=	2000;
	public final static int AG_MAXNUM = 4;			// number of QCAs
	public final static int AG_LOGGER = 222;		// the number for Logger Agent

	public static int[] Comm_Counter;
	// 	private final int QCA_NUMBERS;		//The number of QCAs

 	private String Ag_Name;
 	public int[] Ag_State;
 	public int[] tmp_Ag_State;
 	public int[] Ag_Values;
 	public double[] Ag_Time;
 	public double[] tmp_Ag_Time;
 	
 	
 	public LinkedList<Integer>[] ag_view_R	= (LinkedList<Integer>[]) new LinkedList[AG_MAXNUM];
 	public LinkedList<Integer>[] ag_view_L	= (LinkedList<Integer>[]) new LinkedList[AG_MAXNUM];
 	public LinkedList<Integer>[] tmp_ag_view_R	= (LinkedList<Integer>[]) new LinkedList[AG_MAXNUM];
 	public LinkedList<Integer>[] tmp_ag_view_L	= (LinkedList<Integer>[]) new LinkedList[AG_MAXNUM];
/*
 	List< List<Integer>> nodeLists = new LinkedList< List< Integer>>();
 	List<Integer>[] ag_view_Matrix = new NodeList[4];
// 	public List<Integer> ag_view_L = new LinkedList<Integer>();
// 	public List<Integer>[] aglist;
// 	aglist = (List<Integer>[]) new List[4];
// 	Object[] arr_matrix = new Object()[4];
// 	public LinkedList<Integer>[] myMatrix;
//	myMatrix = (LinkedList<Integer>[]) new LinkedList[5];
//	List< List<Integer>> nodeLists = new LinkedList< List< Integer>>();
	
//	LinkedList[] ll = new LinkedList[5];
//	ArrayList<LinkedList<Integer>> a = new ArrayList<LinkedList<Integer>>();
*/ 	
 	public int[][] D_Nov;
	public int[][][] D_Ov;
 	public int[][] tmp_D_Nov;
	public int[][][] tmp_D_Ov;
	
	//MyFunc func;
//	ArrayList<int[]> ffff = new  
//	ArrayList <int> ov_list = new ArrayList<int>();
	
//	public boolean fault_state,restore_state,steady_state,is_junction;
//	public int ticker = 5000;
	
	public DB(String ag_name, int ag_num){

		this.Ag_Name = ag_name;
		this.value_init();
		this.init_list();
		this.AG_THIS	= ag_num;
		this.AG_LEFT	= AG_THIS-1;
		this.AG_RIGHT	= AG_THIS+1;
		//func = new MyFunc();
	}
	public DB(){
		this.AG_THIS	= -5;
		this.AG_LEFT	= AG_THIS-1;
		this.AG_RIGHT	= AG_THIS+1;
	}

	public void value_init(){
		
		Comm_Counter = new int[AG_MAXNUM];
		for (int i=0; i<AG_MAXNUM; i++)
			Comm_Counter[i]	=	0;
		
		/*		initialize array of LinkedList		*/
		try{
			for(int i=0; i<ag_view_R.length; i++) {
				ag_view_R[i] = new LinkedList<Integer>();
			}
			for(int i=0; i<ag_view_L.length; i++) {
				ag_view_L[i] = new LinkedList<Integer>();
			}
			tmp_ag_view_R	=	ag_view_R;
			tmp_ag_view_L	=	ag_view_L;
		}
		catch(Exception e){
			Func.print_msg("DB Says:	"+e.toString());
		}

/*		
		// another way for array of LinkedList
		try{
			for(int i=0; i<ag_view_Matrix.length; i++)
				ag_view_Matrix[i] = new ArrayList<Integer>();
			ag_view_Matrix[0].add(22);
		}
		catch(Exception e){
			func.print_msg(e.toString());
		}
*/
		
		Ag_State = new int[AG_MAXNUM];
		Ag_Time = new double[AG_MAXNUM];
		tmp_Ag_State = new int[AG_MAXNUM];
		tmp_Ag_Time = new double[AG_MAXNUM];

		Ag_State[0] = S_ACTIVE;
		Ag_State[1] = S_ACTIVE;
		Ag_State[2] = S_ACTIVE;
		Ag_State[3] = S_ACTIVE;
		tmp_Ag_State	=	Ag_State;

		Ag_Time[0] = 5.1;
		Ag_Time[1] = 0.2;
		Ag_Time[2] = 0.3;
		Ag_Time[3] = 3.4;
		tmp_Ag_Time	=	Ag_Time;
		
		D_Nov = new int[4][20];
		tmp_D_Nov = new int[4][20];
        D_Nov[0][0] = 1;
		D_Nov[0][1] = 2;
		D_Nov[0][2] = 3;
		D_Nov[0][3] = 4;

		D_Nov[1][0] = 5;
		D_Nov[1][1] = 6;
		D_Nov[1][2] = 7;
		D_Nov[1][3] = 8;

        D_Nov[2][0] = 9;
		D_Nov[2][1] = 10;
		D_Nov[2][2] = 11;
		D_Nov[2][3] = 12;

        D_Nov[3][0] = 13;
		D_Nov[3][1] = 14;
		D_Nov[3][2] = 15;
		D_Nov[3][3] = 16;

		int Max_Index = 500;
		D_Ov = new int[4][5][Max_Index];
		tmp_D_Ov = new int[4][5][Max_Index];
		D_Ov[0][1][0] = 2;
		D_Ov[0][1][1] = 3;
		D_Ov[0][1][2] = 4;
		D_Ov[0][1][3] = 5;
		D_Ov[0][1][4] = 6;



		D_Ov[1][2][0] = 7;
		D_Ov[1][2][1] = 8;
		D_Ov[1][2][2] = 9;


		D_Ov[2][3][0] = 11;
		D_Ov[2][3][1] = 12;
		D_Ov[2][3][2] = 13;
		D_Ov[2][3][3] = 14;
		D_Ov[2][3][4] = 15;

		/*	The Data Generator	*/
		/*	Note: The data must have not Zero value	*/
		for (int i=0; i < AG_MAXNUM; i++) {
			for(int j=i+1; j <= i+1; j++) {
				for (int k=0; k < this.D_Ov[i][j].length; k++) {
					D_Ov[i][j][k] = i*Max_Index+k+1;
				}
			}
		}
		/*	copy values to the opposite array	*/
		int nonzero_index = 0;
		for (int i=0; i < AG_MAXNUM-1; i++) {
			for(int j=i+1; j <= i+1; j++) {
				for (int k=0; k < this.D_Ov[i][j].length; k++) {
					if (this.D_Ov[i][j][k] != 0) {
						nonzero_index++;
					}
				}
				for (int k=0; k < nonzero_index; k++) {// copy values to the opposite array
					D_Ov[j][i][nonzero_index-k-1] = D_Ov[i][j][k];
				}
				nonzero_index = 0;
			}
		}

		tmp_D_Ov	=	D_Ov;
		tmp_D_Nov	=	D_Nov;
}
/**
 * 
 */
	public void init_list(){
		
	}
/**
 * select_value(int ds, int dd)
 * @param ds = source value d
 * @param dd = dest   value d
 */
	public void select_value(int ds, int dd){
		for (int i=0; i < this.D_Ov[ds][dd].length; i++) {
			try {
				if(this.D_Ov[ds][dd][i]!=0){
					if(ds<dd){
						if ( ! ag_view_R[AG_THIS].contains(this.D_Ov[ds][dd][i])) {	// Check if not assigned before
							ag_view_R[AG_THIS].add(this.D_Ov[ds][dd][i]);
//							display(AG_THIS,"Add Value:	"+Integer.toString(D_Ov[ds][dd][i])+"	from Right");
//							display(AG_THIS,"---------------------------------------------------");
						}
						else
							Func.show_msg("The value was assigned before", "Warning!!!");
					}
					else{
						if ( ! ag_view_L[AG_THIS].contains(this.D_Ov[ds][dd][i])) {	// Check if not assigned before
							ag_view_L[AG_THIS].add(this.D_Ov[ds][dd][i]);
//							display(AG_THIS,"Add Value:	"+Integer.toString(D_Ov[ds][dd][i])+"	from Left");
//							display(AG_THIS,"---------------------------------------------------");
						}
						else
							Func.show_msg("The value was assigned before", "Warning!!!");
					}
					remove_DOv_value(ds,dd,i);
					estimate_agent_time();
					return;
				}
			}catch(Exception e) {
				display(AG_THIS,"DB syas:	"+e.toString());
			}
		}
	}
/**
 * void remove_DOv_value(int ds, int dd, int index)
 * this function removes the selected value from D_Ov (overlapping matrix)
 * @param ds = source value d
 * @param dd = dest   value d
 * @param index = index of the array to be remove
 * 
 */
	public void remove_DOv_value(int ds, int dd, int index){
		int tmp = this.D_Ov[ds][dd][index];
		this.D_Ov[ds][dd][index] = 0;
		for (int i=0; i < this.D_Ov[dd][ds].length; i++) {		// remove the same value for the opposite array
			if (this.D_Ov[dd][ds][i] == tmp)
				this.D_Ov[dd][ds][i] = 0;
		}
	}
	
	/**
	 * update_agent_time()
	 * 
	 * Estimating agent completion time at each iteration according to its  
	 */
	public void estimate_agent_time() {
		this.Ag_Time[AG_THIS]++;
		String tmp 	= "";
		String tmp2 = "";
		for (int i=0; i<AG_MAXNUM; i++) {
			tmp		+= "QCA_"+Integer.toString(i)+": "+Double.toString(this.Ag_Time[i])+"	";
			tmp2	+= "QCA_"+Integer.toString(i)+": "+this.Ag_State[i]+"	";
		}
		display(AG_THIS, "Extimated time for all agents:	"+tmp);
		display(AG_THIS, "State till now for all agents:	"+tmp2);
		display(AG_THIS, "---------------------------------------------------");
//		Func.print_msg("DB syas:	The time is now:	"+Func.IntToStr(Ag_Time[AG_THIS]));
	}

/**
 * void schedule_value()
 * Constructing OPL Model
 * Connecting to CPLEX
 * Scheduling assigned tasks in each iteration
 */
	public void schedule_value(int ag){
//		display(ag, "Constructing OPL MOdel");
//		display(ag, "Connect to CPLEX ...");
		try {
			Thread.sleep(OPL_Sleep_Time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		display(ag, "The Completion time is updated by CPLEX");
//		display(ag, "===================================================");
//		this.Ag_Time[ag] = this.Ag_Time[ag]*.85;
	}
	
	public synchronized void display(int ag, String str) {
		Func.print_queue[ag].add(str);
	}
	
}
