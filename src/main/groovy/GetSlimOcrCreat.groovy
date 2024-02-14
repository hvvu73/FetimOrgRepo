/**
* Business Engine Extension
*/
/****************************************************************************************
Extension Name: GetSlimOcrCreat
Type : ExtendM3Transaction
Script Author: Abhinav Pulimala
Date: 2022-05-23



Description:
Get the details of list of slim order creations which needs to be created in M3.

Revision History:
Name Date Version Description of Changes
Abhinav Pulimala 2022-09-19 1.0 Initial Version
******************************************************************************************/
public class GetSlimOcrCreat extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program

  public GetSlimOcrCreat(MIAPI mi, DatabaseAPI database, ProgramAPI program) {
    this.mi = mi
    this.database = database
    this.program = program
  }
  public void main() {
    int inCONO = mi.in.get("CONO")
    String inITNO = (mi.in.get("ITNO") == null)? "": mi.in.get("ITNO")
    String inWHLO = (mi.in.get("WHLO")== null)? "": mi.in.get("WHLO")
    ExpressionFactory exp_dbaMITWHL00 = database.getExpressionFactory("MITWHL")
    ExpressionFactory exp_dbaMITWHL01 = database.getExpressionFactory("MITWHL")
    exp_dbaMITWHL00 =  (exp_dbaMITWHL00.eq("MWWHTY","01").or(exp_dbaMITWHL00.eq("MWWHTY","02")).or(exp_dbaMITWHL00.eq("MWWHTY","04")))
    exp_dbaMITWHL01 = exp_dbaMITWHL01.eq("MWWHSY","1").and(exp_dbaMITWHL00)
    DBAction dbaMITWHL00 = database.table("MITWHL").index("00").selection("MWWHLO","MWFACI","MWWHSY","MWWHTY").matching(exp_dbaMITWHL01).build()
    DBContainer conMITWHL00 = dbaMITWHL00.getContainer()
    conMITWHL00.set("MWCONO", inCONO)
    conMITWHL00.set("MWWHLO", inWHLO)
    Closure < ? > resultHandlerMITWHL00 = {
      DBContainer data ->
      String MWFACI = data.get("MWFACI").toString()
      ExpressionFactory exp_dbaMITBAL00 = database.getExpressionFactory("MITBAL")
      ExpressionFactory exp_dbaMITBAL01 = database.getExpressionFactory("MITBAL")
	    exp_dbaMITBAL00 =  exp_dbaMITBAL00.eq("MBPUIT","1").or(exp_dbaMITBAL00.eq("MBPUIT","2"))
		  exp_dbaMITBAL01 =  (exp_dbaMITBAL00).and(exp_dbaMITBAL01.ne("MBSUNO",""))
		  DBAction dbaMITBAL00 = database.table("MITBAL").index("00").selection("MBWHLO","MBPUIT","MBITNO","MBSUNO").matching(exp_dbaMITBAL01).build()
      DBContainer conMITBAL00 = dbaMITBAL00.getContainer()
      conMITBAL00.set("MBCONO", inCONO)
      conMITBAL00.set("MBWHLO", inWHLO)
      conMITBAL00.set("MBITNO", inITNO)
      Closure < ? > resultHandlerMITBAL00 = {
        DBContainer data1 ->
        String MBPUIT = data1.get("MBPUIT").toString()
        String MBSUNO = data1.get("MBSUNO").toString()
			  String MWWHLO = conMITWHL00.getString("MWWHLO").trim() 
        mi.outData.put("FACI", MWFACI)
        mi.outData.put("WHLO", MWWHLO)
        mi.outData.put("PUIT", MBPUIT)
        mi.outData.put("SUNO", MBSUNO)
        mi.write()
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