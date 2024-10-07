-- Вставка категорий
INSERT INTO categories (category_text) VALUES ('Документы') ON CONFLICT DO NOTHING;
INSERT INTO categories (category_text) VALUES ('Финансовые услуги') ON CONFLICT DO NOTHING;
INSERT INTO categories (category_text) VALUES ('Банковские услуги') ON CONFLICT DO NOTHING;
INSERT INTO categories (category_text) VALUES ('Цифровые услуги') ON CONFLICT DO NOTHING;
INSERT INTO categories (category_text) VALUES ('Прочее') ON CONFLICT DO NOTHING;
INSERT INTO categories (category_text) VALUES ('Акции') ON CONFLICT DO NOTHING;

-- Вставка банковских услуг
INSERT INTO bank_services (bank_name, bank_price) VALUES ('Помощь в получении кредита', 10) ON CONFLICT DO NOTHING;

-- Вставка цифровых услуг
INSERT INTO digital_services (digital_name, digital_price) VALUES ('Пробив по заказу', 100) ON CONFLICT DO NOTHING;
INSERT INTO digital_services (digital_name, digital_price) VALUES ('Накрутка соц. сетей', 110) ON CONFLICT DO NOTHING;

-- Вставка документов
INSERT INTO documents (document_name, document_price) VALUES ('СТС', 210) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('ПТС', 220) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('Водительское удостоверение', 230) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('ГИМС', 240) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('Номерные знаки', 250) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('Подбор двойника', 260) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('Военный билет', 270) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('Паспорт', 280) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('Аттестат', 290) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('Дипломы', 300) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('Временное гражданство', 310) ON CONFLICT DO NOTHING;
INSERT INTO documents (document_name, document_price) VALUES ('СНИЛС', 320) ON CONFLICT DO NOTHING;

-- Вставка финансовых услуг
INSERT INTO financial_services (financial_name, financial_price) VALUES ('Восстановление', 400) ON CONFLICT DO NOTHING;
INSERT INTO financial_services (financial_name, financial_price) VALUES ('Чистые карты', 410) ON CONFLICT DO NOTHING;

-- Вставка других услуг
INSERT INTO other_services (other_name, other_price) VALUES ('Сим карты', 510) ON CONFLICT DO NOTHING;
INSERT INTO other_services (other_name, other_price) VALUES ('Номера телефона', 520) ON CONFLICT DO NOTHING;
INSERT INTO other_services (other_name, other_price) VALUES ('Мед. справки', 530) ON CONFLICT DO NOTHING;
INSERT INTO other_services (other_name, other_price) VALUES ('Выписки', 540) ON CONFLICT DO NOTHING;

-- Вставка акций
INSERT INTO sales (sale_name, sale_price) VALUES ('Новая акция', 600) ON CONFLICT DO NOTHING;
