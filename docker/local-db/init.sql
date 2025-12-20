CREATE DATABASE IF NOT EXISTS near_connect;
CREATE USER IF NOT EXISTS 'batch_user'@'%' IDENTIFIED BY 'batch_user';
GRANT ALL PRIVILEGES ON near_connect.* TO 'batch_user'@'%';
FLUSH PRIVILEGES;
