/**
 *  Business Engine Extension
 */
 /****************************************************************************************
 Extension Name: EXT480
 Type : ExtendM3Trigger
 Script Author: Arun Tiwari
 Date: 2022-05-26
  
 Description:
       set the consignee based on order division
          
 Revision History:
 Name                    Date             Version          Description of Changes
 Arun Tiwari             2022-05-26                     1.0              Initial Version
 ******************************************************************************************/
public class RetrieveMediaKeyCheck extends ExtendM3Trigger {
  private final ExtensionAPI extension
  private final MethodAPI method
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller
  private String INRC
  private String DIVI
  private boolean CRS949_CHECK

  public RetrieveMediaKeyCheck(ExtensionAPI extension,MethodAPI method,DatabaseAPI database,ProgramAPI program,MICallerAPI miCaller) {
    this.extension = extension
    this.method=method
    this.database = database
    this.program = program
    this.miCaller = miCaller

  }

  public void main() {
    int retryIndex=(int)method.getArgument(0)
    long DLIX = (Long) method.getArgument(1)
    Media media
    CRS949_CHECK = false

    media = retrieveMediaKeys(retryIndex,DLIX)
    if(CRS949_CHECK)
      method.setReturnValue(media)
  }
  
//Retrieve and set CONA as media based on CRS949 else Invoice receipt
  public Media retrieveMediaKeys(int retryIndex, long DLIX) {
    int CONO = (Integer)program.getLDAZD().CONO
    boolean Flag = true
    String COAF = ""
    String CONA = ""
    String WHLO = ""

    //MHDISH
    DBAction MHDISH_query = database.table("MHDISH").index("00").selection("OQCOAF","OQCONA","OQWHLO").build();
    DBContainer MHDISH_container = MHDISH_query.getContainer();
    MHDISH_container.set("OQCONO", CONO)
    MHDISH_container.set("OQDLIX", DLIX)
    MHDISH_container.set("OQINOU", 1);

    Closure<?> processCOAFRecord = { DBContainer record_COAF ->
      COAF = record_COAF.getString("OQCOAF").trim()
      CONA = record_COAF.getString("OQCONA").trim()
      WHLO = record_COAF.getString("OQWHLO").trim()
    }
    MHDISH_query.readAll(MHDISH_container,3, processCOAFRecord)


    //select MHDISL
    DBAction MHDISL_query = database.table("MHDISL").index("00").selection("URRIDN").build()
    DBContainer MHDISL_container = MHDISL_query.getContainer()
    MHDISL_container.set("URCONO", CONO)
    MHDISL_container.set("URDLIX", DLIX)
    MHDISL_container.set("URRORC", 3)
    Closure<?> processDLIXRecord = { DBContainer record_DLIX ->
      String ORNO = record_DLIX.getString("URRIDN").trim();
      if(Flag) {
        def params = [ "ORNO":"${ORNO}".toString() ]

        def callback = {
          Map<String, String> response ->

          if(response.INRC != null){
            INRC = response.INRC
          }
        }

        miCaller.call("OIS100MI","GetHead", params, callback)
        Flag = false
      }
    };
    MHDISL_query.readAll(MHDISL_container,3, processDLIXRecord)

    //Get DIVI from WHLO
    def params_WHLO = [ "WHLO":"${WHLO}".toString() ]

    def callback_WHLO = {
      Map<String, String> response ->

      if(response.DIVI != null){
        DIVI = response.DIVI
      }
    }

    miCaller.call("MMS005MI","GetWarehouse", params_WHLO, callback_WHLO)


    def params = [ "DIVI":"${DIVI}".toString(),"DONR":"900","DOVA":"00","PRF1":"${CONA}".toString(),"MEDC":"MBM","SEQN":"1"]

    def callback = {
      Map<String, String> response ->

      if(response.SIID != null){
        CRS949_CHECK = true
      }
    }

    miCaller.call("CRS949MI","GetPartnerMedia", params, callback)

    if(!CRS949_CHECK) {
      def params1 = [ "DIVI":"${DIVI}".toString(),"DONR":"900","DOVA":"00","PRF1":"${INRC}".toString(),"MEDC":"MBM","SEQN":"1"]

      def callback1 = {
        Map<String, String> response1 ->

        if(response1.SIID != null){
          CRS949_CHECK = true
        }
      }

      miCaller.call("CRS949MI","GetPartnerMedia", params1, callback1)
      return new Media(INRC, COAF, false)
    }
    else {
      return new Media(CONA, COAF, false)
    }


  }
}