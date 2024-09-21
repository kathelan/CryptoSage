-- create-crypto-pairs-1.sql


CREATE TABLE IF NOT EXISTS crypto_currency_pair (
                                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                    crypto_pair VARCHAR(10) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS price_record (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            crypto_currency_pair_id BIGINT,
                                            price DOUBLE,
                                            created_at TIMESTAMP NOT NULL,
                                            FOREIGN KEY (crypto_currency_pair_id) REFERENCES crypto_currency_pair(id)
    );

CREATE INDEX IF NOT EXISTS idx_date_time ON price_record (created_at);
CREATE INDEX IF NOT EXISTS idx_crypto_currency_pair ON crypto_currency_pair (crypto_pair);

alter table PRICE_RECORD
    alter column CRYPTO_CURRENCY_PAIR_ID set not null;

