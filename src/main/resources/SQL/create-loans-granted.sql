CREATE TABLE loans.grants(
		"id" UUID PRIMARY KEY,
		receiver_user_id UUID NOT NULL,
		broker_uuid UUID NOT NULL,
		granted_amount DECIMAL(19, 4) NOT NULL,
		granted_balance_new DECIMAL(19,4),
		granted_time TIMESTAMP(1) NOT NULL,
		granted_bank_token VARCHAR(70) NOT NULL,
		granted_credit_score SMALLINT NOT NULL,
		grant_type SMALLINT NOT NULL,
		
	    CONSTRAINT FK_RECEIVER
	    FOREIGN KEY (receiver_user_id)
	    REFERENCES loans.balance("user_id")
);
	
CREATE INDEX INDX_GRANTED_UID ON loans.grants(granted_user_id);