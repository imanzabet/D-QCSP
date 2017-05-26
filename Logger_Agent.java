package QUAY_CRANES_2;

import QUAY_CRANES_2.DB;
import QUAY_CRANES_2.Func;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import QUAY_CRANES_2.NIOLogger;

public class Logger_Agent extends Agent {
 	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	AID	Logger_AID;
	/**
	 * Constructor of QCA1 and initializer
	 * 
	 * @author Zabet
	 */

	public Logger_Agent () {

		Logger_AID = new AID("Logger_Agent", AID.ISLOCALNAME);
	}

	protected void setup() {
 		
	System.out.println("Hello my name is Logger_Agent");
 	// first QCA should send run to its neighbor
/////////////////
	addBehaviour(new CyclicBehaviour(this) {
		private static final long serialVersionUID = 1L;
		public void action(){
			
			display();

			ACLMessage msg = receive();
			if(msg != null){

				try{
					Object sm = msg.getContentObject();
					if (msg.getPerformative() == ACLMessage.INFORM){
						if (sm instanceof SerialMsg) {
							SerialMsg struct = (SerialMsg) sm;

							if ( Func.is_Str_Equal(struct.Msg_Type, "INFO!")){

//								System.out.println("<--	"+struct.Msg_Type+"	by Logger	from "+struct.SenderName);
								String tmp = Func.getDateTime()+"	"+struct.SenderName+":	"+struct.InternalMsg;
								System.out.println(tmp);
							}
							else{
								Func.show_msg("###	LOGGER:	the received message type from:	"+struct.SenderName+" is : "+struct.Msg_Type+"	and doesn't match");
							}

						}
					}
					
				}catch(Exception e){

					Func.show_msg("###	can't receive message from:	"+msg.getSender().getLocalName() );
	        	}
  			}
			else{
				try{
				Thread.sleep(DB.Logger_Sleep_Time);
				}
				catch(InterruptedException ie){
					ie.printStackTrace();
				}
			}
		}
	});
 	}
	public synchronized void display () {
		try {
			for (int i = 0; i < DB.AG_MAXNUM; i++) {
				while ( !Func.print_queue[i].isEmpty()) {
					String tmp = Func.getDateTime()+"	QCA_"+Func.IntToStr(i)+":	"+Func.print_queue[i].poll();
					System.out.println(tmp);
				}
			}
		}
		catch (Exception e) {
			System.out.println(e.toString());
		}
	}
}