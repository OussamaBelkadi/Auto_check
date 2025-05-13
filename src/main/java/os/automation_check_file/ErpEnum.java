package os.automation_check_file;

public enum ErpEnum {
    ORACLE("oracle"),
    SPA("spa") ;
    private final String value;
    ErpEnum(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
