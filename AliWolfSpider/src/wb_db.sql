create table wb_data (
	id bigint  not null comment '主键' auto_increment,
	gmt_create datetime  not null comment '创建时间',
	gmt_modified datetime  not null comment '修改时间',
	content text  comment '微博内容',
	wb_create	datetime  comment '微博发表时间',
	username varchar(100) comment '会员名',
	primary key (id),
	key idx_id_content_wb_create (id,content,wb_create)
) DEFAULT CHARSET=utf8 comment='微博抓取表';


create table wb_word (
	id bigint  not null comment '主键' auto_increment,
	gmt_create datetime  not null comment '创建时间',
	gmt_modified datetime  not null comment '修改时间',
	word varchar(50)  comment '分词',
	wb_create	datetime  comment '微博发表时间',
	count bigint comment '词频',
	primary key (id),
	key idx_id_content_wb_create (id,word,wb_create)
) DEFAULT CHARSET=utf8 comment='分词表';


create table wb_top_hour (
	id bigint  not null comment '主键' auto_increment,
	gmt_create datetime  not null comment '创建时间',
	gmt_modified datetime  not null comment '修改时间',
	word varchar(50)  comment '分词',
	count bigint comment '词频',
	primary key (id),
	key idx_id_gmt_create (id,word,gmt_create)
) DEFAULT CHARSET=utf8 comment='一小时热度表';


create table wb_top_day (
	id bigint  not null comment '主键' auto_increment,
	gmt_create datetime  not null comment '创建时间',
	gmt_modified datetime  not null comment '修改时间',
	word varchar(50)  comment '分词',
	count bigint comment '词频',
	primary key (id),
	key idx_id_gmt_create (id,word,gmt_create)
) DEFAULT CHARSET=utf8 comment='一天热度表';

create table wb_top_week (
	id bigint  not null comment '主键' auto_increment,
	gmt_create datetime  not null comment '创建时间',
	gmt_modified datetime  not null comment '修改时间',
	word varchar(50)  comment '分词',
	count bigint comment '词频',
	primary key (id),
	key idx_id_gmt_create (id,word,gmt_create)
) DEFAULT CHARSET=utf8 comment='一周热度表';