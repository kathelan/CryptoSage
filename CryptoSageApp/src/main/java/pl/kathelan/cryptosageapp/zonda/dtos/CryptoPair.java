package pl.kathelan.cryptosageapp.zonda.dtos;

public enum CryptoPair {

    BTC_PLN("BTC-PLN"),
    ETH_PLN("ETH-PLN"),
    SOL_PLN("SOL-PLN");

    private final String value;

    CryptoPair(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
