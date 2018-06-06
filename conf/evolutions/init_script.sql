drop table if exists account;

create table account (
  id                            bigint auto_increment not null,
  owner_id                      bigint,
  amount_of_money               DOUBLE,
  currency                      integer,
  constraint ck_account_currency check ( currency in (0,1,2,3)),
  constraint pk_account primary key (id)
);
