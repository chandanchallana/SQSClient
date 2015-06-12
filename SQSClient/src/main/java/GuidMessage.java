/**
 * Created by Joker on 6/10/15.
 */


public class GuidMessage {

    private String guid;
    private String aggreementNumber;
    private String accountCsn;
    private String customerAccName;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid ;

        //this.guid = this.guid.replace(".","");
    }

    public String getAggreementNumber() {
        return aggreementNumber;
    }

    public void setAggreementNumber(String aggreementNumber) {
        this.aggreementNumber = aggreementNumber;

    }

    public String getAccountCsn() {
        return accountCsn;
    }

    public void setAccountCsn(String accountCsn) {
        this.accountCsn = accountCsn;
    }

    public String getCustomerAccName() {
        return customerAccName;
    }

    public void setCustomerAccName(String customerAccName) {
        this.customerAccName = customerAccName;
    }

    public String toString(){
        return "[guid: "+getGuid()+","
                 +"aggreementNumber: "+getAggreementNumber()+ ","
                 +"accountCsn: "+getAccountCsn()+ ","
                 +"customerAccName: "+getCustomerAccName()
                 + "]";
    }
}
