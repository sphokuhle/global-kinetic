DROP TABLE IF EXISTS USERS;

CREATE TABLE USERS (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  username VARCHAR(100) NOT NULL,
  phone VARCHAR(100) DEFAULT NULL,
  password VARCHAR(250) NOT NULL,
  active_date TIMESTAMP DEFAULT NULL,
  logged_in BOOLEAN DEFAULT false
);