use bpa_lab_demonstration_factory;

create table if not exists bike_model
(
    title  varchar(255) not null
    primary key,
    color  varchar(255) null,
    weight double       null
    );

create table if not exists bike_component
(
    title         varchar(255) not null
    primary key,
    color         varchar(255) null,
    quantity      int          null,
    bike_model_id varchar(255) not null,
    constraint FKobd1ubg213tsx5nk18vpwa8vb
    foreign key (bike_model_id) references bike_model (title)
    );

create table if not exists bike_instance
(
    serial_number         char(36)     not null
    primary key,
    customer_order_number varchar(255) null,
    shipped               bit          null,
    bike_model_id         varchar(255) not null,
    constraint FK4v35au35wmk25thne2tawciyp
    foreign key (bike_model_id) references bike_model (title)
    );

create table if not exists customer_order
(
    customer_order_number varchar(255) not null
    primary key,
    creation_date         varchar(255) null,
    customer_adress       varchar(255) null,
    customer_email        varchar(255) null,
    customer_name         varchar(255) null
    );

create table if not exists customer_order_item
(
    id            bigint auto_increment
    primary key,
    quantity      int          null,
    bike_model_id varchar(255) not null,
    order_id      varchar(255) null,
    constraint FK8vxv2sd234ll98c4qf09lhpn3
    foreign key (order_id) references customer_order (customer_order_number),
    constraint FKig8jyjsep7tc42iscu799b6il
    foreign key (bike_model_id) references bike_model (title)
    );

create table if not exists production_order
(
    production_order_number varchar(255) not null
    primary key,
    customer_order_number   varchar(255) null,
    quantity                int          null,
    bike_model_id           varchar(255) not null,
    constraint FKkjyebbajb353vru4rki08j7av
    foreign key (bike_model_id) references bike_model (title)
    );

create table if not exists purchase_order
(
    purchase_order_number   varchar(255) not null
    primary key,
    production_order_number varchar(255) null,
    quantity                int          null,
    bike_component_id       varchar(255) not null,
    vendor_name             varchar(255) not null,
    constraint FKno7xhx9si64ghwxojtf96kn88
    foreign key (bike_component_id) references bike_component (title)
    );

create table if not exists vendor
(
    vendor_name    varchar(255) not null
    primary key,
    vendor_contact varchar(255) null
    );

create table if not exists vendor_bike_component
(
    vendor_name         varchar(255) not null,
    bikecomponent_title varchar(255) not null,
    price               double       null,
    primary key (vendor_name, bikecomponent_title),
    constraint FKiih35bm2koc68i9y3ji8ubmcx
    foreign key (vendor_name) references vendor (vendor_name),
    constraint FKm6qiubla97d6mihosgnkgf7co
    foreign key (bikecomponent_title) references bike_component (title)
    );

insert into bike_model (title, color, weight)
values ('Esel Lastenrad', 'RED', 24.5);
insert into bike_model (title, color, weight)
values ('Schaf Citybike', 'WHITE', 20.6);
insert into bike_model (title, color, weight)
values ('Ziege Mountainbike', 'BLUE', 17.5);

insert into bike_component (title, color, quantity, bike_model_id)
values ('Citybike Component Kit', 'WHITE', 0, 'Schaf Citybike');
insert into bike_component (title, color, quantity, bike_model_id)
values ('Lastenrad Component Kit', 'RED', 1, 'Esel Lastenrad');
insert into bike_component (title, color, quantity, bike_model_id)
values ('Mountainbike Component Kit', 'BLUE', 2, 'Ziege Mountainbike');

insert into vendor
values ("Hans Hansen Comps", "hans@hansen.de");
insert into vendor
values ("Paulsen Bike Componente", "paul@paulsen.it");

insert into vendor_bike_component
values ("Hans Hansen Comps", "Citybike Component Kit", 111.11);
insert into vendor_bike_component
values ("Paulsen Bike Componente", "Citybike Component Kit", 122.22);
insert into vendor_bike_component
values ("Paulsen Bike Componente", "Lastenrad Component Kit", 145);
insert into vendor_bike_component
values ("Paulsen Bike Componente", "Mountainbike Component Kit", 130);
