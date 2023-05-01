--Create table
CREATE TABLE example_table (
                               id NUMBER(10) PRIMARY KEY,
                               name VARCHAR2(50),
                               age NUMBER(3),
                               city VARCHAR2(50)
);

--Insert 10 records
INSERT INTO example_table (id, name, age, city) VALUES (1, 'John', 25, 'New York');
INSERT INTO example_table (id, name, age, city) VALUES (2, 'Mary', 30, 'Los Angeles');
INSERT INTO example_table (id, name, age, city) VALUES (3, 'David', 40, 'Chicago');
INSERT INTO example_table (id, name, age, city) VALUES (4, 'Sarah', 35, 'Houston');
INSERT INTO example_table (id, name, age, city) VALUES (5, 'Tom', 28, 'San Francisco');
INSERT INTO example_table (id, name, age, city) VALUES (6, 'Jane', 22, 'Seattle');
INSERT INTO example_table (id, name, age, city) VALUES (7, 'Steve', 50, 'Boston');
INSERT INTO example_table (id, name, age, city) VALUES (8, 'Megan', 27, 'Dallas');
INSERT INTO example_table (id, name, age, city) VALUES (9, 'Ryan', 31, 'Miami');
INSERT INTO example_table (id, name, age, city) VALUES (10, 'Emily', 29, 'Denver');

--Update a few records
UPDATE example_table SET age = 26 WHERE id = 1;
UPDATE example_table SET age = 32 WHERE id = 2;
UPDATE example_table SET age = 41 WHERE id = 3;

--Delete a record
DELETE FROM example_table WHERE id = 9;

COMMIT;