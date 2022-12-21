/**
* Business Engine Extension
*/
/****************************************************************************************
Extension Name: LstSlimFcsQty
Type : ExtendM3Transaction
Script Author: Abhinav Pulimala
Date: 2022-05-23



Description:
List of Slim Facility and Quantity details.

Revision History:
Name Date Version Description of Changes
Abhinav Pulimala 2022-05-23 1.0 Initial Version
******************************************************************************************/
public class LstSlimFcsQty extends ExtendM3Transaction {
  private final MIAPI mi 
  private final DatabaseAPI database 
  private final ProgramAPI program 
  private final LoggerAPI logger
  public LstSlimFcsQty(MIAPI mi, DatabaseAPI database, ProgramAPI program, LoggerAPI logger) {
    this.mi = mi 
    this.database = database 
    this.program = program 
    this.logger = logger
  }
  public void main() {
    int inCONO = mi.in.get("CONO") 
    String inITNO = (mi.in.get("ITNO") == null)? "": mi.in.get("ITNO") 
    String inDIVI = (mi.in.get("DIVI") == null)? "": mi.in.get("DIVI") 
    int inPETP = mi.in.get("PETP") 
    String inWHLO = (mi.in.get("WHLO") == null)? "": mi.in.get("WHLO") 
    String YEA4 
    String PERI = "00"
    String FDAT 
    String TDAT 
    String MDQT 
    String period 
    String fdate 
    String tdate 
    
    Date now = new Date() 
    int year = now.getYear() + 1900 
    int month = now.getMonth() + 1 
    int fromdate = year * 10000 + month * 100 
    int todate = (year + 1) * 10000 + month * 100
    ExpressionFactory exp_dbaCSYPER00 = database.getExpressionFactory("CSYPER") 
    exp_dbaCSYPER00 =  exp_dbaCSYPER00.ge("CPFDAT",fromdate.toString()).and(exp_dbaCSYPER00.le("CPTDAT",todate.toString())) 
    DBAction dbaCSYPER00 = database.table("CSYPER").index("00").selection("CPCONO", "CPDIVI", "CPPETP", "CPPERI", "CPFDAT", "CPTDAT", "CPYEA4").matching(exp_dbaCSYPER00).build() 
    DBContainer conCSYPER00 = dbaCSYPER00.getContainer() 
    conCSYPER00.set("CPCONO", inCONO) 
    conCSYPER00.set("CPDIVI", inDIVI) 
    conCSYPER00.set("CPPETP", inPETP) 
    Closure < ? > resultHandlerCSYPER00 = {
      DBContainer data ->
      fdate = data.get("CPFDAT").toString() 
      tdate = data.get("CPTDAT").toString() 
      PERI = data.get("CPPERI").toString()  
      YEA4 = data.get("CPYEA4").toString()
      if (PERI.length() == 1) {
        PERI = '0' + PERI
      }
      period = YEA4 + PERI 
      DBAction dbaMITAFO00 = database.table("MITAFO").index("00").selection("MFCONO", "MFWHLO", "MFITNO", "MFCYP6", "MFMDQT").build() 
      DBContainer conMITAFO00 = dbaMITAFO00.getContainer() 
      conMITAFO00.set("MFCONO", inCONO) 
      conMITAFO00.set("MFWHLO", inWHLO) 
      conMITAFO00.set("MFITNO", inITNO) 
      conMITAFO00.set("MFCYP6", period.toInteger()) 
      Closure < ? > resultHandlerMITAFO00 = {
        DBContainer data1 ->
        //count1 = count1 + 1 
        String MFMDQT = data1.get("MFMDQT").toString() 
        mi.outData.put("TDAT",tdate) 
        mi.outData.put("FDAT",fdate) 
        mi.outData.put("MDQT",MFMDQT) 
        mi.write() 
      }
      if (!dbaMITAFO00.readAll(conMITAFO00, 4, resultHandlerMITAFO00)) {
        mi.outData.put("TDAT",tdate) 
        mi.outData.put("FDAT",fdate) 
        mi.outData.put("MDQT","0") 
        mi.write() 
      }
    }
    if (!dbaCSYPER00.readAll(conCSYPER00, 3, resultHandlerCSYPER00)) {
      mi.error("Records does not exist in CSYPER") 
      return 
    }
  }
}

    