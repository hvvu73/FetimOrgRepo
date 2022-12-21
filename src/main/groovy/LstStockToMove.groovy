/**
 *  Business Engine Extension
 */
 /****************************************************************************************
 Extension Name: LstStockToMove
 Type : ExtendM3Transaction
 Script Author: Arun Tiwari
 Date: 2022-01-13
  
 Description:
       Get the Stock qty which can be moved to different location.
          
 Revision History:
 Name                    Date             Version          Description of Changes
 Arun Tiwari            2022-01-13         1.0              Initial Version
 ******************************************************************************************/
 
 import java.time.LocalDate
 import java.text.DecimalFormat
 import java.time.format.DateTimeFormatter
 
public class LstStockToMove extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final LoggerAPI logger
  private String inITNO

  public LstStockToMove(MIAPI mi, DatabaseAPI database, UtilityAPI utility, ProgramAPI program,LoggerAPI logger) {
    this.mi = mi
    this.database = database
    this.program = program
    this.logger = logger
  }

  private static final DecimalFormat df = new DecimalFormat("0.00")
  public void main() {
    int inCONO = mi.in.get("CONO")
    Double DLQT = 0d
    Double IVQT = 0d
    Double STQT = 0d
    int DMCF 
    Double COFA 

    def OAGRLN_Data = []

    LocalDate sdf = LocalDate.now().plusDays(7)
    String dateAfter = sdf.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
    String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"))

    //logger.info("Date After : ${dateAfter}")
    //logger.info("Current Date : ${currentMonth}")
    //select OAGRLN
    ExpressionFactory exp_OAGRLN = database.getExpressionFactory("OAGRLN")
    exp_OAGRLN =  exp_OAGRLN.eq("UWAGST","20").and(exp_OAGRLN.lt("UWSTDT",dateAfter))
    DBAction OAGRLN_query = database.table("OAGRLN").index("00").selection("UWAGQT","UWUNIT","UWAGNO","UWOBV1","UWOBV2","UWCUNO").matching(exp_OAGRLN).build()
    DBContainer OAGRLN_container = OAGRLN_query.getContainer()
    OAGRLN_container.set("UWCONO", inCONO)

    Closure<?> processOAGRLNRecord = { DBContainer record_OAGRLN ->

      String AGNO = record_OAGRLN.getString("UWAGNO").trim()
      String UNIT = record_OAGRLN.getString("UWUNIT").trim()
      String OBV1 = record_OAGRLN.getString("UWOBV1").trim()
      String OBV2 = record_OAGRLN.getString("UWOBV2").trim()
      String CUNO = record_OAGRLN.getString("UWCUNO").trim()
      Double AGQT = Double.parseDouble(record_OAGRLN.get("UWAGQT").toString())

      //select MITMAS
      DBAction MITMAS_query = database.table("MITMAS").index("00").selection("MMUNMS").build()
      DBContainer MITMAS_container = MITMAS_query.getContainer()
      MITMAS_container.set("MMCONO", inCONO)
      MITMAS_container.set("MMITNO", OBV2)

      Closure<?> processMITMASRecord = { DBContainer record_MITMAS ->

        if(UNIT.equals(record_MITMAS.getString("MMUNMS").trim())) {

          //select CMNDIV
          ExpressionFactory exp_CMNDIV = database.getExpressionFactory("CMNDIV")
          exp_CMNDIV =  exp_CMNDIV.eq("CCFACI",OBV1)
          DBAction CMNDIV_query = database.table("CMNDIV").index("00").selection("CCCCD3").matching(exp_CMNDIV).build()
          DBContainer CMNDIV_container = CMNDIV_query.getContainer()
          CMNDIV_container.set("CCCONO", inCONO)


          Closure<?> processCMNDIVRecord = { DBContainer record_CMNDIV ->
            String CCD3 = record_CMNDIV.getString("CCCCD3").trim()
            String WHLO = record_CMNDIV.getString("CCWHLO").trim()

            //select OOLIAR
            ExpressionFactory exp_OOLIAR = database.getExpressionFactory("OOLIAR")
            exp_OOLIAR =  exp_OOLIAR.eq("UXITNO",OBV2)
            DBAction OOLIAR_query = database.table("OOLIAR").index("10").selection("UXDLQT","UXIVQT").matching(exp_OOLIAR).build()
            DBContainer OOLIAR_container = OOLIAR_query.getContainer()
            OOLIAR_container.set("UXCONO", inCONO)
            //OOLIAR_container.set("UXORNO", "")
            OOLIAR_container.set("UXPONR", 0)
            OOLIAR_container.set("UXPOSX", 0)
            OOLIAR_container.set("UXCUNO", CUNO)
            OOLIAR_container.set("UXAGNO", AGNO)
            DLQT = 0d
            IVQT = 0d


            Closure<?> processOOLIARRecord = { DBContainer record_OOLIAR ->

              DLQT = Double.parseDouble(record_OOLIAR.get("UXDLQT").toString())
              IVQT = Double.parseDouble(record_OOLIAR.get("UXIVQT").toString())

            }
            OOLIAR_query.readAll(OOLIAR_container,6, processOOLIARRecord)

            //select MITLOC
            DBAction MITLOC_query = database.table("MITLOC").index("00").selection("MLSTQT").build()
            DBContainer MITLOC_container = MITLOC_query.getContainer()
            MITLOC_container.set("MLCONO", inCONO)
            MITLOC_container.set("MLWHLO", CCD3)
            MITLOC_container.set("MLITNO", OBV2)
            MITLOC_container.set("MLWHSL", AGNO)
            MITLOC_container.set("MLBANO", "")
            STQT = 0d
            Closure<?> processMITLOCRecord = { DBContainer record_MITLOC ->
              STQT = Double.parseDouble(record_MITLOC.get("MLSTQT").toString())
            }
            MITLOC_query.readAll(MITLOC_container,5, processMITLOCRecord)

            //select MITPCE
            DBAction MITPCE_query = database.table("MITPCE").index("00").selection("MSWHLO").build()
            DBContainer MITPCE_container = MITPCE_query.getContainer()
            MITPCE_container.set("MSCONO", inCONO)
            MITPCE_container.set("MSWHLO", WHLO)
            MITPCE_container.set("MSWHSL", AGNO)

            Closure<?> processMITPCERecord = { DBContainer record_MITPCE ->

            }
            MITPCE_query.readAll(MITPCE_container,3, processMITPCERecord)

            DBAction MITAUN_query = database.table("MITAUN").index("00").selection("MUDMCF","MUCOFA").build()
            DBContainer MITAUN_container = MITAUN_query.getContainer()
            MITAUN_container.set("MUCONO", inCONO)
            MITAUN_container.set("MUITNO", OBV2)
            MITAUN_container.set("MUAUTP", 1)
            MITAUN_container.set("MUALUN", "PK")
            DMCF = 0
            COFA = 0d

            Closure<?> processMITAUNRecord = { DBContainer record_MITAUN ->
              DMCF = Integer.parseInt(record_MITAUN.get("MUDMCF").toString())
              COFA = Double.parseDouble(record_MITAUN.get("MUCOFA").toString())
            }
            MITAUN_query.readAll(MITAUN_container,4, processMITAUNRecord)

            if(DMCF == 1)
            {
              COFA =(int) COFA
            }
            else if(DMCF == 0)
            {
              if(!COFA == 0.0)
                COFA = Integer.parseInt(df.format(1/COFA))
              else
                COFA = 0
            }
            else {
              COFA = 1
            }
            int AGQT_New = 0
            if(COFA == 0.0)
              AGQT_New= 0
            else
              AGQT_New= (int)((AGQT - (DLQT+IVQT)-STQT)/COFA)

            if(AGQT_New > 0) {
              OAGRLN_Data.add(AGNO+"|"+OBV1+"|"+OBV2+"|"+CCD3+"|"+(int)COFA+"|"+DMCF+"|"+AGQT_New)
            }
          }
          CMNDIV_query.readAll(CMNDIV_container,1, processCMNDIVRecord)
        }
      }
      MITMAS_query.readAll(MITMAS_container,2, processMITMASRecord)

    }
    if(!OAGRLN_query.readAll(OAGRLN_container, 1, processOAGRLNRecord)) {
      mi.error("Agreement does not exist")
      return
    }

    for(def val : OAGRLN_Data){
      def arr = val.toString().tokenize( '|' )
      DBAction MITLOC_query = database.table("MITLOC").index("00").selection("MLSTQT","MLALQT").build()
      DBContainer MITLOC_container = MITLOC_query.getContainer()
      MITLOC_container.set("MLCONO", inCONO)
      MITLOC_container.set("MLWHLO", arr[3])
      MITLOC_container.set("MLITNO", arr[2])
      MITLOC_container.set("MLWHSL", "NORMAAL")
      MITLOC_container.set("MLBANO", "")

      Closure<?> processMITLOCRecord = { DBContainer record_MITLOC ->
        Double STQT1 = Double.parseDouble(record_MITLOC.get("MLSTQT").toString())
        Double ALQT1 = Double.parseDouble(record_MITLOC.get("MLALQT").toString())

        DBAction MITAFO_query = database.table("MITAFO").index("00").selection("MFMDQT").build()
        DBContainer MITAFO_container = MITAFO_query.getContainer()
        MITAFO_container.set("MFCONO", inCONO)
        MITAFO_container.set("MFWHLO", arr[3])
        MITAFO_container.set("MFITNO", arr[2])
        MITAFO_container.set("MFCYP6", Integer.parseInt(currentMonth))
        Double MDQT = 0d

        Closure<?> processMITAFORecord = { DBContainer record_MITAFO ->
          MDQT = Double.parseDouble(record_MITAFO.get("MFMDQT").toString())

        }
        MITAFO_query.readAll(MITAFO_container,4, processMITAFORecord)

        int mitloc_stqt = (int)((STQT1 - ALQT1-(MDQT*12/26)))

        if(mitloc_stqt > 0) {
          mitloc_stqt = (int) (mitloc_stqt/COFA) 
          mi.outData.put("WHSL",arr[0])
          mi.outData.put("FACI",arr[1])
          mi.outData.put("ITNO",arr[2])
          mi.outData.put("WHLO",arr[3])
          mi.outData.put("COFA",arr[4])
          mi.outData.put("DMCF",arr[5])
          mi.outData.put("AGQT",arr[6])
          mi.outData.put("STQT",String.valueOf(mitloc_stqt))
          mi.write()
        }
      }
      MITLOC_query.readAll(MITLOC_container,5, processMITLOCRecord)

    }
  }
}