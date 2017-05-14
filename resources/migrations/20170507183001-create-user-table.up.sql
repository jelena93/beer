CREATE TABLE IF NOT EXISTS user (
   id INT NOT NULL AUTO_INCREMENT UNIQUE,
   username VARCHAR(20) NOT NULL,
   password VARCHAR(20) NOT NULL,
   first_name VARCHAR(30) NOT NULL,
   last_name VARCHAR(30) NOT NULL,
   email VARCHAR(30) NOT NULL,
   role VARCHAR(20) NOT NULL,
   PRIMARY KEY (id)
);
--;;
INSERT INTO user
(username, password, first_name, last_name, email, role) VALUES
('a', 'a', 'Admin', 'Admin', 'admin@gmail.com', 'admin'),
('u', 'u', 'Admin', 'Admdin', 'admin@gmail.com', 'user'),
('d', 'd', 'Addmin', 'Admin', 'admin@gmail.com', 'user'),
('s', 's', 'Admdin', 'Admin', 'admin@gmail.com', 'user');
