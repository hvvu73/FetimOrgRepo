/**
* Business Engine Extension
*/
/****************************************************************************************
Extension Name: LstSlimOcrDel
Script Author: Abhinav Pulimala
Type : ExtendM3Transaction
Date: 2022-05-23



Description:
List the details of list of slim order creations which needs to be deleted in M3.

Revision History:
Name                    Date            Version           Description of Changes
Abhinav Pulimala      2022-11-05          1.0                 Initial Version
******************************************************************************************/
public class LstSlimOcrDel extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program

  public LstSlimOcrDel(MIAPI mi, DatabaseAPI database, ProgramAPI program) {
    this.mi = mi
    this.database = database
    this.program = program
  }
  public void main() {
    int inCONO = mi.in.get("CONO")
    int inRORC = mi.in.get("RORC")
    // int inPLPN = mi.in.get("PLPN")
    String MREC = mi.inData.get("RECS").trim()                                // Max Records
    int maxRecords = (MREC.isEmpty() || MREC == null || MREC.equals("?")) ? 10000 : MREC.toInteger()
    
    DBAction dbaMPOPLP91 = database.table("MPOPLP").index("91").selection("POCONO", "PORORC", "POWHLO", "POITNO", "POPLPN", "POPLPS").build() 
    DBContainer conMPOPLP91 = dbaMPOPLP91.getContainer() 
    conMPOPLP91.set("POCONO", inCONO) 
    conMPOPLP91.set("PORORC", inRORC) 
    // conMPOPLP91.set("POPLPN", inPLPN) 
    
    Closure < ? > resultHandlerMPOPLP91 = {
      DBContainer data -> 
      DBAction dbaMPREAL00 = database.table("MPREAL").index("00").selection("NACONO", "NAAOCA", "NAARDN", "NAARDL").build() 
      DBContainer conMPREAL00 = dbaMPREAL00.getContainer() 
      conMPREAL00.set("NACONO", inCONO) 
      conMPREAL00.set("NAAOCA", "250") 
      conMPREAL00.set("NAARDN", data.get("POPLPN").toString()) 
      conMPREAL00.set("NAARDL", data.get("POPLPS")) 
      Closure < ? > resultHandlerMPREAL00 = {
      // DBContainer data1 -> 
      }
      if (!dbaMPREAL00.readAll(conMPREAL00, 4, resultHandlerMPREAL00)) {
        mi.outData.put("TYPE", "PO") 
        mi.outData.put("WHLO", data.get("POWHLO").toString()) 
        mi.outData.put("ITNO", data.get("POITNO").toString()) 
        mi.outData.put("PLPN", data.get("POPLPN").toString()) 
        mi.outData.put("PLPS", data.get("POPLPS").toString()) 
        mi.outData.put("OPNO", "0") 
        mi.write()
    }
    }
    if (!dbaMPOPLP91.readAll(conMPOPLP91, 2, maxRecords, resultHandlerMPOPLP91)) {
      mi.error("Records does not exist in MPOPLP") 
      return 
    }
    
    DBAction dbaMMOPLP00 = database.table("MMOPLP").index("00").selection("ROCONO", "ROPLPN").build() 
    DBContainer conMMOPLP00 = dbaMMOPLP00.getContainer() 
    conMMOPLP00.set("ROCONO", inCONO) 
    Closure < ? > resultHandlerMMOPLP00 = {
      DBContainer data ->
      mi.outData.put("TYPE", "MO") 
      mi.outData.put("WHLO", "") 
      mi.outData.put("ITNO", "") 
      mi.outData.put("PLPN", data.get("ROPLPN").toString()) 
      mi.outData.put("PLPS", "0") 
      mi.outData.put("OPNO", "0") 
      mi.write() 
    }
    if(!dbaMMOPLP00.readAll(conMMOPLP00, 1, maxRecords, resultHandlerMMOPLP00)) {
      mi.error("Records does not exist in MMOPLP") 
      return
    }
  }
}