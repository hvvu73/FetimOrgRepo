/**
 *  Business Engine Extension
 */
 /****************************************************************************************
 Extension Name: AddSLMBatch
 Type : ExtendM3Transaction
 Script Author: Arun Tiwari
 Date: 2022-03-23
  
 Description:
       Adding SLIM forecast qty data to dynamic table EXTSLM
          
 Revision History:
 Name                    Date             Version          Description of Changes
 Arun Tiwari            2022-03-23          1.0             Initial Version
 ******************************************************************************************/
 
 import java.time.LocalDate;
 import java.time.LocalTime;
 import java.time.format.DateTimeFormatter;
 
public class AddSLMBatch extends ExtendM3Transaction {
  private final MIAPI mi
  private final ProgramAPI program
  private final DatabaseAPI database

  public AddSLMBatch(MIAPI mi,DatabaseAPI database,ProgramAPI program) {
    this.mi = mi;
    this.database = database
    this.program = program
  }

  public void main() {
    int inCONO = mi.in.get("CONO")
    long inROWN = mi.in.get("ROWN")
    String inDATA = mi.inData.get("DATA").trim()
    long inFRRW = mi.in.get("FRRW")
    long inTORW = mi.in.get("TORW")
    String vUSER = program.getUser()

    //CMNDIVI
    DBAction CMNDIV_query = database.table("CMNDIV").index("00").selection("CCCONO","CCDIVI").build();
    DBContainer CMNDIV_container = CMNDIV_query.getContainer();
    CMNDIV_container.set("CCCONO", inCONO)

    Closure<?> processCONORecord = { DBContainer record_CONO ->
    }
    if(!CMNDIV_query.readAll(CMNDIV_container,1, processCONORecord)) {
      mi.error("Company no doesn't exist")
      return
    }

    // insert record in  EXTSLM table
    DBAction insertEXTSLM = database.table("EXTSLM").build()

    DBContainer EXTSLM = insertEXTSLM.getContainer()
    EXTSLM.set("EXCONO", inCONO)
    EXTSLM.set("EXROWN", inROWN)
    EXTSLM.set("EXDATA", inDATA)
    EXTSLM.set("EXFRRW", inFRRW)
    EXTSLM.set("EXTORW", inTORW)
    EXTSLM.set("EXRGDT", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    EXTSLM.set("EXLMDT", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    EXTSLM.set("EXRGTM", LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    EXTSLM.set("EXCHID", vUSER)
    EXTSLM.set("EXCHNO", 1)

    insertEXTSLM.insert(EXTSLM, RecordExists)

  }

  Closure RecordExists = {
    mi.error("Record already exist")
  }
}