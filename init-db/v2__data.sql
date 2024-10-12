-- Вставка категорий
INSERT INTO categories (category_text) VALUES ('Документы') ON CONFLICT DO NOTHING;
INSERT INTO categories (category_text) VALUES ('Финансовые услуги') ON CONFLICT DO NOTHING;
INSERT INTO categories (category_text) VALUES ('Банковские услуги') ON CONFLICT DO NOTHING;
INSERT INTO categories (category_text) VALUES ('Цифровые услуги') ON CONFLICT DO NOTHING;
INSERT INTO categories (category_text) VALUES ('Прочее') ON CONFLICT DO NOTHING;
INSERT INTO categories (category_text) VALUES ('Акции') ON CONFLICT DO NOTHING;

-- Вставка банковских услуг
INSERT INTO bank_services (bank_name, bank_price) VALUES ('Помощь в получении кредита', 0) ON CONFLICT DO NOTHING;

-- Вставка цифровых услуг
INSERT INTO digital_services (digital_name, digital_price) VALUES ('Пробив по заказу', 0) ON CONFLICT DO NOTHING;
INSERT INTO digital_services (digital_name, digital_price) VALUES ('Накрутка соц. сетей', 0) ON CONFLICT DO NOTHING;

-- Вставка документов
INSERT INTO documents (document_name, document_price) VALUES ('СТС', 22000) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('ПТС', 22000) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('Водительское удостоверение', 150000) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('ГИМС', 15000) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('Номерные знаки', 5000) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('Подбор двойника', 15000) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('Военный билет', 160000) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('Паспорт', 150000) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('Аттестат(не официально)', 50000) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('Дипломы(не официально)', 50000) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('Временное гражданство', 25000) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('СНИЛС', 8000) ON CONFLICT DO NOTHING;

-- Вставка финансовых услуг
INSERT INTO financial_services (financial_name, financial_price) VALUES ('Восстановление', 0) ON CONFLICT DO NOTHING;
INSERT INTO financial_services (financial_name, financial_price) VALUES ('Банковские карты от', 35000) ON CONFLICT DO NOTHING;

-- Вставка других услуг
INSERT INTO other_services (other_name, other_price) VALUES ('Сим карты', 0) ON CONFLICT DO NOTHING;
INSERT INTO other_services (other_name, other_price) VALUES ('Номера телефона', 0) ON CONFLICT DO NOTHING;
INSERT INTO other_services (other_name, other_price) VALUES ('Мед. справки', 15000) ON CONFLICT DO NOTHING;
INSERT INTO other_services (other_name, other_price) VALUES ('Выписки', 0) ON CONFLICT DO NOTHING;

-- Вставка акций

