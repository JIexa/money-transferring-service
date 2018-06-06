# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table account (
  id                            bigint auto_increment not null,
  owner_id                      bigint,
  amount_of_money               decimal(38),
  currency                      integer,
  constraint ck_account_currency check ( currency in (0,1,2,3)),
  constraint pk_account primary key (id)
);


# --- !Downs

drop table if exists account;

