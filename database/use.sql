-- -------------------------------------------------------------
-- CREATE TABLE "role" -----------------------------------------
-- -------------------------------------------------------------
CREATE TABLE `role` (
	                    `role_id` SmallInt UNSIGNED AUTO_INCREMENT NOT NULL,
	                    `name` VarChar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
	                    `description` VarChar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
	                    `last_update` Timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	                    PRIMARY KEY (`role_id`),
	                    UNIQUE KEY `uk_role_name` (`name`)
)
	CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci
ENGINE = InnoDB
AUTO_INCREMENT = 1;

-- -------------------------------------------------------------
-- CREATE TABLE "user" -----------------------------------------
-- -------------------------------------------------------------
CREATE TABLE `user` (
	                    `user_id` Int UNSIGNED AUTO_INCREMENT NOT NULL,
	                    `username` VarChar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
	                    `password` VarChar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
	                    `email` VarChar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
	                    `enabled` TinyInt(1) NOT NULL DEFAULT 1,
	                    `last_update` Timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	                    PRIMARY KEY (`user_id`),
	                    UNIQUE KEY `uk_user_username` (`username`),
	                    UNIQUE KEY `uk_user_email` (`email`)
)
	CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci
ENGINE = InnoDB
AUTO_INCREMENT = 1;

-- -------------------------------------------------------------
-- CREATE TABLE "user_role" ------------------------------------
-- -------------------------------------------------------------
CREATE TABLE `user_role` (
	                         `user_id` Int UNSIGNED NOT NULL,
	                         `role_id` SmallInt UNSIGNED NOT NULL,
	                         `last_update` Timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	                         PRIMARY KEY (`user_id`, `role_id`),
	                         CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
	                         CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`) ON DELETE CASCADE ON UPDATE CASCADE
)
	CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci
ENGINE = InnoDB;
