CREATE TABLE IF NOT EXISTS user (
   id INT NOT NULL AUTO_INCREMENT,
   username VARCHAR(20) NOT NULL UNIQUE,
   password VARCHAR(500) NOT NULL,
   name VARCHAR(30) NOT NULL,
   surname VARCHAR(30) NOT NULL,
   email VARCHAR(30) NOT NULL,
   role VARCHAR(20) NOT NULL,
   PRIMARY KEY (id)
);
--;;
INSERT INTO user
(username, password, name, surname, email, role) VALUES
('admin', '$2a$04$RNs5Xtjo7F3upPGzSbVPTOfEOPkevYQYk96t82l4A47UenCK6l9Ou', 'Test', 'Admin', 'admin@gmail.com', 'admin'),
('user', '$2a$04$s5fLFIIAa/BQhRgFicH6b.p3GhnJx.RJgdk6wv1ZygVrrtz5X.zlO', 'Test', 'User', 'user@gmail.com', 'user');
