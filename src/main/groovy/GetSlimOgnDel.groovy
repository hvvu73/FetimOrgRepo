
/**
* Business Engine Extension
*/
/****************************************************************************************
Extension Name: GetSlimOgnDel
Type : ExtendM3Transaction
Script Author: Abhinav Pulimala
Date: 2022-05-23



Description:
Get the details of list of slim order generations which needs to be deleted in M3.

Revision History:
Name Date Version Description of Changes
Abhinav Pulimala 2022-09-20 1.0 Initial Version
******************************************************************************************/
import java.util.* 
import java.util.regex.* 
public class GetSlimOgnDel extends ExtendM3Transaction {
  private final MIAPI mi 
  private final DatabaseAPI database 
  private final ProgramAPI program 
  private final MICallerAPI miCaller 
  public GetSlimOgnDel(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
    this.mi = mi 
    this.database = database 
    this.program = program 
    this.miCaller = miCaller 
  }
  public void main() {
    int inCONO = mi.in.get("CONO") 
    String inITNO = (mi.in.get("ITNO") == null)? "": mi.in.get("ITNO") 
    String inWHLO = (mi.in.get("WHLO") == null)? "": mi.in.get("WHLO")
    String NAARDL
    String POPLPS
    String NAARDN
    DBAction dbaMPOPLP = database.table("MPOPLP").index("70").selection("POITNO", "POWHLO", "POPLPN", "POPLPS", "PORORC").build() 
    DBContainer conMPOPLP = dbaMPOPLP.getContainer() 
    conMPOPLP.set("POCONO", inCONO) 
    conMPOPLP.set("POWHLO", inWHLO) 
    conMPOPLP.set("POITNO", inITNO) 
    Closure < ? > resultHandlerMPOPLP = {
      DBContainer data ->
		  DBAction dbaMPREAL = database.table("MPREAL").index("00").selection("NACONO", "NAARDN", "NAARDL", "NAAOCA").build() 
      DBContainer conMPREAL = dbaMPREAL.getContainer() 
      conMPREAL.set("NACONO", inCONO) 
      conMPREAL.set("NAARDN", data.get("POPLPN").toString()) 
      conMPREAL.set("NAARDL", data.get("POPLPS").toString()) 
      Closure < ? > resultHandlerMPREAL = {
        DBContainer data1 ->
        if(data1.get("NAAOCA").toString().equals("250")){
		      NAARDN = data1.get("NAARDN").toString()
          POPLPS = data.get("POPLPS").toString()
          NAARDL = data1.get("NAARDL").toString()
        }
			  mi.outData.put("PLPN", NAARDN) 
	  		mi.outData.put("PLPS", POPLPS) 
		  	mi.outData.put("TYPE", NAARDL)
			  mi.write() 
      }
      if (!dbaMPREAL.readAll(conMPREAL, 3, resultHandlerMPREAL)) {
			  mi.outData.put("PLPN", "NoRecs") 
			  mi.outData.put("PLPS", data.get("POPLPS").toString()) 
			  mi.outData.put("TYPE", "Delete")
			  mi.write()
      }
    }
    if (!dbaMPOPLP.readAll(conMPOPLP, 3, resultHandlerMPOPLP)) {
      mi.error("Records does not exist in MPOPLP") 
      return 
    }
  }
}