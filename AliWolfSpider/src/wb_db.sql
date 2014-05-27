create table wb_data (
	id bigint  not null comment '����' auto_increment,
	gmt_create datetime  not null comment '����ʱ��',
	gmt_modified datetime  not null comment '�޸�ʱ��',
	content text  comment '΢������',
	wb_create	datetime  comment '΢������ʱ��',
	username varchar(100) comment '��Ա��',
	primary key (id),
	key idx_id_content_wb_create (id,content,wb_create)
) DEFAULT CHARSET=utf8 comment='΢��ץȡ��';


create table wb_word (
	id bigint  not null comment '����' auto_increment,
	gmt_create datetime  not null comment '����ʱ��',
	gmt_modified datetime  not null comment '�޸�ʱ��',
	word varchar(50)  comment '�ִ�',
	wb_create	datetime  comment '΢������ʱ��',
	count bigint comment '��Ƶ',
	primary key (id),
	key idx_id_content_wb_create (id,word,wb_create)
) DEFAULT CHARSET=utf8 comment='�ִʱ�';


create table wb_top_hour (
	id bigint  not null comment '����' auto_increment,
	gmt_create datetime  not null comment '����ʱ��',
	gmt_modified datetime  not null comment '�޸�ʱ��',
	word varchar(50)  comment '�ִ�',
	count bigint comment '��Ƶ',
	primary key (id),
	key idx_id_gmt_create (id,word,gmt_create)
) DEFAULT CHARSET=utf8 comment='һСʱ�ȶȱ�';


create table wb_top_day (
	id bigint  not null comment '����' auto_increment,
	gmt_create datetime  not null comment '����ʱ��',
	gmt_modified datetime  not null comment '�޸�ʱ��',
	word varchar(50)  comment '�ִ�',
	count bigint comment '��Ƶ',
	primary key (id),
	key idx_id_gmt_create (id,word,gmt_create)
) DEFAULT CHARSET=utf8 comment='һ���ȶȱ�';

create table wb_top_week (
	id bigint  not null comment '����' auto_increment,
	gmt_create datetime  not null comment '����ʱ��',
	gmt_modified datetime  not null comment '�޸�ʱ��',
	word varchar(50)  comment '�ִ�',
	count bigint comment '��Ƶ',
	primary key (id),
	key idx_id_gmt_create (id,word,gmt_create)
) DEFAULT CHARSET=utf8 comment='һ���ȶȱ�';