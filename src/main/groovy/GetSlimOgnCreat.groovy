/**
* Business Engine Extension
*/
/****************************************************************************************
Extension Name: GetSlimOgnCreat
Type : ExtendM3Transaction
Script Author: Abhinav Pulimala
Date: 2022-05-23



Description:
Get the details of list of slim order genrations which needs to be created in M3.

Revision History:
Name Date Version Description of Changes
Abhinav Pulimala 2022-09-20 1.0 Initial Version
******************************************************************************************/
import java.util.* 
import java.util.regex.* 
public class GetSlimOgnCreat extends ExtendM3Transaction {
  private final MIAPI mi 
  private final DatabaseAPI database 
  private final ProgramAPI program 
  private final MICallerAPI miCaller 
  public GetSlimOgnCreat(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
    this.mi = mi 
    this.database = database 
    this.program = program 
    this.miCaller = miCaller 
  }
  public void main() {
    int iCONO = mi.in.get("CONO") 
    String iITNO = (mi.in.get("ITNO") == null)? "": mi.in.get("ITNO") 
    String iWHLO = (mi.in.get("WHLO")== null)? "": mi.in.get("WHLO") 
    String iWHTY = "01" 
    String iPUIT = "1" 
    double iORQA = mi.in.get("ORQA") 
    ExpressionFactory exp_dbaMITWHL00 = database.getExpressionFactory("MITWHL") 
    ExpressionFactory exp_dbaMITWHL01 = database.getExpressionFactory("MITWHL") 
    exp_dbaMITWHL00 =  (exp_dbaMITWHL00.eq("MWWHTY","01").or(exp_dbaMITWHL00.eq("MWWHTY","02")).or(exp_dbaMITWHL00.eq("MWWHTY","04"))) 
    exp_dbaMITWHL01 = exp_dbaMITWHL01.eq("MWWHSY","1").and(exp_dbaMITWHL00)
    DBAction dbaMITWHL00 = database.table("MITWHL").index("00").selection("MWWHLO","MWFACI","MWWHSY","MWWHTY").matching(exp_dbaMITWHL01).build() 
    DBContainer conMITWHL00 = dbaMITWHL00.getContainer() 
    conMITWHL00.set("MWCONO", iCONO) 
    conMITWHL00.set("MWWHLO", iWHLO) 
    Closure < ?> resultHandlerMITWHL00 = {
		DBContainer data ->
		String MWFACI = data.get("MWFACI").toString() 
		ExpressionFactory exp_dbaMITBAL00 = database.getExpressionFactory("MITBAL") 
		exp_dbaMITBAL00 =  exp_dbaMITBAL00.eq("MBPUIT","1").or(exp_dbaMITBAL00.eq("MBPUIT","2")) 		
		DBAction dbaMITBAL00 = database.table("MITBAL").index("00").selection("MBWHLO", "MBITNO").matching(exp_dbaMITBAL00).build() 
		DBContainer conMITBAL00 = dbaMITBAL00.getContainer() 
		conMITBAL00.set("MBCONO", iCONO) 
		conMITBAL00.set("MBWHLO", iWHLO) 
		conMITBAL00.set("MBITNO", iITNO) 
		Closure < ?> resultHandlerMITBAL00 = {
			ExpressionFactory exp_dbaMITAUN00 = database.getExpressionFactory("MITAUN") 
			exp_dbaMITAUN00 =  exp_dbaMITAUN00.eq("MUAUTP","1").and(exp_dbaMITAUN00.eq("MUAUS1","1")) 
			DBAction dbaMITAUN00 = database.table("MITAUN").index("00").selection("MUITNO", "MUAUTP", "MUAUS1","MUDMCF","MUCOFA").matching(exp_dbaMITAUN00).build() 
			DBContainer conMITAUN00 = dbaMITAUN00.getContainer() 
			conMITAUN00.set("MUCONO", iCONO) 
			conMITAUN00.set("MUITNO", iITNO) 
			Closure < ?> resultHandlerMITAUN00 = {
				DBContainer data1 -> 
				String MWWHLO = conMITWHL00.getString("MWWHLO").trim() 
		 		mi.outData.put("FACI", MWFACI) 
		 		mi.outData.put("WHLO", MWWHLO)  
				int mudmcf 
				double mucofa, orqa 
				mudmcf = data1.get("MUDMCF") 
				mucofa = data1.get("MUCOFA") 
				if (mudmcf == 1) {
					orqa = iORQA / mucofa 
				} else if (mudmcf == 2) {
					orqa = iORQA * mucofa 
				} else {
					orqa = iORQA 
				}
		 		mi.outData.put("ORQA", orqa.toString()) 
		 		mi.outData.put("ORQA", iORQA.toString()) 
				mi.write() 
			}
			if (!dbaMITAUN00.readAll(conMITAUN00, 2, resultHandlerMITAUN00)) {
				mi.error("Records doesnot exist in MITAUN") 
				return 
			}
		}
		if (!dbaMITBAL00.readAll(conMITBAL00, 3, resultHandlerMITBAL00)) {
			mi.error("Records doesnot exist in MITBAL") 
			return 
      }	
     }
    if (!dbaMITWHL00.readAll(conMITWHL00, 2, resultHandlerMITWHL00)) {
      mi.error("Records doesnot exist in MITWHL") 
      return 
    }	
	} 
}

