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
INSERT INTO digital_services (digital_name, digital_price) VALUES ('Пробив по заказу', 2000) ON CONFLICT DO NOTHING;


-- Вставка документов
INSERT INTO documents (document_name, document_price) VALUES ('ПТС', 25000) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('СТС', 17000) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('СРМ', 16000) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('ПСМ', 25000) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('Номерные знаки', 5000) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('Подбор двойника', 12000) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('Подбор донора', 15000) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('Права на авто(оф)', 155000) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('Предписание на авто', 95000) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('ГИМС', 14000) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('Диплом(не оф)', 120000) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('Аттестат(не оф)', 100000) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('Паспорт(до 2007 года)', 150000) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('СНИЛС', 10000) ON CONFLICT DO NOTHING;
INSERT INTO other_services (other_name, other_price) VALUES ('Мед. справки', 13000) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('Прописка', 35000) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('Военный билет', 200000) ON CONFLICT DO NOTHING;

-- Вставка финансовых услуг
INSERT INTO financial_services (financial_name, financial_price) VALUES ('Восстановление', 0) ON CONFLICT DO NOTHING;
INSERT INTO financial_services (financial_name, financial_price) VALUES ('Банковские карты от', 25000) ON CONFLICT DO NOTHING;

-- Вставка других услуг
INSERT INTO other_services (other_name, other_price) VALUES ('Сим карты', 0) ON CONFLICT DO NOTHING;



-- Вставка акций
