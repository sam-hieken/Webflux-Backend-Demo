CREATE TABLE loans.customer_address(
	id BIGSERIAL PRIMARY KEY,
	street_number SMALLINT NOT NULL,
	street_address VARCHAR(40) NOT NULL,
	street_address2 VARCHAR(35),
	city VARCHAR(30) NOT NULL,
	state VARCHAR(2) NOT NULL,
	zip_code INT NOT NULL
);

CREATE TABLE loans.customer(
	user_id UUID PRIMARY KEY,
	dwolla_id UUID UNIQUE,
	birthday DATE NOT NULL,
	address BIGINT NOT NULL,
	phone_number VARCHAR(16) NOT NULL,
	ssn_last4 SMALLINT NOT NULL,
	owed DECIMAL(19,4) NOT NULL,
	bank_token VARCHAR(75) UNIQUE,
	verified_status SMALLINT NOT NULL,
	last_payment TIMESTAMP(1),
	last_grant TIMESTAMP(1),
	
	CONSTRAINT FK_ADDRESS
    FOREIGN KEY (address)
    REFERENCES loans.customer_address(id)
);

CREATE INDEX INDX_ADDRESS ON loans.customer(address); -- create index on ALL foreign key fields