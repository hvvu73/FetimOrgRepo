/**
* Business Engine Extension
*/
/****************************************************************************************
Extension Name: GetSlimFcsWhlo
Type : ExtendM3Transaction
Script Author: Abhinav Pulimala
Date: 2022-05-23
 
 
 
Description:
GetSlim Facility and Warehouse location details.
 
Revision History:
Name Date Version Description of Changes
Abhinav Pulimala 2022-05-23 1.0 Initial Version
******************************************************************************************/
 
public class GetSlimFcsWhlo extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program 
  public GetSlimFcsWhlo(MIAPI mi, DatabaseAPI database, ProgramAPI program) {
    this.mi = mi
    this.database = database
    this.program = program
  }
  public void main() {
    int inCONO = mi.in.get("CONO")
    String inITNO = (mi.in.get("ITNO") == null)? "": mi.in.get("ITNO")
    String inWHLO = (mi.in.get("WHLO")== null)? "": mi.in.get("WHLO")    
    ExpressionFactory exp_dbaMITWHL00 = database.getExpressionFactory("MITWHL")
    exp_dbaMITWHL00 =  exp_dbaMITWHL00.eq("MWWHTY","01").and(exp_dbaMITWHL00.eq("MWWHSY","1"))
    DBAction dbaMITWHL00 = database.table("MITWHL").index("00").selection("MWWHLO").matching(exp_dbaMITWHL00).build()
    DBContainer conMITWHL00 = dbaMITWHL00.getContainer()
    conMITWHL00.set("MWCONO", inCONO)
    conMITWHL00.set("MWWHLO", inWHLO)
    Closure < ? > resultHandlerMITWHL00 = {
      DBAction dbaMITBAL00 = database.table("MITBAL").index("00").selection("MBWHLO").build()       
      DBContainer conMITBAL00 = dbaMITBAL00.getContainer()
      conMITBAL00.set("MBCONO", inCONO)
      conMITBAL00.set("MBWHLO", inWHLO)
      conMITBAL00.set("MBITNO", inITNO)
      Closure < ? > resultHandlerMITBAL00 = {
        String MBWHLO = conMITBAL00.getString("MBWHLO").trim()
        ExpressionFactory exp_dbaMITMAS00 = database.getExpressionFactory("MITMAS")
        exp_dbaMITMAS00 =  exp_dbaMITMAS00.ne("MMETRF","LEG").and(exp_dbaMITMAS00.ne("MMITRF","KTP"))
        DBAction dbaMITMAS00 = database.table("MITMAS").index("00").selection("MMETRF", "MMITRF").matching(exp_dbaMITMAS00).build()
        DBContainer conMITMAS00 = dbaMITMAS00.getContainer()
        conMITMAS00.set("MMCONO", inCONO)
        conMITMAS00.set("MMITNO", inITNO)
        Closure < ? > resultHandlerMITMAS00 = {  
          mi.outData.put("WHLO", MBWHLO)
          mi.write()
        }
        if (!dbaMITMAS00.readAll(conMITMAS00, 2, resultHandlerMITMAS00)) {
          mi.error("record doesn't exist")
          return
        }
      }
      if (!dbaMITBAL00.readAll(conMITBAL00, 3, resultHandlerMITBAL00)) {
        mi.error("record doesn't exist")
        return
      }
    }
    if (!dbaMITWHL00.readAll(conMITWHL00, 2, resultHandlerMITWHL00)) {
      mi.error("record doesn't exist")
      return
    }
  }  
}