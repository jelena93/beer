 CREATE TABLE IF NOT EXISTS comments (
  id INT AUTO_INCREMENT NOT NULL,
  user INT NOT NULL,
  beer INT NOT NULL,
  comment TEXT NOT NULL,
  date DATE NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (user) REFERENCES user(id) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (beer) REFERENCES beer(id) ON UPDATE CASCADE ON DELETE CASCADE
);
--;;
INSERT INTO comments
(user, beer, comment, date) VALUES
(2, 1, 'test comment 1', '2017-05-28'),
(2, 2, 'test comment 2', '2017-05-28');
