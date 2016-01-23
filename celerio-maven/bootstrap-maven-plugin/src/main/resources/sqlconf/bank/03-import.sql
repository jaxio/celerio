-- Populate the database
-- Note: it is automatically executed by hibernate
-- We use negative ids to avoid conflict with ids created when using the application
-- Known limitation: it does not support accents... Would be nice to fix it.

INSERT INTO USER (id, login, password, email, is_enabled, version) VALUES (-1, 'admin', 'admin', 'admin@example.com', true, 1);

INSERT INTO ROLE (id, role_name) VALUES (-1, 'ROLE_ADMIN');
INSERT INTO ROLE (id, role_name) VALUES (-2, 'ROLE_USER');
INSERT INTO ROLE (id, role_name) VALUES (-3, 'ROLE_MONITORING');

INSERT INTO USER_ROLE (user_id, role_id) VALUES (-1, -1);
INSERT INTO USER_ROLE (user_id, role_id) VALUES (-1, -2);
INSERT INTO USER_ROLE (user_id, role_id) VALUES (-1, -3);

INSERT INTO CURRENCY (id, code, name, decimal_count, version) VALUES (-1, 'EUR', 'Euro', 2, 1);
INSERT INTO CURRENCY (id, code, name, decimal_count, version) VALUES (-2, 'USD', 'US Dollar', 2, 1);
INSERT INTO CURRENCY (id, code, name, decimal_count, version) VALUES (-3, 'GBP', 'British Sterling', 2, 1);
INSERT INTO CURRENCY (id, code, name, decimal_count, version) VALUES (-4, 'CAD', 'Canadien Dollar', 2, 1);
INSERT INTO CURRENCY (id, code, name, decimal_count, version) VALUES (-5, 'CHF', 'Swiss franc', 2, 1);
INSERT INTO CURRENCY (id, code, name, decimal_count, version) VALUES (-6, 'JPY', 'Yen', 0, 1);
INSERT INTO CURRENCY (id, code, name, decimal_count, version) VALUES (-7, 'SEK', 'Couronne', 2, 1);

INSERT INTO ADDRESS (id, street, zip_code, city, country, version) VALUES (-1, '1 RUE DE LA BANQUE', '75000', 'Paris', 'France', 1);
INSERT INTO ADDRESS (id, street, zip_code, city, country, version) VALUES (-2, '2 RUE DE LA BANQUE', '75000', 'Paris', 'France', 1);
INSERT INTO ADDRESS (id, street, zip_code, city, country, version) VALUES (-3, '3 RUE DE LA BANQUE', '75000', 'Paris', 'France', 1);
INSERT INTO ADDRESS (id, street, zip_code, city, country, version) VALUES (-4, 'NY', '10045', 'New-York', 'Etats-Unis', 1);
INSERT INTO ADDRESS (id, street, zip_code, city, country, version) VALUES (-5, 'AL', '16000', 'Alger', 'Algeria', 1);
INSERT INTO ADDRESS (id, street, zip_code, city, country, version) VALUES (-6, '4 RUE DE LA BANQUE', '75000', 'Paris', 'France', 1);

INSERT INTO CUSTOMER (id, company_name, address_id, version) VALUES (-1, 'Trésor Public', -1, 1);
INSERT INTO CUSTOMER (id, company_name, address_id, version) VALUES (-2, 'BNP Paribas', -2, 1);
INSERT INTO CUSTOMER (id, company_name, address_id, version) VALUES (-3, 'Société Générale', -3, 1);
INSERT INTO CUSTOMER (id, company_name, address_id, version) VALUES (-4, 'Federal Reserve bank of New York', -4, 1);
INSERT INTO CUSTOMER (id, company_name, address_id, version) VALUES (-5, 'Algeria central bank', -5, 1);
INSERT INTO CUSTOMER (id, company_name, address_id, version) VALUES (-6, 'DO Budget', -6, 1);

INSERT INTO ACCOUNT (id, account_number, name, currency_id, customer_id, version) VALUES (-1, '0001001', 'Compte 1', -1, -1, 1);
INSERT INTO ACCOUNT (id, account_number, name, currency_id, customer_id, version) VALUES (-2, '0001002', 'Compte 2', -2, -1, 1);
INSERT INTO ACCOUNT (id, account_number, name, currency_id, customer_id, version) VALUES (-3, '0002001', 'Compte 1', -1, -2, 1);
INSERT INTO ACCOUNT (id, account_number, name, currency_id, customer_id, version) VALUES (-4, '0002002', 'Compte 2', -2, -2, 1);
INSERT INTO ACCOUNT (id, account_number, name, currency_id, customer_id, version) VALUES (-5, '0003001', 'Compte 1', -1, -3, 1);
INSERT INTO ACCOUNT (id, account_number, name, currency_id, customer_id, version) VALUES (-6, '0003002', 'Compte 2', -2, -3, 1);
INSERT INTO ACCOUNT (id, account_number, name, currency_id, customer_id, version) VALUES (-7, '0003003', 'Compte 3', -6, -3, 1);
INSERT INTO ACCOUNT (id, account_number, name, currency_id, customer_id, version) VALUES (-8, '0004001', 'Compte 1', -1, -4, 1);
INSERT INTO ACCOUNT (id, account_number, name, currency_id, customer_id, version) VALUES (-9, '0004002', 'Compte 2', -2, -4, 1);
INSERT INTO ACCOUNT (id, account_number, name, currency_id, customer_id, version) VALUES (-10, '0004003', 'Compte 3', -4, -4, 1);
INSERT INTO ACCOUNT (id, account_number, name, currency_id, customer_id, version) VALUES (-11, '0004004', 'Compte 4', -6, -4, 1);
INSERT INTO ACCOUNT (id, account_number, name, currency_id, customer_id, version) VALUES (-12, '0005001', 'Compte 1', -1, -5, 1);
INSERT INTO ACCOUNT (id, account_number, name, currency_id, customer_id, version) VALUES (-13, '0005002', 'Compte 2', -2, -5, 1);
INSERT INTO ACCOUNT (id, account_number, name, currency_id, customer_id, version) VALUES (-14, '0006001', 'Compte 1', -1, -6, 1);
INSERT INTO ACCOUNT (id, account_number, name, currency_id, customer_id, version) VALUES (-15, '0006002', 'Compte 2', -2, -6, 1);

INSERT INTO TRANSACTION (id, account_id, amount, transaction_date, value_date, currency_id, description, version) VALUES (-1, -1, 100000, {d '2013-01-08'}, {d '2013-01-11'}, -1, 'Invoice 1', 1);
INSERT INTO TRANSACTION (id, account_id, amount, transaction_date, value_date, currency_id, description, version) VALUES (-2, -1, -50000, {d '2013-01-09'}, {d '2013-01-12'}, -1, 'Invoice 2', 1);
INSERT INTO TRANSACTION (id, account_id, amount, transaction_date, value_date, currency_id, description, version) VALUES (-3, -1, 250000, {d '2013-01-10'}, {d '2013-01-13'}, -1, 'Invoice 3', 1);
INSERT INTO TRANSACTION (id, account_id, amount, transaction_date, value_date, currency_id, description, version) VALUES (-4, -1, -5000000, {d '2013-01-11'}, {d '2013-01-14'}, -1, 'Invoice 4', 1);
INSERT INTO TRANSACTION (id, account_id, amount, transaction_date, value_date, currency_id, description, version) VALUES (-5, -1, 2000000, {d '2013-01-12'}, {d '2013-01-15'}, -1, 'Invoice 5', 1);
INSERT INTO TRANSACTION (id, account_id, amount, transaction_date, value_date, currency_id, description, version) VALUES (-6, -1, 100000, {d '2013-01-13'}, {d '2013-01-16'}, -1, 'Invoice 6', 1);
INSERT INTO TRANSACTION (id, account_id, amount, transaction_date, value_date, currency_id, description, version) VALUES (-7, -1, 500000, {d '2013-01-14'}, {d '2013-01-17'}, -1, 'Invoice 7', 1);
INSERT INTO TRANSACTION (id, account_id, amount, transaction_date, value_date, currency_id, description, version) VALUES (-8, -1, -5000000, {d '2013-01-15'}, {d '2013-01-18'}, -1, 'Invoice 8', 1);
INSERT INTO TRANSACTION (id, account_id, amount, transaction_date, value_date, currency_id, description, version) VALUES (-9, -1, 9000000, {d '2013-01-16'}, {d '2013-01-19'}, -1, 'Invoice 9', 1);
INSERT INTO TRANSACTION (id, account_id, amount, transaction_date, value_date, currency_id, description, version) VALUES (-10, -1, 200000, {d '2013-01-17'}, {d '2013-01-20'}, -1, 'Invoice 10', 1);
INSERT INTO TRANSACTION (id, account_id, amount, transaction_date, value_date, currency_id, description, version) VALUES (-11, -2, 100000, {d '2013-01-08'}, {d '2013-01-11'}, -2, 'Invoice 1', 1);
INSERT INTO TRANSACTION (id, account_id, amount, transaction_date, value_date, currency_id, description, version) VALUES (-12, -2, -50000, {d '2013-01-09'}, {d '2013-01-12'}, -2, 'Invoice 2', 1);
INSERT INTO TRANSACTION (id, account_id, amount, transaction_date, value_date, currency_id, description, version) VALUES (-13, -2, 250000, {d '2013-01-10'}, {d '2013-01-13'}, -2, 'Invoice 3', 1);
INSERT INTO TRANSACTION (id, account_id, amount, transaction_date, value_date, currency_id, description, version) VALUES (-14, -2, -5000000, {d '2013-01-11'}, {d '2013-01-14'}, -2, 'Invoice 4', 1);
INSERT INTO TRANSACTION (id, account_id, amount, transaction_date, value_date, currency_id, description, version) VALUES (-15, -2, 2000000, {d '2013-01-12'}, {d '2013-01-15'}, -2, 'Invoice 5', 1);
INSERT INTO TRANSACTION (id, account_id, amount, transaction_date, value_date, currency_id, description, version) VALUES (-16, -2, 100000, {d '2013-01-13'}, {d '2013-01-16'}, -2, 'Invoice 6', 1);
INSERT INTO TRANSACTION (id, account_id, amount, transaction_date, value_date, currency_id, description, version) VALUES (-17, -2, 500000, {d '2013-01-14'}, {d '2013-01-17'}, -2, 'Invoice 7', 1);
INSERT INTO TRANSACTION (id, account_id, amount, transaction_date, value_date, currency_id, description, version) VALUES (-18, -2, -5000000, {d '2013-01-15'}, {d '2013-01-18'}, -2, 'Invoice 8', 1);
INSERT INTO TRANSACTION (id, account_id, amount, transaction_date, value_date, currency_id, description, version) VALUES (-19, -2, 9000000, {d '2013-01-16'}, {d '2013-01-19'}, -2, 'Invoice 9', 1);
INSERT INTO TRANSACTION (id, account_id, amount, transaction_date, value_date, currency_id, description, version) VALUES (-20, -2, 200000, {d '2013-01-17'}, {d '2013-01-20'}, -2, 'Invoice 10', 1);
