/**
 *  Business Engine Extension
 */
 /****************************************************************************************
 Extension Name: LstSLMBatchItem
 Type : ExtendM3Transaction
 Script Author: Arun Tiwari
 Date: 2022-03-25
  
 Description:
       Fetch all the records from EXTSLM table based on FROM and TO value.
          
 Revision History:
 Name                    Date             Version          Description of Changes
 Arun Tiwari               2022-03-25                     1.0              Initial Version
 ******************************************************************************************/
 
public class LstSLMBatchItem extends ExtendM3Transaction {
  private final MIAPI mi
  private final ProgramAPI program
  private final DatabaseAPI database

  public LstSLMBatchItem(MIAPI mi,DatabaseAPI database,ProgramAPI program) {
    this.mi = mi
    this.database = database
    this.program = program
  }

  public void main() {
    int inCONO = mi.in.get("CONO")
    long inFRRW = mi.in.get("FRRW")
    long inTORW = mi.in.get("TORW")

    ExpressionFactory expEXTSLM = database.getExpressionFactory("EXTSLM")
    expEXTSLM =  expEXTSLM.between("EXROWN",inFRRW+"", inTORW+"")
    DBAction EXTSLM_query = database.table("EXTSLM").index("00").selection("EXDATA","EXROWN").matching(expEXTSLM).build()
    DBContainer EXTSLM_container = EXTSLM_query.getContainer()
    EXTSLM_container.set("EXCONO", inCONO)

    Closure<?> processItm = { DBContainer record ->
      mi.outData.put("ROWN",record.getLong("EXROWN").toString().trim() )
      mi.outData.put("DATA",record.getString("EXDATA").toString().trim())
      mi.write()
    };

    if(!EXTSLM_query.readAll(EXTSLM_container, 1,2000, processItm)) {
      mi.error("The record does not exist")
      return
    }

  }
}