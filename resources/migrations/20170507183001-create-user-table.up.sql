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
('a', '$2a$04$nICDa2s1MO7K9AidBviC7.7UPhsGqSOt3YJZbA/AymcEhAGZzViAK', 'Test', 'Admin', 'admin@gmail.com', 'admin'),
('u', '$2a$04$D6IyS9yfO1wrkwAFi0uIr.vH5Ee.vp3QJI1ZijyfmUT1VSjH40CTu', 'Test', 'User', 'user@gmail.com', 'user');
