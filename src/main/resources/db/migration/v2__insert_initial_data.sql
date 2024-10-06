insert into public.categories (category_text) values ('Докумнты');
insert into public.categories (category_text) values ('Финансовые услуги');
insert into public.categories (category_text) values ('Банковские услуги');
insert into public.categories (category_text) values ('Цифровые услуги');
insert into public.categories (category_text) values ('Прочее');
insert into public.categories (category_text) values ('Акции');

insert into public.bank_services (bank_name, bank_price) values ('Помощь в получении кредита', 10);

insert into public.digital_services (digital_name, digital_price) values ('Пробив по заказу', 100);
insert into public.digital_services (digital_name, digital_price) values ('Накрутка соц. сетей', 110);

insert into public.documents (document_name, document_price) values ('СТС', 210);
insert into public.documents (document_name, document_price) values ('ПТС', 220);
insert into public.documents (document_name, document_price) values ('Водительское удостоверение', 230);
insert into public.documents (document_name, document_price) values ('ГИМС', 240);
insert into public.documents (document_name, document_price) values ('Номерные знаки', 250);
insert into public.documents (document_name, document_price) values ('Подбор двойника', 260);
insert into public.documents (document_name, document_price) values ('Военный билет', 270);
insert into public.documents (document_name, document_price) values ('Паспорт', 280);
insert into public.documents (document_name, document_price) values ('Аттестат', 290);
insert into public.documents (document_name, document_price) values ('Дипломы', 300);
insert into public.documents (document_name, document_price) values ('Временное гражданство', 310);
insert into public.documents (document_name, document_price) values ('СНИЛС', 320);

insert into public.financial_services (financial_name, financial_price) values ('Восстановление', 400);
insert into public.financial_services (financial_name, financial_price) values ('Чистые карты', 410);

insert into public.other_services (other_name, other_price) values ('Сим карты', 510);
insert into public.other_services (other_name, other_price) values ('Номера телефона', 520);
insert into public.other_services (other_name, other_price) values ('Мед. справки', 530);
insert into public.other_services (other_name, other_price) values ('Выписки', 540);

insert into public.sales (sale_name, sale_price) values ('Новая акция', 600);