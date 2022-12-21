/**
 *  Business Engine Extension
 */
 /****************************************************************************************
 Extension Name: LstLocationsToCopy
 Type : ExtendM3Transaction
 Script Author: Arun Tiwari
 Date: 2022-02-14
  
 Description:
       LstLocationsToCopy-AKT003
          
 Revision History:
 Name                    Date             Version          Description of Changes
 Arun Tiwari               2022-02-14                     1.0              Initial Version
 ******************************************************************************************/
 
public class LstWHSLToCopy extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private String inITNO

  public LstWHSLToCopy(MIAPI mi, DatabaseAPI database, UtilityAPI utility, ProgramAPI program) {
    this.mi = mi
    this.database = database
    this.program = program
  }

  public void main() {
    int inCONO =  mi.in.get("CONO")
    String CCD3_Old = null
    String AGNO_Old = null
    
    //select OAGRLN
    ExpressionFactory exp_OAGRLN = database.getExpressionFactory("OAGRLN")
    exp_OAGRLN =  exp_OAGRLN.eq("UWAGST","20")
    DBAction OAGRLN_query = database.table("OAGRLN").index("00").selection("UWAGNO","UWOBV1").matching(exp_OAGRLN).build()
    DBContainer OAGRLN_container = OAGRLN_query.getContainer()
    OAGRLN_container.set("UWCONO", inCONO)

    Closure<?> processOAGRLNRecord = { DBContainer record_OAGRLN ->

      String AGNO = record_OAGRLN.getString("UWAGNO").trim()
      String OBV1 = record_OAGRLN.getString("UWOBV1").trim()

      //select CMNDIV
      DBAction CMNDIV_query = database.table("CMNDIV").index("00").selection("CCCCD3").build()
      DBContainer CMNDIV_container = CMNDIV_query.getContainer()
      CMNDIV_container.set("CCCONO" , inCONO)
      CMNDIV_container.set("CCDIVI" , OBV1)

      Closure<?> processCMNDIVRecord = { DBContainer record_CMNDIV ->

        String CCD3 = record_CMNDIV.getString("CCCCD3").trim()
        //select MITPCE
        DBAction MITPCE_query = database.table("MITPCE").index("00").selection("MSWHLO").build()
        DBContainer MITPCE_container = MITPCE_query.getContainer()
        MITPCE_container.set("MSCONO" , inCONO)
        MITPCE_container.set("MSWHLO" , CCD3)
        MITPCE_container.set("MSWHSL" , AGNO)
      
        Closure<?> processMITPCERecord = { DBContainer record_MITPCE ->
        }

        if(MITPCE_query.readAll(MITPCE_container,3, processMITPCERecord) == 0 ) {
          if((!CCD3.equals(CCD3_Old)) || (!AGNO.equals(AGNO_Old))) {
            CCD3_Old = CCD3
            AGNO_Old = AGNO
            mi.outData.put("WHLO" , CCD3)
            mi.outData.put("WHSL" , AGNO)
            mi.write()
          }
        }
      }
      CMNDIV_query.readAll(CMNDIV_container,2, processCMNDIVRecord)
    }

    if(!OAGRLN_query.readAll(OAGRLN_container, 1, processOAGRLNRecord)) {
      mi.error("Agreement does not exist")
      return
    }

  }
}