INSERT INTO
    city (id, name)
VALUES
    (nextval('city_seq'), 'Самара'),
    (nextval('city_seq'), 'Санкт-Петербург'),
    (nextval('city_seq'), 'Москва'),
    (nextval('city_seq'), 'Ростов-на-Дону'),
    (nextval('city_seq'), 'Обнинск'),
    (nextval('city_seq'), 'Дмитров')
;


INSERT INTO
    customer (id, name, email)
VALUES
    (nextval('customer_seq'), 'Александр Шустанов', 'shustanov@springio.ru'),
    (nextval('customer_seq'), 'Илья Кучмин', 'kuchmin@springio.ru'),
    (nextval('customer_seq'), 'Кирилл Толкачев', 'tolkachev@jugrugroup.ru'),
    (nextval('customer_seq'), 'Павел Кислов', 'kislov@springio.ru'),
    (nextval('customer_seq'), 'Михаил Поливаха', 'polivaha@springio.ru'),
    (nextval('customer_seq'), 'Илья Сазонов', 'sazonovi@springio.ru'),
    (nextval('customer_seq'), 'Федор Сазонов', 'sazonovf@springio.ru');

INSERT INTO
    product (id, name, price)
VALUES
    (nextval('product_seq'), 'Пиво', 245.99),
    (nextval('product_seq'), 'Подгузники', 1450.99),
    (nextval('product_seq'), 'Хлеб', 59.99),
    (nextval('product_seq'), 'Молоко', 95.50),
    (nextval('product_seq'), 'Сыр', 489.00),
    (nextval('product_seq'), 'Колбаса', 679.99),
    (nextval('product_seq'), 'Яйца', 120.00),
    (nextval('product_seq'), 'Шоколад', 199.99),
    (nextval('product_seq'), 'Чай', 299.00),
    (nextval('product_seq'), 'Кофе', 899.00),
    (nextval('product_seq'), 'Масло сливочное', 379.50),
    (nextval('product_seq'), 'Макароны', 149.99),
    (nextval('product_seq'), 'Рис', 199.00),
    (nextval('product_seq'), 'Картофель', 49.99),
    (nextval('product_seq'), 'Морковь', 39.99),
    (nextval('product_seq'), 'Лук', 29.99),
    (nextval('product_seq'), 'Апельсины', 249.00),
    (nextval('product_seq'), 'Яблоки', 189.99),
    (nextval('product_seq'), 'Бананы', 159.99),
    (nextval('product_seq'), 'Свинина', 799.00),
    (nextval('product_seq'), 'Говядина', 999.00),
    (nextval('product_seq'), 'Курица', 399.00),
    (nextval('product_seq'), 'Рыба', 659.00),
    (nextval('product_seq'), 'Сметана', 129.00),
    (nextval('product_seq'), 'Йогурт', 89.99),
    (nextval('product_seq'), 'Сок', 220.00),
    (nextval('product_seq'), 'Вода', 49.00);