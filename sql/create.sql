
CREATE TABLE `user` (
`id` bigint(11) NOT NULL AUTO_INCREMENT,
`username` varchar(255) NOT NULL,
`nickname` varchar(255) DEFAULT NULL,
`password` varchar(255) NOT NULL,
`user_role` varchar(20) NOT NULL,
`email` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
`is_delete` tinyint(1) DEFAULT '0',
`remark` text CHARACTER SET utf8 COLLATE utf8_general_ci,
`enable` tinyint(1) DEFAULT '0',
`avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '#',
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '修改时间',
 PRIMARY KEY (`uid`)
 ) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb3;