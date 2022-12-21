/**
 *  Business Engine Extension
 */
 /****************************************************************************************
 Extension Name: GetSLMRowNum
 Type : ExtendM3Transaction
 Script Author: Arun Tiwari
 Date: 2022-03-25
  
 Description:
       Fetch the last row number from EXTSLM table
          
 Revision History:
 Name                    Date             Version          Description of Changes
 Arun Tiwari            2022-03-25          1.0              Initial Version
 ******************************************************************************************/
 
public class GetSLMRowNum extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program

  public GetSLMRowNum(MIAPI mi, DatabaseAPI database, UtilityAPI utility, ProgramAPI program) {
    this.mi = mi
    this.database = database
    this.program = program
  }

  public void main() {
    int company = mi.in.get("CONO")
    String seq = "0"

    //select EXTSLM
    DBAction EXTSLM_query = database.table("EXTSLM").index("01").selection("EXROWN").build()
    DBContainer EXTSLM_container = EXTSLM_query.getContainer()
    EXTSLM_container.set("EXCONO", company)

    Closure<?> processRecord = { DBContainer record ->
      seq = record.getLong("EXROWN").toString()
    }

    if(!EXTSLM_query.readAll(EXTSLM_container,1,1, processRecord)) {
      mi.error("The record does not exist")
      return
    }
    mi.outData.put("ROWN",seq)
    mi.write()
  }
}