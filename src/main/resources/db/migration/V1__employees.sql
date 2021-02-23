CREATE TABLE `employees`(`id` BIGINT AUTO_INCREMENT, `emp_name` VARCHAR(255), CONSTRAINT PRIMARY KEY(`id`));
INSERT INTO `employees` (`emp_name`) VALUES
	('John Doe'),
	('Jane Doe'),
	('Jack Doe'),
	('Joe Doe');