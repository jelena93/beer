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
('asd', 'asd', 'Admin', 'Admin', 'admin@gmail.com', 'admin'),
('usder', 'asdd', 'Admin', 'Admdin', 'admin@gmail.com', 'user'),
('dasd', 'asd', 'Addmin', 'Admin', 'admin@gmail.com', 'user'),
('d', 'asdd', 'Admdin', 'Admin', 'admin@gmail.com', 'user');
