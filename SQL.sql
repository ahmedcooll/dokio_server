alter table sprav_status_dock add column is_default boolean;

**********************************************************************************************************
**********************************************************************************************************
**********************************************************************************************************

alter table customers_orders drop column fio;
alter table customers_orders drop column is_archive;
alter table customers_orders add column is_deleted boolean;
--ALTER TABLE customers_orders DROP COLUMN zip_code; уже сделано
--ALTER TABLE customers_orders ADD COLUMN zip_code varchar(40); уже сделано
--update customers_orders set zip_code = ''; уже сделано
alter table sprav_type_prices add column is_default boolean;

**********************************************************************************************************
**********************************************************************************************************
**********************************************************************************************************

alter table customers_orders_product add column department_id bigint not null;
alter table customers_orders_product add constraint department_id_fkey foreign key (department_id) references departments (id);
alter table customers_orders_product add column shipped_count numeric(15,3);

alter table departments add column is_deleted boolean;

update departments set is_deleted=is_archive;

alter table customers_orders_product add column product_price_of_type_price numeric(12,2);
alter table customers_orders_product add column reserve boolean;
alter table customers_orders_product drop column additional;
alter table customers_orders_product drop column reserved;
alter table customers_orders_product drop column shipped_count;
alter table customers_orders_product drop constraint customers_orders_product_uq;
ALTER TABLE customers_orders_product ADD CONSTRAINT customers_orders_product_uq UNIQUE (customers_orders_id, product_id, department_id) ;

alter table customers_orders_product add column master_id bigint not null;
alter table customers_orders_product add column company_id bigint not null;
ALTER TABLE customers_orders alter COLUMN master_id TYPE bigint USING master_id::bigint;
ALTER TABLE customers_orders alter COLUMN creator_id TYPE bigint USING creator_id::bigint;
ALTER TABLE customers_orders alter COLUMN changer_id TYPE bigint USING changer_id::bigint;
ALTER TABLE customers_orders alter COLUMN company_id TYPE bigint USING company_id::bigint;
ALTER TABLE customers_orders alter COLUMN department_id TYPE bigint USING department_id::bigint;
ALTER TABLE customers_orders alter COLUMN cagent_id TYPE bigint USING cagent_id::bigint;

delete from shipment_product;
alter table shipment_product add column master_id bigint not null;
alter table shipment_product add column company_id bigint not null;
alter table shipment_product drop column additional;
ALTER TABLE shipment alter COLUMN master_id TYPE bigint USING master_id::bigint;
ALTER TABLE shipment alter COLUMN creator_id TYPE bigint USING creator_id::bigint;
ALTER TABLE shipment alter COLUMN changer_id TYPE bigint USING changer_id::bigint;
ALTER TABLE shipment alter COLUMN company_id TYPE bigint USING company_id::bigint;
ALTER TABLE shipment alter COLUMN department_id TYPE bigint USING department_id::bigint;
ALTER TABLE shipment alter COLUMN cagent_id TYPE bigint USING cagent_id::bigint;

alter table customers_orders_product add column id bigint not null;
CREATE SEQUENCE customers_orders_product_id_seq START 1;
alter table customers_orders_product alter column id set default nextval ('customers_orders_product_id_seq');
alter sequence customers_orders_product_id_seq owned by customers_orders_product.id;

alter table customers_orders_product alter price_type_id drop not null;

create table settings_customers_orders (
    id                  bigserial primary key not null,
    master_id           bigint not null, 
    company_id          bigint not null,  
    user_id             bigint not null,
    pricing_type        varchar(16), 
    price_type_id       bigint,
    change_price        numeric(12,2),
    plus_minus          varchar(8),
    change_price_type   varchar(8),
    hide_tenths         boolean,
    save_settings       boolean,
    foreign key (price_type_id) references sprav_type_prices (id),
    foreign key (master_id) references users(id),
    foreign key (user_id) references users(id),
    foreign key (company_id) references companies(id)
);
ALTER TABLE settings_customers_orders ADD CONSTRAINT settings_customers_orders_user_uq UNIQUE (user_id);

alter table settings_customers_orders add column department_id bigint;
alter table settings_customers_orders add constraint department_id_fkey foreign key (department_id) references departments (id);
alter table settings_customers_orders add column customer_id bigint;
alter table settings_customers_orders add constraint customer_id_fkey foreign key (customer_id) references cagents (id);

alter table settings_customers_orders add column priority_type_price_side varchar(8);
alter table settings_customers_orders add column name varchar(120);

--удаление старых таблиц
drop table sell_positions;
drop table kassa_operations;
drop table sessions;
drop table trading_equipment;
drop table sprav_sys_cheque_types;
drop table sprav_sys_kassa_operations;
drop table sprav_sys_trading_equipment;

create table sprav_sys_cheque_types(
    id              int not null,
    name            varchar(100),
    name_api_atol   varchar(32)
);

insert into sprav_sys_cheque_types (id, name, name_api_atol) values
(1,'Чек прихода','sell'),
(2,'Чек возврата прихода','sellReturn'),
(4,'Чек расхода','buy'),
(5,'Чек возврата расхода','buyReturn'),
(7,'Чек коррекции прихода','sellCorrection'),
(8,'Чек коррекции возврата прихода','sellReturnCorrection'),
(9,'Чек коррекции расхода','buyCorrection'),
(10,'Чек коррекции возврата расхода','buyReturnCorrection');

create table sprav_sys_taxation_types(
    id              int not null,
    name            varchar(300) not null,
    name_api_atol   varchar(32) not null,
    is_active       boolean not null
);

insert into sprav_sys_taxation_types (id, name, name_api_atol, is_active) values
(1,'Общая','osn',true),
(2,'Упрощенная доход','usnIncome',true),
(3,'Упрощенная доход минус расход','usnIncomeOutcome',true),
(4,'Единый налог на вменённый доход','envd',false),
(5,'Единый сельскохозяйственный налог','esn',true),
(6,'Патентная система налогообложения','patent',true);

create table sprav_sys_payment_methods(
    id              int not null,
    name            varchar(300) not null,
    id_api_atol     int not null,
    name_api_atol   varchar(32) not null
);

insert into sprav_sys_payment_methods (id, name, id_api_atol, name_api_atol) values
(1,'Наличными',0,'cash'),
(2,'Безналичными',1,'electronically'),
(3,'Предварительная оплата (аванс)',2,'prepaid'),
(4,'Последующая оплата (кредит)',3,'credit'),
(5,'Иная форма оплаты',4,'other');

alter table sprav_sys_nds add column name_api_atol varchar(30);
alter table sprav_sys_nds add column is_active boolean;
update sprav_sys_nds set is_active=true;

update sprav_sys_nds set name_api_atol='none' where id=1;
update sprav_sys_nds set name_api_atol='vat0' where id=4;
update sprav_sys_nds set name_api_atol='vat10' where id=3;
update sprav_sys_nds set name_api_atol='vat20' where id=2;

alter table sprav_sys_nds add column calculated boolean;
update sprav_sys_nds set calculated=false;

insert into sprav_sys_nds (name, name_api_atol, is_active, calculated) values
('10/110','vat110',true,true),
('20/120','vat120',true,true);
update sprav_sys_nds set description='' where calculated=true;

drop table if exists spravsysndsjson;

alter table sprav_sys_ppr add column id_api_atol int;
alter table sprav_sys_ppr add column name_api_atol varchar(100);

update sprav_sys_ppr set name='Товар', id_api_atol=1, name_api_atol='commodity' where id=1;
update sprav_sys_ppr set name='Подакцизный товар', id_api_atol=2, name_api_atol='excise' where id=2;
update sprav_sys_ppr set name='Работа', id_api_atol=3, name_api_atol='job' where id=3;
update sprav_sys_ppr set name='Услуга', id_api_atol=4, name_api_atol='service' where id=4;

insert into sprav_sys_ppr (name, abbreviation, description, id_api_atol, name_api_atol) values
('Ставка азартной игры','','',5,'gamblingBet'),
('Выигрыш азартной игры','','',6,'gamblingPrize'),
('Лотерейный билет','','',7,'lottery'),
('Выигрыш лотереи','','',8,'lotteryPrize'),
('Предост. рез-тов интелл. деятельности','','',9,'intellectualActivity'),
('Платёж','','',10,'payment'),
('Агентское вознаграждение','','',11,'agentCommission'),
('Выплата','','',12,'pay'),
('Иной предмет расчета','','',13,'another'),
('Имущественное право','','',14,'proprietaryLaw'),
('Внереализационный доход','','',15,'nonOperatingIncome'),
('Иные платежи и взносы','','',16,'otherContributions'),
('Торговый сбор','','',17,'merchantTax'),
('Курортный сбор','','',18,'resortFee'),
('Залог','','',19,'deposit'),
('Расход','','',20,'consumption'),
('Взносы на ОПС ИП','','',21,'soleProprietorCPIContributions'),
('Взносы на ОПС','','',22,'cpiContributions'),
('Взносы на ОМС ИП','','',23,'soleProprietorCMIContributions'),
('Взносы на ОМС','','',24,'cmiContributions'),
('Взносы на ОСС','','',25,'csiContributions'),
('Платеж казино','','',26,'casinoPayment');

alter table users add column vatin varchar(30);
------------------------------------------------------------------
ALTER TABLE sprav_sys_taxation_types ADD CONSTRAINT sprav_sys_taxation_types_id_uq UNIQUE (id);
create table kassa(
    id                bigserial primary key not null,
    master_id         bigint not null, 
    creator_id        bigint not null, 
    changer_id        bigint, 
    company_id        bigint not null,
    department_id     bigint not null,
    date_time_created timestamp with time zone not null,
    date_time_changed timestamp with time zone,
    name              varchar(60) not null,
    server_type       varchar(20) not null,
    sno1_id           int not null not null,
    billing_address   varchar(500) not null,
    device_server_uid varchar(20) not null,
    additional        varchar(1000),
    server_address    varchar(300) not null,
    allow_to_use      boolean not null,
    is_deleted        boolean,

    foreign key (master_id) references users(id),
    foreign key (creator_id) references users(id),
    foreign key (changer_id) references users(id),
    foreign key (company_id) references companies(id),
    foreign key (department_id) references departments(id),
    foreign key (sno1_id) references sprav_sys_taxation_types(id)
);

create table kassa_user_settings(
    user_id                bigint primary key not null,
    master_id              bigint not null, 
    company_id             bigint not null,
    selected_kassa_id	   bigint not null,
    --кассир: 'current'-текущая учетная запись, 'another'-другая учетная запись, 'custom' произвольные ФИО
    cashier_value_id       varchar(8),
    customCashierFio       varchar(30),
    customCashierVatin     varchar(12),
    --адрес места расчётов. 'settings' - как в настройках кассы, 'customer' - брать из адреса заказчика, 'custom' произвольный адрес
    billing_address        varchar(8),
    custom_billing_address varchar(500),
    foreign key (selected_kassa_id) references kassa(id),
    foreign key (user_id) references users(id),
    foreign key (master_id) references users(id),
    foreign key (company_id) references companies(id)
);
ALTER TABLE sprav_sys_taxation_types ADD column short_name varchar(30);
update sprav_sys_taxation_types set short_name='ОСН' where id=1;
update sprav_sys_taxation_types set short_name='УСН доход' where id=2;
update sprav_sys_taxation_types set short_name='УСН доход-расход' where id=3;
update sprav_sys_taxation_types set short_name='ЕНВД' where id=4;
update sprav_sys_taxation_types set short_name='ЕСХН' where id=5;
update sprav_sys_taxation_types set short_name='Патент' where id=6;

ALTER TABLE sprav_sys_ppr ADD column is_material boolean;

update sprav_sys_ppr set is_material=true where id in(1,2,7,13);
update sprav_sys_ppr set is_material=false where id not in(1,2,7,13);


ALTER TABLE customers_orders_product ADD column reserved_current numeric(15,3);

alter table customers_orders_product drop column reserve;


alter table settings_customers_orders add column autocreate_on_start boolean;
alter table settings_customers_orders add column autocreate_on_cheque boolean;

alter table settings_customers_orders add column status_id_on_autocreate_on_cheque bigint;
alter table settings_customers_orders add constraint status_id_on_autocreate_on_cheque_fkey foreign key (status_id_on_autocreate_on_cheque) references sprav_status_dock (id);

удалить вручную "product_prices_uq" в product_prices

ALTER TABLE product_prices ADD CONSTRAINT product_prices_uq UNIQUE (product_id, price_type_id) ;

insert into documents (name,page_name,show) values ('Кассы онлайн','kassa',1);

insert into permissions (name,description,document_name,document_id) values
('Боковая панель - отображать в списке документов','Показывать документ в списке документов на боковой панели','Кассы онлайн',24),
('Создание документов по всем предприятиям','Возможность создавать новые документы "Кассы онлайн" по всем предприятиям','Кассы онлайн',24),
('Создание документов своего предприятия','Возможность создавать новые документы "Кассы онлайн" своего предприятия','Кассы онлайн',24),
('Создание документов своих отделений','Возможность создавать новые документы "Кассы онлайн" по своим отделениям','Кассы онлайн',24),
('Удаление документов по всем предприятиям','Возможность удалить документ "Кассы онлайн" в архив по всем предприятиям','Кассы онлайн',24),
('Удаление документов своего предприятия','Возможность удалить документ "Кассы онлайн" своего предприятия в архив','Кассы онлайн',24),
('Удаление документов своих отделений','Возможность удалить документ "Кассы онлайн" одного из своих отделений','Кассы онлайн',24),
('Просмотр документов по всем предприятиям','Прсмотр информации в документах "Кассы онлайн" по всем предприятиям','Кассы онлайн',24),
('Просмотр документов своего предприятия','Прсмотр информации в документах "Кассы онлайн" своего предприятия','Кассы онлайн',24),
('Просмотр документов своих отделений','Прсмотр информации в документах "Кассы онлайн" по своим отделениям','Кассы онлайн',24),
('Редактирование документов по всем предприятиям','Редактирование документов "Кассы онлайн" по всем предприятиям','Кассы онлайн',24),
('Редактирование документов своего предприятия','Редактирование документов "Кассы онлайн" своего предприятия','Кассы онлайн',24),
('Редактирование документов своих отделений','Редактирование документов "Кассы онлайн" по своим отделениям','Кассы онлайн',24);

**********************************************************************************************************
**********************************************************************************************************
**********************************************************************************************************
create table shifts(
 id bigserial primary key not null,
 master_id  bigint not null, 
 creator_id bigint not null,
 closer_id bigint,
 date_time_created timestamp with time zone not null,
 date_time_closed timestamp with time zone,
 company_id bigint not null,  
 department_id bigint not null,
 kassa_id bigint not null,
 shift_number int not null,
 
 foreign key (master_id) references users(id),
 foreign key (creator_id) references users(id),
 foreign key (closer_id) references users(id),
 foreign key (company_id) references companies(id),
 foreign key (department_id) references departments(id)
);

create table retail_sales(
 id bigserial primary key not null,
 master_id  bigint not null, 
 creator_id bigint not null,
 changer_id bigint,
 date_time_created timestamp with time zone not null,
 date_time_changed timestamp with time zone,
 company_id bigint not null,  
 department_id bigint not null,
 customers_orders_id bigint,
 shift_id bigint,
 cagent_id bigint not null,
 status_id bigint,
 doc_number int not null,
 name varchar(120),
 description varchar(2048),
 nds boolean,
 nds_included boolean,
 is_deleted boolean,
 
 foreign key (master_id) references users(id),
 foreign key (creator_id) references users(id),
 foreign key (changer_id) references users(id),
 foreign key (shift_id) references shifts(id),
 foreign key (customers_orders_id) references customers_orders(id),
 foreign key (company_id) references companies(id),
 foreign key (department_id) references departments(id),
 foreign key (cagent_id) references cagents(id),
 foreign key (status_id) references sprav_status_dock (id) ON DELETE SET NULL
);

insert into documents (name,page_name,show) values ('Розничные продажи','retailsales',1);

insert into permissions (name,description,document_name,document_id) values
('Боковая панель - отображать в списке документов','Показывать документ в списке документов на боковой панели','Розничные продажи',25),
('Создание документов по всем предприятиям','Возможность создавать новые документы "Розничные продажи" по всем предприятиям','Розничные продажи',25),
('Создание документов своего предприятия','Возможность создавать новые документы "Розничные продажи" своего предприятия','Розничные продажи',25),
('Создание документов своих отделений','Возможность создавать новые документы "Розничные продажи" по своим отделениям','Розничные продажи',25),
('Удаление документов по всем предприятиям','Возможность удалить документ "Розничные продажи" в архив по всем предприятиям','Розничные продажи',25),
('Удаление документов своего предприятия','Возможность удалить документ "Розничные продажи" своего предприятия в архив','Розничные продажи',25),
('Удаление документов своих отделений','Возможность удалить документ "Розничные продажи" одного из своих отделений','Розничные продажи',25),
('Удаление документов созданных собой','Возможность удалить документ "Розничные продажи", созданных собой','Розничные продажи',25),
('Просмотр документов по всем предприятиям','Прсмотр информации в документах "Розничные продажи" по всем предприятиям','Розничные продажи',25),
('Просмотр документов своего предприятия','Прсмотр информации в документах "Розничные продажи" своего предприятия','Розничные продажи',25),
('Просмотр документов своих отделений','Прсмотр информации в документах "Розничные продажи" по своим отделениям','Розничные продажи',25),
('Просмотр документов созданных собой','Прсмотр информации в документах "Розничные продажи", созданных собой','Розничные продажи',25),
('Редактирование документов по всем предприятиям','Редактирование документов "Розничные продажи" по всем предприятиям','Розничные продажи',25),
('Редактирование документов своего предприятия','Редактирование документов "Розничные продажи" своего предприятия','Розничные продажи',25),
('Редактирование документов своих отделений','Редактирование документов "Розничные продажи" по своим отделениям','Розничные продажи',25),
('Редактирование документов созданных собой','Редактирование документов "Розничные продажи", созданных собой','Розничные продажи',25);

create table retail_sales_product (
 id bigserial primary key not null,
 master_id bigint not null,
 company_id bigint not null,
 product_id bigint not null,
 retail_sales_id bigint not null,
 product_count numeric(15,3) not null,
 product_price numeric(12,2),
 product_sumprice numeric(15,2),
 edizm_id int not null,
 price_type_id bigint,
 nds_id bigint not null,
 department_id bigint not null,
 product_price_of_type_price numeric(12,2),

 foreign key (retail_sales_id) references retail_sales (id),
 foreign key (edizm_id) references sprav_sys_edizm (id),
 foreign key (nds_id) references sprav_sys_nds (id),
 foreign key (price_type_id) references sprav_type_prices (id),
 foreign key (product_id ) references products (id),
 foreign key (department_id ) references departments (id)
);

ALTER TABLE retail_sales_product ADD CONSTRAINT retail_sales_product_uq UNIQUE (product_id, retail_sales_id, department_id);

create table settings_retail_sales (
    id                          bigserial primary key not null,
    master_id                   bigint not null, 
    company_id                  bigint not null,  
    user_id                     bigint not null,
    customer_id                 bigint,
    department_id               bigint,
    name                        varchar(120),
    priority_type_price_side    varchar(8),
    pricing_type                varchar(16), 
    price_type_id               bigint,
    change_price                numeric(12,2),
    plus_minus                  varchar(8),
    change_price_type           varchar(8),
    hide_tenths                 boolean,
    save_settings               boolean,
    autocreate_on_cheque        boolean,
    status_id_on_autocreate_on_cheque bigint,
    foreign key (price_type_id) references sprav_type_prices (id),
    foreign key (master_id) references users(id),
    foreign key (user_id) references users(id),
    foreign key (company_id) references companies(id)
);
alter table settings_retail_sales add constraint settings_retail_sales_user_uq UNIQUE (user_id);
alter table settings_retail_sales add constraint department_id_fkey foreign key (department_id) references departments (id);
alter table settings_retail_sales add constraint customer_id_fkey foreign key (customer_id) references cagents (id);
alter table settings_retail_sales add constraint status_id_on_autocreate_on_cheque_fkey foreign key (status_id_on_autocreate_on_cheque) references sprav_status_dock (id);

---------------  после паузы в программировании  --------------------------------

update documents set name='Розничная продажа' where id=25;

create table receipts(
 id bigserial primary key not null,
 master_id bigint not null,
 creator_id bigint not null,
 company_id bigint not null,
 department_id bigint not null,
 kassa_id bigint not null, -- id кассового аппарата
 shift_id bigint not null, --id смены 
 document_id int not null, -- id документа, в котором был отбит чек (например, розничные продажи - 25)
 retail_sales_id bigint, -- если чек из розничных продаж - ставится id розничной продажи
 date_time_created timestamp with time zone not null, --дата и время создания чека
 operation_id varchar(64), -- sell, buyCorrection, sellReturnCorrection ...
 sno_id int not null, -- id системы налогообложения кассы 
 billing_address varchar(256), -- место расчета
 payment_type varchar(16), -- тип оплаты (нал, бнал, смешанная) cash | electronically | mixed
 cash numeric(15,2), -- сколько оплачено налом
 electronically numeric(15,2), -- склько оплачено безналом
 foreign key (master_id) references users(id),
 foreign key (creator_id) references users(id),
 foreign key (company_id) references companies(id),
 foreign key (department_id) references departments(id),
 foreign key (kassa_id) references kassa(id),
 foreign key (shift_id) references shifts(id),
 foreign key (document_id) references documents(id),
 foreign key (retail_sales_id) references retail_sales(id),
 foreign key (sno_id) references sprav_sys_taxation_types(id)
)

alter table retail_sales add column receipt_id bigint; --id чека, отбитого из розничной продажи
alter table retail_sales add constraint receipt_id_fkey foreign key (receipt_id) references receipts (id);

alter table kassa add column zn_kkt varchar(64); --заводской номер ккт
alter table kassa add constraint znkkt_company_uq UNIQUE (company_id, zn_kkt); -- заводской номер кассы в пределах предприятия должен быть уникальный. Почему в пределах а не вообще? Потому что master (владелец предприятий) может перерегистрировать кассу на другое свое предприятие. Так же, в облачной версии Докио, владелец кассы может снять ее с регистрации и продать другому пользователю Докио.

alter table shifts add column zn_kkt varchar(64) not null; --заводской номер ккт
alter table shifts add column shift_status_id varchar(8) not null; --статус смены: opened closed expired
alter table shifts add column shift_expired_at varchar(32) not null; -- время истечения (экспирации) смены, генерируется ККМ в виде строки
alter table shifts add column fn_serial varchar(32) not null; --серийный номер ФН
alter table shifts add constraint kassaid_shiftnumber_fnserial_uq UNIQUE (kassa_id, shift_number, fn_serial); --по каждой кассе должна быть только одна открытая смена. Номер смены сбрасывается при смене ФН, и он не может обеспечить уникальность смены ККМ, поэтому для уникальности смены также берется серийный номер ФН

CREATE SEQUENCE developer_shiftnum START 1;

create table settings_dashboard (
    id                  bigserial primary key not null,
    master_id           bigint not null, 
    user_id             bigint not null,
    company_id          bigint not null,  
    foreign key (master_id) references users(id),
    foreign key (user_id) references users(id),
    foreign key (company_id) references companies(id)
);

alter table settings_dashboard add constraint settings_dashboard_user_uq UNIQUE (user_id);

insert into documents (name,page_name,show) values ('Стартовая страница','retailsales',1);

insert into permissions (name,description,document_name,document_id) values
('Отображать','Показывать стартовую страницу','Стартовая страница',26),
('Отчёт "Объёмы" - просмотр по всем предприятиям','Возможность построения отчётов по объёмам продаж, закупок и др. по всем предприятиям','Стартовая страница',26);
insert into permissions (name,description,document_name,document_id) values
('Отчёт "Объёмы" - просмотр по своему предприятию','Возможность построения отчётов по объёмам продаж, закупок и др. по всем отделениям своего предпрития','Стартовая страница',26),
('Отчёт "Объёмы" - просмотр по своим отделениям','Возможность построения отчётов по объёмам продаж, закупок и др. по своим отделениям своего предпрития','Стартовая страница',26);


delete from receipts;
delete from shifts;
delete from retail_sales_product;
delete from retail_sales;

ALTER SEQUENCE retail_sales_id_seq RESTART WITH 1;
ALTER SEQUENCE retail_sales_product_id_seq RESTART WITH 1;

--на боевом сервере:
insert into retail_sales(
id,
master_id,
creator_id,
company_id,
department_id,
date_time_created,
cagent_id,
name,
description,
doc_number)
select
id,
master_id,
creator_id,
company_id,
department_id,
date_time_created,
cagent_id,
'Восстановленная',
'Продажа восстановлена по документу Заказ покупателя 21.07.2021',
doc_number
from customers_orders where coalesce(is_deleted,false)!=true
order by date_time_created;

--на боевом сервере:
insert into retail_sales_product(
id,
master_id,
company_id,
department_id,
retail_sales_id,
product_id,
product_count,
product_price,
product_sumprice,
edizm_id,
nds_id,
product_price_of_type_price)
select
id,
master_id,
company_id,
department_id,
customers_orders_id,
product_id,
product_count,
product_price,
product_sumprice,
edizm_id,
nds_id,
product_price_of_type_price
from customers_orders_product 
where customers_orders_id in (select id from customers_orders where coalesce(is_deleted,false)!=true);

ALTER SEQUENCE retail_sales_id_seq RESTART WITH 6000;
ALTER SEQUENCE retail_sales_product_id_seq RESTART WITH 9000;

insert into retail_sales(
master_id,
creator_id,
company_id,
department_id,
date_time_created,
cagent_id,
name,
description,
doc_number)
select
master_id,
creator_id,
company_id,
department_id,
date_time_created,
(select id from cagents where name='Обезличенный покупатель' and company_id=1),
'Восстановленная',
'Продажа восстановлена по истории документа "Итоги смен", товары не совпадают, т.к. в Итогах смен товары не прописывались. На один Итог смены создана одна Розничная продажа.',
0
from traderesults where company_id=1
and date_time_created<to_date('2021-02-01','YYYY-MM-DD');

insert into retail_sales_product(
master_id,
company_id,
department_id,
retail_sales_id,
product_id,
product_count,
product_price,
product_sumprice,
edizm_id,
nds_id)
select
master_id,
company_id,
department_id,
(select id from retail_sales where date_time_created=tr.date_time_created),
(select id from products where name='Предмет расчёта без наименования'),
1,
(incoming_cash_checkout+incoming_cashless_checkout+incoming_cash2+incoming_cashless2)/100,
(incoming_cash_checkout+incoming_cashless_checkout+incoming_cash2+incoming_cashless2)/100,
12,
1
from traderesults tr where company_id=1
and date_time_created<to_date('2021-02-01','YYYY-MM-DD')
and date_time_created>to_date('2019-09-30','YYYY-MM-DD')
order by tr.id;


ALTER SEQUENCE retail_sales_id_seq RESTART WITH 7000;
ALTER SEQUENCE retail_sales_product_id_seq RESTART WITH 10000;



***********************************************************************************************************************************************
***********************************************************************************************************************************************
***********************************************************************************************************************************************
--alter table kassa add column zn_kkt varchar(64); --заводской номер ккт - СДЕЛАТЬ NOT NULL !!!

create table inventory(
 id bigserial primary key not null,
 master_id  bigint not null, 
 company_id bigint not null,  
 department_id bigint not null,
 creator_id bigint not null,
 changer_id bigint,
 date_time_created timestamp with time zone not null,
 date_time_changed timestamp with time zone,
 status_id bigint,
 doc_number int not null,
 name varchar(120),
 description varchar(2048),
 is_deleted boolean,
 foreign key (master_id) references users(id),
 foreign key (creator_id) references users(id),
 foreign key (changer_id) references users(id),
 foreign key (company_id) references companies(id),
 foreign key (department_id) references departments(id),
 foreign key (status_id) references sprav_status_dock (id) ON DELETE SET NULL
);

insert into documents (name,page_name,show) values ('Инвентаризация','inventory',1);

insert into permissions (name,description,document_name,document_id) values
('Боковая панель - отображать в списке документов','Показывать документ в списке документов на боковой панели','Инвентаризация',27),
('Создание документов по всем предприятиям','Возможность создавать новые документы "Инвентаризация" по всем предприятиям','Инвентаризация',27),
('Создание документов своего предприятия','Возможность создавать новые документы "Инвентаризация" своего предприятия','Инвентаризация',27),
('Создание документов своих отделений','Возможность создавать новые документы "Инвентаризация" по своим отделениям','Инвентаризация',27),
('Удаление документов по всем предприятиям','Возможность удалить документ "Инвентаризация" в архив по всем предприятиям','Инвентаризация',27),
('Удаление документов своего предприятия','Возможность удалить документ "Инвентаризация" своего предприятия в архив','Инвентаризация',27),
('Удаление документов своих отделений','Возможность удалить документ "Инвентаризация" одного из своих отделений','Инвентаризация',27),
('Удаление документов созданных собой','Возможность удаления документов "Инвентаризация", созданных собой','Инвентаризация',27),
('Просмотр документов по всем предприятиям','Прсмотр информации в документах "Инвентаризация" по всем предприятиям','Инвентаризация',27),
('Просмотр документов своего предприятия','Прсмотр информации в документах "Инвентаризация" своего предприятия','Инвентаризация',27),
('Просмотр документов своих отделений','Прсмотр информации в документах "Инвентаризация" по своим отделениям','Инвентаризация',27),
('Просмотр документов созданных собой','Прсмотр информации в документах "Инвентаризация", созданных собой','Инвентаризация',27),
('Редактирование документов по всем предприятиям','Редактирование документов "Инвентаризация" по всем предприятиям','Инвентаризация',27),
('Редактирование документов своего предприятия','Редактирование документов "Инвентаризация" своего предприятия','Инвентаризация',27),
('Редактирование документов своих отделений','Редактирование документов "Инвентаризация" по своим отделениям','Инвентаризация',27),
('Редактирование документов созданных собой','Редактирование документов "Инвентаризация", созданных собой','Инвентаризация',27);

create table inventory_product (
 id bigserial primary key not null,
 master_id bigint not null,
 company_id bigint not null,
 product_id bigint not null,
 inventory_id bigint not null,
 estimated_balance numeric(15,3) not null,
 actual_balance numeric(15,3) not null,
 product_price numeric(12,2),
 foreign key (inventory_id) references inventory (id),
 foreign key (master_id ) references users (id),
 foreign key (product_id ) references products (id),
 foreign key (company_id ) references companies (id)
);

ALTER TABLE inventory_product ADD CONSTRAINT inventory_product_uq UNIQUE (product_id, inventory_id);

create table settings_inventory (
    id                          bigserial primary key not null,
    master_id                   bigint not null, 
    company_id                  bigint not null,  
    user_id                     bigint not null,
    department_id               bigint,
    name                        varchar(120),
    pricing_type                varchar(16), 
    price_type_id               bigint,
    change_price                numeric(12,2),
    plus_minus                  varchar(8),
    change_price_type           varchar(8),
    hide_tenths                 boolean,
    status_on_finish_id         bigint,

    foreign key (price_type_id) references sprav_type_prices (id),
    foreign key (master_id) references users(id),
    foreign key (user_id) references users(id),
    foreign key (status_on_finish_id) references sprav_status_dock (id),
    foreign key (department_id) references departments (id),
    foreign key (company_id) references companies(id)
);
alter table settings_inventory add constraint settings_inventory_user_uq UNIQUE (user_id);

alter table inventory add column is_completed boolean;

alter table settings_retail_sales alter column pricing_type TYPE varchar (32) USING pricing_type::varchar (32);
alter table settings_customers_orders alter column pricing_type TYPE varchar (32) USING pricing_type::varchar (32);
alter table settings_inventory alter column pricing_type TYPE varchar (32) USING pricing_type::varchar (32);

alter table settings_inventory add column default_actual_balance varchar(16);
alter table settings_inventory add column other_actual_balance numeric(15,3);
alter table settings_inventory add column auto_add boolean;

alter table writeoff add column inventory_id bigint;
alter table writeoff add constraint inventory_id_fkey foreign key (inventory_id) references inventory (id);

alter table posting add column inventory_id bigint;
alter table posting add constraint inventory_id_fkey foreign key (inventory_id) references inventory (id);

alter table products_history add constraint products_history_quantity_check CHECK (quantity >= 0);
alter table product_quantity add constraint products_quantity_check CHECK (quantity >= 0);

alter table writeoff_product alter column edizm_id drop not null;
alter table posting_product alter column edizm_id drop not null;

ALTER TABLE posting_product ADD CONSTRAINT posting_product_uq UNIQUE (product_id, posting_id);
ALTER TABLE acceptance_product ADD CONSTRAINT acceptance_product_uq UNIQUE (product_id, acceptance_id);
ALTER TABLE writeoff_product ADD CONSTRAINT writeoff_product_uq UNIQUE (product_id, writeoff_id);
***********************************************************************************************************************************************
***********************************************************************************************************************************************
***********************************************************************************************************************************************
create table inventory_files (
 inventory_id bigint not null,
 file_id bigint not null,
 foreign key (file_id) references files (id) ON DELETE CASCADE,
 foreign key (inventory_id ) references inventory (id) ON DELETE CASCADE
);

CREATE INDEX CONCURRENTLY sales_quantity_index ON sales_table (quantity);
***********************************************************************************************************************************************
***********************************************************************************************************************************************
***********************************************************************************************************************************************
create table return(
 id bigserial primary key not null,
 master_id  bigint not null, 
 company_id bigint not null,  
 department_id bigint not null,
 cagent_id bigint not null, 
 creator_id bigint not null,
 changer_id bigint,
 date_time_created timestamp with time zone not null,
 date_time_changed timestamp with time zone,
 status_id bigint,
 doc_number int not null,
 description varchar(2048),
 nds boolean,
 date_return timestamp with time zone not null,
 is_completed boolean,
 is_deleted boolean,
 foreign key (master_id) references users(id),
 foreign key (creator_id) references users(id),
 foreign key (changer_id) references users(id),
 foreign key (cagent_id) references cagents(id),
 foreign key (company_id) references companies(id),
 foreign key (department_id) references departments(id),
 foreign key (status_id) references sprav_status_dock (id) ON DELETE SET NULL
);

insert into documents (name,page_name,show) values ('Возврат покупателя','return',1);

insert into permissions (name,description,document_name,document_id) values
('Боковая панель - отображать в списке документов','Показывать документ в списке документов на боковой панели','Возврат покупателя',28),
('Создание документов по всем предприятиям','Возможность создавать новые документы "Возврат покупателя" по всем предприятиям','Возврат покупателя',28),
('Создание документов своего предприятия','Возможность создавать новые документы "Возврат покупателя" своего предприятия','Возврат покупателя',28),
('Создание документов своих отделений','Возможность создавать новые документы "Возврат покупателя" по своим отделениям','Возврат покупателя',28),
('Удаление документов по всем предприятиям','Возможность удалить документ "Возврат покупателя" в архив по всем предприятиям','Возврат покупателя',28),
('Удаление документов своего предприятия','Возможность удалить документ "Возврат покупателя" своего предприятия в архив','Возврат покупателя',28),
('Удаление документов своих отделений','Возможность удалить документ "Возврат покупателя" одного из своих отделений','Возврат покупателя',28),
('Удаление документов созданных собой','Возможность удаления документов "Возврат покупателя", созданных собой','Возврат покупателя',28),
('Просмотр документов по всем предприятиям','Прсмотр информации в документах "Возврат покупателя" по всем предприятиям','Возврат покупателя',28),
('Просмотр документов своего предприятия','Прсмотр информации в документах "Возврат покупателя" своего предприятия','Возврат покупателя',28),
('Просмотр документов своих отделений','Прсмотр информации в документах "Возврат покупателя" по своим отделениям','Возврат покупателя',28),
('Просмотр документов созданных собой','Прсмотр информации в документах "Возврат покупателя", созданных собой','Возврат покупателя',28),
('Редактирование документов по всем предприятиям','Редактирование документов "Возврат покупателя" по всем предприятиям','Возврат покупателя',28),
('Редактирование документов своего предприятия','Редактирование документов "Возврат покупателя" своего предприятия','Возврат покупателя',28),
('Редактирование документов своих отделений','Редактирование документов "Возврат покупателя" по своим отделениям','Возврат покупателя',28),
('Редактирование документов созданных собой','Редактирование документов "Возврат покупателя", созданных собой','Возврат покупателя',28);

create table return_product (
 id bigserial primary key not null,
 master_id bigint not null,
 company_id bigint not null,
 product_id bigint not null,
 return_id bigint not null,
 product_count numeric(15,3) not null,
 product_price numeric(12,2) not null,
 product_netcost numeric(12,2),
 nds_id int,
 product_sumprice numeric(15,2) not null, 
 product_sumnetcost numeric(15,2) , 
 foreign key (return_id) references return (id),
 foreign key (master_id ) references users (id),
 foreign key (product_id ) references products (id),
 foreign key (nds_id ) references sprav_sys_nds (id),
 foreign key (company_id ) references companies (id)
);

ALTER TABLE return_product ADD CONSTRAINT return_product_uq UNIQUE (product_id, return_id);

create table settings_return (
    id                          bigserial primary key not null,
    master_id                   bigint not null, 
    company_id                  bigint not null,  
    user_id                     bigint not null,
    department_id               bigint,
    status_on_finish_id         bigint,
    auto_add                    boolean,
    foreign key (master_id) references users(id),
    foreign key (user_id) references users(id),
    foreign key (status_on_finish_id) references sprav_status_dock (id),
    foreign key (department_id) references departments (id),
    foreign key (company_id) references companies(id)
);
alter table settings_return add constraint settings_return_user_uq UNIQUE (user_id);


create table return_files (
 return_id bigint not null,
 file_id bigint not null,
 foreign key (file_id) references files (id) ON DELETE CASCADE,
 foreign key (return_id ) references return (id) ON DELETE CASCADE
);

alter table writeoff add column return_id bigint;
alter table writeoff add constraint return_id_fkey foreign key (return_id) references return (id);

-- атрибут неделимости для товара
alter table products add column indivisible boolean;
update products set indivisible = true;
alter table products alter column indivisible set not null;

alter table return add column retail_sales_id bigint;
alter table return add constraint retail_sales_id_fkey foreign key (retail_sales_id) references retail_sales (id);

alter table settings_return add column show_kkm boolean;
alter table settings_retail_sales add column show_kkm boolean;
update settings_retail_sales set show_kkm=true;
alter table settings_retail_sales add column auto_add boolean;
alter table retail_sales add column uid varchar(36);

***********************************************************************************************************************************************
***********************************************************************************************************************************************
***********************************************************************************************************************************************

ALTER TABLE public.documents ALTER COLUMN id DROP DEFAULT;
DROP SEQUENCE documents_id_seq;
ALTER TABLE public.permissions ALTER COLUMN id DROP DEFAULT;
DROP SEQUENCE permissions_id_seq;

create table returnsup(
 id bigserial primary key not null,
 master_id  bigint not null, 
 company_id bigint not null,  
 department_id bigint not null,
 cagent_id bigint not null, 
 creator_id bigint not null,
 changer_id bigint,
 date_time_created timestamp with time zone not null,
 date_time_changed timestamp with time zone,
 status_id bigint,
 doc_number int not null,
 description varchar(2048),
 nds boolean,
 date_return timestamp with time zone not null,
 is_completed boolean,
 is_deleted boolean,
 foreign key (master_id) references users(id),
 foreign key (creator_id) references users(id),
 foreign key (changer_id) references users(id),
 foreign key (cagent_id) references cagents(id),
 foreign key (company_id) references companies(id),
 foreign key (department_id) references departments(id),
 foreign key (status_id) references sprav_status_dock (id) ON DELETE SET NULL
);

insert into documents (id,name,page_name,show) values (29,'Возврат поставщику','returnsup',1);

insert into permissions (id,name,description,document_name,document_id) values
(360,'Боковая панель - отображать в списке документов','Показывать документ в списке документов на боковой панели','Возврат поставщику',29),
(361,'Создание документов по всем предприятиям','Возможность создавать новые документы "Возврат поставщику" по всем предприятиям','Возврат поставщику',29),
(362,'Создание документов своего предприятия','Возможность создавать новые документы "Возврат поставщику" своего предприятия','Возврат поставщику',29),
(363,'Создание документов своих отделений','Возможность создавать новые документы "Возврат поставщику" по своим отделениям','Возврат поставщику',29),
(364,'Удаление документов по всем предприятиям','Возможность удалить документ "Возврат поставщику" в архив по всем предприятиям','Возврат поставщику',29),
(365,'Удаление документов своего предприятия','Возможность удалить документ "Возврат поставщику" своего предприятия в архив','Возврат поставщику',29),
(366,'Удаление документов своих отделений','Возможность удалить документ "Возврат поставщику" одного из своих отделений','Возврат поставщику',29),
(367,'Удаление документов созданных собой','Возможность удаления документов "Возврат поставщику", созданных собой','Возврат поставщику',29),
(368,'Просмотр документов по всем предприятиям','Прсмотр информации в документах "Возврат поставщику" по всем предприятиям','Возврат поставщику',29),
(369,'Просмотр документов своего предприятия','Прсмотр информации в документах "Возврат поставщику" своего предприятия','Возврат поставщику',29),
(370,'Просмотр документов своих отделений','Прсмотр информации в документах "Возврат поставщику" по своим отделениям','Возврат поставщику',29),
(371,'Просмотр документов созданных собой','Прсмотр информации в документах "Возврат поставщику", созданных собой','Возврат поставщику',29),
(372,'Редактирование документов по всем предприятиям','Редактирование документов "Возврат поставщику" по всем предприятиям','Возврат поставщику',29),
(373,'Редактирование документов своего предприятия','Редактирование документов "Возврат поставщику" своего предприятия','Возврат поставщику',29),
(374,'Редактирование документов своих отделений','Редактирование документов "Возврат поставщику" по своим отделениям','Возврат поставщику',29),
(375,'Редактирование документов созданных собой','Редактирование документов "Возврат поставщику", созданных собой','Возврат поставщику',29);

create table returnsup_product (
    id bigserial primary key not null,
    master_id bigint not null,
    company_id bigint not null,
    product_id bigint not null,
    returnsup_id bigint not null,
    product_count numeric(15,3) not null,
    product_price numeric(12,2) not null,
    nds_id int,
    product_sumprice numeric(15,2) not null, 
    foreign key (returnsup_id) references returnsup (id),
    foreign key (master_id ) references users (id),
    foreign key (product_id ) references products (id),
    foreign key (nds_id ) references sprav_sys_nds (id),
    foreign key (company_id ) references companies (id)
);

ALTER TABLE returnsup_product ADD CONSTRAINT returnsup_product_uq UNIQUE (product_id, returnsup_id);

create table settings_returnsup (
    id                          bigserial primary key not null,
    master_id                   bigint not null, 
    company_id                  bigint not null,  
    user_id                     bigint not null,
    department_id               bigint,
    status_on_finish_id         bigint,
    auto_add                    boolean,
    foreign key (master_id) references users(id),
    foreign key (user_id) references users(id),
    foreign key (status_on_finish_id) references sprav_status_dock (id),
    foreign key (department_id) references departments (id),
    foreign key (company_id) references companies(id)
);
alter table settings_returnsup add constraint settings_returnsup_user_uq UNIQUE (user_id);

create table returnsup_files (
    returnsup_id bigint not null,
    file_id bigint not null,
    foreign key (file_id) references files (id) ON DELETE CASCADE,
    foreign key (returnsup_id ) references returnsup (id) ON DELETE CASCADE
);

alter table returnsup add column acceptance_id bigint;
alter table returnsup add constraint acceptance_id_fkey foreign key (acceptance_id) references acceptance (id);

create table settings_acceptance (
    id                          bigserial primary key not null,
    master_id                   bigint not null, 
    company_id                  bigint not null,  
    user_id                     bigint not null,
    department_id               bigint,
    status_on_finish_id         bigint,
    auto_add                    boolean,
    foreign key (master_id) references users(id),
    foreign key (user_id) references users(id),
    foreign key (status_on_finish_id) references sprav_status_dock (id),
    foreign key (department_id) references departments (id),
    foreign key (company_id) references companies(id)
);
alter table settings_acceptance add constraint settings_acceptance_user_uq UNIQUE (user_id);

alter table writeoff add column status_id bigint;
alter table writeoff add constraint status_id_fkey foreign key (status_id) references sprav_status_dock (id);


alter table acceptance add column status_id bigint;
alter table acceptance add constraint status_id_fkey foreign key (status_id) references sprav_status_dock (id);

alter table posting add column status_id bigint;
alter table posting add constraint status_id_fkey foreign key (status_id) references sprav_status_dock (id);

ALTER TABLE acceptance RENAME COLUMN is_archive TO is_deleted;
ALTER TABLE posting RENAME COLUMN is_archive TO is_deleted;
ALTER TABLE writeoff RENAME COLUMN is_archive TO is_deleted;

create table settings_posting (
    id                          bigserial primary key not null,
    master_id                   bigint not null, 
    company_id                  bigint not null,  
    user_id                     bigint not null,
    department_id               bigint,
    status_on_finish_id         bigint,
    auto_add                    boolean,
    foreign key (master_id) references users(id),
    foreign key (user_id) references users(id),
    foreign key (status_on_finish_id) references sprav_status_dock (id),
    foreign key (department_id) references departments (id),
    foreign key (company_id) references companies(id)
);
alter table settings_posting add constraint settings_posting_user_uq UNIQUE (user_id);

create table settings_writeoff (
    id                          bigserial primary key not null,
    master_id                   bigint not null, 
    company_id                  bigint not null,  
    user_id                     bigint not null,
    department_id               bigint,
    status_on_finish_id         bigint,
    auto_add                    boolean,
    foreign key (master_id) references users(id),
    foreign key (user_id) references users(id),
    foreign key (status_on_finish_id) references sprav_status_dock (id),
    foreign key (department_id) references departments (id),
    foreign key (company_id) references companies(id)
);
alter table settings_writeoff add constraint settings_writeoff_user_uq UNIQUE (user_id);

alter table writeoff_product alter reason_id drop not null;

create table moving(
 id bigserial primary key not null,
 master_id  bigint not null, 
 company_id bigint not null,  
 department_from_id bigint not null,
 department_to_id bigint not null,
 creator_id bigint not null,
 changer_id bigint,
 date_time_created timestamp with time zone not null,
 date_time_changed timestamp with time zone,
 status_id bigint,
 doc_number int not null,
 description varchar(2048),
 customers_orders_id bigint,
 is_completed boolean,
 is_deleted boolean,
 overhead numeric(12,2),
 overhead_netcost_method int,
 foreign key (master_id) references users(id),
 foreign key (creator_id) references users(id),
 foreign key (changer_id) references users(id),
 foreign key (customers_orders_id) references customers_orders(id),
 foreign key (company_id) references companies(id),
 foreign key (department_from_id) references departments(id),
 foreign key (department_to_id) references departments(id),
 foreign key (status_id) references sprav_status_dock (id) ON DELETE SET NULL
);

insert into documents (id,name,page_name,show) values (30,'Перемещение','moving',1);

insert into permissions (id,name,description,document_name,document_id) values
(376,'Боковая панель - отображать в списке документов','Показывать документ в списке документов на боковой панели','Перемещение',30),
(377,'Создание документов по всем предприятиям','Возможность создавать новые документы "Перемещение" по всем предприятиям','Перемещение',30),
(378,'Создание документов своего предприятия','Возможность создавать новые документы "Перемещение" своего предприятия','Перемещение',30),
(379,'Создание документов своих отделений','Возможность создавать новые документы "Перемещение" по своим отделениям','Перемещение',30),
(380,'Удаление документов по всем предприятиям','Возможность удалить документ "Перемещение" в архив по всем предприятиям','Перемещение',30),
(381,'Удаление документов своего предприятия','Возможность удалить документ "Перемещение" своего предприятия в архив','Перемещение',30),
(382,'Удаление документов своих отделений','Возможность удалить документ "Перемещение" одного из своих отделений','Перемещение',30),
(383,'Удаление документов созданных собой','Возможность удаления документов "Перемещение", созданных собой','Перемещение',30),
(384,'Просмотр документов по всем предприятиям','Прсмотр информации в документах "Перемещение" по всем предприятиям','Перемещение',30),
(385,'Просмотр документов своего предприятия','Прсмотр информации в документах "Перемещение" своего предприятия','Перемещение',30),
(386,'Просмотр документов своих отделений','Прсмотр информации в документах "Перемещение" по своим отделениям','Перемещение',30),
(387,'Просмотр документов созданных собой','Прсмотр информации в документах "Перемещение", созданных собой','Перемещение',30),
(388,'Редактирование документов по всем предприятиям','Редактирование документов "Перемещение" по всем предприятиям','Перемещение',30),
(389,'Редактирование документов своего предприятия','Редактирование документов "Перемещение" своего предприятия','Перемещение',30),
(390,'Редактирование документов своих отделений','Редактирование документов "Перемещение" по своим отделениям','Перемещение',30),
(391,'Редактирование документов созданных собой','Редактирование документов "Перемещение", созданных собой','Перемещение',30),
(392,'Проведение документов по всем предприятиям','Проведение документов "Перемещение" по всем предприятиям','Перемещение',30),
(393,'Проведение документов своего предприятия','Проведение документов "Перемещение" своего предприятия','Перемещение',30),
(394,'Проведение документов своих отделений','Проведение документов "Перемещение" по своим отделениям','Перемещение',30);

create table moving_product (
    id bigserial primary key not null,
    master_id bigint not null,
    company_id bigint not null,
    product_id bigint not null,
    moving_id bigint not null,
    product_count numeric(15,3) not null,
    product_price numeric(12,2) not null,
    product_sumprice numeric(15,2) not null, 
    product_netcost numeric(12,2),
    foreign key (moving_id) references moving (id),
    foreign key (master_id ) references users (id),
    foreign key (product_id ) references products (id),
    foreign key (company_id ) references companies (id)
);

ALTER TABLE moving_product ADD CONSTRAINT moving_product_uq UNIQUE (product_id, moving_id);

create table settings_moving (
    id                          bigserial primary key not null,
    master_id                   bigint not null, 
    company_id                  bigint not null,  
    user_id                     bigint not null,
    department_from_id          bigint,
    department_to_id            bigint,
    status_on_finish_id         bigint,
    auto_add                    boolean,
    foreign key (master_id) references users(id),
    foreign key (user_id) references users(id),
    foreign key (status_on_finish_id) references sprav_status_dock (id),
    foreign key (department_from_id) references departments(id),
    foreign key (department_to_id) references departments(id),
    foreign key (company_id) references companies(id)
);
alter table settings_moving add constraint settings_moving_user_uq UNIQUE (user_id);

create table moving_files (
    moving_id bigint not null,
    file_id bigint not null,
    foreign key (file_id) references files (id) ON DELETE CASCADE,
    foreign key (moving_id ) references moving (id) ON DELETE CASCADE
);

alter table settings_moving add column pricing_type  varchar(32);
alter table settings_moving add column price_type_id       bigint;
alter table settings_moving add column change_price        numeric(12,2);
alter table settings_moving add column plus_minus          varchar(8);
alter table settings_moving add column change_price_type   varchar(8);
alter table settings_moving add column hide_tenths         boolean;
alter table settings_moving add constraint price_type_id_fkey foreign key (price_type_id) references sprav_type_prices (id);

alter table settings_posting add column pricing_type  varchar(32);
alter table settings_posting add column price_type_id       bigint;
alter table settings_posting add column change_price        numeric(12,2);
alter table settings_posting add column plus_minus          varchar(8);
alter table settings_posting add column change_price_type   varchar(8);
alter table settings_posting add column hide_tenths         boolean;
alter table settings_posting add constraint price_type_id_fkey foreign key (price_type_id) references sprav_type_prices (id);

alter table settings_writeoff add column pricing_type  varchar(32);
alter table settings_writeoff add column price_type_id       bigint;
alter table settings_writeoff add column change_price        numeric(12,2);
alter table settings_writeoff add column plus_minus          varchar(8);
alter table settings_writeoff add column change_price_type   varchar(8);
alter table settings_writeoff add column hide_tenths         boolean;
alter table settings_writeoff add constraint price_type_id_fkey foreign key (price_type_id) references sprav_type_prices (id);

alter table settings_returnsup add column pricing_type  varchar(32);
alter table settings_returnsup add column price_type_id       bigint;
alter table settings_returnsup add column change_price        numeric(12,2);
alter table settings_returnsup add column plus_minus          varchar(8);
alter table settings_returnsup add column change_price_type   varchar(8);
alter table settings_returnsup add column hide_tenths         boolean;
alter table settings_returnsup add constraint price_type_id_fkey foreign key (price_type_id) references sprav_type_prices (id);

alter table settings_acceptance add column auto_price         boolean;

insert into permissions (id,name,description,document_name,document_id) values
(395,'Проведение документов созданных собой','Проведение документов "Перемещение" созданных собой','Перемещение',30);

***********************************************************************************************************************************************
***********************************************************************************************************************************************
***********************************************************************************************************************************************

create table linked_docs_groups (
    id                          bigserial primary key not null,
    master_id                   bigint not null, 
    company_id                  bigint not null,  
    date_time_created timestamp with time zone not null,
    foreign key (master_id) references users(id),
    foreign key (company_id) references companies(id)
);

create table linked_docs (
    id                          bigserial primary key not null,
    master_id                   bigint not null, 
    company_id                  bigint not null,  
    group_id                    bigint not null,
    doc_id                      bigint not null,
    doc_uid                     varchar(36) not null,
    tablename                   varchar(40) not null,
    acceptance_id               bigint,
    customers_orders_id         bigint,
    return_id                   bigint,
    returnsup_id                bigint,
    shipment_id                 bigint,
    retail_sales_id             bigint,
    products_id                 bigint,
    inventory_id                bigint,
    writeoff_id                 bigint,
    posting_id                  bigint,
    moving_id                   bigint,

    foreign key (group_id) references linked_docs_groups(id),
    foreign key (master_id) references users(id),
    foreign key (company_id) references companies(id),
    foreign key (customers_orders_id) references customers_orders(id),
    foreign key (acceptance_id) references acceptance(id),
    foreign key (return_id) references return(id),
    foreign key (returnsup_id) references returnsup(id),
    foreign key (shipment_id) references shipment(id),
    foreign key (retail_sales_id) references retail_sales(id),
    foreign key (products_id) references products(id),
    foreign key (inventory_id) references inventory(id),
    foreign key (writeoff_id) references writeoff(id),
    foreign key (posting_id) references posting(id),
    foreign key (moving_id) references moving(id)
);

create table linked_docs_links(
    id                          bigserial primary key not null,
    master_id                   bigint not null, 
    company_id                  bigint not null,  
    group_id                    bigint not null,
    parent_uid                    varchar(36),
    child_uid                      varchar(36),
    foreign key (group_id) references linked_docs_groups(id),
    foreign key (master_id) references users(id),
    foreign key (company_id) references companies(id)
);
 
ALTER TABLE linked_docs_links ADD CONSTRAINT uuids_uq UNIQUE (parent_uid, child_uid);

alter table customers_orders add column uid varchar(36);
alter table acceptance add column uid varchar(36);
alter table return add column uid varchar(36);
alter table returnsup add column uid varchar(36);
alter table shipment add column uid varchar(36);
alter table products add column uid varchar(36);
alter table inventory add column uid varchar(36);
alter table writeoff add column uid varchar(36);
alter table posting add column uid varchar(36);
alter table moving add column uid varchar(36);

alter table customers_orders add column linked_docs_group_id bigint;
alter table acceptance add column linked_docs_group_id bigint;
alter table return add column linked_docs_group_id bigint;
alter table returnsup add column linked_docs_group_id bigint;
alter table shipment add column linked_docs_group_id bigint;
alter table retail_sales add column linked_docs_group_id bigint;
alter table products add column linked_docs_group_id bigint;
alter table inventory add column linked_docs_group_id bigint;
alter table writeoff add column linked_docs_group_id bigint;
alter table posting add column linked_docs_group_id bigint;
alter table moving add column linked_docs_group_id bigint;

alter table customers_orders add constraint linked_docs_group_id_fkey foreign key (linked_docs_group_id) references linked_docs_groups (id);
alter table acceptance add constraint linked_docs_group_id_fkey foreign key (linked_docs_group_id) references linked_docs_groups (id);
alter table return add constraint linked_docs_group_id_fkey foreign key (linked_docs_group_id) references linked_docs_groups (id);
alter table returnsup add constraint linked_docs_group_id_fkey foreign key (linked_docs_group_id) references linked_docs_groups (id);
alter table shipment add constraint linked_docs_group_id_fkey foreign key (linked_docs_group_id) references linked_docs_groups (id);
alter table retail_sales add constraint linked_docs_group_id_fkey foreign key (linked_docs_group_id) references linked_docs_groups (id);
alter table products add constraint linked_docs_group_id_fkey foreign key (linked_docs_group_id) references linked_docs_groups (id);
alter table inventory add constraint linked_docs_group_id_fkey foreign key (linked_docs_group_id) references linked_docs_groups (id);
alter table writeoff add constraint linked_docs_group_id_fkey foreign key (linked_docs_group_id) references linked_docs_groups (id);
alter table posting add constraint linked_docs_group_id_fkey foreign key (linked_docs_group_id) references linked_docs_groups (id);
alter table moving add constraint linked_docs_group_id_fkey foreign key (linked_docs_group_id) references linked_docs_groups (id);

ALTER TABLE linked_docs ADD CONSTRAINT linked_docs_uq UNIQUE (tablename, doc_id);

ALTER TABLE documents add column table_name varchar(40);
ALTER TABLE documents add column doc_name_ru varchar(40);
update documents set table_name=page_name;
update documents set doc_name_ru=name;
alter table documents alter column doc_name_ru set not null;
update documents set table_name =null where id=19;
update documents set doc_name_ru ='Заказ покупателя' where id=23;

alter table retail_sales add column is_completed boolean;

ALTER TABLE linked_docs ADD CONSTRAINT linked_docs_uid_uq UNIQUE (master_id, doc_uid);

alter table shipment drop column is_archive;
alter table shipment add column is_deleted boolean;
alter table shipment add column status_id bigint;
alter table shipment add constraint status_id_fkey foreign key (status_id) references sprav_status_dock (id);

insert into permissions (id,name,description,document_name,document_id) values
(396,'Проведение документов по всем предприятиям','Проведение документов "Отгрузка" по всем предприятиям','Отгрузка',21),
(397,'Проведение документов своего предприятия','Проведение документов "Отгрузка" своего предприятия','Отгрузка',21),
(398,'Проведение документов своих отделений','Проведение документов "Отгрузка" по своим отделениям','Отгрузка',21),
(399,'Проведение документов созданных собой','Проведение документов "Отгрузка" созданных собой','Отгрузка',21);


create table settings_shipment (
    id                          bigserial primary key not null,
    master_id                   bigint not null, 
    company_id                  bigint not null,  
    user_id                     bigint not null,
    customer_id                 bigint,
    department_id               bigint,
    name                        varchar(120),
    priority_type_price_side    varchar(8),
    pricing_type                varchar(32), 
    price_type_id               bigint,
    change_price                numeric(12,2),
    plus_minus                  varchar(8),
    change_price_type           varchar(8),
    hide_tenths                 boolean,
    save_settings               boolean,
    autocreate                  boolean,
    status_id_on_complete       bigint,
    show_kkm                    boolean,
    auto_add                    boolean,
    foreign key (price_type_id) references sprav_type_prices (id),
    foreign key (master_id) references users(id),
    foreign key (user_id) references users(id),
    foreign key (company_id) references companies(id)
);
alter table settings_shipment add constraint settings_shipment_user_uq UNIQUE (user_id);
alter table settings_shipment add constraint department_id_fkey foreign key (department_id) references departments (id);
alter table settings_shipment add constraint customer_id_fkey foreign key (customer_id) references cagents (id);
alter table settings_shipment add constraint status_id_on_complete_fkey foreign key (status_id_on_complete) references sprav_status_dock (id);

alter table shipment add column shift_id bigint;
alter table shipment add constraint shift_id_fkey foreign key (shift_id) references shifts (id);
alter table shipment add column customers_orders_id bigint;
alter table shipment add constraint customers_orders_id_fkey foreign key (customers_orders_id) references customers_orders (id);
alter table receipts add column shipment_id bigint;
alter table receipts add constraint shipment_id_fkey foreign key (shipment_id) references shipment (id);

drop table shipment_product;

create table shipment_product (
 id bigserial primary key not null,
 master_id bigint not null,
 company_id bigint not null,
 product_id bigint not null,
 shipment_id bigint not null,
 product_count numeric(15,3) not null,
 product_price numeric(12,2),
 product_sumprice numeric(15,2),
 edizm_id int not null,
 price_type_id bigint,
 nds_id bigint not null,
 department_id bigint not null,
 product_price_of_type_price numeric(12,2),

 foreign key (shipment_id) references shipment (id),
 foreign key (edizm_id) references sprav_sys_edizm (id),
 foreign key (nds_id) references sprav_sys_nds (id),
 foreign key (price_type_id) references sprav_type_prices (id),
 foreign key (product_id ) references products (id),
 foreign key (department_id ) references departments (id)
);

ALTER TABLE shipment_product ADD CONSTRAINT shipment_product_uq UNIQUE (product_id, shipment_id, department_id);

alter table shipment_product drop column edizm_id;
update documents set page_name='customersorders' where id=23;
update documents set table_name='retail_sales' where id=25;
alter table retail_sales_product drop column edizm_id;

insert into permissions (id,name,description,document_name,document_id) values
(400,'Проведение документов по всем предприятиям','Проведение документов "Заказ покупателя" по всем предприятиям','Заказ покупателя',23),
(401,'Проведение документов своего предприятия','Проведение документов "Заказ покупателя" своего предприятия','Заказ покупателя',23),
(402,'Проведение документов своих отделений','Проведение документов "Заказ покупателя" по своим отделениям','Заказ покупателя',23),
(403,'Проведение документов созданных собой','Проведение документов "Заказ покупателя" созданных собой','Заказ покупателя',23);

alter table customers_orders drop column is_archive;


insert into documents (id, name, page_name, show, table_name, doc_name_ru) values (31,'Счет покупателю','invoiceout',1,'invoiceout','Счет покупателю');
insert into documents (id, name, page_name, show, table_name, doc_name_ru) values (32,'Счет поставщика','invoicein',1,'invoicein','Счет поставщика');
insert into documents (id, name, page_name, show, table_name, doc_name_ru) values (33,'Входящий платеж','paymentin',1,'paymentin','Входящий платеж');
insert into documents (id, name, page_name, show, table_name, doc_name_ru) values (34,'Исходящий платеж','paymentout',1,'paymentout','Исходящий платеж');
insert into documents (id, name, page_name, show, table_name, doc_name_ru) values (35,'Приходный ордер','orderin',1,'orderin','Приходный ордер');
insert into documents (id, name, page_name, show, table_name, doc_name_ru) values (36,'Расходный ордер','orderout',1,'orderout','Расходный ордер');
insert into documents (id, name, page_name, show, table_name, doc_name_ru) values (37,'Счет-фактура выданный','vatinvoiceout',1,'vatinvoiceout','Счет-фактура выданный');
insert into documents (id, name, page_name, show, table_name, doc_name_ru) values (38,'Счет-фактура полученный','vatinvoicein',1,'vatinvoicein','Счет-фактура полученный');

create table invoiceout(
 id bigserial primary key not null,
 master_id  bigint not null, 
 creator_id bigint not null,
 changer_id bigint,
 date_time_created timestamp with time zone not null,
 date_time_changed timestamp with time zone,
 company_id bigint not null,  
 department_id bigint not null,
 cagent_id bigint not null,
 status_id bigint,
 doc_number int not null,
 description varchar(2048),
 nds boolean,
 nds_included boolean,
 is_deleted boolean,
 invoiceout_date date,
 is_completed boolean,
 uid varchar (36),
 linked_docs_group_id bigint,
 
 foreign key (master_id) references users(id),
 foreign key (creator_id) references users(id),
 foreign key (changer_id) references users(id),
 foreign key (company_id) references companies(id),
 foreign key (department_id) references departments(id),
 foreign key (cagent_id) references cagents(id),
 foreign key (linked_docs_group_id) references linked_docs_groups(id),
 foreign key (status_id) references sprav_status_dock (id) ON DELETE SET NULL
);

alter table linked_docs add column invoiceout_id bigint;

alter table linked_docs add constraint invoiceout_id_fkey foreign key (invoiceout_id) references invoiceout (id);

create table settings_invoiceout (
    id                          bigserial primary key not null,
    master_id                   bigint not null, 
    company_id                  bigint not null,  
    user_id                     bigint  UNIQUE not null,
    customer_id                 bigint,
    department_id               bigint,
    priority_type_price_side    varchar(8),
    pricing_type                varchar(32), 
    price_type_id               bigint,
    change_price                numeric(12,2),
    plus_minus                  varchar(8),
    change_price_type           varchar(8),
    hide_tenths                 boolean,
    save_settings               boolean,
    autocreate                  boolean,
    status_id_on_complete       bigint,
    auto_add                    boolean,
    foreign key (price_type_id) references sprav_type_prices (id),
    foreign key (master_id) references users(id),
    foreign key (department_id) references departments(id),
    foreign key (customer_id) references cagents(id),
    foreign key (user_id) references users(id),
    foreign key (status_id_on_complete) references sprav_status_dock(id),
    foreign key (company_id) references companies(id)
);

create table invoiceout_product (
 id bigserial primary key not null,
 master_id bigint not null,
 company_id bigint not null,
 product_id bigint not null,
 invoiceout_id bigint not null,
 product_count numeric(15,3) not null,
 product_price numeric(12,2),
 product_sumprice numeric(15,2),
 price_type_id bigint,
 nds_id bigint not null,
 department_id bigint not null,
 product_price_of_type_price numeric(12,2),

 foreign key (invoiceout_id) references invoiceout (id), 
 foreign key (nds_id) references sprav_sys_nds (id),
 foreign key (price_type_id) references sprav_type_prices (id),
 foreign key (product_id ) references products (id),
 foreign key (department_id ) references departments (id)
);

ALTER TABLE invoiceout_product ADD CONSTRAINT invoiceout_product_uq UNIQUE (product_id, invoiceout_id, department_id);

insert into permissions (id,name,description,document_name,document_id) values
(404,'Боковая панель - отображать в списке документов','Показывать документ в списке документов на боковой панели','Счет покупателя',31),
(405,'Создание документов по всем предприятиям','Возможность создавать новые документы "Счет покупателя" по всем предприятиям','Счет покупателя',31),
(406,'Создание документов своего предприятия','Возможность создавать новые документы "Счет покупателя" своего предприятия','Счет покупателя',31),
(407,'Создание документов своих отделений','Возможность создавать новые документы "Счет покупателя" по своим отделениям','Счет покупателя',31),
(408,'Удаление документов по всем предприятиям','Возможность удалить документ "Счет покупателя" в архив по всем предприятиям','Счет покупателя',31),
(409,'Удаление документов своего предприятия','Возможность удалить документ "Счет покупателя" своего предприятия в архив','Счет покупателя',31),
(410,'Удаление документов своих отделений','Возможность удалить документ "Счет покупателя" одного из своих отделений','Счет покупателя',31),
(411,'Удаление документов созданных собой','Возможность удаления документов "Счет покупателя", созданных собой','Счет покупателя',31),
(412,'Просмотр документов по всем предприятиям','Прсмотр информации в документах "Счет покупателя" по всем предприятиям','Счет покупателя',31),
(413,'Просмотр документов своего предприятия','Прсмотр информации в документах "Счет покупателя" своего предприятия','Счет покупателя',31),
(414,'Просмотр документов своих отделений','Прсмотр информации в документах "Счет покупателя" по своим отделениям','Счет покупателя',31),
(415,'Просмотр документов созданных собой','Прсмотр информации в документах "Счет покупателя", созданных собой','Счет покупателя',31),
(416,'Редактирование документов по всем предприятиям','Редактирование документов "Счет покупателя" по всем предприятиям','Счет покупателя',31),
(417,'Редактирование документов своего предприятия','Редактирование документов "Счет покупателя" своего предприятия','Счет покупателя',31),
(418,'Редактирование документов своих отделений','Редактирование документов "Счет покупателя" по своим отделениям','Счет покупателя',31),
(419,'Редактирование документов созданных собой','Редактирование документов "Счет покупателя", созданных собой','Счет покупателя',31),
(420,'Проведение документов по всем предприятиям','Проведение документов "Счет покупателя" по всем предприятиям','Счет покупателя',31),
(421,'Проведение документов своего предприятия','Проведение документов "Счет покупателя" своего предприятия','Счет покупателя',31),
(422,'Проведение документов своих отделений','Проведение документов "Счет покупателя" по своим отделениям','Счет покупателя',31),
(423,'Проведение документов созданных собой','Проведение документов "Счет покупателя" созданных собой','Счет покупателя',31);


ALTER TABLE inventory_product ADD column product_sumprice numeric(15,2);

insert into documents (id, name, page_name, show, table_name, doc_name_ru) values (39,'Заказ поставщику','ordersup',1,'ordersup','Заказ поставщику');


create table ordersup(
                       id bigserial primary key not null,
                       master_id  bigint not null,
                       creator_id bigint not null,
                       changer_id bigint,
                       date_time_created timestamp with time zone not null,
                       date_time_changed timestamp with time zone,
                       company_id bigint not null,
                       department_id bigint not null,
                       cagent_id bigint not null,
                       status_id bigint,
                       doc_number int not null,
                       description varchar(2048),
                       nds boolean,
                       nds_included boolean,
                       is_deleted boolean,
                       ordersup_date date,
                       is_completed boolean,
                       uid varchar (36),
                       linked_docs_group_id bigint,

                       foreign key (master_id) references users(id),
                       foreign key (creator_id) references users(id),
                       foreign key (changer_id) references users(id),
                       foreign key (company_id) references companies(id),
                       foreign key (department_id) references departments(id),
                       foreign key (cagent_id) references cagents(id),
                       foreign key (linked_docs_group_id) references linked_docs_groups(id),
                       foreign key (status_id) references sprav_status_dock (id) ON DELETE SET NULL
);

alter table linked_docs add column ordersup_id bigint;

alter table linked_docs add constraint ordersup_id_fkey foreign key (ordersup_id) references ordersup (id);

create table settings_ordersup (
                                 id                          bigserial primary key not null,
                                 master_id                   bigint not null,
                                 company_id                  bigint not null,
                                 user_id                     bigint  UNIQUE not null,
                                 cagent_id                   bigint,
                                 department_id               bigint,
                                 autocreate                  boolean,
                                 status_id_on_complete       bigint,
                                 auto_add                    boolean,
                                 auto_price                  boolean,
                                 name                        varchar(512),
                                 foreign key (master_id) references users(id),
                                 foreign key (department_id) references departments(id),
                                 foreign key (cagent_id) references cagents(id),
                                 foreign key (user_id) references users(id),
                                 foreign key (status_id_on_complete) references sprav_status_dock(id),
                                 foreign key (company_id) references companies(id)
);

create table ordersup_product (
                                id bigserial primary key not null,
                                master_id bigint not null,
                                company_id bigint not null,
                                product_id bigint not null,
                                ordersup_id bigint not null,
                                product_count numeric(15,3) not null,
                                product_price numeric(12,2),
                                product_sumprice numeric(15,2),
                                nds_id bigint not null,

                                foreign key (ordersup_id) references ordersup (id),
                                foreign key (nds_id) references sprav_sys_nds (id),
                                foreign key (product_id ) references products (id)
);

ALTER TABLE ordersup_product ADD CONSTRAINT ordersup_product_uq UNIQUE (product_id, ordersup_id);

insert into permissions (id,name,description,document_name,document_id) values
(424,'Боковая панель - отображать в списке документов','Показывать документ в списке документов на боковой панели','Заказ поставщику',39),
(425,'Создание документов по всем предприятиям','Возможность создавать новые документы "Заказ поставщику" по всем предприятиям','Заказ поставщику',39),
(426,'Создание документов своего предприятия','Возможность создавать новые документы "Заказ поставщику" своего предприятия','Заказ поставщику',39),
(427,'Создание документов своих отделений','Возможность создавать новые документы "Заказ поставщику" по своим отделениям','Заказ поставщику',39),
(428,'Удаление документов по всем предприятиям','Возможность удалить документ "Заказ поставщику" в архив по всем предприятиям','Заказ поставщику',39),
(429,'Удаление документов своего предприятия','Возможность удалить документ "Заказ поставщику" своего предприятия в архив','Заказ поставщику',39),
(430,'Удаление документов своих отделений','Возможность удалить документ "Заказ поставщику" одного из своих отделений','Заказ поставщику',39),
(431,'Удаление документов созданных собой','Возможность удаления документов "Заказ поставщику", созданных собой','Заказ поставщику',39),
(432,'Просмотр документов по всем предприятиям','Прсмотр информации в документах "Заказ поставщику" по всем предприятиям','Заказ поставщику',39),
(433,'Просмотр документов своего предприятия','Прсмотр информации в документах "Заказ поставщику" своего предприятия','Заказ поставщику',39),
(434,'Просмотр документов своих отделений','Прсмотр информации в документах "Заказ поставщику" по своим отделениям','Заказ поставщику',39),
(435,'Просмотр документов созданных собой','Прсмотр информации в документах "Заказ поставщику", созданных собой','Заказ поставщику',39),
(436,'Редактирование документов по всем предприятиям','Редактирование документов "Заказ поставщику" по всем предприятиям','Заказ поставщику',39),
(437,'Редактирование документов своего предприятия','Редактирование документов "Заказ поставщику" своего предприятия','Заказ поставщику',39),
(438,'Редактирование документов своих отделений','Редактирование документов "Заказ поставщику" по своим отделениям','Заказ поставщику',39),
(439,'Редактирование документов созданных собой','Редактирование документов "Заказ поставщику", созданных собой','Заказ поставщику',39),
(440,'Проведение документов по всем предприятиям','Проведение документов "Заказ поставщику" по всем предприятиям','Заказ поставщику',39),
(441,'Проведение документов своего предприятия','Проведение документов "Заказ поставщику" своего предприятия','Заказ поставщику',39),
(442,'Проведение документов своих отделений','Проведение документов "Заказ поставщику" по своим отделениям','Заказ поставщику',39),
(443,'Проведение документов созданных собой','Проведение документов "Заказ поставщику" созданных собой','Заказ поставщику',39);

alter table ordersup add column name varchar(512);

create table ordersup_files (
                               ordersup_id bigint not null,
                               file_id bigint not null,
                               foreign key (file_id) references files (id) ON DELETE CASCADE,
                               foreign key (ordersup_id ) references ordersup (id) ON DELETE CASCADE
);


create table invoicein(
                        id bigserial primary key not null,
                        master_id  bigint not null,
                        creator_id bigint not null,
                        changer_id bigint,
                        date_time_created timestamp with time zone not null,
                        date_time_changed timestamp with time zone,
                        company_id bigint not null,
                        department_id bigint not null,
                        cagent_id bigint not null,
                        status_id bigint,
                        doc_number int not null,
                        description varchar(2048),
                        nds boolean,
                        nds_included boolean,
                        is_deleted boolean,
                        invoicein_date date,
                        is_completed boolean,
                        uid varchar (36),
                        linked_docs_group_id bigint,
                        name varchar(512),
                        income_number varchar(64),
                        income_number_date date,

                        foreign key (master_id) references users(id),
                        foreign key (creator_id) references users(id),
                        foreign key (changer_id) references users(id),
                        foreign key (company_id) references companies(id),
                        foreign key (department_id) references departments(id),
                        foreign key (cagent_id) references cagents(id),
                        foreign key (linked_docs_group_id) references linked_docs_groups(id),
                        foreign key (status_id) references sprav_status_dock (id) ON DELETE SET NULL
);

alter table linked_docs add column invoicein_id bigint;

alter table linked_docs add constraint invoicein_id_fkey foreign key (invoicein_id) references invoicein (id);

create table settings_invoicein (
                                  id                          bigserial primary key not null,
                                  master_id                   bigint not null,
                                  company_id                  bigint not null,
                                  user_id                     bigint  UNIQUE not null,
                                  cagent_id                   bigint,
                                  department_id               bigint,
                                  autocreate                  boolean,
                                  status_id_on_complete       bigint,
                                  auto_add                    boolean,
                                  auto_price                  boolean,
                                  name                        varchar(512),
                                  foreign key (master_id) references users(id),
                                  foreign key (department_id) references departments(id),
                                  foreign key (cagent_id) references cagents(id),
                                  foreign key (user_id) references users(id),
                                  foreign key (status_id_on_complete) references sprav_status_dock(id),
                                  foreign key (company_id) references companies(id)
);

create table invoicein_product (
                                 id bigserial primary key not null,
                                 master_id bigint not null,
                                 company_id bigint not null,
                                 product_id bigint not null,
                                 invoicein_id bigint not null,
                                 product_count numeric(15,3) not null,
                                 product_price numeric(12,2),
                                 product_sumprice numeric(15,2),
                                 nds_id bigint not null,

                                 foreign key (invoicein_id) references invoicein (id),
                                 foreign key (nds_id) references sprav_sys_nds (id),
                                 foreign key (product_id ) references products (id)
);

ALTER TABLE invoicein_product ADD CONSTRAINT invoicein_product_uq UNIQUE (product_id, invoicein_id);

insert into permissions (id,name,description,document_name,document_id) values
(444,'Боковая панель - отображать в списке документов','Показывать документ в списке документов на боковой панели','Счёт поставщика',32),
(445,'Создание документов по всем предприятиям','Возможность создавать новые документы "Счёт поставщика" по всем предприятиям','Счёт поставщика',32),
(446,'Создание документов своего предприятия','Возможность создавать новые документы "Счёт поставщика" своего предприятия','Счёт поставщика',32),
(447,'Создание документов своих отделений','Возможность создавать новые документы "Счёт поставщика" по своим отделениям','Счёт поставщика',32),
(448,'Удаление документов по всем предприятиям','Возможность удалить документ "Счёт поставщика" в архив по всем предприятиям','Счёт поставщика',32),
(449,'Удаление документов своего предприятия','Возможность удалить документ "Счёт поставщика" своего предприятия в архив','Счёт поставщика',32),
(450,'Удаление документов своих отделений','Возможность удалить документ "Счёт поставщика" одного из своих отделений','Счёт поставщика',32),
(451,'Удаление документов созданных собой','Возможность удаления документов "Счёт поставщика", созданных собой','Счёт поставщика',32),
(452,'Просмотр документов по всем предприятиям','Прсмотр информации в документах "Счёт поставщика" по всем предприятиям','Счёт поставщика',32),
(453,'Просмотр документов своего предприятия','Прсмотр информации в документах "Счёт поставщика" своего предприятия','Счёт поставщика',32),
(454,'Просмотр документов своих отделений','Прсмотр информации в документах "Счёт поставщика" по своим отделениям','Счёт поставщика',32),
(455,'Просмотр документов созданных собой','Прсмотр информации в документах "Счёт поставщика", созданных собой','Счёт поставщика',32),
(456,'Редактирование документов по всем предприятиям','Редактирование документов "Счёт поставщика" по всем предприятиям','Счёт поставщика',32),
(457,'Редактирование документов своего предприятия','Редактирование документов "Счёт поставщика" своего предприятия','Счёт поставщика',32),
(458,'Редактирование документов своих отделений','Редактирование документов "Счёт поставщика" по своим отделениям','Счёт поставщика',32),
(459,'Редактирование документов созданных собой','Редактирование документов "Счёт поставщика", созданных собой','Счёт поставщика',32),
(460,'Проведение документов по всем предприятиям','Проведение документов "Счёт поставщика" по всем предприятиям','Счёт поставщика',32),
(461,'Проведение документов своего предприятия','Проведение документов "Счёт поставщика" своего предприятия','Счёт поставщика',32),
(462,'Проведение документов своих отделений','Проведение документов "Счёт поставщика" по своим отделениям','Счёт поставщика',32),
(463,'Проведение документов созданных собой','Проведение документов "Счёт поставщика" созданных собой','Счёт поставщика',32);

create table invoicein_files (
                               invoicein_id bigint not null,
                               file_id bigint not null,
                               foreign key (file_id) references files (id) ON DELETE CASCADE,
                               foreign key (invoicein_id ) references invoicein (id) ON DELETE CASCADE
);


create table paymentin(
                        id bigserial primary key not null,
                        master_id  bigint not null,
                        creator_id bigint not null,
                        changer_id bigint,
                        date_time_created timestamp with time zone not null,
                        date_time_changed timestamp with time zone,
                        company_id bigint not null,
                        cagent_id bigint not null,
                        status_id bigint,
                        doc_number int not null,
                        description varchar(2048),
                        summ  numeric(15,3) not null,
                        nds  numeric(15,3) not null,
                        is_deleted boolean,
                        is_completed boolean,
                        uid varchar (36),
                        linked_docs_group_id bigint,
                        income_number varchar(64),
                        income_number_date date,

                        foreign key (master_id) references users(id),
                        foreign key (creator_id) references users(id),
                        foreign key (changer_id) references users(id),
                        foreign key (company_id) references companies(id),
                        foreign key (cagent_id) references cagents(id),
                        foreign key (linked_docs_group_id) references linked_docs_groups(id),
                        foreign key (status_id) references sprav_status_dock (id) ON DELETE SET NULL
);

alter table linked_docs add column paymentin_id bigint;

alter table linked_docs add constraint paymentin_id_fkey foreign key (paymentin_id) references paymentin (id);

create table settings_paymentin (
                                  id                          bigserial primary key not null,
                                  master_id                   bigint not null,
                                  company_id                  bigint not null,
                                  user_id                     bigint  UNIQUE not null,
                                  cagent_id                   bigint,
                                  autocreate                  boolean,
                                  status_id_on_complete       bigint,
                                  foreign key (master_id) references users(id),
                                  foreign key (cagent_id) references cagents(id),
                                  foreign key (user_id) references users(id),
                                  foreign key (status_id_on_complete) references sprav_status_dock(id),
                                  foreign key (company_id) references companies(id)
);

insert into permissions (id,name,description,document_name,document_id) values
(464,'Боковая панель - отображать в списке документов','Показывать документ в списке документов на боковой панели','Входящий платеж',33),
(465,'Создание документов по всем предприятиям','Возможность создавать новые документы "Входящий платеж" по всем предприятиям','Входящий платеж',33),
(466,'Создание документов своего предприятия','Возможность создавать новые документы "Входящий платеж" своего предприятия','Входящий платеж',33),
(467,'Удаление документов по всем предприятиям','Возможность удалить документ "Входящий платеж" в архив по всем предприятиям','Входящий платеж',33),
(468,'Удаление документов своего предприятия','Возможность удалить документ "Входящий платеж" своего предприятия в архив','Входящий платеж',33),
(469,'Просмотр документов по всем предприятиям','Прсмотр информации в документах "Входящий платеж" по всем предприятиям','Входящий платеж',33),
(470,'Просмотр документов своего предприятия','Прсмотр информации в документах "Входящий платеж" своего предприятия','Входящий платеж',33),
(471,'Редактирование документов по всем предприятиям','Редактирование документов "Входящий платеж" по всем предприятиям','Входящий платеж',33),
(472,'Редактирование документов своего предприятия','Редактирование документов "Входящий платеж" своего предприятия','Входящий платеж',33),
(473,'Проведение документов по всем предприятиям','Проведение документов "Входящий платеж" по всем предприятиям','Входящий платеж',33),
(474,'Проведение документов своего предприятия','Проведение документов "Входящий платеж" своего предприятия','Входящий платеж',33);

create table paymentin_files (
                               paymentin_id bigint not null,
                               file_id bigint not null,
                               foreign key (file_id) references files (id) ON DELETE CASCADE,
                               foreign key (paymentin_id ) references paymentin (id) ON DELETE CASCADE
);


create table orderin(
                      id bigserial primary key not null,
                      master_id  bigint not null,
                      creator_id bigint not null,
                      changer_id bigint,
                      date_time_created timestamp with time zone not null,
                      date_time_changed timestamp with time zone,
                      company_id bigint not null,
                      cagent_id bigint not null,
                      status_id bigint,
                      doc_number int not null,
                      description varchar(2048),
                      summ  numeric(15,3) not null,
                      nds  numeric(15,3) not null,
                      is_deleted boolean,
                      is_completed boolean,
                      uid varchar (36),
                      linked_docs_group_id bigint,

                      foreign key (master_id) references users(id),
                      foreign key (creator_id) references users(id),
                      foreign key (changer_id) references users(id),
                      foreign key (company_id) references companies(id),
                      foreign key (cagent_id) references cagents(id),
                      foreign key (linked_docs_group_id) references linked_docs_groups(id),
                      foreign key (status_id) references sprav_status_dock (id) ON DELETE SET NULL
);

alter table linked_docs add column orderin_id bigint;

alter table linked_docs add constraint orderin_id_fkey foreign key (orderin_id) references orderin (id);

create table settings_orderin (
                                id                          bigserial primary key not null,
                                master_id                   bigint not null,
                                company_id                  bigint not null,
                                user_id                     bigint  UNIQUE not null,
                                cagent_id                   bigint,
                                autocreate                  boolean,
                                status_id_on_complete       bigint,
                                foreign key (master_id) references users(id),
                                foreign key (cagent_id) references cagents(id),
                                foreign key (user_id) references users(id),
                                foreign key (status_id_on_complete) references sprav_status_dock(id),
                                foreign key (company_id) references companies(id)
);

insert into permissions (id,name,description,document_name,document_id) values
(475,'Боковая панель - отображать в списке документов','Показывать документ в списке документов на боковой панели','Приходный ордер',35),
(476,'Создание документов по всем предприятиям','Возможность создавать новые документы "Приходный ордер" по всем предприятиям','Приходный ордер',35),
(477,'Создание документов своего предприятия','Возможность создавать новые документы "Приходный ордер" своего предприятия','Приходный ордер',35),
(478,'Удаление документов по всем предприятиям','Возможность удалить документ "Приходный ордер" в архив по всем предприятиям','Приходный ордер',35),
(479,'Удаление документов своего предприятия','Возможность удалить документ "Приходный ордер" своего предприятия в архив','Приходный ордер',35),
(480,'Просмотр документов по всем предприятиям','Прсмотр информации в документах "Приходный ордер" по всем предприятиям','Приходный ордер',35),
(481,'Просмотр документов своего предприятия','Прсмотр информации в документах "Приходный ордер" своего предприятия','Приходный ордер',35),
(482,'Редактирование документов по всем предприятиям','Редактирование документов "Приходный ордер" по всем предприятиям','Приходный ордер',35),
(483,'Редактирование документов своего предприятия','Редактирование документов "Приходный ордер" своего предприятия','Приходный ордер',35),
(484,'Проведение документов по всем предприятиям','Проведение документов "Приходный ордер" по всем предприятиям','Приходный ордер',35),
(485,'Проведение документов своего предприятия','Проведение документов "Приходный ордер" своего предприятия','Приходный ордер',35);

create table orderin_files (
                             orderin_id bigint not null,
                             file_id bigint not null,
                             foreign key (file_id) references files (id) ON DELETE CASCADE,
                             foreign key (orderin_id ) references orderin (id) ON DELETE CASCADE
);