package cl.vetnova.inventario.dto;

public class StockTotalResponse {

    private Integer stockTotal;

    public StockTotalResponse() {
    }

    public StockTotalResponse(Integer stockTotal) {
        this.stockTotal = stockTotal;
    }

    public Integer getStockTotal() { return stockTotal; }
    public void setStockTotal(Integer stockTotal) { this.stockTotal = stockTotal; }
}
