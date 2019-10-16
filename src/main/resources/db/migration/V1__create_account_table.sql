create table ACCOUNT
(
	ID BIGINT auto_increment primary key,
	ACCOUNT_REF TEXT default false,
	LOCKED BOOLEAN default false,
	BALANCE DECIMAL(19,4) default 0,
	CURRENCY VARCHAR(10) default 'EUR',
	IS_ACTIVE BOOLEAN default true
)
;

