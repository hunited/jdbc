CREATE TABLE `employees`(`id` BIGINT auto_increment, `emp_name` VARCHAR(255), CONSTRAINT primary key(id));
INSERT INTO `employees` (`emp_name`) VALUES
	('John Doe'),
	('Jane Doe'),
	('Jack Doe'),
	('Joe Doe');