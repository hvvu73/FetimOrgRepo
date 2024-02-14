/**
 *  Business Engine Extension
 */
 /****************************************************************************************
 Extension Name: UpdStatus
 Type : ExtendM3Transaction
 Script Author: Arun Tiwari
 Date: 2021-09-17
  
 Description:
       Update delivery status to 60 in ODHEAD
          
 Revision History:
 Name                    Date             Version          Description of Changes
 Arun Tiwari             2021-09-17                     1.0              Initial Version
 ******************************************************************************************/
 
 import java.time.LocalDate
 import java.time.format.DateTimeFormatter
 
public class UpdStatus extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program

  public UpdStatus(MIAPI mi, DatabaseAPI database, ProgramAPI program) {
    this.mi = mi
    this.database = database
    this.program = program
  }

  public void main() {
    int inCONO = mi.in.get("CONO")
    String inORNO = (mi.in.get("ORNO") == null)? "": mi.in.get("ORNO")
    String inWHLO = (mi.in.get("WHLO") == null)? "": mi.in.get("WHLO")
    int inDLIX = mi.in.get("DLIX")
    String inTEPY = (mi.in.get("TEPY") == null)? "": mi.in.get("TEPY")
    int inORST = mi.in.get("ORST")

    String vUSER = program.getUser();

    DBAction dbaODHEADUpd = database.table("ODHEAD").index("00").selection("UAORST").build();
    DBContainer conODHEADUpd = dbaODHEADUpd.getContainer();
    conODHEADUpd.set("UACONO", inCONO);
    conODHEADUpd.set("UAORNO", inORNO);
    conODHEADUpd.set("UAWHLO", inWHLO);
    conODHEADUpd.set("UADLIX", inDLIX);
    conODHEADUpd.set("UATEPY", inTEPY);

    if(!dbaODHEADUpd.read(conODHEADUpd)) {
      mi.error("The record does not exist")
      return
    }
    else {
      String odhead_orst = conODHEADUpd.get("UAORST")

      if(!odhead_orst.equals("62") && !odhead_orst.equals("63") && !odhead_orst.equals("64")) {
        mi.error("Order status is not correct , status should be 62,63 and 64 only")
        return
      }
      if(!inORST.equals(60)) {
        mi.error("ORST should be 60!")
        return
      }
    }
    Closure<?> updateODHEAD  = { LockedResult lockedResult ->
      lockedResult.set("UAORST", inORST.toString())
      lockedResult.set("UACHID", vUSER)
      lockedResult.set("UALMDT", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
      lockedResult.set("UACHNO", lockedResult.getInt("UACHNO") + 1)
      lockedResult.update()
    }
    dbaODHEADUpd.readLock(conODHEADUpd,updateODHEAD)
  }
}