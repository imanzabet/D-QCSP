package QUAY_CRANES_2;
/*						Modify the following items for each agent
 * 1- class name	
 * 2- AG_THIS
 * 3- class constructor
 */
import QUAY_CRANES_2.DB;
import QUAY_CRANES_2.Func;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import javax.swing.JOptionPane;
import java.io.Serializable;
import ilog.concert.*;
import ilog.cp.*;
import ilog.cplex.*;
import ilog.opl.*;

@SuppressWarnings("unused")
public class QCA_3 extends Agent {
 	private static final long serialVersionUID = 1L;
 	private final static int AG_THIS = 3;
 	private final int AG_LEFT = AG_THIS-1;
 	private final int AG_RIGHT = AG_THIS+1;
 	private static final int S_ACTIVE = 0;
 	private static final int S_PASSIVE = 1;
 	private static final int S_DONE= 2;
 
	public AID QCA_AID;
	DB adb = new DB("QCA_"+String.valueOf(AG_THIS)	, AG_THIS);
	Func func = new Func();

	ACLMessage prepared_msg;
	private String Logger_Msg;
	
	private int Comm_Count = 0;
	int counter = 2000;
	/**
	 * Constructor of QCA1 and initializer
	 * 
	 * @author Zabet
	 */
	public QCA_3(){
		QCA_AID = new AID("QCA_"+Integer.toString(AG_THIS), AID.ISLOCALNAME);
//		if (AG_THIS == 0)
//			new Logger_Agent();
	}

	protected void setup() {
 	
		Func.print_queue[AG_THIS].add("Hello World! My name is "+ this.getLocalName());
 	
 	// first QCA should send run to its neighbor
/////////////////
	addBehaviour(new CyclicBehaviour(this) {
		private static final long serialVersionUID = 1L;
		public void action(){  
		 	if (AG_THIS == 0	&&	++counter >= DB.Send_Counter){
		 		counter = 0;
		 		try{
			 		int option = Func.show_msg_return("run! = YES, test=NO", "Question");
			 		if (option == JOptionPane.YES_OPTION) {
			 			adb.D_Ov		=	adb.tmp_D_Ov;
			 			adb.D_Nov		=	adb.tmp_D_Nov;
			 			adb.ag_view_R	=	adb.tmp_ag_view_R;
			 			adb.ag_view_L	=	adb.tmp_ag_view_L;
			 			adb.Ag_State	=	adb.tmp_Ag_State;
			 			adb.Ag_Time		=	adb.tmp_Ag_Time;
			 			send_msg("run!", AG_THIS, AG_RIGHT);
			 		}
			 		else if (option == JOptionPane.NO_OPTION) {
			 			log("try to send first run!",AG_THIS);
			 			send_msg("test!", AG_THIS, AG_RIGHT);
			 		}
			 		else if (option == JOptionPane.CANCEL_OPTION){
						Thread.sleep(5);
			 		}
			 		//send_msg("run!", AG_THIS, AG_RIGHT+1);
			 		//send_msg("run!", AG_THIS, AG_RIGHT+2);
				}
				catch(InterruptedException ie){
					ie.printStackTrace();
				}
		 	}
			ACLMessage msg = receive();
			if(msg != null){
				//System.out.println(func.getDateTime()+"   "+myAgent.getLocalName()+ "	receive message from:		"+msg.getSender().getLocalName());
        		//int joption = JOptionPane.showConfirmDialog(null,"A message is received", "Information", JOptionPane.YES_NO_OPTION);joption++;
				Comm_Count++;
				DB.Comm_Counter[AG_THIS]++;
				try{
					Object sm = msg.getContentObject();
					if (msg.getPerformative() == ACLMessage.REQUEST){
						if (sm instanceof SerialMsg) {
							SerialMsg struct = (SerialMsg) sm;
							String log_str = "<--	"+struct.Msg_Type+"	from "+struct.SenderName;
							//System.out.println(func.getDateTime()+"   "+getLocalName()+" : receive Serialliable Messeage from:	QCA_"+Integer.toString(struct.Sender));
							if ( Func.is_Str_Equal(struct.Msg_Type, "RUN!")){
								// convert string num to its relative int
								//int src = Integer.valueOf(msg.getSender().getLocalName().toCharArray()[4]) - 48 ; //receive interger value of char
								log(log_str, AG_THIS);
								update_view(struct);
								check_agent_view();
							}
							else if	( Func.is_Str_Equal(struct.Msg_Type, "REQUEST_VALUE?")){
								log(log_str, AG_THIS);
//								update_view();
								if (adb.Ag_State[AG_THIS] == S_DONE){
									adb.Ag_State[AG_THIS] = S_PASSIVE;
								}
//								remove(requested value);
//								send_msg("ADD_VALUE!", AG_THIS, ag_des)
								adb.schedule_value(AG_THIS);
							}
							else if	( Func.is_Str_Equal(struct.Msg_Type, "ADD_VALUE!")){
								log(log_str, AG_THIS);
//								update_view(struct);
//								select_value(d);
								check_agent_view();
								adb.schedule_value(AG_THIS);
							}
							else if	( Func.is_Str_Equal(struct.Msg_Type, "NO_VALUE!")){
								log(log_str, AG_THIS);
								update_view(struct);
//								if (adb.Ag_State[???] == S_ACTIVE){
//									Ag_State[???] = S_PASSIVE;
//									update_view();
//								}
								check_agent_view();
							}
							else if	( Func.is_Str_Equal(struct.Msg_Type, "UPDATE_VALUE!")){
								log(log_str, AG_THIS);
								update_view(struct);
//								if (adb.Ag_State[???] == S_ACTIVE){
//									Ag_State[???] = S_PASSIVE;
//									update_view();
//								}
							}
							else if	( Func.is_Str_Equal(struct.Msg_Type, "TEST!")){
								log(log_str, AG_THIS);
								check_agent_view();
							}
							else{
								log("###	the received message type from:	"+struct.SenderName+" is : "+struct.Msg_Type+"	and doesn't match", AG_THIS);
							}

						}
					}
					
				}catch(Exception e){

					log("###	can't receive message from:	"+msg.getSender().getLocalName(), AG_THIS);
	        	}
  			}
			else{
				try{
				Thread.sleep(DB.Loop_Sleep_Time);
				}
				catch(InterruptedException ie){
					ie.printStackTrace();
				}
			}
		}
	});
 	} 
	/* *********************************************************************************************** */	
	/**
	 * 
	 */
	public void log(String msg, int ag) {
		Logger_Msg	=	msg;
		send_msg ("info!", ag, DB.AG_LOGGER);
	}
	
	
	/**
	 * send_msg (String type_msg, int ag_src, int ag_des)
	 */
	public void send_msg (String type_msg, int ag_src, int ag_des){
		if ( !( 	(ag_des>=0 && ag_des<=DB.AG_MAXNUM) 
				||	ag_des == DB.AG_LOGGER)	){
    		Func.show_msg("###	The destination is not resolved for sending message.", "Warning!!!");
			return;
		}
		SerialMsg sm = new SerialMsg();
		if (type_msg	==	"run!"){

			sm.Ag_State		=	adb.Ag_State;
			sm.Ag_Time		=	adb.Ag_Time;
			sm.Ag_Values	=	adb.Ag_Values;
			sm.ag_view_L	=	adb.ag_view_L;
			sm.ag_view_R	=	adb.ag_view_R;
			sm.D_Nov		=	adb.D_Nov;
			sm.D_Ov			=	adb.D_Ov;
			sm.Msg_Type		=	"RUN!";
			sm.SenderNum	=	AG_THIS;
			sm.SenderName	=	this.getLocalName();
			
			this.prepared_msg = new ACLMessage(ACLMessage.REQUEST);
			this.prepared_msg.addReceiver(new AID("QCA_"+Integer.toString(ag_des),AID.ISLOCALNAME));
			try{
				this.prepared_msg.setContentObject(sm);
        		send(this.prepared_msg);
//        		func.show_msg("send run! from "+Integer.toString(ag_src)+" to "+Integer.toString(ag_des), "Information");
        		log("-->	"+sm.Msg_Type+ "	to QCA_"+Integer.toString(ag_des), AG_THIS);

			}catch(Exception e){
        	
				log("###	can't send.		"+e.toString(), AG_THIS);
        	}
		}
		
		else if (type_msg	==	"no_value!"){
			sm.Ag_State		=	adb.Ag_State;
			sm.Ag_Time		=	adb.Ag_Time;
			sm.Ag_Values	=	adb.Ag_Values;
			sm.ag_view_L	=	adb.ag_view_L;
			sm.ag_view_R	=	adb.ag_view_R;
			sm.D_Nov		=	adb.D_Nov;
			sm.D_Ov			=	adb.D_Ov;
			sm.Msg_Type		=	"NO_VALUE!";
			sm.SenderNum	=	AG_THIS;
			sm.SenderName	=	this.getLocalName();
			
			this.prepared_msg = new ACLMessage(ACLMessage.REQUEST);
			this.prepared_msg.addReceiver(new AID("QCA_"+Integer.toString(ag_des),AID.ISLOCALNAME));
			try{
				this.prepared_msg.setContentObject(sm);
        		send(this.prepared_msg);
//        		func.show_msg("send run! from "+Integer.toString(ag_src)+" to "+Integer.toString(ag_des), "Information");
        		log("-->	"+sm.Msg_Type+ "	to QCA_"+Integer.toString(ag_des), AG_THIS);

			}catch(Exception e){
        	
				log("###	can't send.		"+e.toString(), AG_THIS);
        	}
		}
		else if (type_msg	==	"update_value!"){
			sm.Ag_State		=	adb.Ag_State;
			sm.Ag_Time		=	adb.Ag_Time;
			sm.Ag_Values	=	adb.Ag_Values;
			sm.ag_view_L	=	adb.ag_view_L;
			sm.ag_view_R	=	adb.ag_view_R;
			sm.D_Nov		=	adb.D_Nov;
			sm.D_Ov			=	adb.D_Ov;
			sm.Msg_Type		=	"UPDATE_VALUE!";
			sm.SenderNum	=	AG_THIS;
			sm.SenderName	=	this.getLocalName();
			
			this.prepared_msg = new ACLMessage(ACLMessage.REQUEST);
			this.prepared_msg.addReceiver(new AID("QCA_"+Integer.toString(ag_des),AID.ISLOCALNAME));
			try{
				this.prepared_msg.setContentObject(sm);
        		send(this.prepared_msg);
//        		func.show_msg("send run! from "+Integer.toString(ag_src)+" to "+Integer.toString(ag_des), "Information");
        		log("-->	"+sm.Msg_Type+ "	to QCA_"+Integer.toString(ag_des), AG_THIS);

			}catch(Exception e){
        	
				log("###	can't send.		"+e.toString(), AG_THIS);
        	}
		}
		
		else if (type_msg	==	"request_value"){
			/*	NOT COMPLETED YET*/
			sm.Msg_Type	=	"REQUEST_VALUE?";

 			log("@@@-->	Send	"+sm.Msg_Type+ "	to QCA_"+Integer.toString(ag_des), AG_THIS);
		}

		else if (type_msg	==	"test!"){
			sm.Msg_Type	=	"TEST!";
			sm.SenderNum	=	AG_THIS;
			sm.SenderName	=	this.getLocalName();
			
			this.prepared_msg = new ACLMessage(ACLMessage.REQUEST);
			this.prepared_msg.addReceiver(new AID("QCA_"+Integer.toString(ag_des),AID.ISLOCALNAME));
			try{
				this.prepared_msg.setContentObject(sm);
        		send(this.prepared_msg);
//        		func.show_msg("send run! from "+Integer.toString(ag_src)+" to "+Integer.toString(ag_des), "Information");
				log("-->	"+sm.Msg_Type+ "	to QCA_"+Integer.toString(ag_des), AG_THIS);
        	}catch(Exception e){
        		log("@@@	can't send.	"+e.toString(), AG_THIS);
        	}
		}
		else if (type_msg	==	"info!"){
			sm.Ag_State		=	adb.Ag_State;
			sm.Ag_Time		=	adb.Ag_Time;
			sm.Ag_Values	=	adb.Ag_Values;
			sm.ag_view_L	=	adb.ag_view_L;
			sm.ag_view_R	=	adb.ag_view_R;
			sm.D_Nov		=	adb.D_Nov;
			sm.D_Ov			=	adb.D_Ov;
			
			sm.Msg_Type		=	"INFO!";
			sm.SenderNum	=	AG_THIS;
			sm.SenderName	=	this.getLocalName();
			sm.InternalMsg	=	Logger_Msg;
			
			
			this.prepared_msg = new ACLMessage(ACLMessage.INFORM);
			this.prepared_msg.addReceiver(new AID("Logger_Agent",AID.ISLOCALNAME));
			try{
				this.prepared_msg.setContentObject(sm);
        		send(this.prepared_msg);
			}catch(Exception e){
        	
				log("###	can't send.		"+e.toString(), AG_THIS);
        	}
		}
		else{
    		Func.show_msg("###	Incorrect Message Type", "Warning!!!");
			log("###	cannot send *** INCORRECT MESSAGE ***", AG_THIS);
		}
	}
	/**
	 * check agent view ()
	 */
	public void check_agent_view(){
	try {
/* ************************************************************************************************ */	
/* Check if agent is in MIDLLE								PHASE A									*/
/* ************************************************************************************************ */	
		Thread.sleep(DB.Loop_Sleep_Time_2);
		if (	AG_THIS>0 && AG_THIS<DB.AG_MAXNUM-1 ){									//(A)
			log("State:	(A):	The Middle Agent", AG_THIS);

			// Check if agent is within its neighbors
			if ( 	(		adb.Ag_Time[AG_THIS]<=	adb.Ag_Time[AG_LEFT]) 
			     	 &&(	adb.Ag_Time[AG_THIS]<=	adb.Ag_Time[AG_RIGHT])				//(A-I)
			    	){// && t(i)<t(i+1) )
				log("State:	(A-I):	t(i-1) >= T <= t(i+1)", AG_THIS);

				// Check agent states (is active)
				if (adb.Ag_State[AG_THIS] == S_ACTIVE){									//(A-I-a)
					log("State:	(A-I-a):	agent is ACTIVE", AG_THIS);

					// Select first from right
					if ( ! func.is_empty(adb.D_Ov[AG_THIS][AG_RIGHT]) ){				//(A-I-a-1) 
						log("State:	(A-I-a-1):	select from right", AG_THIS);

						adb.select_value(AG_THIS, AG_RIGHT);//d from Di,i+1)
						adb.schedule_value(AG_THIS);
						send_msg("run!", AG_THIS, AG_RIGHT);//run(), (xi,si,di,ti) to xi+1)
					}
					// else select from left
					else if ( ! func.is_empty(adb.D_Ov[AG_THIS][AG_LEFT]) ){ 			//(A-I-a-2)
						log("State:	(A-I-a-2):	select from left", AG_THIS);

						adb.select_value(AG_THIS, AG_LEFT); //d from Di-1,i)
						adb.schedule_value(AG_THIS);
						send_msg("run!", AG_THIS, AG_LEFT);	//run(), (xi,si,di,ti) to xi-1)
					}
					// if no value exists to select neither from right nor from left
					else{																//(A-I-a-3)
						log("State:	(A-I-a-3):	no value to select -> x(i)=PASSIVE", AG_THIS);

						adb.Ag_State[AG_THIS] = S_PASSIVE;
						// send no_value to the neighbors
						send_msg("no_value!", AG_THIS, AG_RIGHT);
						send_msg("no_value!", AG_THIS, AG_LEFT);
					}
				}
				// if all values were selected by their near agents
				else if (	adb.Ag_State[AG_THIS]	==	S_PASSIVE
						&&	(adb.Ag_State[AG_LEFT]	==	S_PASSIVE)
						&&	(adb.Ag_State[AG_RIGHT]	==	S_PASSIVE)){					//(A-I-b)
					log("State:	(A-I-b):	x(i)=PASSIVE", AG_THIS);

					// First select non-selected value from left
					if(			! func.is_empty(adb.D_Ov[AG_THIS][AG_LEFT]) ) {			//(A-I-b-1)
						log("State:	(A-I-b-1):	BackTrack from Left", AG_THIS);

						backtrack(AG_THIS, AG_LEFT);
					}
					else if(	! func.is_empty(adb.D_Ov[AG_THIS][AG_RIGHT]) ) {		//(A-I-b-2)
						log("State:	(A-I-b-2):	BackTrack from Right", AG_THIS);

						backtrack(AG_THIS, AG_RIGHT);
					}
					else if(	func.is_empty(adb.D_Ov[AG_THIS][AG_LEFT])
							&&	func.is_empty(adb.D_Ov[AG_THIS][AG_RIGHT])
							&&	(adb.Ag_State[AG_LEFT]	==	S_PASSIVE )
							&&	(adb.Ag_State[AG_RIGHT]	==	S_PASSIVE )){				//(A-I-b-3)
						log("State:	(A-I-b-3):	no value to select -> DONE", AG_THIS);

						adb.Ag_State[AG_THIS] = S_DONE;
						// send no_value to the neighbors
						send_msg("no_value!", AG_THIS, AG_RIGHT);
						send_msg("no_value!", AG_THIS, AG_LEFT);
					}
				}
				// xi is passive && any neighbor is not passive
				else if (	adb.Ag_State[AG_THIS] 	== 	S_PASSIVE
						&&	(	(adb.Ag_State[AG_LEFT]	!=	S_PASSIVE )
							||	(adb.Ag_State[AG_RIGHT]	!=	S_PASSIVE )) ){				//(A-I-c)
					log("State:	(A-I-c):	x(i)=PASSIVE & any neighbours is PASSIVE", AG_THIS);

					if	(adb.Ag_State[AG_RIGHT]	==	S_ACTIVE) {							//(A-I-c-1)
						log("State:	(A-I-c-1):	right is ACTIVE", AG_THIS);

						send_msg("run!", AG_THIS, AG_RIGHT);
					}
					else if	(adb.Ag_State[AG_LEFT]	==	S_ACTIVE) {						//(A-I-c-2)
						log("State:	(A-I-c-2):	left is ACTIVE", AG_THIS);

						send_msg("run!", AG_THIS, AG_LEFT);
					}
					else if (	adb.Ag_State[AG_RIGHT]	==	S_DONE
							&&	adb.Ag_State[AG_LEFT]	==	S_DONE){					//(A-I-c-3)
						log("State:	(A-I-c-3): all neighbors are DONE", AG_THIS);

						adb.Ag_State[AG_THIS] = S_PASSIVE;

						send_msg("no_value!", AG_THIS, AG_RIGHT);
						send_msg("no_value!", AG_THIS, AG_LEFT);
					}
				}
			}
			// Check  if all values were selected by neighbors		
			else if (	(	adb.Ag_Time[AG_THIS]< 	adb.Ag_Time[AG_LEFT]	) 
		             &&	(	adb.Ag_Time[AG_THIS]>= 	adb.Ag_Time[AG_RIGHT]	)
		             ){// && t(i)<t(i-1) && (t(i)>=t(i+1) ) && (xi is active) )			//(A-II)
				log("State:	(A-II):	t(i-1) > T >= t(i+1)", AG_THIS);

				if (	adb.Ag_State[AG_THIS]	==	S_ACTIVE) {
					if (	(adb.Ag_State[AG_RIGHT]	==	S_ACTIVE)) {					//(A-II-a)
						log("State:	(A-II-a):	x(i)=ACTIVE and x(i+1)=ACTIVE", AG_THIS);

						send_msg("run!", AG_THIS, AG_RIGHT);	//run(x(i+1));
					}
					else if (adb.Ag_State[AG_RIGHT]	!=	S_ACTIVE){						//(A-II-b)
						log("State:	(A-II-b):	x(i)=ACTIVE and x(i+1)!=ACTIVE " +
								"-> select_value and send run to neighbors", AG_THIS);

						adb.select_value(AG_THIS, AG_RIGHT);//select_value(D(i,i+1));
						adb.schedule_value(AG_THIS);
						
						// send run! to neighbors
						send_msg("run!", AG_THIS, AG_RIGHT);	//run(x(i+1));
						send_msg("run!", AG_THIS, AG_LEFT);		//run(x(i-1));
					}
				}
			}
			else if (	(	adb.Ag_Time[AG_THIS]> 	adb.Ag_Time[AG_LEFT]) 				//(A-III)
		             &&	(	adb.Ag_Time[AG_THIS]<= 	adb.Ag_Time[AG_RIGHT])
		 			){// && t(i)>t(i-1) && (t(i)<t(i+1) ) && (xi is active) )
				log("State:	(A-III):	t(i-1) < T <= t(i+1)", AG_THIS);

				if (adb.Ag_State[AG_THIS]	==	S_ACTIVE) {
					if (	(adb.Ag_State[AG_LEFT]	==	S_ACTIVE)) {					//(A-III-a)
						log("State:	(A-III-a):	x(i)=ACTIVE and x(i-1)=ACTIVE", AG_THIS);

						send_msg("run!", AG_THIS, AG_LEFT);	//run(x(i-1));
					}
					else if (adb.Ag_State[AG_LEFT]	!=	S_ACTIVE){						//(A-III-b)
						log("State:	(A-III-b):	x(i)=ACTIVE && x(i-1)!=ACTIVE " +
								"-> select_value and send run to neighbors", AG_THIS);

						adb.select_value(AG_THIS, AG_LEFT);	//select_value(D(i-1,i));
						adb.schedule_value(AG_THIS);
						
						// send run! to neighbors
						send_msg("run!", AG_THIS, AG_RIGHT);	//run(x(i+1));
						send_msg("run!", AG_THIS, AG_LEFT);		//run(x(i-1));
					}
					else																//(A-III-c)
						send_msg("run!", AG_THIS, AG_RIGHT);	//run(x(i+1));
				}
			}
			// && t(i)>t(i-1) && (t(i)>t(i+1) ) && (xi is active) )
			else if (	(	adb.Ag_Time[AG_THIS]> 	adb.Ag_Time[AG_LEFT]) 
		             &&	(	adb.Ag_Time[AG_THIS]> 	adb.Ag_Time[AG_RIGHT])
		             &&	adb.Ag_State[AG_THIS]	==	S_ACTIVE) {							//(A-IV)
				log("State:	(A-IV):	t(i-1) < T > t(i+1)", AG_THIS);
		 			
				if (	adb.Ag_Time[AG_RIGHT] > 	adb.Ag_Time[AG_LEFT]
				    &&	adb.Ag_State[AG_LEFT]	==	S_ACTIVE ) {						//(A-IV-a)
					log("State:	(A-IV-a):	t(i+1) > t(i-1) && x(i-1)=ACTIVE", AG_THIS);
					
					send_msg("run!", AG_THIS, AG_LEFT);	//run(x(i-1));
				}
				else if (	adb.Ag_Time[AG_RIGHT] < 	adb.Ag_Time[AG_LEFT]
						&&	adb.Ag_State[AG_RIGHT]	==	S_ACTIVE ) {					//(A-IV-b)
					log("State:	(A-IV-b):	t(i+1) < t(i-1) && x(i+1)=ACTIVE", AG_THIS);
					
					send_msg("run!", AG_THIS, AG_RIGHT);	//run(x(i-1));
				}
				else if (	adb.Ag_State[AG_RIGHT]	!=	S_ACTIVE){						//(A-IV-c)
					log("State:	(A-IV-c):	x(i+1)!=ACTIVE -> select_value from right & run! to right", AG_THIS);

					adb.select_value(AG_THIS, AG_RIGHT);	//select_value(D(i,i+1));
					adb.schedule_value(AG_THIS);
					// send run! to neighbors
					send_msg("run!", AG_THIS, AG_RIGHT);	//run(x(i+1));
				}
				
				else if (adb.Ag_State[AG_LEFT]	!=	S_ACTIVE){							//(A-IV-d)
					log("State:	(A-IV-d):	x(i-1)!=ACTIVE -> select_value from left & run! to left", 
							AG_THIS);

					adb.select_value(AG_THIS, AG_LEFT);		//select_value(D(i,i+1));
					adb.schedule_value(AG_THIS);
					// send run! to neighbors
					send_msg("run!", AG_THIS, AG_LEFT);		//run(x(i-1));
				}
			}
			// xi is passive
			else if (	adb.Ag_State[AG_THIS]	==	S_PASSIVE){							//(A-V)
				log("State:	(A-V):	x(i)=PASSIVE", AG_THIS);
				// check if still any neighbor agent is being in active state
				// send run! to  the active neighbors
				if (	adb.Ag_State[AG_RIGHT]	==	S_ACTIVE	) {						//(A-V-a)
					log("State:	(A-V-a):	x(i+1)=ACTIVE", AG_THIS);
					send_msg("run!", AG_THIS, AG_RIGHT);	//run(x(i+1));
				}
				else if (	adb.Ag_State[AG_LEFT]	==	S_ACTIVE	) {					//(A-V-b)
					log("State:	(A-V-b):	x(i-1)=ACTIVE", AG_THIS);
					send_msg("run!", AG_THIS, AG_LEFT);		//run(x(i-1));
				}
				// check if all neighbors has no possible value and check problem’s consistency
				else if (	func.is_empty(adb.D_Ov[AG_THIS][AG_LEFT]) ){				//(A-V-c)
					log("State:	(A-V-c):	D(i-1)=Empty", AG_THIS);
					double abs_dif_left = Math.abs(adb.Ag_Time[AG_THIS]-adb.Ag_Time[AG_LEFT]);
					// check sub_solution consistent with neighbor x_j – first left then right
					if (	abs_dif_left	> 	adb.TresholdTime){						//(A-V-c-1)
						log("State:	(A-V-c-1):	|t(i-1)-T| > Threshold", AG_THIS);
						if (	adb.Ag_State[AG_LEFT]	==	S_DONE){
							log("State:	(A-V-c-1-1)", AG_THIS);
//							ask_state(left,passive);
							adb.Ag_State[AG_LEFT]	=	S_PASSIVE;
							send_msg("update_value!",AG_THIS, AG_RIGHT);
							send_msg("update_value!",AG_THIS, AG_LEFT);
						}
					//request the most appropriate task from neighbor
//					request_value (left);
//					backtrack(left);
					}
					else {																//(A-V-c-2)
						log("State:	(A-V-c-2):	|t(i-1)-T| <<< Threshold", AG_THIS);
						adb.Ag_State[AG_THIS]	=	S_DONE;
						send_msg("no_value!", AG_THIS, AG_RIGHT);
						send_msg("no_value!", AG_THIS, AG_LEFT);
					}
				}
				else if (func.is_empty(adb.D_Ov[AG_THIS][AG_RIGHT]) ){					//(A-V-d)
					log("State:	(A-V-d):	D(i+1)=Empty", AG_THIS);
					double abs_dif_right= Math.abs(adb.Ag_Time[AG_THIS]-adb.Ag_Time[AG_RIGHT]);
					
					if (	abs_dif_right	>	adb.TresholdTime){						//(A-V-d-1)
						log("State:	(A-V-d-1):	|t(i+1)-T| > Threshold", AG_THIS);
						if (	adb.Ag_State[AG_RIGHT]	==	S_DONE){
//							ask_state(right,passive);
							adb.Ag_State[AG_RIGHT]	=	S_PASSIVE;
							send_msg("update_value!",AG_THIS, AG_RIGHT);
							send_msg("update_value!",AG_THIS, AG_LEFT);
						}
					//request the most appropriate task from neighbor
//					request_value (right);
//					backtrack(right);
					}
					else {																//(A-V-d-2)
						log("State:	(A-V-d-2):	|t(i+1)-T| <<< Threshold", AG_THIS);
						adb.Ag_State[AG_THIS]	=	S_DONE;
						send_msg("no_value!", AG_THIS, AG_RIGHT);
						send_msg("no_value!", AG_THIS, AG_LEFT);
					}
				}
			}
			// xi is Done			
			else if (	adb.Ag_State[AG_THIS]	==	S_DONE){							//(A-VI)
				log("State:	(A-VI):	x(i)=DONE", AG_THIS);
				if(			adb.Ag_State[AG_LEFT]	==	S_ACTIVE	)	{				//(A-VI-a)
					log("State:	(A-VI-a):	x(i-1)=ACTIVE", AG_THIS);
					
					send_msg("run!", AG_THIS, AG_LEFT); //run(x(i-1));
				}
				
				else if (	adb.Ag_State[AG_RIGHT]	==	S_ACTIVE	){					//(A-VI-b)
					log("State:	(A-VI-b):	x(i+1)=ACTIVE", AG_THIS);
					
					send_msg("run!", AG_THIS, AG_RIGHT);	//run(x(i+1));
				}
				
				else if(			adb.Ag_State[AG_LEFT]	==	S_PASSIVE	){			//(A-VI-c)
					log("State:	(A-VI-c):	x(i-1)=PASSIVE", AG_THIS);
					
					send_msg("run!", AG_THIS, AG_LEFT);		//run(x(i-1));
				}
				
				else if (	adb.Ag_State[AG_RIGHT]	==	S_PASSIVE	){					//(A-VI-d)
					log("State:	(A-VI-d):	x(i+1)=PASSIVE", AG_THIS);
					send_msg("run!", AG_THIS, AG_RIGHT);	//run(x(i+1));
				}
				
				else if ((	adb.Ag_State[AG_LEFT]	==	S_DONE	)
						&&(	adb.Ag_State[AG_RIGHT]	==	S_DONE	)){						//(A-VI-e)
					log("State:	(A-VI-e):	all DONE -> @@@ Terminate @@@", AG_THIS);
		            
					log("is terminated !!!", AG_THIS);
					log("is terminated !!!", AG_THIS);
					log("is terminated !!!", AG_THIS);
		    		//consistent_subset <- this_solution;
		    		//broadcast terminate algorithm;
		    		// agents may able to find another consistent_solution
		    		// and compare with this_solution 
		    		//so they may restart algorithm again
				}
			}
		}
/* ************************************************************************************************ */	
/* Check if agent is in the most LEFT						PHASE B									*/
/* ************************************************************************************************ */	
		else if ( AG_THIS == 0 ){														//(B)
			log("State:	(B):	The Most Left Agent", AG_THIS);

			// Check if agent is within its neighbors
			if ( 	(	adb.Ag_Time[AG_THIS]<= adb.Ag_Time[AG_RIGHT])					//(B-I)
			    	){// && t(i)<t(i+1) )
				log("State:	(B-I):	t(i-1)>= T <= t(i+1)", AG_THIS);

				// Check agent states (is active)
				if (adb.Ag_State[AG_THIS] == S_ACTIVE){									//(B-I-a)
					log("State:	(B-I-a):	agent is ACTIVE", AG_THIS);

					// Select first from right
					if ( ! func.is_empty(adb.D_Ov[AG_THIS][AG_RIGHT]) ){				//(B-I-a-1) 
						log("State:	(B-I-a-1):	select from right", AG_THIS);

						adb.select_value(AG_THIS, AG_RIGHT);//d from Di,i+1)
						adb.schedule_value(AG_THIS);
						send_msg("run!", AG_THIS, AG_RIGHT);//run(), (xi,si,di,ti) to xi+1)
					}
					// else select from left
//					else if ( ! func.is_empty(adb.D_Ov[AG_THIS][AG_LEFT]) ){ 			//(B-I-a-2)
//						log("State:	(B-I-a-2):	select from left", AG_THIS);
//
//						adb.select_value(AG_THIS, AG_LEFT); //d from Di-1,i)
//						send_msg("run!", AG_THIS, AG_LEFT);	//run(), (xi,si,di,ti) to xi-1)
//						adb.schedule_value(AG_THIS);
//					}
					// if no value exists to select neither from right nor from left
					else{																//(B-I-a-3)
						log("State:	(B-I-a-3):	no value to select -> x(i)=PASSIVE", AG_THIS);

						adb.Ag_State[AG_THIS] = S_PASSIVE;
						// send no_value to the neighbors
						send_msg("no_value!", AG_THIS, AG_RIGHT);
//						send_msg("no_value!", AG_THIS, AG_LEFT);
					}
				}
				// if all values were selected by their near agents
				else if (	adb.Ag_State[AG_THIS]	==	S_PASSIVE
//						&&	(adb.Ag_State[AG_LEFT]	==	S_PASSIVE)
						&&	(adb.Ag_State[AG_RIGHT]	==	S_PASSIVE)){					//(B-I-b)
					log("State:	(B-I-b):	x(i)=PASSIVE", AG_THIS);

					// First select non-selected value from left
//					if(			! func.is_empty(adb.D_Ov[AG_THIS][AG_LEFT]) ) {			//(B-I-b-1)
//						log("State:	(B-I-b-1):	BackTrack from Left", AG_THIS);
//
//						backtrack(AG_THIS, AG_LEFT);
//					}
					if(	! func.is_empty(adb.D_Ov[AG_THIS][AG_RIGHT]) ) {				//(B-I-b-2)
						log("State:	(B-I-b-2):	BackTrack from Right", AG_THIS);

						backtrack(AG_THIS, AG_RIGHT);
					}
					else if(	//func.is_empty(adb.D_Ov[AG_THIS][AG_LEFT])
								func.is_empty(adb.D_Ov[AG_THIS][AG_RIGHT])
//							&&	(adb.Ag_State[AG_LEFT]	==	S_PASSIVE )
							&&	(adb.Ag_State[AG_RIGHT]	==	S_PASSIVE )){				//(B-I-b-3)
						log("State:	(B-I-b-3):	no value to select -> DONE", AG_THIS);

						adb.Ag_State[AG_THIS] = S_DONE;
						// send no_value to the neighbors
						send_msg("no_value!", AG_THIS, AG_RIGHT);
//						send_msg("no_value!", AG_THIS, AG_LEFT);
					}
				}
				// xi is passive && any neighbor is not passive
				else if (	adb.Ag_State[AG_THIS] 	== 	S_PASSIVE
						&&	(adb.Ag_State[AG_RIGHT]	!=	S_PASSIVE )	){					//(B-I-c)
					log("State:	(B-I-c):	x(i)=PASSIVE & any neighbours is PASSIVE", AG_THIS);

					if	(adb.Ag_State[AG_RIGHT]	==	S_ACTIVE) {							//(B-I-c-1)
						log("State:	(B-I-c-1):	right is ACTIVE", AG_THIS);

						send_msg("run!", AG_THIS, AG_RIGHT);
					}
//					else if	(adb.Ag_State[AG_LEFT]	==	S_ACTIVE) {						//(B-I-c-2)
//						log("State:	(B-I-c-2):	left is ACTIVE", AG_THIS);
//
//						send_msg("run!", AG_THIS, AG_LEFT);
//					}
					else if (	adb.Ag_State[AG_RIGHT]	==	S_DONE){					//(B-I-c-3)
						log("State:	(B-I-c-3): all neighbors are DONE", AG_THIS);

						adb.Ag_State[AG_THIS] = S_PASSIVE;
						send_msg("no_value!", AG_THIS, AG_RIGHT);
						send_msg("no_value!", AG_THIS, AG_LEFT);
					}
				}
			}
			// Check  if all values were selected by neighbors		
			else if (	(	adb.Ag_Time[AG_THIS]>= 	adb.Ag_Time[AG_RIGHT]	)
		             ){// && t(i)<t(i-1) && (t(i)>=t(i+1) ) && (xi is active) )			//(B-II)
				log("State:	(B-II):	t(i-1) > T >= t(i+1)", AG_THIS);

				if (	adb.Ag_State[AG_THIS]	==	S_ACTIVE) {
					if (	(adb.Ag_State[AG_RIGHT]	==	S_ACTIVE)) {					//(B-II-a)
						log("State:	(B-II-a):	x(i)=ACTIVE and x(i+1)=ACTIVE", AG_THIS);

						send_msg("run!", AG_THIS, AG_RIGHT);	//run(x(i+1));
					}
					else if (adb.Ag_State[AG_RIGHT]	!=	S_ACTIVE){						//(B-II-b)
						log("State:	(B-II-b):	x(i)=ACTIVE and x(i+1)!=ACTIVE " +
								"-> select_value and send run to neighbor", AG_THIS);

						adb.select_value(AG_THIS, AG_RIGHT);//select_value(D(i,i+1));
						adb.schedule_value(AG_THIS);
						
						// send run! to neighbors
						send_msg("run!", AG_THIS, AG_RIGHT);	//run(x(i+1));
//						send_msg("run!", AG_THIS, AG_LEFT);		//run(x(i-1));
					}
				}
			}
			else if (	(	adb.Ag_Time[AG_THIS]<= 	adb.Ag_Time[AG_RIGHT])
		 			){// && t(i)>t(i-1) && (t(i)<t(i+1) ) && (xi is active) )			//(B-III)
				log("State:	(B-III):	t(i-1) < T <= t(i+1)", AG_THIS);

//				if (adb.Ag_State[AG_THIS]	==	S_ACTIVE) {
//					if (	(adb.Ag_State[AG_LEFT]	==	S_ACTIVE)) {					//(B-III-a)
//						log("State:	(B-III-a):	x(i)=ACTIVE and x(i-1)=ACTIVE", AG_THIS);
//
//						send_msg("run!", AG_THIS, AG_LEFT);	//run(x(i-1));
//					}
//					else if (adb.Ag_State[AG_LEFT]	!=	S_ACTIVE
//							&& 	AG_LEFT>=0){											//(B-III-b)
//						log("State:	(B-III-b):	x(i)=ACTIVE && x(i-1)!=ACTIVE " +
//							"-> select_value and send run to neighbors", AG_THIS);
//
//						adb.select_value(AG_THIS, AG_LEFT);	//select_value(D(i-1,i));
//						
//						// send run! to neighbors
//						send_msg("run!", AG_THIS, AG_RIGHT);	//run(x(i+1));
//						send_msg("run!", AG_THIS, AG_LEFT);		//run(x(i-1));
//						adb.schedule_value(AG_THIS);
//					}
//				}
				send_msg("run!", AG_THIS, AG_RIGHT);	//run(x(i+1));
			}
			// && t(i)>t(i-1) && (t(i)>t(i+1) ) && (xi is active) )
			else if (	(	adb.Ag_Time[AG_THIS]> 	adb.Ag_Time[AG_RIGHT])
		             &&		(adb.Ag_State[AG_THIS]	==	S_ACTIVE) 	){					//(B-IV)
				log("State:	(B-IV):	t(i-1) < T > t(i+1)", AG_THIS);
		 			
//				if (	adb.Ag_Time[AG_RIGHT] > 	adb.Ag_Time[AG_LEFT]
//				    &&	adb.Ag_State[AG_LEFT]	==	S_ACTIVE ) {						//(B-IV-a)
//					log("State:	(B-IV-a):	t(i+1) > t(i-1) && x(i-1)=ACTIVE", AG_THIS);
//					
//					send_msg("run!", AG_THIS, AG_LEFT);	//run(x(i-1));
//				}
				if (	adb.Ag_State[AG_RIGHT]	==	S_ACTIVE ) {						//(B-IV-b)
					log("State:	(B-IV-b):	t(i+1) < t(i-1) && x(i+1)=ACTIVE", AG_THIS);
					
					send_msg("run!", AG_THIS, AG_RIGHT);	//run(x(i-1));
				}
				else if (	adb.Ag_State[AG_RIGHT]	!=	S_ACTIVE){						//(B-IV-c)
					log("State:	(B-IV-c):	x(i+1)!=ACTIVE -> select_value from right & run! to right",	AG_THIS);

					adb.select_value(AG_THIS, AG_RIGHT);	//select_value(D(i,i+1));
					adb.schedule_value(AG_THIS);
					// send run! to neighbors
					send_msg("run!", AG_THIS, AG_RIGHT);	//run(x(i+1));
				}
				
//				else if (adb.Ag_State[AG_LEFT]	!=	S_ACTIVE){							//(B-IV-d)
//					log("State:	(B-IV-d):	x(i-1)!=ACTIVE -> select_value from left & run! to left", 
//							AG_THIS);
//
//					adb.select_value(AG_THIS, AG_LEFT);		//select_value(D(i,i+1));
//					// send run! to neighbors
//					send_msg("run!", AG_THIS, AG_LEFT);		//run(x(i-1));
//					adb.schedule_value(AG_THIS);
//				}
			}
			// xi is passive
			else if (	adb.Ag_State[AG_THIS]	==	S_PASSIVE){							//(B-V)
				log("State:	(B-V):	x(i)=PASSIVE", AG_THIS);
				// check if still any neighbor agent is being in active state
				// send run! to  the active neighbors
				if (	adb.Ag_State[AG_RIGHT]	==	S_ACTIVE	) {						//(B-V-a)
					log("State:	(B-V-a):	x(i+1)=ACTIVE", AG_THIS);
					send_msg("run!", AG_THIS, AG_RIGHT);	//run(x(i+1));
				}
//				else if (	adb.Ag_State[AG_LEFT]	==	S_ACTIVE	) {					//(B-V-b)
//					log("State:	(B-V-b):	x(i-1)=ACTIVE", AG_THIS);
//					send_msg("run!", AG_THIS, AG_LEFT);		//run(x(i-1));
//				}
//				// check if all neighbors has no possible value and check problem’s consistency
//				else if (	func.is_empty(adb.D_Ov[AG_THIS][AG_LEFT]) ){				//(B-V-c)
//					log("State:	(B-V-c):	D(i-1)=Empty", AG_THIS);
//					int abs_dif_left = Math.abs(adb.Ag_Time[AG_THIS]-adb.Ag_Time[AG_LEFT]);
//					// check sub_solution consistent with neighbor x_j – first left then right
//					if (	abs_dif_left	> 	adb.TresholdTime){						//(B-V-c-1)
//						log("State:	(B-V-c-1):	|t(i-1)-T| > Threshold", AG_THIS);
//						if (	adb.Ag_State[AG_LEFT]	==	S_DONE){
//							log("State:	(B-V-c-1-1)", AG_THIS);
//							ask_state(left,passive);
//							adb.Ag_State[AG_LEFT]	=	S_PASSIVE;
//						}
//					//request the most appropriate task from neighbor
//					request_value (left);
//					backtrack(left);
//					}
//					else {																//(B-V-c-2)
//						log("State:	(B-V-c-2):	|t(i-1)-T| <<< Threshold", AG_THIS);
//						send_msg("no_value!", AG_THIS, AG_RIGHT);
//						send_msg("no_value!", AG_THIS, AG_LEFT);
//						adb.Ag_State[AG_THIS]	=	S_DONE;
//					}
//				}
				else if (func.is_empty(adb.D_Ov[AG_THIS][AG_RIGHT]) ){					//(B-V-d)
					log("State:	(B-V-d):	D(i+1)=Empty", AG_THIS);
					double abs_dif_right= Math.abs(adb.Ag_Time[AG_THIS]-adb.Ag_Time[AG_RIGHT]);
					
					if (	abs_dif_right	>	adb.TresholdTime){						//(B-V-d-1)
						log("State:	(B-V-d-1):	|t(i+1)-T| > Threshold", AG_THIS);
						if (	adb.Ag_State[AG_RIGHT]	==	S_DONE){
//							ask_state(right,passive);
							adb.Ag_State[AG_RIGHT]	=	S_PASSIVE;
							send_msg("update_value!",AG_THIS, AG_RIGHT);
						}
					//request the most appropriate task from neighbor
//					request_value (right);
//					backtrack(right);
					}
					else {																//(B-V-d-2)
						log("State:	(B-V-d-2):	|t(i+1)-T| <<< Threshold", AG_THIS);

						adb.Ag_State[AG_THIS]	=	S_DONE;
						send_msg("no_value!", AG_THIS, AG_RIGHT);
//						send_msg("no_value!", AG_THIS, AG_LEFT);
					}
				}
			}
			// xi is Done			
			else if (	adb.Ag_State[AG_THIS]	==	S_DONE){							//(B-VI)
				log("State:	(B-VI):	x(i)=DONE", AG_THIS);
//				if(			adb.Ag_State[AG_LEFT]	==	S_ACTIVE	)	{				//(B-VI-a)
//					log("State:	(B-VI-a):	x(i-1)=ACTIVE", AG_THIS);
//					
//					send_msg("run!", AG_THIS, AG_LEFT); //run(x(i-1));
//				}
				
				if (	adb.Ag_State[AG_RIGHT]	==	S_ACTIVE	){						//(B-VI-b)
					log("State:	(B-VI-b):	x(i+1)=ACTIVE", AG_THIS);
					
					send_msg("run!", AG_THIS, AG_RIGHT);	//run(x(i+1));
				}
				
//				else if(			adb.Ag_State[AG_LEFT]	==	S_PASSIVE	){			//(B-VI-c)
//					log("State:	(B-VI-c):	x(i-1)=PASSIVE", AG_THIS);
//					
//					send_msg("run!", AG_THIS, AG_LEFT);		//run(x(i-1));
//				}
				
				else if (	adb.Ag_State[AG_RIGHT]	==	S_PASSIVE	){					//(B-VI-d)
					log("State:	(B-VI-d):	x(i+1)=PASSIVE", AG_THIS);
					send_msg("run!", AG_THIS, AG_RIGHT);	//run(x(i+1));
				}
				
				else if ((	adb.Ag_State[AG_RIGHT]	==	S_DONE	)){						//(B-VI-e)
					log("State:	(B-VI-e):	all DONE -> @@@ Terminate @@@", AG_THIS);
		            
					log("is terminated !!!", AG_THIS);
					log("is terminated !!!", AG_THIS);
					log("is terminated !!!", AG_THIS);
		    		//consistent_subset <- this_solution;
		    		//broadcast terminate algorithm;
		    		// agents may able to find another consistent_solution
		    		// and compare with this_solution 
		    		//so they may restart algorithm again
				}
			}
		}
/* ************************************************************************************************ */	
/* Check if agent is in the most RIGHT 					PHASE C										*/
/* ************************************************************************************************ */	

		else if ( AG_THIS == DB.AG_MAXNUM-1 ){											//(C)
			log("State:	(C):	The Most Right Agent", AG_THIS);

			// Check if agent is within its neighbors
			if ( 	(		adb.Ag_Time[AG_THIS]<=	adb.Ag_Time[AG_LEFT])				//(C-I)
			    	){// && t(i)<t(i+1) )
				log("State:	(C-I):	t(i-1) >= T", AG_THIS);

				// Check agent states (is active)
				if (adb.Ag_State[AG_THIS] == S_ACTIVE){									//(C-I-a)
					log("State:	(C-I-a):	agent is ACTIVE", AG_THIS);

					// Select first from right
//					if ( ! func.is_empty(adb.D_Ov[AG_THIS][AG_RIGHT]) ){				//(C-I-a-1) 
//						log("State:	(C-I-a-1):	select from right", AG_THIS);
//
//						adb.select_value(AG_THIS, AG_RIGHT);//d from Di,i+1)
//						send_msg("run!", AG_THIS, AG_RIGHT);//run(), (xi,si,di,ti) to xi+1)
//						adb.schedule_value(AG_THIS);
//					}
					// else select from left
					if ( ! func.is_empty(adb.D_Ov[AG_THIS][AG_LEFT]) ){ 				//(C-I-a-2)
						log("State:	(C-I-a-2):	select from left", AG_THIS);

						adb.select_value(AG_THIS, AG_LEFT); //d from Di-1,i)
						adb.schedule_value(AG_THIS);
						send_msg("run!", AG_THIS, AG_LEFT);	//run(), (xi,si,di,ti) to xi-1)
					}
					// if no value exists to select neither from right nor from left
					else{																//(C-I-a-3)
						log("State:	(C-I-a-3):	no value to select -> x(i)=PASSIVE", AG_THIS);

						adb.Ag_State[AG_THIS] = S_PASSIVE;
						// send no_value to the neighbors
//						send_msg("no_value!", AG_THIS, AG_RIGHT);
						send_msg("no_value!", AG_THIS, AG_LEFT);
					}
				}
				// if all values were selected by their near agents
				else if (	adb.Ag_State[AG_THIS]	==	S_PASSIVE
						&&	(adb.Ag_State[AG_LEFT]	==	S_PASSIVE)){					//(C-I-b)
					log("State:	(C-I-b):	x(i)=PASSIVE", AG_THIS);

					// First select non-selected value from left
					if(			! func.is_empty(adb.D_Ov[AG_THIS][AG_LEFT]) ) {			//(C-I-b-1)
						log("State:	(C-I-b-1):	BackTrack from Left", AG_THIS);

						backtrack(AG_THIS, AG_LEFT);
					}
//					else if(	! func.is_empty(adb.D_Ov[AG_THIS][AG_RIGHT]) ) {		//(C-I-b-2)
//						log("State:	(C-I-b-2):	BackTrack from Right", AG_THIS);
//
//						backtrack(AG_THIS, AG_RIGHT);
//					}
					else if(	func.is_empty(adb.D_Ov[AG_THIS][AG_LEFT])
							&&	(adb.Ag_State[AG_LEFT]	==	S_PASSIVE )){				//(C-I-b-3)
						log("State:	(C-I-b-3):	no value to select -> DONE", AG_THIS);

						adb.Ag_State[AG_THIS] = S_DONE;
						// send no_value to the neighbors
//						send_msg("no_value!", AG_THIS, AG_RIGHT);
						send_msg("no_value!", AG_THIS, AG_LEFT);
					}
				}
				// xi is passive && any neighbor is not passive
				else if (	adb.Ag_State[AG_THIS] 	== 	S_PASSIVE
						&&	(	(adb.Ag_State[AG_LEFT]	!=	S_PASSIVE )) ){				//(C-I-c)
					log("State:	(C-I-c):	x(i)=PASSIVE & any neighbours is PASSIVE", AG_THIS);

//					if	(adb.Ag_State[AG_RIGHT]	==	S_ACTIVE) {							//(C-I-c-1)
//						log("State:	(C-I-c-1):	right is ACTIVE", AG_THIS);
//
//						send_msg("run!", AG_THIS, AG_RIGHT);
//					}
					if	(adb.Ag_State[AG_LEFT]	==	S_ACTIVE) {							//(C-I-c-2)
						log("State:	(C-I-c-2):	left is ACTIVE", AG_THIS);

						send_msg("run!", AG_THIS, AG_LEFT);
					}
					else if (	adb.Ag_State[AG_LEFT]	==	S_DONE){					//(C-I-c-3)
						log("State:	(C-I-c-3): all neighbors are DONE", AG_THIS);

						adb.Ag_State[AG_THIS] = S_PASSIVE;

//						send_msg("no_value!", AG_THIS, AG_RIGHT);
						send_msg("no_value!", AG_THIS, AG_LEFT);
					}
				}
			}
			// Check  if all values were selected by neighbors		
			else if (	(	adb.Ag_Time[AG_THIS]< 	adb.Ag_Time[AG_LEFT]	)
		             ){// && t(i)<t(i-1) && (t(i)>=t(i+1) ) && (xi is active) )			//(C-II)
				log("State:	(C-II):	t(i-1) > T >= t(i+1)", AG_THIS);

				if (	adb.Ag_State[AG_THIS]	==	S_ACTIVE) {
//					if (	(adb.Ag_State[AG_RIGHT]	==	S_ACTIVE)) {					//(C-II-a)
//						log("State:	(C-II-a):	x(i)=ACTIVE and x(i+1)=ACTIVE", AG_THIS);
//
//						send_msg("run!", AG_THIS, AG_RIGHT);	//run(x(i+1));
//					}
//					else if (adb.Ag_State[AG_RIGHT]	!=	S_ACTIVE){						//(C-II-b)
//						log("State:	(C-II-b:	x(i)=ACTIVE and x(i+1)!=ACTIVE "+
//									"-> select_value and send run to neighbor", AG_THIS);
//
//						adb.select_value(AG_THIS, AG_RIGHT);//select_value(D(i,i+1));
//						
//						// send run! to neighbors
//						send_msg("run!", AG_THIS, AG_RIGHT);	//run(x(i+1));
//						send_msg("run!", AG_THIS, AG_LEFT);		//run(x(i-1));
//						adb.schedule_value(AG_THIS);
//					}
				}
			}
			else if (	(	adb.Ag_Time[AG_THIS]> 	adb.Ag_Time[AG_LEFT])
		 			){// && t(i)>t(i-1) && (t(i)<t(i+1) ) && (xi is active) )			//(C-III)
				log("State:	(C-III):	t(i-1) < T <= t(i+1)", AG_THIS);

				if (adb.Ag_State[AG_THIS]	==	S_ACTIVE) {
					if (	(adb.Ag_State[AG_LEFT]	==	S_ACTIVE)) {					//(C-III-a)
						log("State:	(C-III-a):	x(i)=ACTIVE and x(i-1)=ACTIVE", AG_THIS);

						send_msg("run!", AG_THIS, AG_LEFT);	//run(x(i-1));
					}
					else if (adb.Ag_State[AG_LEFT]	!=	S_ACTIVE
							&& 	AG_LEFT>=0){											//(C-III-b)
						log("State:	(C-III-b):	x(i)=ACTIVE && x(i-1)!=ACTIVE " +
								"-> select_value and send run to neighbors", AG_THIS);

						adb.select_value(AG_THIS, AG_LEFT);	//select_value(D(i-1,i));
						
						adb.schedule_value(AG_THIS);
						// send run! to neighbors
//						send_msg("run!", AG_THIS, AG_RIGHT);	//run(x(i+1));
						send_msg("run!", AG_THIS, AG_LEFT);		//run(x(i-1));
					}
				}
			}
			// && t(i)>t(i-1) && (t(i)>t(i+1) ) && (xi is active) )
			else if (	(	adb.Ag_Time[AG_THIS]> 	adb.Ag_Time[AG_LEFT])
		             &&	adb.Ag_State[AG_THIS]	==	S_ACTIVE) {							//(C-IV)
				log("State:	(C-IV):	t(i-1) < T > t(i+1)", AG_THIS);
		 			
				if (	adb.Ag_State[AG_LEFT]	==	S_ACTIVE ) {						//(C-IV-a)
					log("State:	(C-IV-a):	t(i+1) > t(i-1) && x(i-1)=ACTIVE", AG_THIS);
					
					send_msg("run!", AG_THIS, AG_LEFT);	//run(x(i-1));
				}
//				else if (	adb.Ag_Time[AG_RIGHT] < 	adb.Ag_Time[AG_LEFT]
//						&&	adb.Ag_State[AG_RIGHT]	==	S_ACTIVE ) {					//(C-IV-b)
//					log("State:	(C-IV-b):	t(i+1) < t(i-1) && x(i+1)=ACTIVE", AG_THIS);
//					
//					send_msg("run!", AG_THIS, AG_RIGHT);	//run(x(i-1));
//				}
//				else if (	adb.Ag_State[AG_RIGHT]	!=	S_ACTIVE){						//(C-IV-c)
//					log("State:	(C-IV-c):	x(i+1)!=ACTIVE -> select_value from right & run! to right", 
//							AG_THIS);
//
//					adb.select_value(AG_THIS, AG_RIGHT);	//select_value(D(i,i+1));
//					// send run! to neighbors
//					send_msg("run!", AG_THIS, AG_RIGHT);	//run(x(i+1));
//					adb.schedule_value(AG_THIS);
//				}
				
				else if (adb.Ag_State[AG_LEFT]	!=	S_ACTIVE){							//(C-IV-d)
					log("State:	(C-IV-d):	x(i-1)!=ACTIVE -> select_value from left & run! to left", 
							AG_THIS);

					adb.select_value(AG_THIS, AG_LEFT);		//select_value(D(i,i+1));
					adb.schedule_value(AG_THIS);
					// send run! to neighbors
					send_msg("run!", AG_THIS, AG_LEFT);		//run(x(i-1));
				}
			}
			// xi is passive
			else if (	adb.Ag_State[AG_THIS]	==	S_PASSIVE){							//(C-V)
				log("State:	(C-V):	x(i)=PASSIVE", AG_THIS);
				// check if still any neighbor agent is being in active state
				// send run! to  the active neighbors
//				if (	adb.Ag_State[AG_RIGHT]	==	S_ACTIVE	) {						//(C-V-a)
//					log("State:	(C-V-a):	x(i+1)=ACTIVE", AG_THIS);
//					send_msg("run!", AG_THIS, AG_RIGHT);	//run(x(i+1));
//				}
				if (	adb.Ag_State[AG_LEFT]	==	S_ACTIVE	) {						//(C-V-b)
					log("State:	(C-V-b):	x(i-1)=ACTIVE", AG_THIS);
					send_msg("run!", AG_THIS, AG_LEFT);		//run(x(i-1));
				}
				// check if all neighbors has no possible value and check problem’s consistency
				else if (	func.is_empty(adb.D_Ov[AG_THIS][AG_LEFT]) ){				//(C-V-c)
					log("State:	(C-V-c):	D(i-1)=Empty", AG_THIS);
					double abs_dif_left = Math.abs(adb.Ag_Time[AG_THIS]-adb.Ag_Time[AG_LEFT]);
					// check sub_solution consistent with neighbor x_j – first left then right
					if (	abs_dif_left	> 	adb.TresholdTime){						//(C-V-c-1)
						log("State:	(C-V-c-1):	|t(i-1)-T| > Threshold", AG_THIS);
						if (	adb.Ag_State[AG_LEFT]	==	S_DONE){
							log("State:	(C-V-c-1-1)", AG_THIS);
//							ask_state(left,passive);
							adb.Ag_State[AG_LEFT]	=	S_PASSIVE;
							send_msg("update_value!",AG_THIS, AG_LEFT);
						}
					//request the most appropriate task from neighbor
//					request_value (left);
//					backtrack(left);
					}
					else {																//(C-V-c-2)
						log("State:	(C-V-c-2):	|t(i-1)-T| <<< Threshold", AG_THIS);

						adb.Ag_State[AG_THIS]	=	S_DONE;
//						send_msg("no_value!", AG_THIS, AG_RIGHT);
						send_msg("no_value!", AG_THIS, AG_LEFT);
					}
				}
//				else if (func.is_empty(adb.D_Ov[AG_THIS][AG_RIGHT]) ){					//(C-V-d)
//					log("State:	(C-V-d):	D(i+1)=Empty", AG_THIS);
//					int abs_dif_right= Math.abs(adb.Ag_Time[AG_THIS]-adb.Ag_Time[AG_RIGHT]);
//					
//					if (	abs_dif_right	>	adb.TresholdTime){						//(C-V-d-1)
//						log("State:	(C-V-d-1):	|t(i+1)-T| > Threshold", AG_THIS);
//						if (	adb.Ag_State[AG_RIGHT]	==	S_DONE){
////							ask_state(right,passive);
//							adb.Ag_State[AG_RIGHT]	=	S_PASSIVE;
//						}
//					//request the most appropriate task from neighbor
////					request_value (right);
////					backtrack(right);
//					}
//					else {																//(C-V-d-2)
//						log("State:	(C-V-d-2):	|t(i+1)-T| <<< Threshold", AG_THIS);
//						send_msg("no_value!", AG_THIS, AG_RIGHT);
//						send_msg("no_value!", AG_THIS, AG_LEFT);
//						adb.Ag_State[AG_THIS]	=	S_DONE;
//					}
//				}
			}
			// xi is Done			
			else if (	adb.Ag_State[AG_THIS]	==	S_DONE){							//(C-VI)
				log("State:	(A-VI):	x(i)=DONE", AG_THIS);
				if(			adb.Ag_State[AG_LEFT]	==	S_ACTIVE	)	{				//(C-VI-a)
					log("State:	(C-VI-a):	x(i-1)=ACTIVE", AG_THIS);
					
					send_msg("run!", AG_THIS, AG_LEFT); //run(x(i-1));
				}
				
//				else if (	adb.Ag_State[AG_RIGHT]	==	S_ACTIVE	){					//(C-VI-b)
//					log("State:	(C-VI-b):	x(i+1)=ACTIVE", AG_THIS);
//					
//					send_msg("run!", AG_THIS, AG_RIGHT);	//run(x(i+1));
//				}
				
				else if(			adb.Ag_State[AG_LEFT]	==	S_PASSIVE	){			//(C-VI-c)
					log("State:	(C-VI-c):	x(i-1)=PASSIVE", AG_THIS);
					
					send_msg("run!", AG_THIS, AG_LEFT);		//run(x(i-1));
				}
				
//				else if (	adb.Ag_State[AG_RIGHT]	==	S_PASSIVE	){					//(C-VI-d)
//					log("State:	(C-VI-d):	x(i+1)=PASSIVE", AG_THIS);
//					
//					send_msg("run!", AG_THIS, AG_RIGHT);	//run(x(i+1));
//				}
				
				else if ((	adb.Ag_State[AG_LEFT]	==	S_DONE	)){						//(C-VI-e)
					log("State:	(C-VI-e):	all DONE -> @@@ Terminate @@@", AG_THIS);
		            
					log("is terminated !!!", AG_THIS);
					log("is terminated !!!", AG_THIS);
					log("is terminated !!!", AG_THIS);
		    		//consistent_subset <- this_solution;
		    		//broadcast terminate algorithm;
		    		// agents may able to find another consistent_solution
		    		// and compare with this_solution 
		    		//so they may restart algorithm again
				}
			}
		}
	}catch (Exception e) {
		log("can't send.	"+e.toString(), AG_THIS);
	}
	}
	/**********************************************************************/	
	/**
	 * request_value()
	 */
	public void request_value(){
		
	}
	/**
	 * Backtrack Procedure
	 * procedure backtrack(d within D_ov)
	 * inconsistent_subset <- this_solution;
	 * //check if neighbor x_j can remove the value
	 * if (x_j is passive) do
	 * if (D_ov is not empty) do
	 * 		remove this_solution from consistent_subset;
	 * 		send ( request_value?, (x_i,s_i,d_i,t_i) ,d_j ) to neighbor x_j;
	 * 	elseif (D_ov is empty)
	 * 
	 * s_i <- done!;
	 * end do;
	 * end do;
	 *  
	 */
	public void backtrack (int src, int des){
		if ( adb.Ag_State[des] == S_PASSIVE ){
			if ( ! func.is_empty(adb.D_Ov[src][des]) ){
				// remove this_solution from consistent_subset;
				// send ( request_value?, (x_i,s_i,d_i,t_i) ,d_j ) to neighbor x_j;
				request_value();
			}
			else {
				// send ( no_value!, (x_i,s_i,d_i,t_i) ) to the neighbors;
				send_msg("no_value!", AG_THIS, AG_RIGHT);
				send_msg("no_value!", AG_THIS, AG_LEFT);
				adb.Ag_State[AG_THIS] = S_DONE;
			}
		}
	}
		
	/**
	 * void update_view(SerialMsg sm)
	 * 
	 * Updating received packets
	 */
	public void update_view(SerialMsg sm){
		adb.Ag_State	=	sm.Ag_State;
		adb.Ag_Time		=	sm.Ag_Time;
		adb.Ag_Values	=	sm.Ag_Values;
		adb.ag_view_L	=	sm.ag_view_L;
		adb.ag_view_R	=	sm.ag_view_R;
		adb.D_Nov	=	sm.D_Nov;
		adb.D_Ov	=	sm.D_Ov;
		log("Update its Data Base", AG_THIS);
	}
// 	public void remove_agent_view(){
// 		
// 	}
}