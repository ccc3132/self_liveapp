package egovframework.egovwebpractice.common.service;

public class SystemCheckVO {

    private int id;
    private String message;
    private String dbProduct;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDbProduct() {
        return dbProduct;
    }

    public void setDbProduct(String dbProduct) {
        this.dbProduct = dbProduct;
    }
}
