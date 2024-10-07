CREATE TABLE if not exists categories(
  id SERIAL PRIMARY KEY,
  category_text varchar(255) not null
);
CREATE TABLE if not exists customers(
  chat_id bigint PRIMARY KEY,
  username varchar(255) not null
);
CREATE TABLE if not exists bank_services(
  id SERIAL PRIMARY KEY,
  bank_name varchar(255) not null,
  bank_price int not null
);
CREATE TABLE if not exists digital_services(
  id SERIAL PRIMARY KEY,
  digital_name varchar(255) not null,
  digital_price int not null
);
CREATE TABLE if not exists documents(
  id SERIAL PRIMARY KEY,
  document_name varchar(255) not null,
  document_price int not null
);
CREATE TABLE if not exists financial_services(
  id SERIAL PRIMARY KEY,
  financial_name varchar(255) not null,
  financial_price int not null
);
CREATE TABLE if not exists other_services(
  id SERIAL PRIMARY KEY,
  other_name varchar(255) not null,
  other_price int not null
);
CREATE TABLE if not exists sales(
  id SERIAL PRIMARY KEY,
  sale_name varchar(255) not null,
  sale_price int not null
);