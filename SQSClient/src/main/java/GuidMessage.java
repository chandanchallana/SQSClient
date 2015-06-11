/**
 * Created by Joker on 6/10/15.
 */
public class GuidMessage {

    private String guid;
    private String aggrementNumber;
    private String accountCsn;
    private String customerAccName;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = "\""+guid+ "\"";

        //this.guid = this.guid.replace(".","");
    }

    public String getAggrementNumber() {
        return aggrementNumber;
    }

    public void setAggrementNumber(String aggrementNumber) {
        this.aggrementNumber = "\""+aggrementNumber+ "\"";

    }

    public String getAccountCsn() {
        return accountCsn;
    }

    public void setAccountCsn(String accountCsn) {
        this.accountCsn = "\""+accountCsn+ "\"";
    }

    public String getCustomerAccName() {
        return customerAccName;
    }

    public void setCustomerAccName(String customerAccName) {
        this.customerAccName = "\""+customerAccName+ "\"";
    }

    public String toString(){
        return "{\"guid\": "+guid+","
                 +" \"AggreementNumber\": "+aggrementNumber+ ","
                 +" \"accountCsn\": "+accountCsn+ ","
                 +" \"customerAccName\": "+customerAccName
                 + " }";
    }
}
