-- name: get-user
select * from user where username = :username and password = :password

-- name: add-user!
INSERT INTO user (username, password, first_name, last_name, email, role)
VALUES (:username, :password, :first_name, :last_name, :email, :role)
