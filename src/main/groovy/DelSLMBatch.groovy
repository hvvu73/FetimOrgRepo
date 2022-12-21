/**
 *  Business Engine Extension
 */
 /****************************************************************************************
 Extension Name: DelSLMBatch
 Type : ExtendM3Transaction
 Script Author: Arun Tiwari
 Date: 2022-04-01
  
 Description:
       Delete last 10K records from EXTSLM table
          
 Revision History:
 Name                    Date             Version          Description of Changes
 Arun Tiwari            2022-04-01          1.0              Initial Version
 ******************************************************************************************/
 
public class DelSLMBatch extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final LoggerAPI logger

  public DelSLMBatch(MIAPI mi, DatabaseAPI database, ProgramAPI program,LoggerAPI logger) {
    this.mi = mi
    this.database = database
    this.program = program
    this.logger = logger
  }

  public void main() {
    int company = mi.in.get("CONO")

    DBAction query = database.table("EXTSLM").index("01").selection("EXROWN").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", company)

    Closure<?> updateCallBack = { DBContainer dbcontainer ->

      Closure<?> updateCallBack1 = { LockedResult lockedResult1 ->

        lockedResult1.delete()

      }
      query.readLock(dbcontainer,updateCallBack1)

    }

    if(!query.readAll(container,1,100,updateCallBack)) {
      mi.error("Record does not exist")
      return
    }

  }
}